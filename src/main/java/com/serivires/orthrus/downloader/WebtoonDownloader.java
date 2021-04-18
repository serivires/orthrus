package com.serivires.orthrus.downloader;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.util.UriComponentsBuilder;

import com.serivires.orthrus.model.DownloadFileInfo;
import com.serivires.orthrus.model.Webtoon;
import com.serivires.orthrus.parse.WebtoonParser;
import com.serivires.orthrus.view.DefaultViewer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebtoonDownloader {
	private static final String DEFAULT_SYSTEM_PATH =
			String.format("%s%sDesktop", System.getProperty("user.home"), File.separator);

	private final WebtoonParser webtoonParser;
	private final DefaultViewer viewer;

	public WebtoonDownloader() throws Exception {
		webtoonParser = new WebtoonParser();
		viewer = new DefaultViewer();
	}

	/**
	 * 실제 웹툰이 보여지는 페이지 주소를 반환합니다.
	 */
	URI buildDetailPageUri(String titleId, String no) {
		return UriComponentsBuilder.fromHttpUrl("http://comic.naver.com").path("webtoon/detail.nhn")
			.queryParam("titleId", titleId).queryParam("no", no).build().toUri();
	}

	/**
	 * 웹툰 검색페이지 주소를 반환합니다.
	 */
	URI buildWebtoonSearchPageUri(final String title) {
		return UriComponentsBuilder
			.fromHttpUrl("http://comic.naver.com")
			.path("search.nhn")
			.queryParam("keyword", title)
			.encode()
			.build()
			.toUri();
	}

	/**
	 * 타이틀과 부분 일치하는 웹툰의 모든 화를 다운로드 합니다.
	 */
	public void autoSave(final String title) throws Exception {
		final Optional<Webtoon> rawWebtoonInfo = getWebtoonInfo(title);
		if (rawWebtoonInfo.isEmpty()) {
			return;
		}

		int downloadCount = 0;
		final Webtoon webtoon = rawWebtoonInfo.get();
		final int lastPageNumber = webtoon.getLastPage();
		final String prePath = String.format("%s%s%s%s", DEFAULT_SYSTEM_PATH, File.separator, webtoon.getTitle(), File.separator);

		System.out.println("<<" + title + ">> 다운로드");
		for (int i = 1; i <= lastPageNumber; i++) {
			final URI uri = buildDetailPageUri(webtoon.getId(), String.valueOf(i));
			final String viewerTitle = String.format("%s - %d화", title, i);
			downloadCount += saveByOnePage(uri, prePath + i + File.separator, viewerTitle);

			System.out.printf("%d개. %.1f%% 완료되었습니다.%n",
				downloadCount, ((double)i / (double)lastPageNumber) * 100.0);
		}

		System.out.println("총 " + downloadCount + "개의파일이 다운로드 되었습니다.");
	}

	/**
	 * 한 페이지 내에 있는 유효한 이미지 파일을 저장합니다.
	 */
	private int saveByOnePage(final URI uri, final String path, final String title) throws Exception {
		final List<String> imageUrlList = webtoonParser.selectImageUrlsBy(uri.toURL());

		final List<DownloadFileInfo> fileUrlList = imageUrlList.parallelStream().map(imageURL -> {
			DownloadFileInfo fileInfo = new DownloadFileInfo(uri.toString(), path);
			fileInfo.setDownloadUrl(imageURL);
			return fileInfo;
		}).collect(Collectors.toList());

		FileDownloadUtils.parallel(fileUrlList);
		writeViewer(imageUrlList, path, title);

		return imageUrlList.size();
	}

	/**
	 * viewer 파일을 생성합니다.
	 */
	private void writeViewer(final List<String> imageUrlList, final String savePath, final String title) {
		final List<String> downloadFileNames = imageUrlList.stream().map(imageURL -> {
			String[] depths = imageURL.split("/");
			return depths[depths.length - 1];
		}).collect(Collectors.toList());

		final Map<String, Object> model = new HashMap<>();
		model.put("title", title);
		model.put("fileNames", downloadFileNames);
		viewer.write(model, new File(savePath, "viewer.html"));
	}

	/**
	 * 제목과 일치하는 웹툰 정보를 반환합니다.
	 *
	 * @param title:
	 * @return Optional<Webtoon>
	 * @throws Exception:
	 */
	private Optional<Webtoon> getWebtoonInfo(final String title) throws Exception {
		final URI uri = buildWebtoonSearchPageUri(title);
		final Optional<Webtoon> webtoonInfo = webtoonParser.getWebtoonInfo(uri.toURL());

		if (webtoonInfo.isEmpty()) {
			System.out.println("검색 결과가 없습니다.");
			return webtoonInfo;
		}

		final Webtoon webtoon = webtoonInfo.get();
		int lastPage = getLastPageNumber(webtoon.getId());
		if (lastPage <= 0) {
			System.out.println("접속이 차단되었습니다.");
			return webtoonInfo;
		}

		webtoon.setLastPage(lastPage);
		return Optional.of(webtoon);
	}

	/**
	 * 웹툰의 마지막화 번호를 반환합니다.
	 *
	 * - http://comic.naver.com/webtoon/detail.nhn?titleId=316912&no=188
	 * - no 파라미터에 유효한 숫자를 넘기지 않으면 마지막화 페이지로 이동한다.
	 */
	private int getLastPageNumber(String titleId) throws Exception {
		final URI uri = buildDetailPageUri(titleId, "0");
		return webtoonParser.getLastPageNumber(uri.toURL());
	}
}
