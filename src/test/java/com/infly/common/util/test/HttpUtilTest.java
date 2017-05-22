package com.infly.common.util.test;

import org.junit.Test;

import com.infly.common.util.http.HttpClientUtil;

import junit.framework.TestCase;

public class HttpUtilTest extends TestCase{

	@Test
	public void testGet(){
		
		String url = "http://www.baidu.com";
		HttpClientUtil.get(url);
	}
}
