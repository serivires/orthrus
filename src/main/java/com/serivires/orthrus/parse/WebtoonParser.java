package com.serivires.orthrus.parse;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.serivires.orthrus.model.Webtoon;

public class WebtoonParser {

	/**
	 * 
	 * <meta property="og:url" content=
	 * "http://comic.naver.com/webtoon/detail.nhn?titleId=258206&amp;no=67">
	 * 
	 * @param htmlString
	 * @return
	 * @throws Exception
	 */
	public int getLastPageNumber(String htmlString) throws Exception {
		Document doc = Jsoup.parse(htmlString);
		Elements elements = doc.select("head meta");

		if (elements.isEmpty()) {
			return 0;
		}

		for (Element element : elements) {
			Elements tmp = element.getElementsByAttributeValue("property", "og:url");
			if (!tmp.isEmpty()) {
				String url = tmp.first().attr("abs:content");
				String no = url.substring(url.indexOf("no=")).replace("no=", "");
				return Integer.parseInt(no);
			}
		}

		return 0;
	}

	public Element selectElementByFirst(Elements elements, String selectQuery) {
		for (Element element : elements) {
			Elements tmp = element.select(selectQuery);
			if (tmp.isEmpty()) {
				continue;
			}

			return tmp.first();
		}

		return null;
	}

	private String getIDByUrl(String url) {
		int startIndex = url.indexOf("titleId=");
		return url.substring(startIndex).replace("titleId=", "");
	}

	public Webtoon getWebToonInfo(String HtmlString) throws Exception {
		Document doc = Jsoup.parse(HtmlString);
		Elements elements = doc.select(".resultList li");

		if (elements.isEmpty()) {
			return Webtoon.emptyObject;
		}

		Element element = selectElementByFirst(elements, "h5 a");
		if (element != null) {
			Webtoon webToon = new Webtoon();
			String urlPath = element.attr("href");
			webToon.setId(getIDByUrl(urlPath));
			webToon.setTitle(element.html());

			return webToon;
		}

		return Webtoon.emptyObject;
	}

	/**
	 * 한 페이지 내에 있는 유효한 이미지 파일 주소 목록을 반환합니다.
	 * 
	 * @param doc
	 * @return
	 */
	public List<String> selectImgByHtmlPage(String HtmlString) {
		Document doc = Jsoup.parse(HtmlString);
		Elements elements = doc.select(".wt_viewer img");

		List<String> imageUrlList = new ArrayList<String>();
		for (Element element : elements) {
			String fileUrl = element.attr("abs:src");
			imageUrlList.add(fileUrl);
		}

		return imageUrlList;
	}

}
