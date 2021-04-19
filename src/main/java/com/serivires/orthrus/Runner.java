package com.serivires.orthrus;

import org.apache.commons.lang3.ArrayUtils;

import com.serivires.orthrus.downloader.WebtoonDownloader;

public class Runner {
	/**
	 * 제목을 입력하면 제목과 부분 일치하는 웹툰을 자동으로 다운로드 합니다.
	 * <p>
	 * <p>현재는 네이버 웹툰만 지원합니다.
	 *
	 * @param args:
	 * @throws Exception:
	 */
	public static void main(String[] args) throws Exception {
		if (ArrayUtils.isEmpty(args) || args.length != 1) {
			System.out.println("제목을 다시 입력해 주세요.");
			return;
		}

		final String title = args[0];

		WebtoonDownloader downloader = new WebtoonDownloader();

		// TODO: 검색된 목록을 보여주고 선택 가능하도록 변경 or GUI 도입?
		// TODO: javaFX 도입 검토
		downloader.autoSave(title);
	}
}
