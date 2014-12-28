package com.serivires.orthrus.model;

import java.io.File;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class DownloadFileInfo {
  private String refererUrl;
  private String downloadUrl;
  private String preSavePath;

  public DownloadFileInfo() {

  }

  public DownloadFileInfo(String refererUrl, String preSavePath) {
    this.refererUrl = refererUrl;
    this.preSavePath = preSavePath;
  }

  public String getRefererUrl() {
    return refererUrl;
  }

  public void setRefererUrl(String refererUrl) {
    this.refererUrl = refererUrl;
  }

  public String getDownloadUrl() {
    return downloadUrl;
  }

  public void setDownloadUrl(String downloadUrl) {
    this.downloadUrl = downloadUrl;
  }

  public String getPreSavePath() {
    return preSavePath;
  }

  public void setPreSavePath(String preSavePath) {
    this.preSavePath = preSavePath;
  }

  public String getFileName() {
    String[] depths = downloadUrl.split("/");
    return depths[depths.length - 1];
  }

  public File getSaveFileInfo() {
    return new File(this.preSavePath, this.getFileName());
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}
