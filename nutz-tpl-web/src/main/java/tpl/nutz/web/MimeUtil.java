package tpl.nutz.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;

public class MimeUtil {
	private Map<String, String> types = new HashMap<String, String>();

	public boolean isFileBinary(String fileName) {
		String t = getType(fileName);
		return isTypeBinary(t);
	}

	public boolean isTypeBinary(String t) {
		if (Strings.isBlank(t)) return true;

		//TODO More properties to identify. Possibly use a list or map.
		if (t.indexOf("text") >= 0) return false;
		if (t.indexOf("js") >= 0) return false;
		if (t.indexOf("script") >= 0) return false;
		if (t.indexOf("css") >= 0) return false;

		return true;
	}

	public String getType(String fileName) {
		if (Strings.isBlank(fileName)) return null;
		String ext = extractExtension(fileName);
		String t = types.get(ext);
		if (Strings.isBlank(t)) {
			t = URLConnection.guessContentTypeFromName(fileName);
		}
		if (Strings.isBlank(t)) {
			InputStream ins = getResourceStream(fileName);
			if (ins != null) {
				try {
					t = URLConnection.guessContentTypeFromStream(ins);
				} catch (IOException e) {
					//
				} finally {
					Streams.safeClose(ins);
				}
			}
		}
		return t;
	}

	private InputStream getResourceStream(String fn) {
		try {
			return new FileInputStream(fn);
		} catch (FileNotFoundException e) {
			
		}
		List<NutResource> lst = Scans.me().scan(fn);
		if (lst != null && lst.size() > 0) {
			try {
				return lst.get(0).getInputStream();
			} catch (IOException e) {
			}
		}
		return null;
	}

	private String extractExtension(String fn) {
		if (Strings.isEmpty(fn)) return "";
		String fname = fn.trim();
		int ix = fname.lastIndexOf(".");
		if (ix >= 0) return fname.substring(ix);
		return fname;
	}

	public String getTypeDef(String fileName, String def) {
		String t = getType(fileName);
		return Strings.isBlank(t) ? def : t;
	}

	public void addTypes(Map<String, String> types) {
		if (types != null && !types.isEmpty()) {
			this.types.putAll(types);
		}
	}

	public void setTypes(Map<String, String> types) {
		this.types = types;
	}
}
