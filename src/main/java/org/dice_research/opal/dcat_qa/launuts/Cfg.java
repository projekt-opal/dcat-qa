package org.dice_research.opal.dcat_qa.launuts;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public abstract class Cfg {

	protected final static String RESOURCE_NAME = "config.properties";
	protected static Properties properties = null;

	public static String get(String key) {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(Cfg.class.getClassLoader().getResourceAsStream(RESOURCE_NAME));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return properties.getProperty(key);
	}

	public static File getTmpDir() {
		File tmpDir = new File(Cfg.get("data.dir"));
		if (!tmpDir.exists()) {
			tmpDir.mkdirs();
		}
		return tmpDir;
	}

	public static String getEntpointUri() {
		return Cfg.get("endpint.uri");
	}

}