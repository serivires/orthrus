package com.serivires.orthrus.view;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class DefaultViewer {
    private final VelocityEngine velocityEngine;

    /**
     * VelocityEngine 초기화
     *
     * @throws Exception:
     */
    public DefaultViewer() throws Exception {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class",
            ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    /**
     * defaultView.vm에 정보를 입력하여 viewer 파일을 생성합니다.
     *
     * @param model:
     * @param file:
     */
    public void write(final Map<String, Object> model, final File file) {
        final StringWriter stringWriter = new StringWriter();
        final VelocityContext context = new VelocityContext(model);

        try {
            velocityEngine.mergeTemplate("velocity/defaultView.vm", context, stringWriter);
            FileUtils.write(file, stringWriter.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
