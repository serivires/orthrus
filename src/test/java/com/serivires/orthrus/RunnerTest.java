package com.serivires.orthrus;

import org.junit.Test;

public class RunnerTest {

	@Test
	public void mainTest() throws Exception {
		// given
		String[] args = new String[] {"에리타"};

		// when
		Runner.main(args);
	}
}
