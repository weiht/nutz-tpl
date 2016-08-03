package tpl.javasrc;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.nutz.lang.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tpl.javasrc.WatchDir.EventPair;

public class RuntimeClassFinder {
	private static final Logger logger = LoggerFactory.getLogger(RuntimeClassFinder.class);
	
	public static final String SYS_PROP_RUNTIME_SRC_PATHS = "tpl.javasrc.srcpath";
	public static final String SYS_PROP_RUNTIME_CLASSPATHS = "tpl.javasrc.classpath";
	public static final String PATH_SEPARATOR = "[\\,,\\;,\\:,\\n]";
	
	// TODO Rebuild loader when class path or target directory change.
	
	private Path[] sourcePaths;
	private String sourceDirs;
	private String classpaths;
	private WatchDir watchSource, watchCp;
	private String targetDir = new File(System.getenv("java.io.tmpdir"), "tpl-gen/" + System.currentTimeMillis()).getAbsolutePath();
	private Path targetPath;
	private URL[] classpathUrls;
	private boolean reloadLoader = false, rebuildLoader = false;
	private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	private StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
	
	private ClassLoader loader;
	
	private synchronized void ensureLoader() {
		if (loader == null || rebuildLoader) {
			loader = new URLClassLoader(cpurls(), RuntimeClassFinder.class.getClassLoader());
		} else if (reloadLoader) {
			loader = new URLClassLoader(cpurls(), loader);
		}
		rebuildLoader = false;
		reloadLoader = false;
	}
	
	private synchronized URL[] cpurls() {
		if (classpathUrls == null) {
			List<URL> urls = new ArrayList<URL>();
			addFileUrl(urls, targetDir);
			if (classpaths != null && !(classpaths.isEmpty())) {
				for (String fn: classpaths.split(PATH_SEPARATOR)) {
					addFileUrl(urls, fn);
				}
			}
			classpathUrls = urls.toArray(new URL[]{});
			watchClasspath();
		}
		return classpathUrls;
	}
	
	private void watchClasspath() {
		List<Path> paths = new ArrayList<Path>();
		if (classpaths != null && !(classpaths.isEmpty())) {
			for (String fn: classpaths.split(PATH_SEPARATOR)) {
				addPathToList(paths, fn);
			}
		}
		if (!paths.isEmpty()) {
			watchCp = watchPaths(watchCp, paths.toArray(new Path[]{}));
		}
	}

	private void addFileUrl(List<URL> urls, String fn) {
		Path p = Paths.get(fn);
		if (Files.exists(p)) {
			try {
				urls.add(p.toUri().toURL());
			} catch (MalformedURLException e) {
				logger.debug("", e);
			}
		}
	}

	public Class<?> findClass(String fqcn) throws ClassNotFoundException {
		checkCpChange();
		compileIfNeeded();
		ensureLoader();
		return loader.loadClass(fqcn);
	}

	private synchronized Path[] loadSourcePaths() {
		if (sourcePaths == null) {
			List<Path> paths = new ArrayList<Path>();
			parsePaths(paths, System.getProperty(SYS_PROP_RUNTIME_SRC_PATHS));
			parsePaths(paths, sourceDirs);
			sourcePaths = paths.toArray(new Path[]{});
			watchSource = watchPaths(watchSource, sourcePaths);
		}
		return sourcePaths;
	}

	private WatchDir watchPaths(WatchDir watch, Path[] paths) {
		if (watch != null) {
			watch.unregisterAll();
			watch = null;
		}
		try {
			watch = new WatchDir(paths);
		} catch (IOException e) {
			logger.warn("", e);
		}
		return watch;
	}

	private void parsePaths(List<Path> paths, String dirs) {
		if (dirs != null && !dirs.isEmpty()) {
			for (String dir: dirs.split(PATH_SEPARATOR)) {
				addPathToList(paths, dir);
			}
		}
	}
	
	private void addPathToList(List<Path> paths, String dir) {
		Path p = Paths.get(dir);
		if (Files.exists(p) && Files.isDirectory(p)) {
			paths.add(p);
		}
	}
	
