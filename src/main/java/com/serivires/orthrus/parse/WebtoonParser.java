package com.serivires.orthrus.parse;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.serivires.orthrus.model.Webtoon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebtoonParser {

	private Optional<Document> parse(final URL url, final int timeoutMillis) {
		try {
			return Optional.ofNullable(Jsoup.parse(url, timeoutMillis));
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	/**
	 * 해당 페이지에 있는 현재 페이지 숫자 정보를 반환합니다.
	 * <p>
	 * <p>http://comic.naver.com/webtoon/detail.nhn?titleId=316912&no=188 <meta property="og:url"
	 * content= "http://comic.naver.com/webtoon/detail.nhn?titleId=316912&amp;no=188">
	 */
	public int getLastPageNumber(final URL url) {
		final Optional<Document> document = parse(url, 5000);
		final Document doc = document.orElseThrow(IllegalArgumentException::new);

		final String lastPageUrl = doc.select("head meta[property=og:url]").first().attr("abs:content");
		final String pageNumber = lastPageUrl.substring(lastPageUrl.indexOf("no=")).replace("no=", "");
		return Integer.parseInt(pageNumber);
	}

	/**
	 * url에서 웹툰 고유 ID를 반환합니다.
	 */
	private String getIdBy(final String url) {
		final int startIndex = url.indexOf("titleId=");
		return url.substring(startIndex).replace("titleId=", "");
	}

	/**
	 * 웹툰 이름으로 검색된 결과의 첫번째 항목에 대한 정보를 반환합니다.
	 */
	public Optional<Webtoon> getWebtoonInfo(final URL url) {
		final Optional<Document> rawDocument = parse(url, 5000);
		final Elements elements =
			rawDocument.orElseThrow(IllegalArgumentException::new).select(".resultList li");

		if (elements.isEmpty()) {
			return Optional.empty();
		}

		final Element element = elements.select("h5 a").first();
		final String urlPath = element.attr("href");

		final Webtoon webToon = new Webtoon();
		webToon.setId(getIdBy(urlPath));
		webToon.setTitle(element.html());

		return Optional.of(webToon);
	}

	/**
	 * 한 페이지 내에 있는 유효한 이미지 파일 주소 목록을 반환합니다.
	 */
	public List<String> selectImageUrlsBy(final URL url) throws IOException {
		final Document doc = Jsoup.parse(url, 5000);
		final List<Element> elements = doc.select(".wt_viewer img");

		return elements.stream()
			.map(element -> element.attr("src"))
			.filter(StringUtils::isNotBlank)
			.filter(src -> !StringUtils.contains(src, "txt_ads.png"))
			.collect(Collectors.toList());
	}
}
