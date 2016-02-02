package tpl.nutz.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TplWebContextListener
implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(TplWebContextListener.class);
	
	private static final String PARAM_RUNNER_SEPARATOR = "[\\,,\\;,\\s]";
	private static final String PARAM_STARTUP_RUNNERS = "tpl-startup-runners";
	private static final String PARAM_SHUTDOWN_RUNNERS = "tpl-shutdown-runners";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String runners = sce.getServletContext().getInitParameter(PARAM_STARTUP_RUNNERS);
		runRunners(runners);
	}

	private void runRunners(String runners) {
		if (runners != null && !runners.isEmpty()) {
			for (String r: runners.split(PARAM_RUNNER_SEPARATOR)) {
				run(r.trim());
			}
		}
	}
	
	private void run(String runner) {
		if (runner.isEmpty()) return;
		try {
			doRun(runner);
		} catch (Exception e) {
			logger.error("Error running runner: {}", runner, e);
		}
	}
	
	private void doRun(String runner) {
		Runnable r = TplJsonIocProvider.nutzIoc().get(Runnable.class, runner);
		if (r != null) {
			r.run();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		String runners = sce.getServletContext().getInitParameter(PARAM_SHUTDOWN_RUNNERS);
		runRunners(runners);
	}
}
