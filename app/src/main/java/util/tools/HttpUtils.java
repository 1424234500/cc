package util.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;  
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.net.HttpURLConnection;  
import java.net.URL;  

import com.cc.R;
public class HttpUtils  
{  

	
	/**
     * 从网络获取json数据,(String byte[})

     * @param path
     * @return
     */
    public static String getJsonByInternet(String path){
        try {
            URL url = new URL(path.trim());
            //打开连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            if(200 == urlConnection.getResponseCode()){
                //得到输入流
                InputStream is =urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while(-1 != (len = is.read(buffer))){
                    baos.write(buffer,0,len);
                    baos.flush();
                }
                return baos.toString("utf-8");
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
	
	

  //获取其他页面的数据
      /**
       * POST请求获取数据
       */
      public static String postDownloadJson(String path,String post){
          URL url = null;
          try {
              url = new URL(path);
              HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
              httpURLConnection.setRequestMethod("POST");// 提交模式
              // conn.setConnectTimeout(10000);//连接超时 单位毫秒
              // conn.setReadTimeout(2000);//读取超时 单位毫秒
              // 发送POST请求必须设置如下两行
              httpURLConnection.setDoOutput(true);
              httpURLConnection.setDoInput(true);
              // 获取URLConnection对象对应的输出流
              PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
              // 发送请求参数
              printWriter.write(post);//post的参数 xx=xx&yy=yy
              // flush输出流的缓冲
              printWriter.flush();
              //开始获取数据
              BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
              ByteArrayOutputStream bos = new ByteArrayOutputStream();
              int len;
              byte[] arr = new byte[1024];
              while((len=bis.read(arr))!= -1){
                  bos.write(arr,0,len);
                  bos.flush();
              }
              bos.close();
              return bos.toString("utf-8");
          } catch (Exception e) {
              e.printStackTrace();
          }
          return null;
      }
	
	
	
	
	
	
  
    private static final int TIMEOUT_IN_MILLIONS = 5000;  
  
    public interface CallBack  
    {  
        void onRequestComplete(String result);  
    }  
  
  
    /** 
     * 异步的Get请求 
     *  
     * @param urlStr 
     * @param callBack 
     */  
    public static void doGetAsyn(final String urlStr, final CallBack callBack)  
    {  
        new Thread()  
        {  
            public void run()  
            {  
                try  
                {  
                    String result = doGet(urlStr);  
                    if (callBack != null)  
                    {  
                        callBack.onRequestComplete(result);  
                    }  
                } catch (Exception e)  
                {  
                    e.printStackTrace();  
                }  
  
            };  
        }.start();  
    }  
  
    /** 
     * 异步的Post请求 
     * @param urlStr 
     * @param params 
     * @param callBack 
     * @throws Exception 
     */  
    public static void doPostAsyn(final String urlStr, final String params,  
            final CallBack callBack) throws Exception  
    {  
        new Thread()  
        {  
            public void run()  
            {  
                try  
                {  
                    String result = doPost(urlStr, params);  
                    if (callBack != null)  
                    {  
                        callBack.onRequestComplete(result);  
                    }  
                } catch (Exception e)  
                {  
                    e.printStackTrace();  
                }  
  
            };  
        }.start();  
  
    }  
  
    /** 
     * Get请求，获得返回数据 
     *  
     * @param urlStr 
     * @return 
     * @throws Exception 
     */  
    public static String doGet(String urlStr)   
    {  
        URL url = null;  
        HttpURLConnection conn = null;  
        InputStream is = null;  
        ByteArrayOutputStream baos = null;  
        try  
        {  
            url = new URL(urlStr);  
            conn = (HttpURLConnection) url.openConnection();  
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);  
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);  
            conn.setRequestMethod("GET");  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            if (conn.getResponseCode() == 200)  
            {  
                is = conn.getInputStream();  
                baos = new ByteArrayOutputStream();  
                int len = -1;  
                byte[] buf = new byte[128];  
  
                while ((len = is.read(buf)) != -1)  
                {  
                    baos.write(buf, 0, len);  
                }  
                baos.flush();  
                return baos.toString();  
            } else  
            {  
                throw new RuntimeException(" responseCode is not 200 ... ");  
            }  
  
        } catch (Exception e)  
        {  
            e.printStackTrace();  
        } finally  
        {  
            try  
            {  
                if (is != null)  
                    is.close();  
            } catch (IOException e)  
            {  
            }  
            try  
            {  
                if (baos != null)  
                    baos.close();  
            } catch (IOException e)  
            {  
            }  
            conn.disconnect();  
        }  
          
        return null ;  
  
    }  
  
    /**  
     * 向指定 URL 发送POST方法的请求  
     *   
     * @param url  
     *            发送请求的 URL  
     * @param param  
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。  
     * @return 所代表远程资源的响应结果  
     * @throws Exception  
     */  
    public static String doPost(String url, String param)   
    {  
        PrintWriter out = null;  
        BufferedReader in = null;  
        String result = "";  
        try  
        {  
            URL realUrl = new URL(url);  
            // 打开和URL之间的连接  
            HttpURLConnection conn = (HttpURLConnection) realUrl  
                    .openConnection();  
            // 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestMethod("POST");  
            conn.setRequestProperty("Content-Type",  
                    "application/x-www-form-urlencoded");  
            conn.setRequestProperty("charset", "utf-8");  
            conn.setUseCaches(false);  
            // 发送POST请求必须设置如下两行  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);  
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);  
  
            if (param != null && !param.trim().equals(""))  
            {  
                // 获取URLConnection对象对应的输出流  
                out = new PrintWriter(conn.getOutputStream());  
                // 发送请求参数  
                out.print(param);  
                // flush输出流的缓冲  
                out.flush();  
            }  
            // 定义BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(  
                    new InputStreamReader(conn.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null)  
            {  
                result += line;  
            }  
        } catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
        // 使用finally块来关闭输出流、输入流  
        finally  
        {  
            try  
            {  
                if (out != null)  
                {  
                    out.close();  
                }  
                if (in != null)  
                {  
                    in.close();  
                }  
            } catch (IOException ex)  
            {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
}  