package com.serivires.orthrus.commons;

import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	private HttpClientUtils() {
		new AssertionError();
	}

	public static String readHtmlPage(URI uri) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(uri);

		String htmlString = null;
		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity httpEntity = response.getEntity();
			InputStream inputStream = httpEntity.getContent();
			htmlString = IOUtils.toString(inputStream);

			System.out.println(httpget.getURI());
			System.out.println(response.getStatusLine());
		} catch (Exception e) {
			logger.error("getHtmlString(): {}", e.toString());
			e.printStackTrace();
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		return htmlString;
	}
}
