package net.techgy.idgenerator.dbbase;

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import net.techgy.idgenerator.IIDAssignment;
import net.techgy.idgenerator.dbbase.impl.IdAssignment;
import net.techgy.idgenerator.dbbase.impl.JdbcUtils;
import net.techgy.idgenerator.dbbase.impl.MysqlBaseIdMap;

public class GeneratorTest {

	private MysqlBaseIdMap mapper = new MysqlBaseIdMap();
	
	@Test
	public void testInsertIdAssinment() throws SQLException {
		IdAssignment ia = new IdAssignment();
		ia.setClientId("0");
		ia.setCreateDate(new Date());
		ia.setEntityId(IdAssignment.class.getName());
		ia.setId(-11);
		ia.setIdInitValue(1l);
		ia.setIdStepLen(1);
		ia.setIdValue(1l);
		ia.setIdValueType("string");
		ia.setPrefixValue(001l);
		ia.setPrefixValueLen(3);
		ia.setStatu("using");
		ia.setTableName("");
		ia.setUpdateDate(new Date());
		JdbcUtils.getIns().insert(ia);	
	}
	
	
	@Test
	public void testGetIdAssinment() throws Exception {
		String sql = " select * from t_id_assignment where statu='using' ";
		IdAssignment ia = JdbcUtils.getIns().findSimpleRefResult(sql, null, IdAssignment.class);
		System.out.println(ia.getCreateDate());
	}
	
	@Test
	public void testLoadAllUsingAssigment() {
		Set<IIDAssignment> set = mapper.loadAllUsingAssigment();
		Assert.assertNotNull(set);
	}
	
	@Test
	public void testMaxPrefixValue() {
		long val = mapper.maxPrefixValue("0", IdAssignment.class.getName());
		Assert.assertTrue(val>0);
	}
	
	@Test
	public void testIsExistPrefix() {
		boolean val = mapper.isExistPrefix("0", IdAssignment.class.getName());
		Assert.assertTrue(val);
	}
	
	@Test
	public void testUpdatePrefixAssignmentConfig() {
		IdAssignment ia = new IdAssignment();
		ia.setClientId("0");
		ia.setCreateDate(new Date());
		ia.setEntityId(IdAssignment.class.getName());
		ia.setId(-11);
		ia.setIdInitValue(1l);
		ia.setIdStepLen(1);
		ia.setIdValue(1l);
		ia.setIdValueType("string");
		ia.setPrefixValue(001l);
		ia.setPrefixValueLen(3);
		ia.setStatu("using");
		ia.setTableName("");
		ia.setUpdateDate(new Date());

		int val = mapper.updatePrefixAssignmentConfig(ia);
		Assert.assertTrue(val>0);
	}
	
	@Test
	public void testUpdateIdAssignmentValue() {
		IdAssignment ia = new IdAssignment();
		ia.setClientId("0");
		ia.setCreateDate(new Date());
		ia.setEntityId(IdAssignment.class.getName());
		ia.setId(-11);
		ia.setIdInitValue(1l);
		ia.setIdStepLen(1);
		ia.setIdValue(1l);
		ia.setIdValueType("string");
		ia.setPrefixValue(001l);
		ia.setPrefixValueLen(3);
		ia.setStatu("using");
		ia.setTableName("");
		ia.setUpdateDate(new Date());
		
		int val = mapper.updateIdAssignmentValue(ia,2);
		Assert.assertTrue(val>0);
	}
	
	@Test
	public void testUpdateIdAssignmentStatu() {
		IdAssignment ia = new IdAssignment();
		ia.setClientId("0");
		ia.setCreateDate(new Date());
		ia.setEntityId(IdAssignment.class.getName());
		ia.setId(-11);
		ia.setIdInitValue(1l);
		ia.setIdStepLen(1);
		ia.setIdValue(1l);
		ia.setIdValueType("string");
		ia.setPrefixValue(001l);
		ia.setPrefixValueLen(3);
		ia.setStatu("using");
		ia.setTableName("");
		ia.setUpdateDate(new Date());
		int val = mapper.updateIdAssignmentStatu(ia);
		Assert.assertTrue(val>0);
	}
	
}
