package tpl.javasrc;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

/**
 * @author atamur
 * @since 15-Oct-2009
 */
public class ClassloaderJavaFileManager implements JavaFileManager {
	private final ClassLoader classLoader;
	private final StandardJavaFileManager standardFileManager;
	private final PackageInternalsFinder finder;
	private final Path targetPath;

	public ClassloaderJavaFileManager(ClassLoader classLoader,
			StandardJavaFileManager standardFileManager,
			Path targetPath) {
		this.classLoader = classLoader;
		this.standardFileManager = standardFileManager;
		finder = new PackageInternalsFinder(classLoader);
		this.targetPath = targetPath;
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		return classLoader;
	}

	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (file instanceof CustomJavaFileObject) {
			return ((CustomJavaFileObject) file).binaryName();
		} else {
			return standardFileManager.inferBinaryName(location, file);
		}
	}

	@Override
	public boolean isSameFile(FileObject a, FileObject b) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean handleOption(String current, Iterator<String> remaining) {
		return true;
	}

	@Override
	public boolean hasLocation(Location location) {
		return location == StandardLocation.CLASS_PATH || location == StandardLocation.PLATFORM_CLASS_PATH;
	}

	@Override
	public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
			FileObject sibling) throws IOException {
		if (targetPath == null || !Files.isDirectory(targetPath)) return standardFileManager.getJavaFileForOutput(location, className, kind, sibling);
		return new CustomJavaFileObject(className, targetPath.resolve(className.replaceAll("[\\.]", "/") + ".class").toUri());
	}

	@Override
	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling)
			throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void flush() throws IOException {
		// do nothing
	}

	@Override
	public void close() throws IOException {
		// do nothing
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds,
			boolean recurse) throws IOException {
		List<JavaFileObject> result = finder.find(packageName);
		if (targetPath != null) {
			Path dir = targetPath.resolve(packageName.replaceAll("[\\.]", "/"));
			if (Files.isDirectory(dir)) {
				if (kinds.contains(Kind.SOURCE)) addFiles(result, dir, packageName, ".java");
				if (kinds.contains(Kind.CLASS)) addFiles(result, dir, packageName, ".class");
			}
		}
		for (JavaFileObject fo: standardFileManager.list(location, packageName, kinds, recurse)) {
			result.add(fo);
		}
		Set<JavaFileObject> s = new HashSet<JavaFileObject>();
		s.addAll(result);
		return s;
	}

	private void addFiles(List<JavaFileObject> result, Path dir, String packageName, String ext) {
		for (File f: org.nutz.lang.Files.files(dir.toFile(), ext)) {
			String bn = packageName + "." + f.getName();
			bn = bn.substring(0, bn.length() - ext.length());
			result.add(new CustomJavaFileObject(bn, f.toURI()));
		}
	}

	@Override
	public int isSupportedOption(String option) {
		return -1;
	}

}