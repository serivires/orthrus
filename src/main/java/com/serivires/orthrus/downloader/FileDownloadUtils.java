package com.serivires.orthrus.downloader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.serivires.orthrus.model.DownloadFileInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileDownloadUtils {
	private static final RestTemplate restTemplate = new RestTemplate();
	private static final String CHROME_USER_AGENT =
		"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36";

	/**
	 * 단일 파일을 다운로드 합니다.
	 */
	private static void write(final DownloadFileInfo fileInfo) {
		restTemplate.execute(fileInfo.getDownloadUrl(), HttpMethod.GET, (req) -> {
			final HttpHeaders httpHeaders = req.getHeaders();
			httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
			httpHeaders.set(HttpHeaders.REFERER, fileInfo.getRefererUrl());
			httpHeaders.set(HttpHeaders.USER_AGENT, CHROME_USER_AGENT);
		}, (res) -> {
			final Path path = fileInfo.getPath();
			if (!Files.exists(path.getParent())) {
				Files.createDirectories(path.getParent());
			}

			Files.copy(res.getBody(), path, StandardCopyOption.REPLACE_EXISTING);
			//log.info("File: {}", fileInfo.getSaveFileInfo());
			return fileInfo.getSaveFileInfo();
		});
	}

	/**
	 * 파일을 병렬 다운로드 합니다.
	 */
	static void parallel(final List<DownloadFileInfo> files) {
		files.parallelStream().forEach(FileDownloadUtils::write);
	}
}
