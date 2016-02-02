package tpl.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;

public class PlaceholderConfigure {
	public static final String PATTERN = "(\\$\\{[\\w,/,\\.]+})";
	private static final Pattern pattern = Pattern.compile(PATTERN);
	private PlaceholderSource source;
	
	public String get(String key) {
		String value = source.get(key);
		if (Strings.isBlank(value)) return value;
		Matcher m;
		while((m = pattern.matcher(value)).find()) {
			String g = m.group(1);
			String k = g.substring(2, g.length() - 1);
			String v = get(k);
			String replace = "(\\$\\{" + k + "})";
			value = value.replaceAll(replace, v);
		}
		return value;
	}

	public void setSource(PlaceholderSource source) {
		this.source = source;
	}
}
