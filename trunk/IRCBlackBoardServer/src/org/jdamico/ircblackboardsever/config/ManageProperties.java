package org.jdamico.ircblackboardsever.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;



public class ManageProperties {
	private static ManageProperties INSTANCE;

	public static ManageProperties getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ManageProperties();
		}
		return INSTANCE;
	}

	public String read(String prop) {
		// Read properties file.
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(Constants.PROPERTIES_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties.getProperty(prop);
	}
}
