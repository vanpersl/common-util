package com.infly.common.util.https;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpsUtil {

  public static void main(String[] args) {
    sendPost("https://10.1.1.1/api/v4/projects/1/repository/tags", "tag_name=1.1.1&ref=master",
        "xxxxxxxxxxxxx");
  }

  /**
   * 向指定URL发送GET方法的请求
   * 
   * @param url 发送请求的URL
   * @param token 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @return URL 所代表远程资源的响应结果
   */
  public static String sendGet(String url, String token) {
    StringBuilder result = new StringBuilder();
    BufferedReader in = null;
    try {
      String urlNameString = url;
      URL realUrl = new URL(urlNameString);
      if ("https".equalsIgnoreCase(realUrl.getProtocol())) {
        SslUtil.ignoreSsl();
      }
      // 打开和URL之间的连接
      URLConnection connection = realUrl.openConnection();
      // 设置通用的请求属性
      connection.setRequestProperty("accept", "*/*");
      connection.setRequestProperty("connection", "Keep-Alive");
      connection.setRequestProperty("user-agent",
          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      connection.setRequestProperty("PRIVATE-TOKEN", token);
      // 建立实际的连接
      connection.connect();
      // 获取所有响应头字段
      Map<String, List<String>> map = connection.getHeaderFields();
      // 遍历所有的响应头字段
      for (Map.Entry<String, List<String>> entry : map.entrySet()) {
        log.debug(entry.getKey() + "--->" + entry.getValue());
      }
      // 定义 BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result.append(line);
      }
    } catch (Exception e) {
      log.info("发送GET请求出现异常！" + e);
      log.info(e.getMessage(), e);
    }
    // 使用finally块来关闭输入流
    finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e2) {
        log.info(e2.getMessage(), e2);
      }
    }
    return result.toString();
  }

  /**
   * 向指定 URL 发送POST方法的请求
   * 
   * @param url 发送请求的 URL
   * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
   * @param token 所代表远程资源的响应结果
   * @return
   */
  public static String sendPost(String url, String param, String token) {
    PrintWriter out = null;
    BufferedReader in = null;
    StringBuilder result = new StringBuilder();
    try {
      URL realUrl = new URL(url);
      if ("https".equalsIgnoreCase(realUrl.getProtocol())) {
        SslUtil.ignoreSsl();
      }
      // 打开和URL之间的连接
      URLConnection conn = realUrl.openConnection();
      // 设置通用的请求属性
      conn.setRequestProperty("accept", "*/*");
      conn.setRequestProperty("connection", "Keep-Alive");
      conn.setRequestProperty("user-agent",
          "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
      conn.setRequestProperty("PRIVATE-TOKEN", token);
      // 发送POST请求必须设置如下两行
      conn.setDoOutput(true);
      conn.setDoInput(true);
      // 获取URLConnection对象对应的输出流
      out = new PrintWriter(conn.getOutputStream());
      // 发送请求参数
      out.print(param);
      // flush输出流的缓冲
      out.flush();
      // 定义BufferedReader输入流来读取URL的响应
      in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = in.readLine()) != null) {
        result.append(line);
      }
    } catch (Exception e) {
      log.info("发送 POST 请求出现异常！" + e);
      log.info(e.getMessage(), e);
    }
    // 使用finally块来关闭输出流、输入流
    finally {
      try {
        if (out != null) {
          out.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        log.info(ex.getMessage(), ex);
      }
    }
    return result.toString();
  }
}
