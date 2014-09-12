package com.serivires.orthrus;

import com.serivires.orthrus.downloader.WebtoonDownloader;

public class Runner {
	public static void main(String[] args) throws Exception {
		if (isArrayEmpty(args)) {
			System.out.println("제목을 올바르게 입력해 주세요.");
			return;
		}

		String title = args[0];
		
		WebtoonDownloader downloader = new WebtoonDownloader();
		downloader.autoSave(title);
	}

	public static boolean isArrayEmpty(String[] array) {
		if (array == null || array.length == 0) {
			return true;
		}

		return false;
	}
}