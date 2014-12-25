package com.serivires.orthrus;

import java.time.Clock;

import com.serivires.orthrus.downloader.WebtoonDownloader;

public class Runner {
	public static void main(String[] args) throws Exception {
		if (isEmptyBy(args)) {
			System.out.println("제목을 올바르게 입력해 주세요.");
			return;
		}

		String title = args[0];

		Clock clock = Clock.systemDefaultZone();
		long time = clock.millis();

		WebtoonDownloader downloader = new WebtoonDownloader();
		downloader.autoSave(title);

		System.out.println(clock.millis() - time);
	}

	public static boolean isEmptyBy(String[] array) {
		return (array == null || array.length == 0);
	}
}