package tpl.entities;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

@Table("core_property_definitions")
@PK({"name", "entityName"})
public class PropertyDef {
	@Column("property_name") @ColDefine(notNull=true, update=false, width=200)
	private String name;
	@Column("entity_name") @ColDefine(notNull=true, update=false, width=200)
	private String entityName;
	@Column("value_type") @ColDefine(notNull=true, update=false, width=200)
	private ColType type;
	@Column("usable_status") @ColDefine(notNull=true, width=200)
	private int status;
	@Column("col_width") @ColDefine(notNull=true, width=200)
	private int width;
	@Column("col_precision") @ColDefine(notNull=true, width=200)
	private int precision;
	@Column("not_null") @ColDefine(notNull=true, width=200)
	private int notNull;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public ColType getType() {
		return type;
	}

	public void setType(ColType type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getNotNull() {
		return notNull;
	}

	public void setNotNull(int notNull) {
		this.notNull = notNull;
	}
}
