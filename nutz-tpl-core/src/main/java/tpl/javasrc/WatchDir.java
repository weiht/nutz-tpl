package tpl.javasrc;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example to watch a directory (or tree) for changes to files.
 * <p>
 * Oirignal source code: <a href="https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java">https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java</a>
 * <p>
 * Reference: <a href="https://docs.oracle.com/javase/tutorial/essential/io/notification.html">https://docs.oracle.com/javase/tutorial/essential/io/notification.html</a>
 */

public class WatchDir {
	private static final Logger logger = LoggerFactory.getLogger(WatchDir.class);

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	
	static final class EventPair {
		final Path parent, path;
		final WatchEvent<Path> event;
		
		EventPair(Path p, WatchEvent<Path> e) {
			parent = p;
			event = e;
			path = p.resolve(e.context());
		}
	}

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Register the given directory with the WatchService
	 */
	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		if (logger.isDebugEnabled()) {
			Path prev = keys.get(key);
			if (prev == null) {
				logger.debug("register: {}", dir);
			} else {
				if (!dir.equals(prev)) {
					logger.debug("update: {} -> {}", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	WatchDir(Path... dirs) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, Path>();

		for (Path dir: dirs) {
			logger.debug("Scanning {}", dir);
			registerAll(dir);
			logger.debug("Done.");
		}
	}

	List<EventPair> events() {
		WatchKey key;
		List<EventPair> allEvents = new ArrayList<EventPair>();
		while((key = watcher.poll()) != null) {
			Path dir = keys.get(key);
			if (dir == null) {
				logger.warn("WatchKey not recognized!!");
				return null;
			}
			
			List<WatchEvent<?>> lst = key.pollEvents();
			
			for (WatchEvent<?> event : lst) {
				WatchEvent.Kind kind = event.kind();
	
				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}
	
				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);
	
				// print out event
				logger.debug("{}: {}", event.kind().name(), child);
	
				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (kind == ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
						logger.warn("", x);
					}
				}
				allEvents.add(new EventPair(dir, ev));
			}
	
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);
			}
		}
		return allEvents;
	}

	void unregisterAll() {
		if (!keys.isEmpty()) {
			for (WatchKey k: keys.keySet()) {
				k.cancel();
			}
		}
	}
}