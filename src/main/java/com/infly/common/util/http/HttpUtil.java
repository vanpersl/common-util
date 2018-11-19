package com.infly.common.util.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	private HttpUtil() {
	}

	public static String post(String url, Map<String, String> params) {

		CloseableHttpClient httpClient = buildHttpClient(url);
		HttpPost post = postForm(url, params);
		// post.addHeader("PRIVATE-TOKEN", "ABC");
		CloseableHttpResponse response = null;
		String body = null;
		try {
			response = httpClient.execute(post);
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			body = EntityUtils.toString(entity, "UTF-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return body;
	}

	public static String get(String url) {

		CloseableHttpClient httpClient = buildHttpClient(url);
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(getProxy(httpClient));
		CloseableHttpResponse response = null;
		String body = null;
		try {
			response = httpClient.execute(httpGet);
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			body = EntityUtils.toString(entity, "UTF-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return body;
	}

	private static CloseableHttpClient buildHttpClient(String url) {
		if (url.startsWith("https")) {
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", SslUtil.trustAllHttpsCertificates()).build();
			// 创建ConnectionManager，添加Connection配置信息
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
					socketFactoryRegistry);
			CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
			return httpClient;
		}
		return HttpClients.createDefault();
	}

	private static HttpPost postForm(String url, Map<String, String> params) {

		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return httpost;
	}

	/**
	 * 内网抓取外网数据时需要设置代理 创建请求方法的实例， 并指定请求url HttpGet httpget = new
	 * HttpGet("http://www.qq.com"); 使用配置 httpget.setConfig(config);
	 * 
	 * @param httpClient
	 * @return
	 */
	public static RequestConfig getProxy(CloseableHttpClient httpClient) {

		// 代理对象
		HttpHost proxy = new HttpHost("127.17.18.19", 8080, "http");
		// 配置对象
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		return config;
	}

	/**
	 * 以流的形式返回访问某个HttpUrl的响应结果
	 * 
	 * @param httpUrl
	 * @return
	 */
	@Deprecated
	public static InputStream getHttpURLInputStream(CloseableHttpClient httpClient, String httpUrl) {

		HttpGet httpget = null;
		httpget = new HttpGet(httpUrl);

		HttpResponse response;
		try {
			response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				return entity.getContent();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
