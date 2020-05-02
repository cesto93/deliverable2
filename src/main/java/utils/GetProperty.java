package utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GetProperty {
	private static GetProperty instance = null;
	private static final Logger LOGGER = Logger.getLogger(GetProperty.class.getName());
	private static final String propFileName  = "config.properties";
	
	public GetProperty() {
		
	}
	
	public static  GetProperty getInstance() {
        if (instance == null) 
            instance = new GetProperty(); 
  
        return instance; 
	}
	
	public String getProperty(String name) {
		Properties prop = new Properties();
		try (
				InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			)
		{
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				LOGGER.log(Level.SEVERE, "property file '" + propFileName + "' not found in the classpath");
			}
 
			return  prop.getProperty(name);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		return null;
	}
}
