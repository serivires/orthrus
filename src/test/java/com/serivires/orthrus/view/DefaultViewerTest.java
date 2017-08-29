package com.serivires.orthrus.view;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultViewerTest {
    private DefaultViewer defaultViewer;

    @Before public void setup() throws Exception {
        defaultViewer = new DefaultViewer();
    }

    @Test public void writeTest() {
        Map<String, Object> model = new HashMap<>();
        File file = new File("test.html");
        defaultViewer.write(model, file);
    }
}
