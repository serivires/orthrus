package com.serivires.orthrus.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.serivires.orthrus.model.DownloadFileInfo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class FileDownload {
	private static final OkHttpClient client = new OkHttpClient()
			.newBuilder()
			.readTimeout(5, TimeUnit.SECONDS)
			.writeTimeout(5, TimeUnit.SECONDS)
			.retryOnConnectionFailure(true)
			.build();

	private static final String CHROME_USER_AGENT =
			"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36";

	/**
	 * 단일 파일을 다운로드 합니다.
	 */
	private void download(final DownloadFileInfo fileInfo) throws IOException {
		final Request request = new Request.Builder().url(fileInfo.getDownloadUrl())
				.addHeader("Referer", fileInfo.getRefererUrl())
				.addHeader("User-Agent", CHROME_USER_AGENT)
				.build();

		final Response response = client.newCall(request).execute();
		final InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();

		writeFile(fileInfo.getPath(), inputStream);
		Objects.requireNonNull(response.body()).close();
	}

	private void writeFile(final Path path, final InputStream inputStream) throws IOException {
		if (!Files.exists(path.getParent())) {
			Files.createDirectories(path.getParent());
		}

		Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
	}

	public void single(final DownloadFileInfo fileInfo) {
		try {
			download(fileInfo);
		} catch (IOException ex) {
			log.error("downloadFile. error! ex=", ex);
		}
	}

	/**
	 * 파일을 병렬 다운로드 합니다.
	 */
	public void parallel(final List<DownloadFileInfo> files) {
		files.parallelStream().forEach(this::single);
	}
}
