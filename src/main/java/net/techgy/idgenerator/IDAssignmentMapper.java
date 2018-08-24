package net.techgy.idgenerator;

import java.util.List;
import java.util.Set;

public interface IDAssignmentMapper {

	int insert(IIDAssignment record);

	Set<IIDAssignment> loadAllUsingAssigment();
	
    long maxPrefixValue(String clientId,String entitId);
	
	boolean isExistPrefix(String clientId,String entitId);
	
	int updatePrefixAssignmentConfig(IIDAssignment pa);
	
	int updateIdAssignmentValue(IIDAssignment pa,long idNum);
	
	int updateIdAssignmentStatu(IIDAssignment pa);
	
	List<String> getClient();
	
	int resetID( String entityId, long idNum);
}
