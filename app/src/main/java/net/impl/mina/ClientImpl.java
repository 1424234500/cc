package net.impl.mina;

import android.os.Looper;

import interfac.CallString;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.Client;
import net.MSGTYPE;
import net.impl.mina.coder.MyCharsetCodecFactory;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.cc.Constant;


import util.AndroidTools;
import util.JsonUtil;
import util.Tools;

public abstract class ClientImpl implements Client {
    private NioSocketConnector connector;
    private ConnectFuture future;
    private IoSession session;

    public static int port;
    public static String ip;


    public ClientImpl() {
    }

    @Override
    public void out(String str) {
        AndroidTools.out("Mina." + str);
    }

    @Override
    public boolean start() {
        connect();
        return true;
    }

    @Override
    public boolean stop() {
        out(" 客户端关闭");
        try {
            session.close();
            future.cancel();
            connector.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    int ipcount = 0;
    static boolean ifOnConn = false;

    public void whileConn() {
        ifOnConn = true;

        new Thread() {
            public void run() {
                while (ifOnConn) {
                    try {
                        Constant.serverIp = Constant.serverIps[ipcount];
                        ipcount = (ipcount + 1) % Constant.serverIps.length;
                        ip = Constant.serverIp;
                        port = Constant.serverPort;

                        // 连接服务器，知道端口、地址
                        future = connector.connect(new InetSocketAddress(ip, port));
                        AndroidTools.out("等待服务器响应" + ip + ":" + port);
                        // 等待连接创建完成
                        future.awaitUninterruptibly();
                        // 获取当前session
                        session = future.getSession();
                        AndroidTools.out(" 连接服务器成功！ ");
                        ifOnConn = false;
                        break;
                    } catch (Exception e) {
                        AndroidTools.out("连接异常,稍后后尝试重新连接");
                        AndroidTools.sleep(100);
                    }
                }
            }
        }.start();

    }

    public boolean connect() {
        // 创建一个socket连接
        if (connector == null) {
            connector = new NioSocketConnector();
            // 设置链接超时时间
            connector.setConnectTimeoutMillis(6000);
            // 获取过滤器链
            DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
            // 添加编码过滤器 处理乱码、编码问题
            filterChain.addLast("codec", new ProtocolCodecFilter( /*new TextLineCodecFactory(Charset.forName("UTF-8"))*/ new MyCharsetCodecFactory()));
            // 消息核心处理器
            connector.setHandler(new IoHandlerAdapter(){
                public void messageReceived(IoSession session, Object message) throws Exception {
                    String content = (String)message;
                    out("Rece<<<<" + content);
                    onReceive(content);
                }

                public void messageSent(IoSession session, Object message) throws Exception {
                    out("Send>>>>" + (String)message);
                }

                @Override
                public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                    out("clientimpl 异常" + cause.getMessage());
                    disConn();
                    //reconnect("");
                }
            });
        }
        whileConn();

        return true;
    }

    public void setAttribute(Object key, Object value) {
        session.setAttribute(key, value);
    }
    public Object getAttribute(Object key, Object value) {
        return session.getAttribute(key, value);
    }
    @Override
    public void send(String message) {
        if (session != null && session.isConnected()) {
            session.write(message);
        } else {
            onReceive(JsonUtil.makeJson(MSGTYPE.TOAST, "连接中"));
            reconnect("");
        }
    }

    public void disConn() {
        onReceive(JsonUtil.makeJson(MSGTYPE.CLOSE, "连接断开"));
    }


    @Override
    public void reconnect(String string) {
        if(string.length() > 0){
            String[] arr = new String[Constant.serverIps.length + 1];
            //新添加ip作为连接ip集合
            for(int i = 0; i < Constant.serverIps.length; i++){
                arr[i] = Constant.serverIps[i];
            }
            arr[Constant.serverIps.length] = string;
            Constant.serverIps = arr;
            ipcount = Constant.serverIps.length - 1;
        }
        if (ifOnConn) {

        } else {
            stop();
            AndroidTools.log("断开重连");
            connect();
        }


    }

}