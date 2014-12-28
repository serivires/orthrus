package com.serivires.orthrus.downloader;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serivires.orthrus.model.DownloadFileInfo;


public class FileDownloadUtils {
  private static Logger logger = LoggerFactory.getLogger(FileDownloadUtils.class);

  /**
   * 해당 페이지를 문자열로 반환합니다.
   * 
   * @param fileUrl
   * @return
   */
  public static boolean get(DownloadFileInfo fileUrl) {
    CloseableHttpClient httpclient = HttpClientBuilder.create().build();

    try {
      HttpGet httpget = new HttpGet(fileUrl.getDownloadUrl());
      httpget.setHeader("Referer", fileUrl.getRefererUrl());
      CloseableHttpResponse response = httpclient.execute(httpget);

      try {
        HttpEntity httpEntity = response.getEntity();
        InputStream inputStream = httpEntity.getContent();
        FileUtils.copyInputStreamToFile(inputStream, fileUrl.getSaveFileInfo());

        logger.info("FileName: {}", fileUrl.getFileName());
      } catch (Exception e) {
        throw e;
      } finally {
        response.close();
        httpclient.close();
      }
    } catch (Exception e) {
      logger.error("FileDownloadUtils::get {}", e.toString());
      return false;
    }

    return true;
  }

  /**
   * 파일을 병렬 다운로드 합니다.
   * 
   * @param fileUrlList
   * @param isAsync
   * @return
   */
  public static ForkJoinPool parallel(List<DownloadFileInfo> fileUrlList, boolean isAsync) {
    ForkJoinPool pool = new ForkJoinPool(5);
    fileUrlList.forEach(fileURL -> pool.execute(() -> get(fileURL)));
    pool.shutdown();

    if (isAsync == false) {
      try {
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return pool;
  }
}