	private synchronized void checkCpChange() {
		// Check class path watcher for change.
		if (watchCp == null) return;
		List<EventPair> watchEvents = watchCp.events();
		if (watchEvents != null && !watchEvents.isEmpty()) {
			for (EventPair e: watchEvents) {
				Kind<Path> kind = e.event.kind();
				if (kind == ENTRY_MODIFY || kind == ENTRY_DELETE) {
					rebuildLoader = true;
					return;
				}
			}
			reloadLoader = true;
		}
	}
	
	private void compileAll() {
		// Compile all classes in the source directories.
		List<String> srcfiles = new ArrayList<String>();
		for (Path p: sourcePaths) {
			scanForSrcFiles(srcfiles, p);
		}
		logger.debug("Compiling all source files: {}", srcfiles);
		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(targetPath.toString());
		options.add("-cp");
		options.add(targetPath.toString());
		Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjectsFromStrings(srcfiles);
		CompilationTask task = compiler.getTask(null, fileManager, null,
				options, null, javaFiles);
		try {
			Boolean result = task.call();
			logger.debug("Compiler result: {}", result);
		} catch (IllegalStateException e) {
			logger.warn("", e);
		}
	}
	
	private void scanForSrcFiles(final List<String> srcfiles, Path p) {
		try {
			Files.walkFileTree(p, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String fn = file.toString();
					if (fn.endsWith(".java")) srcfiles.add(fn);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	private void compileIfNeeded() {
		// Compile changed sources, or remove deleted classes.
		logger.debug("Checking if compiling is needed...");
		List<EventPair> events = watchSource.events();
		if (events == null || events.isEmpty()) return;
		logger.debug("Preparing files to compile...");
		Map<String, Kind<Path>> srcmap = new HashMap<String, WatchEvent.Kind<Path>>();
		for (EventPair ev: events) {
			Path pf = ev.path;
			Kind<Path> kind = ev.event.kind();
			if (Files.isDirectory(pf)) {
				if (kind == ENTRY_DELETE) {
					org.nutz.lang.Files.deleteDir(pf.toFile());
				} else if (kind == ENTRY_CREATE) {
					for (File f: org.nutz.lang.Files.files(pf.toFile(), ".java")) {
						srcmap.put(f.getAbsolutePath(), kind);
					}
				}
			} else {
				String fn = pf.toString();
				if (kind == ENTRY_DELETE) {
					org.nutz.lang.Files.deleteFile(pf.toFile());
					srcmap.remove(fn);
					rebuildLoader = true;
				} else if (kind == ENTRY_MODIFY) {
					srcmap.put(fn, kind);
					rebuildLoader = true;
				} else {
					srcmap.put(fn, kind);
					reloadLoader = true;
				}
			}
		}
		List<String> srcfiles = Lang.collection2list(srcmap.keySet());
		if (srcfiles.isEmpty()) {
			logger.warn("No source files to compile.");
			return;
		}
		logger.debug("Compiling modified source files: {}", srcfiles);
		List<String> options = new ArrayList<String>();
		options.add("-d");
		options.add(targetPath.toString());
		Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjectsFromStrings(srcfiles);
		CompilationTask task = compiler.getTask(null, fileManager, null,
				options, null, javaFiles);
		Boolean result = task.call();
		logger.debug("Compiler result: {}", result);
	}

	public void init() {
		ensureTargetDir();
		sourcePaths = null;
		loadSourcePaths();
		compileAll();
		classpathUrls = null;
		cpurls();
		ensureLoader();
	}

	private synchronized void ensureTargetDir() {
		if (targetPath == null) {
			targetPath = Paths.get(targetDir);
			if (!Files.exists(targetPath)) {
				try {
					Files.createDirectories(targetPath);
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}

	public String getClasspaths() {
		return classpaths;
	}

	public void setClasspaths(String classpaths) {
		this.classpaths = classpaths;
	}

	public String getSourceDirs() {
		return sourceDirs;
	}

	public void setSourceDirs(String sourceDirs) {
		this.sourceDirs = sourceDirs;
	}

	public String getTargetDir() {
		return targetDir;
	}

	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}
}
