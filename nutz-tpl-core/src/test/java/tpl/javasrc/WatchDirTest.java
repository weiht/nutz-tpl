package tpl.javasrc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import tpl.javasrc.WatchDir.EventPair;

public class WatchDirTest {
	WatchDir watchdir;
	Path watched;
	
	@Before
	public void setup() throws IOException {
		File f = new File(System.getProperty("java.io.tmpdir"), "watchdir-test/" + UUID.randomUUID().toString());
		f.mkdirs();
		watched = Paths.get(f.getAbsolutePath());
		System.out.println(watched);
		watchdir = new WatchDir(watched);
	}
	
	private void printEvents(List<EventPair> events) {
		if (events == null || events.isEmpty()) {
			System.out.println("Events: no events.");
		} else {
			System.out.println("Events: ");
			for (EventPair e: events) {
				// print out event
				System.out.format("%s: [%s], %s\n", e.event.kind().name(), e.event.context(), e.path);
			}
		}
	}

	@Test
	public void test() throws IOException, InterruptedException {
		System.out.println("Starting dir watching test...");
		List<EventPair> events;
		int total;
		events = watchdir.events();
		assertNotNull(events);
		assertEquals(0, events.size());
		System.out.println("Creating 1 file...");
		Files.createTempFile(watched, "", "");
		events = watchdir.events();
		printEvents(events);
		assertNotNull(events);
		assertEquals(1, events.size());
		total = 4;
		System.out.println("Creating " + total + " files...");
		for (int i = 0; i < total; i ++) {
			Files.createTempFile(watched, "", "");
		}
		events = watchdir.events();
		printEvents(events);
		assertNotNull(events);
		assertEquals(total, events.size());
		System.out.println("Dir watching tested.");
	}
}
