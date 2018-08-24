package net.techgy.idgenerator.dbbase.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.techgy.idgenerator.IDAssignmentMapper;
import net.techgy.idgenerator.IIDAssignment;

public class MysqlBaseIdMap implements IDAssignmentMapper {

	@Override
	public int insert(IIDAssignment record) {
		try {
			return JdbcUtils.getIns().insert(record);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Set<IIDAssignment> loadAllUsingAssigment() {
		String sql = " select * from t_test_idassignment where statu='using' ";
		
		Set<IIDAssignment> results = new HashSet<IIDAssignment>();
		
		Set<IdAssignment> sets = null;
		try {
			sets = JdbcUtils.getIns().findMoreRefResult(sql, null, IdAssignment.class);
		} catch (Exception e) {
			e.printStackTrace();
			return results;
		}
		
		for(Iterator<IdAssignment> ite = sets.iterator(); ite.hasNext();) {
			results.add(ite.next());
		}
		return results;
	}

	@Override
	public long maxPrefixValue(String clientId, String entitId) {
		String sql = " SELECT IFNULL(max(PREFIX_VALUE),0) as PREFIX_VALUE FROM t_test_idassignment where statu='using' and client_id=? and entity_id=?";
		List<Object> params = new ArrayList<Object>(2);
		params.add(clientId);
		params.add(entitId);
		try {
			Map<String, Object>  ia = JdbcUtils.getIns().findSimpleResult(sql, params);
			if( ia != null) {
				return Long.parseLong(ia.get("PREFIX_VALUE").toString());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean isExistPrefix(String clientId, String entitId) {
		String sql = " SELECT count(*) as CNT FROM t_test_idassignment where statu='using' and client_id=? and entity_id=?";
		List<Object> params = new ArrayList<Object>(2);
		params.add(clientId);
		params.add(entitId);
		try {
			Map<String, Object>  ia = JdbcUtils.getIns().findSimpleResult(sql, params);
			if( ia != null) {
				return Integer.parseInt(ia.get("CNT").toString()) > 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  false;
	}

	@Override
	public int updatePrefixAssignmentConfig(IIDAssignment pa) {
		String sql = " update t_test_idassignment " + 
				" SET PREFIX_VALUE_LEN = "+pa.getPrefixValue()+"," + 
				" ID_VALUE_TYPE = '"+pa.getIdValueType()+"'," + 
				" ID_STEP_LEN = "+pa.getIdStepLen()+"," + 
				" TABLE_NAME ='"+pa.getTableName()+"'," + 
				" UPDATE_DATE = now()" + 
				" where statu='using'" + 
				" and CLIENT_ID='" +pa.getClientId() + "'"+ 
				" and ENTITY_ID='" +pa.getEntityId() + "'";
		try {
			return JdbcUtils.getIns().updateByPreparedStatement(sql, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

	@Override
	public int updateIdAssignmentValue(IIDAssignment pa, long idNum) {
		
		String sql = " update t_test_idassignment SET " + 
				" ID_VALUE = ID_VALUE+"+idNum+"," + 
				" UPDATE_DATE = now()" + 
				" where statu='using'" + 
				" and CLIENT_ID='" +pa.getClientId() + "'"+ 
				" and ENTITY_ID='" +pa.getEntityId() + "'";
		try {
			return JdbcUtils.getIns().updateByPreparedStatement(sql, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

	@Override
	public int updateIdAssignmentStatu(IIDAssignment pa) {
		
		String sql = " update t_test_idassignment SET " + 
				" STATU = '"+pa.getStatu()+"'," + 
				" UPDATE_DATE = now()" + 
				" where statu='using'" + 
				" and CLIENT_ID='" +pa.getClientId() + "'"+ 
				" and ENTITY_ID='" +pa.getEntityId() + "'";
		try {
			return JdbcUtils.getIns().updateByPreparedStatement(sql, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

	@Override
	public List<String> getClient() {
		List<String> cs = new ArrayList<String>();
		cs.add("0");
		return cs;
	}

	@Override
	public int resetID(String entityId, long idNum) {
		
		return 0;
	}
	
	

}
