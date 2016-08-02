package tpl.javasrc;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Before;
import org.junit.Test;

public class CompilerTest {
	static final String SOURCE_CODE_ROOT = Paths.get("./src/main/java").toString();
	static final String DEST_ROOT = Paths.get(System.getProperty("java.io.tmpdir")).toString();
	JavaCompiler compiler;
	StandardJavaFileManager fileManager;
	List<String> options;
	
	@Before
	public void setup() throws IOException {
		compiler = ToolProvider.getSystemJavaCompiler();
		fileManager = compiler.getStandardFileManager(null, null, null);
		Path destDir = Paths.get(DEST_ROOT, "compile-test", "" + System.currentTimeMillis());
		Files.createDirectories(destDir);
		options = new ArrayList<String>();
		options.add("-d");
		options.add(destDir.toString());
	}

	@Test
	public void test() throws IOException {
		List<String> files = searchForJavaFiles(SOURCE_CODE_ROOT);
		System.out.println(files);
		Iterable<? extends JavaFileObject> javaFiles = fileManager.getJavaFileObjectsFromStrings(files);
		CompilationTask task = compiler.getTask(null, fileManager, null,
				options, null, javaFiles);
		Boolean result = task.call();
		assertTrue(result);
	}

	private List<String> searchForJavaFiles(String root) throws IOException {
		final List<String> r = new ArrayList<String>();
		Files.walkFileTree(Paths.get(root), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				System.out.println("" + file);
				String fn = file.toString();
				if (fn.endsWith(".java")) r.add(fn);
				return FileVisitResult.CONTINUE;
			}
		});
		return r;
	}
}
