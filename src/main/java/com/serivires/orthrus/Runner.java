package com.serivires.orthrus;

import com.serivires.orthrus.downloader.WebtoonDownloader;

public class Runner {
	public static void main(String[] args) throws Exception {
		if (isEmptyBy(args)) {
			System.out.println("제목을 올바르게 입력해 주세요.");
			return;
		}

		String title = args[0];

		WebtoonDownloader downloader = new WebtoonDownloader();
		downloader.autoSave(title);
	}

	public static boolean isEmptyBy(String[] array) {
		return (array == null || array.length == 0);
	}
}