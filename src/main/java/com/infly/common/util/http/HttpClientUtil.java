package com.infly.common.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class HttpClientUtil {

	private HttpClientUtil() {
	}

	public static String post(String url, Map<String, String> params) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String body = null;

		HttpPost post = postForm(url, params);

		post.addHeader("Cookie", "JSESSIONID=1bt9beikg5wyz11c7c31n5xqg6");

		body = invoke(httpclient, post);

		httpclient.getConnectionManager().shutdown();

		return body;
	}

	public static String get(String url) {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		String body = null;

		HttpGet get = new HttpGet(url);
		body = invoke(httpclient, get);

		httpclient.getConnectionManager().shutdown();

		return body;
	}

	private static String invoke(DefaultHttpClient httpclient, HttpUriRequest httpost) {

		HttpResponse response = sendRequest(httpclient, httpost);
		String body = paseResponse(response);

		return body;
	}

	private static String paseResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
			String line = null;
			StringBuffer sb = new StringBuffer();
			line = reader.readLine();
			sb.append(line);
			if (line != null) {
				while ((line = reader.readLine()) != null) {
					sb.append("\n" + line);
				}
			}
			if (entity != null) {
				entity.consumeContent();
			}
			System.out.println(sb.toString());
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		EntityUtils.getContentCharSet(entity);
//		String body = null;
//		try {
//			body = EntityUtils.toString(entity);
//			System.out.println(body);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		return null;
	}

	private static HttpResponse sendRequest(DefaultHttpClient httpclient, HttpUriRequest httpost) {

		HttpResponse response = null;

		setProxyInfo(httpclient);
		try {
			response = httpclient.execute(httpost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	private static HttpPost postForm(String url, Map<String, String> params) {

		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}

		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return httpost;
	}

	/**
	 * 内网抓取外网数据时需要设置代理
	 * 
	 * @param httpClient
	 * @return
	 */
	private static void setProxyInfo(DefaultHttpClient httpClient) {

		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		String proxyHost = "127.17.17.80";
		int proxyPort = 8080;
		String userName = "";
		String password = "";

		credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
				new UsernamePasswordCredentials(userName, password));

		httpClient.setCredentialsProvider(credsProvider);

		HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");

		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	/**
	 * 以流的形式返回访问某个HttpUrl的响应结果
	 * 
	 * @param httpUrl
	 * @return
	 */
	public static InputStream getHttpURLInputStream(DefaultHttpClient httpClient, String httpUrl) {

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
