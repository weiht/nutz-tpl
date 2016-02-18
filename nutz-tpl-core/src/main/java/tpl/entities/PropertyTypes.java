package tpl.entities;

import org.nutz.dao.entity.annotation.ColType;

public class PropertyTypes {
	public static final ColType[] usableTypes = {
		ColType.VARCHAR, ColType.TEXT,
		ColType.INT, ColType.FLOAT,
		ColType.DATE, ColType.DATETIME,
		ColType.BINARY
	};
}
