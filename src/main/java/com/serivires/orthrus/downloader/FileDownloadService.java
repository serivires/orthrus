package com.serivires.orthrus.downloader;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serivires.orthrus.model.DownloadFileInfo;



public class FileDownloadService {
	private static Logger logger = LoggerFactory.getLogger(FileDownloadService.class);
	
	public static boolean downloadFile(DownloadFileInfo fileURL) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(fileURL.getDownloadURL());
		httpget.setHeader("Referer", fileURL.getRefererURL());

		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity httpEntity = response.getEntity();
			InputStream inputStream = httpEntity.getContent();
			FileUtils.copyInputStreamToFile(inputStream, fileURL.getSaveFileInfo());
			logger.info("FileName: {}", fileURL.getFileName());
		} catch (Exception e) {
			logger.error("getHtmlString(): {}", e.toString());
			return false;
		} finally {
			httpclient.getConnectionManager().shutdown();
		}

		return true;
	}

	public ExecutorService downloadFiles(List<DownloadFileInfo> fileURLs, boolean isAsync) {
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		for (DownloadFileInfo fileURL : fileURLs) {
			executorService.execute(new FileDownloadRunnableImpl(fileURL));
		}
		executorService.shutdown();
		
		if (isAsync == false) {
			while(!executorService.isTerminated()){};
		}
		
		return executorService;
	}

	public class FileDownloadRunnableImpl implements Runnable {
		private DownloadFileInfo fileURL;

		public FileDownloadRunnableImpl(DownloadFileInfo fileURL) {
			this.fileURL = fileURL;
		}

		public void run() {
			FileDownloadService.downloadFile(fileURL);
		}
	}
}
