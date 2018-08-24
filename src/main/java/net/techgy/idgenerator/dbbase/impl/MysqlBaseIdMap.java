package net.techgy.idgenerator.dbbase.impl;

import java.util.List;
import java.util.Set;

import net.techgy.idgenerator.IDAssignmentMapper;
import net.techgy.idgenerator.IIDAssignment;

public class MysqlBaseIdMap implements IDAssignmentMapper {

	@Override
	public int insert(IIDAssignment record) {
		
		return 0;
	}

	@Override
	public Set<IIDAssignment> loadAllUsingAssigment() {
		
		return null;
	}

	@Override
	public int maxPrefixValue(String clientId, String entitId) {
		
		return 0;
	}

	@Override
	public boolean isExistPrefix(String clientId, String entitId) {
		
		return false;
	}

	@Override
	public int updatePrefixAssignmentConfig(IIDAssignment pa) {
		
		return 0;
	}

	@Override
	public int updateIdAssignmentValue(IIDAssignment pa, long idNum) {
		
		return 0;
	}

	@Override
	public int updateIdAssignmentStatu(IIDAssignment pa) {
		
		return 0;
	}

	@Override
	public List<String> getClient() {
		
		return null;
	}

	@Override
	public int resetID(String entityId, long idNum) {
		
		return 0;
	}

}
