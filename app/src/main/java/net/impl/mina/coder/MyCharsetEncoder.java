package net.impl.mina.coder;

import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import util.tools.Tools;

//mina编解码流程：request->MyDecoder->MyHandler->MyEncode->response

public class MyCharsetEncoder extends ProtocolEncoderAdapter {
	Charset charset = Charset.forName("UTF-8");

	// 在此处实现包的编码工作，并把它写入输出流中
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		// TODO Auto-generated method stub
		//写入 message string json
		byte[] dataBytes = ((String) message).getBytes(charset);
		byte[] sizeBytes = Tools.int2bytes(dataBytes.length);
		Tools.out("编码：len=" + dataBytes.length + " 数据：" + message);

		IoBuffer buffer;
		buffer = IoBuffer.allocate(100).setAutoExpand(true);
		
		buffer.putInt(dataBytes.length);	//写入长度
		// 	buffer.put(sizeBytes);
		buffer.put(dataBytes);	//写入对应的jsonstr 的 字节数组
		buffer.flip();

		out.write(buffer);
		out.flush();
		buffer.free();

	}

}