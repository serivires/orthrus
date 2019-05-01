package com.serivires.orthrus.downloader;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class WebtoonDownloaderTest {
	WebtoonDownloader webtoonDownload;

	@Before
	public void setup() throws Exception {
		webtoonDownload = new WebtoonDownloader();
	}

	@Test
	public void buildWebtoonDetailPageURITest() throws Exception {
		// given
		String title = "신의 탑";
		String no = "1";

		// when
		URI resultUri = webtoonDownload.buildDetailPageUri(title, no);

		// then
		assertThat(resultUri.getPath(), is("/webtoon/detail.nhn"));
		assertThat(resultUri.getQuery(), is("titleId=신의 탑&no=1"));
	}

	@Test
	public void buildWebtoonSearchPageURITest() throws Exception {
		// given
		String title = "신의 탑";

		// when
		URI resultUri = webtoonDownload.buildWebtoonSearchPageUri(title);

		// then
		assertThat(resultUri.getPath(), is("/search.nhn"));
		assertThat(resultUri.getQuery(), is("keyword=신의 탑"));
	}
}
