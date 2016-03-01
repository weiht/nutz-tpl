package tpl.entities;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Table;

@Table("core_entity_datasets")
@PK({"dataSourceName", "entityName"})
public class EntityDataSource {
	@Column("ds_name") @ColDefine(notNull=true, update=false, width=200)
	private String dataSourceName;
	@Column("entity_name") @ColDefine(notNull=true, update=false, width=200)
	private String entityName;

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
}
