package tpl.nutz.web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JsonRunnable {
	public abstract Object run(String path, Map<String, Object> context,
			HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException;
}
