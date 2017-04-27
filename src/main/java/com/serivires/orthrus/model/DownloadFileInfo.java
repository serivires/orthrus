package com.serivires.orthrus.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.File;

@Data
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

  public String getFileName() {
    String[] depths = downloadUrl.split("/");
    return depths[depths.length - 1];
  }

  public File getSaveFileInfo() {
    return new File(this.preSavePath, this.getFileName());
  }
}
