package com.infly.common.util.htmlunit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUnitUtil {

  public static final String URL = "http://a.b.c:8180/gauge/prd/";

  public static void main(String[] args) {
    parseHtmlResult();
  }

  public static void parseHtmlResult() {
    Properties properties = new Properties();
    final WebClient webClient = new WebClient();
    try {
      properties.load(HtmlUnitUtil.class.getResourceAsStream("/config.properties"));
      List<String> list = new ArrayList<String>();
      for (Entry<Object, Object> item : properties.entrySet()) {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.waitForBackgroundJavaScript(10 * 1000);

        HtmlPage page = webClient.getPage(URL + item.getValue().toString());
        HtmlElement resultElmt = (HtmlElement) page.getElementById("pie-chart");
        String total = resultElmt.getAttribute("data-total");
        String items = resultElmt.getAttribute("data-results");
        list.add(item.getValue().toString() + "," + total + "," + items);
      }
      for (String str : list) {
        System.out.println(str);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      webClient.close();
    }
  }
  
  public static void parseHtmlResultByClass() {
    Properties properties = new Properties();
    final WebClient webClient = new WebClient();
    try {
      properties.load(HtmlUnitUtil.class.getResourceAsStream("/config.properties"));
      List<String> list = new ArrayList<String>();
      for (Entry<Object, Object> item : properties.entrySet()) {
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setActiveXNative(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.waitForBackgroundJavaScript(10 * 1000);

        HtmlPage page = webClient.getPage(URL + item.getValue().toString());
        DomNodeList<DomElement> elementsByTagNameSubmit = page.getElementsByTagName("input");
        for (DomElement domElement : elementsByTagNameSubmit) {
            if (domElement.getAttribute("class").contains("shadow")
                    || domElement.getAttribute("type").contains("submit")) {
                System.out.println(domElement.getTextContent());
                break;
            }
        }
      }
      for (String str : list) {
        System.out.println(str);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      webClient.close();
    }
  }
}
