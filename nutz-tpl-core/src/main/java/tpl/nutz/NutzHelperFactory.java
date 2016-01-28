package tpl.nutz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class NutzHelperFactory {
	public static Object aliasFor(Object bean) {
		return bean;
	}
	
	public static <T> List<T> insertToList(List<T> lst, int ix, T bean) {
		if (lst != null) {
			lst.add(ix, bean);
		}
		return lst;
	}
	
	public static Map<String, Object> addToMap(Map<String, Object> map, String key, Object value) {
		if (map != null) {
			map.put(key, value);
		}
		return map;
	}
	
	public static Object methodCall(Object bean, String method, Object[] args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (Method m: bean.getClass().getMethods()) {
			if (m.getName().equals(method)) {
				if (argsMatch(m.getParameterTypes(), args)) {
					m.invoke(bean, args);
					break;
				}
			}
		}
		return bean;
	}

	private static boolean argsMatch(Class<?>[] parameterTypes, Object[] args) {
		if (parameterTypes.length != args.length) return false;
		for (int i = 0; i < parameterTypes.length; i ++) {
			if (!argMatch(parameterTypes[i], args[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean argMatch(Class<?> argType, Object object) {
		if (object == null) {
			if (argType.isPrimitive()) return false;
			else return true;
		}
		Class<?> clazz = object.getClass();
		return argType.isAssignableFrom(clazz);
	}
}
