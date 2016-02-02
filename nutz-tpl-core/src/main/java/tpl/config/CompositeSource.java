package tpl.config;

import java.util.List;

public class CompositeSource
implements PlaceholderSource {
	private List<PlaceholderSource> sourceList;
	
	@Override
	public String get(String key) {
		if (sourceList == null || sourceList.isEmpty()) return null;
		for (PlaceholderSource src: sourceList) {
			if (src != null) {
				String v = src.get(key);
				// Empty string is valid.
				// TODO Add a configuration to allow skipping of empty string values.
				if (v != null) return v;
			}
		}
		return null;
	}

	/**
	 * Indices DO matter!
	 * 
	 * @param sourceList
	 */
	public void setSourceList(List<PlaceholderSource> sourceList) {
		this.sourceList = sourceList;
	}
}
