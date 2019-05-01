package com.serivires.orthrus.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Webtoon {
	private String title;
	private String id;
	private int lastPage;
}
