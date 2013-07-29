package com.serivires.orthrus.downloader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;

import com.serivires.orthrus.downloader.WebtoonDownloader;

public class WebtoonDownloaderTest {
	WebtoonDownloader webtoonDownload = new WebtoonDownloader();

	@Test
	public void buildWebtoonDetailPageURITest() throws Exception {
		// given
		String title = "신의 탑";
		String no = "1";

		// when
		URI resultUri = webtoonDownload.buildWebtoonDetailPageURI(title, no);

		// then
		assertThat(resultUri.getPath(), is("/webtoon/detail.nhn"));
		assertThat(resultUri.getQuery(), is("titleId=신의+탑&no=1"));
	}

	@Test
	public void buildWebtoonSearchPageURITest() throws Exception {
		// given
		String title = "신의 탑";

		// when
		URI resultUri = webtoonDownload.buildWebtoonSearchPageURI(title);

		// then
		assertThat(resultUri.getPath(), is("/search.nhn"));
		assertThat(resultUri.getQuery(), is("m=webtoon&type=title&keyword=신의+탑"));
	}
}