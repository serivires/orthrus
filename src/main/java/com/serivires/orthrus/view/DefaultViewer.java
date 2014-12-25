package com.serivires.orthrus.view;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class DefaultViewer {
	private final VelocityEngine velocityEngine;

	public DefaultViewer() throws Exception {
		velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
	}

	public void write(Map<String, Object> model, File file) {
		StringWriter stringWriter = new StringWriter();
		VelocityContext context = new VelocityContext(model);

		try {
			velocityEngine.mergeTemplate("velocity/defaultView.vm", context, stringWriter);
			FileUtils.write(file, stringWriter.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
