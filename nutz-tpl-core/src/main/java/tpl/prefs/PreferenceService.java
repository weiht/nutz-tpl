package tpl.prefs;

public interface PreferenceService {
	public abstract Integer intValue(String key, Integer defVal);
	public abstract Long longValue(String key, Long defVal);
	public abstract Float floatValue(String key, Float defVal);
	public abstract Double doubleValue(String key, Double defVal);
	public abstract String stringValue(String key, String defVal);
}
