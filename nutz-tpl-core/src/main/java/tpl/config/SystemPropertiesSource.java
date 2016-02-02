package tpl.config;

public class SystemPropertiesSource
extends PropertiesSource {
	public SystemPropertiesSource() {
		this.properties.putAll(System.getenv());
		this.properties.putAll(System.getProperties());
	}
}
