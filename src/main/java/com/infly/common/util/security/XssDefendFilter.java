package com.infly.common.util.security;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XssDefendFilter implements Filter {

	private FilterConfig filterConfig = null;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// Referer验证
		checkReferer(request, response);
		// XSS验证
		checkXssDefend(request, response, chain);
	}

	@Override
	public void destroy() {

	}

	/**
	 * CSRF攻击校验
	 * 
	 * @param servletRequest
	 * @param servletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	public void checkReferer(ServletRequest servletRequest, ServletResponse servletResponse)
			throws ServletException, IOException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		// 从 HTTP 头中取得 Referer 值
		String referer = request.getHeader("referer");
		boolean refererCorrect = false;
		if (referer != null) {
			if (referer.trim().contains("projectName")) {
				refererCorrect = true;
			}
		}
		// 判断 referer 是否合法
		if (!refererCorrect) {
			System.out.println("Referer:" + referer);
			request.getRequestDispatcher("error.html").forward(request, response);
		}
	}

	/**
	 * XSS攻击校验
	 * 
	 * @param servletRequest
	 * @param servletResponse
	 * @param chain
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private void checkXssDefend(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		String xss = filterConfig.getInitParameter("xss");
		String xssPath = filterConfig.getInitParameter("xssPath");
		String xssWord = filterConfig.getInitParameter("xssWord");

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		// 如果web.xml中的开关开启
		if (Boolean.valueOf(xss)) {
			Enumeration en = request.getParameterNames();
			String paramName = "-";
			boolean flag = false;
			if (request.getServletPath() != null) {
				if (!checkXssPath(request.getServletPath(), xssPath)) {
					while (en.hasMoreElements()) {
						paramName = (String) en.nextElement();
						if (request.getParameterValues(paramName) != null) {
							for (String value : request.getParameterValues(paramName)) {
								if (paramName != null && value != null) {
									if (checkXssWord(value, xssWord)) {
										System.out.println("ERROR parameter:" + paramName + "=" + value);
										flag = true;
										break;
									}
								}
							}
						}
					}
				}
			}
			if (!flag) {
				chain.doFilter(request, response);
			} else {
				// 错误定向
				response.sendRedirect(request.getContextPath() + "error.html");
			}

		} else {
			chain.doFilter(request, response);
		}

	}

	/**
	 * 关键字是否在XSS规则中
	 * 
	 * @param value
	 * @param xssWord
	 * @return
	 */
	private boolean checkXssWord(String value, String xssWord) {

		for (String word : xssWord.split(",")) {
			if (value.indexOf(word) >= 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * URL是否在例外的规则之中
	 * 
	 * @param servletPath
	 * @param xssRule
	 * @return
	 */
	private boolean checkXssPath(String servletPath, String xssPath) {

		for (String path : xssPath.split(",")) {
			if (servletPath.indexOf(path) >= 0) {
				return true;
			}
		}
		return false;
	}

}
