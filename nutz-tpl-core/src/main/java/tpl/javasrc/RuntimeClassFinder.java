package tpl.javasrc;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeClassFinder {
	private static final Logger logger = LoggerFactory.getLogger(RuntimeClassFinder.class);
	
	public static final String SYS_PROP_RUNTIME_SRC_PATHS = "tpl.javasrc.srcpath";
	public static final String SYS_PROP_RUNTIME_CLASSPATHS = "tpl.javasrc.classpath";
	
	private String sourcePaths;
	private String classpaths;
	
	private ClassFinder finder = new ClassFinder();
	private ClassLoader loader = new ClassLoader(RuntimeClassFinder.class.getClassLoader()) {
	};
	
	private static final class RuntimeClassLoader extends ClassLoader {
		public RuntimeClassLoader() {
			super(RuntimeClassFinder.class.getClassLoader());
		}
	}
	
	private static final class ClassFinder {
		private ClassFinder previousFinder;
		private RuntimeClassLoader classLoader = new RuntimeClassLoader();
		private long timestamp = System.currentTimeMillis();
		
		public Class<?> findClass(File f, long timestamp, boolean compiled) throws ClassNotFoundException {
			if (previousFinder != null && previousFinder.timestamp < timestamp) {
				return previousFinder.findClass(f, timestamp, compiled);
			}
			throw new ClassNotFoundException(f.getName());
		}
	}
	
	public Class<?> findClass(String fqcn) throws ClassNotFoundException {
		boolean compiled = false;
		File f = findSource(fqcn);
		if (f == null) {
			f = findCompiled(fqcn);
			compiled = true;
		}
		if (f == null) {
			return loader.loadClass(fqcn);
		}
		long ts = f.lastModified();
		if (finder.timestamp < ts) {
			synchronized(this) {
				ClassFinder fnd = new ClassFinder();
				fnd.previousFinder = finder;
				finder = fnd;
			}
		}
		return finder.findClass(f, ts, compiled);
	}
	
	private File findCompiled(String fqcn) {
		// TODO Auto-generated method stub
		return null;
	}

	private File findSource(String fqcn) {
		// TODO Auto-generated method stub
		return null;
	}

	private void loadSourcePaths() {
		//
	}
	
	private void loadClasspaths() {
		//
	}
	
	public void init() {
		loadSourcePaths();
		loadClasspaths();
	}

	public String getSourcePaths() {
		return sourcePaths;
	}

	public void setSourcePaths(String sourcePaths) {
		this.sourcePaths = sourcePaths;
	}

	public String getClasspaths() {
		return classpaths;
	}

	public void setClasspaths(String classpaths) {
		this.classpaths = classpaths;
	}
}
