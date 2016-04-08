package tpl.entities;

import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("core_entity_definitions")
public class EntityDef {
	@Name @Column("entity_name") @ColDefine(notNull=true, update=false, width=200)
	private String name;
	@Column("display_name") @ColDefine(notNull=true, width=200)
	private String displayName;
	@Column("table_name") @ColDefine(notNull=true, update=false, width=200)
	private String tableName;
	@Column("descriptions") @ColDefine(width=200)
	private String description;
	@Column("applied_status")
	private int status;
	@Many(target=PropertyDef.class, key="name", field="entityName")
	private List<PropertyDef> properties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<PropertyDef> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyDef> properties) {
		this.properties = properties;
	}
}
