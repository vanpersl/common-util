package com.infly.common.util.test;

import org.junit.Test;

import com.infly.common.util.http.HttpUtil;

import junit.framework.TestCase;

public class HttpUtilTest extends TestCase{

	@Test
	public void testGet(){
		
		String url = "http://www.baidu.com";
		HttpUtil.get(url);
	}
	
	@Test
	public void testGetHttps() {

		String url = "https://github.com/";
		String result = HttpUtil.get(url);
		System.out.println("result:" + result);
	}
}
