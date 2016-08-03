package tpl.javasrc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class RuntimeClassFinderTest {
	RuntimeClassFinder finder;
	Path srcPath, targetPath, tplPath;
	
	@Before
	public void setup() throws IOException {
		String timestamp = "" + System.currentTimeMillis();
		srcPath = Paths.get(System.getProperty("java.io.tmpdir"),
				"classfinder-test",
				timestamp,
				"src");
		targetPath = Paths.get(System.getProperty("java.io.tmpdir"),
				"classfinder-test",
				timestamp,
				"target");
		tplPath = Paths.get(".", "src/test/resources");
		Files.createDirectories(srcPath);
		Files.createDirectories(targetPath);
		finder = new RuntimeClassFinder();
		finder.setSourceDirs(srcPath.toString());
		finder.setTargetDir(targetPath.toString());
	}

	@Test
	public void test() throws IOException, ClassNotFoundException,
	InstantiationException, IllegalAccessException, InterruptedException {
		assertEquals(0, org.nutz.lang.Files.dirs(targetPath.toFile()).length);
		assertEquals(0, org.nutz.lang.Files.files(targetPath.toFile(), ".class").length);
		org.nutz.lang.Files.copy(tplPath.resolve("Nothing.tpl").toFile(), srcPath.resolve("Nothing.java").toFile());
		finder.init();
		assertNotNull(finder.findClass("Nothing"));
		assertEquals(0, org.nutz.lang.Files.dirs(targetPath.toFile()).length);
		assertEquals(1, org.nutz.lang.Files.files(targetPath.toFile(), ".class").length);
		org.nutz.lang.Files.copy(
				tplPath.resolve("tpl" + File.separator + "Nobody.tpl").toFile(),
				srcPath.resolve("tpl" + File.separator + "Nobody.java").toFile());
		Thread.sleep(100);
		assertNotNull(finder.findClass("Nothing"));
		assertNotNull(finder.findClass("tpl.Nobody"));
		assertEquals(1, org.nutz.lang.Files.dirs(targetPath.toFile()).length);
		assertEquals(1, org.nutz.lang.Files.files(targetPath.toFile(), ".class").length);
		assertEquals(1, org.nutz.lang.Files.files(targetPath.resolve("tpl").toFile(), ".class").length);
		org.nutz.lang.Files.copy(
				tplPath.resolve("tpl" + File.separator + "Nowhere.tpl").toFile(),
				srcPath.resolve("tpl" + File.separator + "Nowhere.java").toFile());
		Thread.sleep(100);
		assertNotNull(finder.findClass("Nothing"));
		assertNotNull(finder.findClass("tpl.Nobody"));
		assertNotNull(finder.findClass("tpl.Nowhere"));
		assertEquals(1, org.nutz.lang.Files.dirs(targetPath.toFile()).length);
		assertEquals(1, org.nutz.lang.Files.files(targetPath.toFile(), ".class").length);
		assertEquals(2, org.nutz.lang.Files.files(targetPath.resolve("tpl").toFile(), ".class").length);
		System.out.println(finder.findClass("tpl.Nowhere").newInstance().toString());
	}
}
