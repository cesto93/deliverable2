package utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GetProperty {
	private static final Logger LOGGER = Logger.getLogger(GetProperty.class.getName());
	private static final String FILENAME  = "config.properties";
	
	private GetProperty() {
		throw new IllegalStateException("Utility class");
	}
	
	public static String getProperty(String name) {
		Properties prop = new Properties();
		try (
				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILENAME);
			)
		{
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				LOGGER.log(Level.SEVERE, "property file '" + FILENAME + "' not found in the classpath");
			}
			return  prop.getProperty(name);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
}
