package net.techgy.idgenerator.dbbase.impl;

import java.util.Date;

import net.techgy.idgenerator.IIDAssignment;

@Table("t_test_idassignment")
public class IdAssignment implements IIDAssignment {

	@Column("id")
	private Integer id;
	
	@Column("CLIENT_ID")
	private String clientId;
	
	@Column("ENTITY_ID")
	private String entityId;
	
	@Column("PREFIX_VALUE")
	private Long prefixValue;
	
	@Column("PREFIX_VALUE_LEN")
	private Integer prefixValueLen;
	
	@Column("ID_VALUE_TYPE")
	private String idValueType;
	
	@Column("ID_INIT_VALUE")
	private Long idInitValue;
	
	@Column("ID_VALUE")
	private Long idValue;
	
	@Column("ID_STEP_LEN")
	private Integer idStepLen;
	
	@Column("STATU")
	private String statu;
	
	@Column("TABLE_NAME")
	private String tableName;
	
	@Column("CREATE_DATE")
	private Date createDate;
	
	@Column("UPDATE_DATE")
	private Date updateDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public Long getPrefixValue() {
		return prefixValue;
	}
	public void setPrefixValue(Long prefixValue) {
		this.prefixValue = prefixValue;
	}
	public Integer getPrefixValueLen() {
		return prefixValueLen;
	}
	public void setPrefixValueLen(Integer prefixValueLen) {
		this.prefixValueLen = prefixValueLen;
	}
	public String getIdValueType() {
		return idValueType;
	}
	public void setIdValueType(String idValueType) {
		this.idValueType = idValueType;
	}
	public Long getIdInitValue() {
		return idInitValue;
	}
	public void setIdInitValue(Long idInitValue) {
		this.idInitValue = idInitValue;
	}
	public Long getIdValue() {
		return idValue;
	}
	public void setIdValue(Long idValue) {
		this.idValue = idValue;
	}
	public Integer getIdStepLen() {
		return idStepLen;
	}
	public void setIdStepLen(Integer idStepLen) {
		this.idStepLen = idStepLen;
	}
	public String getStatu() {
		return statu;
	}
	public void setStatu(String statu) {
		this.statu = statu;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
