package tpl.prefs;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("core_preferences")
public class Preference {
	@Name @Column("pref_key") @ColDefine(notNull=true, update=false, width=200)
	private String key;
	@Column("pref_value") @ColDefine(type=ColType.TEXT)
	private String value;
	@Column("descriptions") @ColDefine(width=200)
	private String description;
	@Column("pref_ns") @ColDefine(update=false, width=200)
	private String namespace;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
