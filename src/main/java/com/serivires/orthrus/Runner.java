package com.serivires.orthrus;

import org.apache.commons.lang3.StringUtils;

import com.serivires.orthrus.downloader.WebtoonDownloader;

public class Runner {
	public static void main(String[] args) throws Exception {
		String title = args[0];
		if (StringUtils.isEmpty(title)) {
			System.out.println("제목을 올바르게 입력해 주세요.");
			return;
		}

		WebtoonDownloader downloader = new WebtoonDownloader();
		downloader.autoSave(title);
	}
}