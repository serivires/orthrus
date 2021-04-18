package com.serivires.orthrus.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class DownloadFileInfo {
	private String refererUrl;
	private String downloadUrl;
	private String preSavePath;

	public DownloadFileInfo(String refererUrl, String preSavePath) {
		this.refererUrl = refererUrl;
		this.preSavePath = preSavePath;
	}

	private String getFileName() {
		String[] depths = downloadUrl.split("/");
		return depths[depths.length - 1];
	}

	public Path getPath() {
		return Paths.get(getPreSavePath() + getFileName());
	}

	public File getSaveFileInfo() {
		return new File(this.preSavePath, this.getFileName());
	}
}
