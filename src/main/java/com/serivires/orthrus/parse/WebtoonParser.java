package com.serivires.orthrus.parse;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.serivires.orthrus.model.Webtoon;

public class WebtoonParser {

	/**
	 * 해당 페이지에 있는 현재 페이지 숫자 정보를 반환합니다.
	 * 
	 * http://comic.naver.com/webtoon/detail.nhn?titleId=316912&no=188 <meta
	 * property="og:url" content=
	 * "http://comic.naver.com/webtoon/detail.nhn?titleId=316912&amp;no=188">
	 * 
	 * @param htmlString
	 * @return
	 * @throws Exception
	 */
	public int getLastPageNumber(String htmlString) {
		Document doc = Jsoup.parse(htmlString);
		Element urlElement = doc.select("head meta[property=og:url]").first();
		String url = urlElement.attr("abs:content");

		String pageNumber = url.substring(url.indexOf("no=")).replace("no=", "");
		return Integer.parseInt(pageNumber);
	}

	/**
	 * 
	 * @param url
	 * @return String
	 */
	private String getIDByUrl(String url) {
		int startIndex = url.indexOf("titleId=");
		return url.substring(startIndex).replace("titleId=", "");
	}

	/**
	 * 웹툰 이름으로 검색된 결과의 첫번째 항목에 대한 정보를 반환합니다.
	 * 
	 * @param HtmlString
	 * @return Webtoon
	 */
	public Webtoon getWebToonInfo(String HtmlString) {
		Document doc = Jsoup.parse(HtmlString);
		Elements elements = doc.select(".resultList li");

		if (elements.isEmpty() == true) {
			return Webtoon.emptyObject;
		}

		Element element = elements.select("h5 a").first();
		String urlPath = element.attr("href");

		Webtoon webToon = new Webtoon();
		webToon.setId(getIDByUrl(urlPath));
		webToon.setTitle(element.html());

		return webToon;
	}

	/**
	 * 한 페이지 내에 있는 유효한 이미지 파일 주소 목록을 반환합니다.
	 * 
	 * @param doc
	 * @return
	 */
	public List<String> selectImgByHtmlPage(String HtmlString) {
		Document doc = Jsoup.parse(HtmlString);
		List<Element> elements = (List<Element>) doc.select(".wt_viewer img");

		return elements.stream().map(element -> element.attr("abs:src"))//
				.filter(src -> StringUtils.isNotBlank(src))//
				.filter(src -> !StringUtils.contains(src, "txt_ads.png"))//
				.collect(Collectors.toList());
	}
}
