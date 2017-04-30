package com.serivires.orthrus;

import org.junit.Test;

public class RunnerTest {

    @Test public void mainTest() throws Exception {
        // given
        String[] args = new String[] {"아메리카노 엑소더스"};

        // when
        Runner.main(args);
    }
}
