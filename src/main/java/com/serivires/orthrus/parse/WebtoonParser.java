package com.serivires.orthrus.parse;

import com.serivires.orthrus.model.Webtoon;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WebtoonParser {

    private Optional<Document> parse(URL url, int timeoutMillis) {
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
     *
     * @param url:
     * @return int
     */
    public int getLastPageNumber(URL url) {
        Optional<Document> document = parse(url, 5000);
        Document doc = document.orElseThrow(IllegalArgumentException::new);

        String lastPageUrl = doc.select("head meta[property=og:url]").first().attr("abs:content");
        String pageNumber = lastPageUrl.substring(lastPageUrl.indexOf("no=")).replace("no=", "");
        return Integer.valueOf(pageNumber);
    }

    /**
     * url에서 웹툰 고유 ID를 반환합니다.
     *
     * @param url:
     * @return String
     */
    private String getIdBy(String url) {
        int startIndex = url.indexOf("titleId=");
        return url.substring(startIndex).replace("titleId=", "");
    }

    /**
     * 웹툰 이름으로 검색된 결과의 첫번째 항목에 대한 정보를 반환합니다.
     *
     * @param url:
     * @return Webtoon
     */
    public Optional<Webtoon> getWebtoonInfo(URL url) {
        Optional<Document> document = parse(url, 5000);
        Elements elements =
            document.orElseThrow(IllegalArgumentException::new).select(".resultList li");

        if (elements.isEmpty()) {
            return Optional.empty();
        }

        Element element = elements.select("h5 a").first();
        String urlPath = element.attr("href");

        Webtoon webToon = new Webtoon();
        webToon.setId(getIdBy(urlPath));
        webToon.setTitle(element.html());

        return Optional.of(webToon);
    }

    /**
     * 한 페이지 내에 있는 유효한 이미지 파일 주소 목록을 반환합니다.
     *
     * @param url:
     * @return List<String>
     * @throws IOException:
     */
    public List<String> selectImageUrlsBy(URL url) throws IOException {
        Document doc = Jsoup.parse(url, 5000);
        List<Element> elements = doc.select(".wt_viewer img");

        return elements.stream().map(element -> element.attr("src")).filter(StringUtils::isNotBlank)
            .filter(src -> !StringUtils.contains(src, "txt_ads.png")).collect(Collectors.toList());
    }
}
