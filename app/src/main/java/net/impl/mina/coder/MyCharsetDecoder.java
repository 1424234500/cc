package net.impl.mina.coder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import util.AndroidTools;
import util.Tools;

public class MyCharsetDecoder extends CumulativeProtocolDecoder {

	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

		if (in.remaining() > 4) {// 前4字节是包头
			// 标记当前position的快照标记mark，以便后继的reset操作能恢复position位置
			in.mark();

			// byte[] l = new byte[4];
			// in.get(l);
			// int len = Tools.bytes2int(l);// 将byte转成int
			int len = in.getInt();
			out("解码：" + " len=" + len);
			// 注意上面的get操作会导致下面的remaining()值发生变化
			if (in.remaining() < len) {
				// 如果消息内容不够，则重置恢复position位置到操作前,进入下一轮, 接收新数据，以拼凑成完整数据
				in.reset();
				return false;
			} else {
				// 消息内容足够
				// in.reset();// 重置恢复position位置到操作前
				int sumlen = /* 4 + */len;// 总长 = 包头+包体
				byte[] packArr = new byte[sumlen];
				in.get(packArr, 0, sumlen);

				String jsonstr = new String(packArr,
						MyCharsetCodecFactory.charset);
				out("数据:" + jsonstr);

				out.write(jsonstr);

				if (in.remaining() > 0) {// 如果读取一个完整包内容后还粘了包，就让父类再调用一次，进行下一次解析
					return true;
				}
			}
		}
		return false;// 处理成功，让父类进行接收下个包
	}
	public void out(String str){
//		AndroidTools.out(str);
	}
}