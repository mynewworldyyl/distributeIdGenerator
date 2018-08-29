package net.techgy.idgenerator.dbbase;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import net.techgy.idgenerator.BaseIDGenerator;
import net.techgy.idgenerator.IIDGenerator;
import net.techgy.idgenerator.dbbase.impl.MysqlBaseIdMap;

public class MysqlBaseIdGeneratorTest {

	private MysqlBaseIdMap mapper = new MysqlBaseIdMap();
	
	@Test
	public void testInsertIdAssinment() throws SQLException {
		IIDGenerator gen = new BaseIDGenerator(mapper,"net.techgy",true);
		String val = gen.getStringId(TestEntity.class,"0","test");
		Assert.assertNotNull(val);
	}
	
}
