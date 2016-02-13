package tpl.ds;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("core_datasources")
public class DataSourceDef {
	public static final Integer STATUS_ACTIVATED = 1;
	
	@Name @Column("ds_name") @ColDefine(notNull=true, update=false, width=200)
	private String name;
	@Column("ds_url") @ColDefine(type=ColType.TEXT)
	private String url;
	@Column("ds_user") @ColDefine(width=50)
	private String user;
	@Column("ds_password") @ColDefine(width=200)
	private String password;
	@Column("descriptions") @ColDefine(width=200)
	private String description;
	@Column("descriptions") @ColDefine(notNull=true)
	private Integer status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
