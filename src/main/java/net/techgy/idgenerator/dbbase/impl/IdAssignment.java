package net.techgy.idgenerator.dbbase.impl;

import java.util.Date;

import net.techgy.idgenerator.IIDAssignment;

public class IdAssignment implements IIDAssignment {

	private Integer id;
	private String clientId;
	private String entityId;
	private Long prefixValue;
	private Integer prefixValueLen;
	private String idValueType;
	private Long idInitValue;
	private Long idValue;
	private Integer idStepLen;
	private String statu;
	private String tableName;
	private Date createDate;
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
