package tpl.nutz.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RawRunnable {
	public abstract void run(String path, Map<String, Object> context,
			HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException;
}
