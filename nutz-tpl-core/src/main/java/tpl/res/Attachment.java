package tpl.res;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("core_attachments")
public class Attachment {
	@Name @Column("attachment_id") @ColDefine(notNull=true, update=false, width=200)
	private String id;
	@Column("disk_file_name") @ColDefine(notNull=true, update=false, width=200)
	private String name;
	@Column("relative_path") @ColDefine(notNull=true, update=false, width=200)
	private String path;
	@Column("status_flags") @ColDefine(notNull=true)
	private long flags;
	@Column("original_file_name") @ColDefine(notNull=true, update=false, width=200)
	private String originName;
	@Column("mime_type") @ColDefine(notNull=true, update=false, width=200)
	private String mimeType;
	@Column("create_time") @ColDefine(notNull=true, update=false)
	private Date createTime;
	@Column("create_user") @ColDefine(notNull=true, update=false)
	private String createUser;
	@Column("file_size") @ColDefine(notNull=true, update=false)
	private Long fileSize;
	@Column("modify_time")
	private Date modifyTime;
	@Column("modify_user") @ColDefine(width=200)
	private String modifyUser;
	@Column("module_name") @ColDefine(width=200)
	private String module;
	@Column("referred_by") @ColDefine(width=200)
	private String referredBy;
	@Column("referred_name") @ColDefine(width=200)
	private String referredName;
	@Column("referred_id") @ColDefine(width=200)
	private Integer referredId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	public String getOriginName() {
		return originName;
	}

	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public String getReferredName() {
		return referredName;
	}

	public void setReferredName(String referredName) {
		this.referredName = referredName;
	}

	public Integer getReferredId() {
		return referredId;
	}

	public void setReferredId(Integer referredId) {
		this.referredId = referredId;
	}
}
