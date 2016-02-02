package tpl.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFileSource
extends PropertiesSource {
	private static final Logger logger = LoggerFactory.getLogger(PropertiesFileSource.class);
	
	public PropertiesFileSource(String[] files) {
		if (files != null && files.length > 0) {
			for (String f: files) {
				load(f);
			}
		}
	}
	
	private void load(String fn) {
        List<NutResource> resources = Scans.me().loadResource("^.+[.]properties$", fn);
        for (NutResource r: resources) {
        	try {
				properties.putAll(read(r.getInputStream()));
			} catch (IOException e) {
				logger.warn("Error loading resource: {}", r, e);
			}
        }
	}
	
	private Properties read(InputStream ins) throws IOException {
		Properties props = new Properties();
		try {
			props.load(ins);
		} finally {
			ins.close();
		}
		return props;
	}
}
