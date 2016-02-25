package tpl.admin.api.res;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import tpl.groovy.GroovyConfig;
import tpl.velocity.VelocityConfig;

public class ResourceUtil {
	private VelocityConfig velocityConfig;
	private GroovyConfig groovyConfig;
	
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
	
	private String[] getScriptRepositories() {
		return groovyConfig.getGroovyClasspaths();
	}

	public void setVelocityConfig(VelocityConfig velocityConfig) {
		this.velocityConfig = velocityConfig;
	}

	public void setGroovyConfig(GroovyConfig groovyConfig) {
		this.groovyConfig = groovyConfig;
	}
}
