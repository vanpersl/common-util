package com.infly.common.util.https;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HttpsClientUtil extends DefaultHttpClient {
	
	private boolean useProxy;
	
	public HttpsClientUtil(boolean proxy) throws Exception {
		super();
		useProxy = proxy;
		if (proxy) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			String proxyHost = "127.17.17.80";
			int proxyPort = 8080;
			String userName = "";
			String password = "";
			credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), 
					new UsernamePasswordCredentials(userName, password));
			super.setCredentialsProvider(credsProvider);

			HttpHost proxyinfo = new HttpHost(proxyHost, proxyPort, "http");
			super.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyinfo);
		}
		
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		ctx.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		ClientConnectionManager ccm = this.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https", ssf, 443));
	}

	public String doPostCollection(String url, List<NameValuePair> list) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		String charset = "utf-8";
		try {
			httpClient = new HttpsClientUtil(useProxy);
			httpPost = new HttpPost(url);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
			// 设置参数
			httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, charset);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	public String doPostJson(String url, JSONObject json) {
		HttpClient httpClient = null;
		HttpPost httpPost = null;
		String result = null;
		try {
			httpClient = new HttpsClientUtil(useProxy);
			httpPost = new HttpPost(url);
			// 设置请求头
			httpPost = new HttpPost(url);
			httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			s.setContentType("application/x-www-form-urlencoded");
			httpPost.setEntity(s);
			HttpResponse response = httpClient.execute(httpPost);
			// 获取响应头
			for (Header head : response.getAllHeaders()) {
				if (head.getName().toString().equals("xxx")) {
					System.out.println("[Header]" + head.getName() + ":" + head.getValue());
				}
			}
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "UTF-8");
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	public String doGet(String url) {
		HttpClient httpClient = null;
		HttpGet httpGet = null;
		String result = null;
		try {
			httpClient = new HttpsClientUtil(useProxy);
			httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "utf-8");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

}
