package interfac;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

//通信接口 
public interface InterfaceCS {
	public void out(String str); 
	public boolean start();
	public boolean stop();
	public void send(String str);
	public void receive(String str);
}
