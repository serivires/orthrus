package com.serivires.orthrus.model;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class DownloadFileInfo {
	private String name;

	private String refererURL;

	private String downloadURL;
	
	private String preSavePath;

	public DownloadFileInfo() {

	}

	public DownloadFileInfo(String refererURL, String downloadURL) {
		this.refererURL = refererURL;
		this.downloadURL = downloadURL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRefererURL() {
		return refererURL;
	}

	public void setRefererURL(String refererURL) {
		this.refererURL = refererURL;
	}

	public String getDownloadURL() {
		return downloadURL;
	}

	public void setDownloadURL(String downloadURL) {
		this.downloadURL = downloadURL;
	}

	public String getFileName() {
		String[] depths = downloadURL.split("/");
		return depths[depths.length - 1];
	}
	
	public String getPreSavePath() {
		return preSavePath;
	}

	public void setPreSavePath(String preSavePath) {
		this.preSavePath = preSavePath;
	}

	public File getSaveFileInfo() {
		return new File(this.preSavePath, this.getFileName());
	}

	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}