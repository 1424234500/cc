package net.impl.mina.coder;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * <b>function:</b> 字符编码、解码工厂类，编码过滤工厂
 * //mina编解码流程：request->MyDecoder->MyHandler->MyEncode->response
 */
public class MyCharsetCodecFactory implements ProtocolCodecFactory {
	public static Charset charset = Charset.forName("UTF-8"/*"ISO-8859-1"*/);

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return new MyCharsetDecoder( );
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return new MyCharsetEncoder( );
	}
}