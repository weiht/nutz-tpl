package tpl.res;

import java.io.File;

public class AttachmentManager {
	private String rootPath;
	
	public File findFile(Attachment a) {
		String mod = a.getModule();
		File dir = mod == null || mod.isEmpty() ? new File(rootPath) : new File(rootPath, mod);
		if (!dir.exists()) return null;
		File f;
		String p = a.getPath();
		f = new File(dir, p);
		if (f.exists()) return f;
		p = generatePath(a.getName());
		f = new File(new File(dir, p), a.getName());
		if (f.exists()) return f;
		f = new File(dir, a.getName());
		if (f.exists()) return f;
		return null;
	}
	
	public static String generatePath(String fileName) {
		if (fileName == null) return null;
		if (fileName.length() <= 2) return null;
		return fileName.substring(0, 2);
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
}
