package tpl.admin.api.res;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.nutz.ioc.Ioc2;
import org.nutz.lang.Streams;

import tpl.groovy.GroovyConfig;
import tpl.velocity.VelocityConfig;

public class ResourceUtil {
	private VelocityConfig velocityConfig;
	private GroovyConfig groovyConfig;
	private Ioc2 ioc;
	
	public List<String> findAllPages(String matchers) {
		List<String> lst = new ArrayList<String>();
		for (String m: matchers.split("\\n")){
			if (!m.isEmpty()) {
				findPages(lst, m);
			}
		}
		return lst;
	}
	
	private List<String> findPages(List<String> lst, String matcher) {
		return findResList(lst, matcher, getPageRepositories());
	}

	private String[] getPageRepositories() {
		return velocityConfig.getRepositories();
	}

	public List<String> findAllScripts(String matchers) {
		List<String> lst = new ArrayList<String>();
		for (String m: matchers.split("\\n")){
			if (!m.isEmpty()) {
				findScripts(lst, m);
			}
		}
		return lst;
	}

	private List<String> findScripts(List<String> lst, String matcher) {
		return findResList(lst, matcher, getScriptRepositories());
	}

	private List<String> findResList(List<String> lst, String matcher, String[] repos) {
		final Pattern pattern = Pattern.compile(matcher);
		FilenameFilter flt = new FilenameFilter() {
			public boolean accept(File dir, String fname) {
				return pattern.matcher(fname).matches();
			};
		};
		for (String repo: repos) {
			findFilesInRepo(lst, flt, repo, new File(repo));
		}
		return lst;
	}

	private List<String> findFilesInRepo(List<String> lst, FilenameFilter flt, String repo, File dir) {
		FileFilter ff = new FileFilter() {
			public boolean accept(File f) {
				return f.isFile();
			};
		};
		for (File f: dir.listFiles(ff)) {
			if (flt.accept(dir, f.getName()))
				lst.add(f.getAbsolutePath().substring(repo.length()).replaceAll("[\\\\]", "/"));
		}
		FileFilter df = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			};
		};
		for (File d: dir.listFiles(df)) {
			findFilesInRepo(lst, flt, repo, d);
		}
		return lst;
	}
	
	public String ensurePageResource(String apath, String atpl, String arepo) {
		String path = apath;
		if (path == null || path.isEmpty()) return null;
		if (path.startsWith("view:")) path = path.substring(5);
		String[] repos = getPageRepositories();
		String res = findReadResourceAsString(repos, path);
		if (res == null || res.isEmpty()) {
			res = createPageResource(path, atpl, arepo);
		}
		return res;
	}
	
	private String createPageResource(String apath, String atpl, String arepo) {
		String path = apath;
		String repo = arepo;
		if (repo == null || repo.isEmpty()) repo = getPageRepositories()[0];
		String tpl = findReadResourceAsString(getPageRepositories(), atpl);
		if (tpl == null) tpl = "";
		return createResource(repo, path, tpl, null);
	}
	
	public String ensureScriptResource(String apath, String atpl, String arepo, Map<String, Object> section) {
		String path = apath;
		if (path == null || path.isEmpty()) return null;
		String[] repos = getScriptRepositories();
		String res = findReadResourceAsString(repos, path);
		if (res == null || res.isEmpty()) {
			res = createScriptResource(path, atpl, arepo, section);
		}
		return res;
	}
	
	private String findReadResourceAsString(String[] repos, String path) {
		for (String r: repos) {
			File f = new File(r, path);
			if (f.exists()) {
				return fileToString(f);
			}
		}
		InputStream rins = getClass().getClassLoader().getResourceAsStream(path);
		if (rins != null) {
			try {
				return Streams.readAndClose(Streams.utf8r(rins));
			} finally {
				try {
					rins.close();
				} catch(Exception e) {}
			}
		}
		return null;
	}

	private String fileToString(File f) {
		return Streams.readAndClose(Streams.fileInr(f));
	}
	
	private void stringToFile(File f, String content) {
		Streams.writeAndClose(Streams.fileOutw(f), content);
	}
	
	private String createScriptResource(String apath, String atpl, String arepo, Map<String, Object> section) {
		String path = apath;
		String repo = arepo;
		if (repo == null || repo.isEmpty()) repo = getScriptRepositories()[0];
		String tpl = findReadResourceAsString(getPageRepositories(), atpl);
		return createResource(repo, path, tpl, section);
	}

	private String createResource(String repo, String path, String tpl, Map<String, Object> section) {
		if (tpl == null) return null;
		File f = new File(repo, path);
		if (f.exists()) {
			String content = fileToString(f);
			if (content != null && !content.isEmpty()) return content.toString();
		}
		if (!f.getParentFile().exists() && !f.getParentFile().mkdirs()) return null;
		try {
			if (!f.exists() && !f.createNewFile()) return null;
		} catch (IOException e) {
			return null;
		}
		if (section != null) {
			VelocityEngine engine = velocityConfig.getEngine();
			VelocityContext vctx = new VelocityContext();
			vctx.put("ioc", ioc);
			vctx.put("section", section);
			StringWriter w = new StringWriter();
			engine.evaluate(vctx, w, path, tpl);
			tpl = w.toString();
		}
		stringToFile(f, tpl);
		return tpl;
	}
	
	public String getPageResource(String apath) {
		String path = apath;
		if (path == null || path.isEmpty()) return null;
		if (path.startsWith("view:")) path = path.substring(5);
		String[] repos = getPageRepositories();
		return findReadResourceAsString(repos, path);
	}
	
	public String getScriptResource(String apath) {
		String path = apath;
		if (path == null || path.isEmpty()) return null;
		String[] repos = getScriptRepositories();
		return findReadResourceAsString(repos, path);
	}
	
	public String savePageResource(String apath, String acontent, String arepo) {
		String path = apath;
		if (path == null || path.isEmpty()) return null;
		if (path.startsWith("view:")) path = path.substring(5);
		String repo = arepo;
		if (repo == null || repo.isEmpty()) repo = getPageRepositories()[0];
		return saveContentToFile(path, repo, acontent);
	}
	
	public String saveScriptResource(String apath, String acontent, String arepo) {
		String path = apath;
		if (path == null || path.isEmpty()) return null;
		String repo = arepo;
		if (repo == null || repo.isEmpty()) repo = getScriptRepositories()[0];
		return saveContentToFile(path, repo, acontent);
	}

	private String saveContentToFile(String apath, String arepo, String acontent) {
		File f = new File(arepo, apath);
		try {
			if (!f.exists()
				&& (!f.getParentFile().exists() && !f.getParentFile().mkdirs() || !f.createNewFile())) return null;
		} catch (IOException e) {
			return null;
		}
		stringToFile(f, acontent);
		return acontent;
	}
	
	private String[] getScriptRepositories() {
		return groovyConfig.getGroovyClasspaths();
	}

	public void setVelocityConfig(VelocityConfig velocityConfig) {
		this.velocityConfig = velocityConfig;
	}

	public void setGroovyConfig(GroovyConfig groovyConfig) {
		this.groovyConfig = groovyConfig;
	}

	public void setIoc(Ioc2 ioc) {
		this.ioc = ioc;
	}
}
