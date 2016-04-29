package tpl.entities;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

@Table("core_entity_datasources")
@PK({"dataSourceName", "entityName"})
public class EntityDataSource {
	@Column("ds_name") @ColDefine(notNull=true, update=false, width=200)
	private String dataSourceName;
	@Column("entity_name") @ColDefine(notNull=true, update=false, width=200)
	private String entityName;
	@Column("table_status") @ColDefine(notNull=true)
	private int tableStatus;

	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public int getTableStatus() {
		return tableStatus;
	}

	public void setTableStatus(int tableStatus) {
		this.tableStatus = tableStatus;
	}
}
