package com.serivires.orthrus.downloader;

import com.serivires.orthrus.model.DownloadFileInfo;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

public class FileDownloadUtils {
  private static Logger logger = LoggerFactory.getLogger(FileDownloadUtils.class);
  private static RestTemplate restTemplate = new RestTemplate();
  private static String CHROME_USER_AGENT =
      "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36";

  /**
   * 단일 파일을 다운로드 합니다.
   *
   * @param fileInfo:
   */
  private static void write(DownloadFileInfo fileInfo) {
    restTemplate.execute(
        fileInfo.getDownloadUrl(),
        HttpMethod.GET,
        (req) -> {
          HttpHeaders httpHeaders = req.getHeaders();
          httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
          httpHeaders.set(HttpHeaders.REFERER, fileInfo.getRefererUrl());
          httpHeaders.set(HttpHeaders.USER_AGENT, CHROME_USER_AGENT);
        },
        (res) -> {
          FileUtils.copyInputStreamToFile(res.getBody(), fileInfo.getSaveFileInfo());
          logger.info("File: {}", fileInfo.getSaveFileInfo());
          return fileInfo.getSaveFileInfo();
        });
  }

  /**
   * 파일을 병렬 다운로드 합니다.
   *
   * @param fileInfos:
   */
  public static void parallel(List<DownloadFileInfo> fileInfos) {
    fileInfos.parallelStream().forEach(FileDownloadUtils::write);
  }
}
