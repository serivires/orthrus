package com.serivires.orthrus.commons;

import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {
  private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

  private HttpClientUtils() {
    new AssertionError();
  }

  /**
   * 해당 주소 페이지를 문자열로 반환합니다.
   * 
   * @param uri
   * @return
   */
  public static String read(URI uri) {
    CloseableHttpClient httpclient = HttpClientBuilder.create().build();
    String htmlString = null;

    try {
      HttpGet httpget = new HttpGet(uri);
      CloseableHttpResponse response = httpclient.execute(httpget);

      try {
        HttpEntity httpEntity = response.getEntity();
        InputStream inputStream = httpEntity.getContent();
        htmlString = IOUtils.toString(inputStream, "utf-8");

        logger.info(httpget.getURI().toString());
        logger.info(response.getStatusLine().toString());
      } catch (Exception e) {
        throw e;
      } finally {
        response.close();
        httpclient.close();
      }
    } catch (Exception e) {
      logger.error("readHtmlPage(): {}", e.toString());
      e.printStackTrace();
    }

    return htmlString;
  }
}
