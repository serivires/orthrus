package com.serivires.orthrus.downloader;

import com.serivires.orthrus.model.DownloadFileInfo;
import com.serivires.orthrus.model.Webtoon;
import com.serivires.orthrus.parse.WebtoonParser;
import com.serivires.orthrus.view.DefaultViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WebtoonDownloader {
    private static Logger logger = LoggerFactory.getLogger(WebtoonDownloader.class);

    private final WebtoonParser webtoonParser;
    private final DefaultViewer viewer;

    public WebtoonDownloader() throws Exception {
        webtoonParser = new WebtoonParser();
        viewer = new DefaultViewer();
    }

    /**
     * 실제 웹툰이 보여지는 페이지 주소를 반환합니다.
     *
     * @param titleId:
     * @param no:
     * @return URI
     */
    public URI buildDetailPageUri(String titleId, String no) {
        return UriComponentsBuilder.fromHttpUrl("http://comic.naver.com").path("webtoon/detail.nhn")
            .queryParam("titleId", titleId).queryParam("no", no).build().toUri();
    }

    /**
     * 웹툰 검색페이지 주소를 반환합니다.
     *
     * @param title
     * @return
     * @throws URISyntaxException
     */
    protected URI buildWebtoonSearchPageUri(String title) {
        return UriComponentsBuilder.fromHttpUrl("http://comic.naver.com").path("webtoon/search.nhn")
            .queryParam("m", "webtoon").queryParam("type", "title").queryParam("keyword", title)
            .build().toUri();
    }

    /**
     * 타이틀과 부분 일치하는 웹툰의 모든 화를 다운로드 합니다.
     *
     * @param title:
     * @throws Exception:
     */
    public void autoSave(String title) throws Exception {
        Optional<Webtoon> webtoonInfo = getWebtoonInfo(title);
        if (!webtoonInfo.isPresent()) {
            return;
        }

        int downloadCount = 0;
        Webtoon webtoon = webtoonInfo.get();
        int lastPageNumber = webtoon.getLastPage();
        String prePath =
            String.format("%s\\Desktop\\%s\\", System.getProperty("user.home"), webtoon.getTitle());

        for (int i = 1; i <= lastPageNumber; i++) {
            URI uri = buildDetailPageUri(webtoon.getId(), String.valueOf(i));
            downloadCount += saveByOnePage(uri, prePath + i + File.separator);

            System.out.println(String.format("%d개. %.1f%% 완료되었습니다.", downloadCount,
                ((double) i / (double) lastPageNumber) * 100.0));
        }

        System.out.println("총 " + downloadCount + "개의파일이 다운로드 되었습니다.");
    }

    /**
     * 한 페이지 내에 있는 유효한 이미지 파일을 저장합니다.
     *
     * @param uri:
     * @param path:
     * @return int
     * @throws Exception:
     */
    public int saveByOnePage(final URI uri, final String path) throws Exception {
        List<String> imageUrlList = webtoonParser.selectImageUrlsBy(uri.toURL());

        List<DownloadFileInfo> fileUrlList = imageUrlList.parallelStream().map(imageURL -> {
            DownloadFileInfo fileInfo = new DownloadFileInfo(uri.toString(), path);
            fileInfo.setDownloadUrl(imageURL);
            return fileInfo;
        }).collect(Collectors.toList());

        FileDownloadUtils.parallel(fileUrlList);
        writeViewer(imageUrlList, path);

        return imageUrlList.size();
    }

    /**
     * viewer 파일을 생성합니다.
     *
     * @param imageUrlList:
     * @param savePath:
     */
    private void writeViewer(List<String> imageUrlList, String savePath) {
        List<String> downloadFileNames = imageUrlList.stream().map(imageURL -> {
            String[] depths = imageURL.split("/");
            return depths[depths.length - 1];
        }).collect(Collectors.toList());

        Map<String, Object> model = new HashMap<>();
        model.put("fileNames", downloadFileNames);
        viewer.write(model, new File(savePath, "viewer.html"));
    }

    /**
     * 제목과 일치하는 웹툰 정보를 반환합니다.
     *
     * @param title :
     * @return Webtoon
     * @throws Exception:
     */
    private Optional<Webtoon> getWebtoonInfo(String title) throws Exception {
        URI uri = buildWebtoonSearchPageUri(title);
        Optional<Webtoon> webtoonInfo = webtoonParser.getWebtoonInfo(uri.toURL());

        if (!webtoonInfo.isPresent()) {
            logger.info("검색 결과가 없습니다.");
            return webtoonInfo;
        }

        Webtoon webtoon = webtoonInfo.get();
        int lastPage = getLastPageNumber(webtoon.getId());
        if (lastPage <= 0) {
            logger.info("접속이 차단되었습니다.");
            return webtoonInfo;
        }

        webtoon.setLastPage(lastPage);
        return Optional.of(webtoon);
    }

    /**
     * 웹툰의 마지막화 번호를 반환합니다.
     * <p>
     * <p>http://comic.naver.com/webtoon/detail.nhn?titleId=316912&no=188 <br>
     * no 파라미터에 유효한 숫자를 넘기지 않으면 마지막화 페이지로 이동한다.
     *
     * @param titleId:
     * @return int
     * @throws Exception:
     */
    private int getLastPageNumber(String titleId) throws Exception {
        URI uri = buildDetailPageUri(titleId, "0");
        return webtoonParser.getLastPageNumber(uri.toURL());
    }
}
