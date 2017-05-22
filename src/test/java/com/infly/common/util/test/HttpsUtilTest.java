package com.infly.common.util.test;

import org.junit.Test;

import com.infly.common.util.https.HttpsClientUtil;

import junit.framework.TestCase;

public class HttpsUtilTest extends TestCase {

	@Test
	public void testGet() {

		String url = "https://github.com/";
		HttpsClientUtil sslClient;
		try {
			sslClient = new HttpsClientUtil(true);
			String result = sslClient.doGet(url);
			System.out.println("result:" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetTags() {
		String url = "https://github.com/api/v3/projects/10/repository/tags";
		HttpsClientUtil sslClient;
		try {
			sslClient = new HttpsClientUtil(true);
			String result = sslClient.doGet(url);
			System.out.println("result:" + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
