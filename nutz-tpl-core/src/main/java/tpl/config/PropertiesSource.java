package tpl.config;

import java.util.Properties;

public class PropertiesSource
implements PlaceholderSource {
	protected Properties properties = new Properties();
	
	@Override
	public String get(String key) {
		return properties.getProperty(key);
	}

	public void setProperties(Properties properties) {
		this.properties.putAll(properties);
	}
}
