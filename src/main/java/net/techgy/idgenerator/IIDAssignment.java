package net.techgy.idgenerator;

import java.util.Date;

public interface IIDAssignment {
	
	public Integer getId();
	
	public void setId(Integer id);
	
	public String getClientId();
	
	public void setClientId(String clientId);

	public String getEntityId();
	
	public void setEntityId(String entityId);
	
	public Long getPrefixValue();
	
	public void setPrefixValue(Long prefixValue);
	
	public Integer getPrefixValueLen();
	
	public void setPrefixValueLen(Integer prefixValueLen);
	
	public String getIdValueType();
	
	public void setIdValueType(String idValueType);
	
	public Long getIdInitValue();
	
	public void setIdInitValue(Long idInitValue);
	
	public Long getIdValue();
	
	public void setIdValue(Long idValue);
	
	public Integer getIdStepLen();
	
	public void setIdStepLen(Integer idStepLen);
	
	public String getStatu();
	
	public void setStatu(String statu);
	
	public String getTableName();
	
	public void setTableName(String tableName);
	
	public Date getCreateDate();
	
	public void setCreateDate(Date createDate);
	
	public Date getUpdateDate();

	public void setUpdateDate(Date updateDate);
}
