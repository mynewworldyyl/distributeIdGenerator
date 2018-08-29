package net.techgy.idgenerator;

import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class CacheBaseIDManager implements IIDGenerator{

	private Map<String,Queue<Object>> idcacheMap =  new ConcurrentHashMap<String,Queue<Object>>();
	
	private Map<String,IDStrategy> cacheSizeMap = new ConcurrentHashMap<String,IDStrategy>();
	
	private int  maxCacheSize=10;
	
	private IIDGenerator generator;
	
	public CacheBaseIDManager(IIDGenerator generator) {
		this.generator = generator;
	}

	@Override
	public String getStringId(Class<?> entityCls,String clientId, String...prefixStrs) {
		return this.getStringIds(entityCls,clientId, 1, 0, prefixStrs).iterator().next();
	}
	

	public Set<String> getStringIds(Class<?> entityCls,String clientId, int idNum,String...prefixStrs) {
		Set<String> ids = this.getStringIds(entityCls,clientId, idNum, 0, prefixStrs);
		return ids;
	}
	

	@Override
	public String getStringId(Class<?> entityCls,String clientId, int idLen, String... prefixStrs) {
		return this.getStringIds(entityCls,clientId, 1, idLen, prefixStrs).iterator().next();
	}


	public Set<String> getStringIds(Class<?> entityCls,String clientId, int idNum, int idLen, String... prefixStrs) {
		
		String entityId = ClassScannerUtils.getInstance().getEntityID(entityCls);
		String key = this.getKey(entityId, clientId);
		IDStrategy idstrategy = this.cacheSizeMap.get(entityId);
		if(idstrategy == null) {
			throw new IDGeneratorException("NotKnowException",entityId);
		}
		int cacheSize = idstrategy.cacheSize();
		Queue<Object> q = this.idcacheMap.get(key);
		cacheSize = cacheSize > this.maxCacheSize ? this.maxCacheSize:cacheSize;
		if(q == null) {
			q = new LinkedBlockingQueue<Object>();
			this.idcacheMap.put(key, q);
		}		
		if(q.size() < idNum) {
			int needIdNum =  cacheSize < 1 ? 1: cacheSize;
			needIdNum = needIdNum < idNum ? idNum : needIdNum;
			Set<String> cacheIds = null;
			cacheIds = this.generator.getStringIds(entityCls,clientId,needIdNum,idLen,prefixStrs);
			q.addAll(cacheIds);
		}
		Set<String> ids = new HashSet<String>();
	    for(;idNum >0;idNum--) {
	    	ids.add((String)q.poll());
	    }
		return ids;
	}

	@Override
	public <T extends Number> T getNumId(Class<?> entityCls,String clientId) {
		return (T)this.getNumIds(entityCls,clientId,1).iterator().next();
	}

	@Override
	public <T extends Number> T getNumId(Class<?> entityCls) {
		return getNumId(entityCls,null);
	}

	@Override
	public IIDAssignment createNewPrefixIDAssignment(IIDAssignment pa) {
		return generator.createNewPrefixIDAssignment(pa);
	}


	/*@Override
	public String getStringId(Class<?> entityCls, String... prefixStrs) {
		return this.getStringId(entityCls, null, prefixStrs);
	}*/

	@Override
	public String getStringId(Class<?> entityCls, int idLen, String... prefixStrs) {
		return this.getStringId(entityCls, null,idLen, prefixStrs);
	}

	@Override
	public <T extends Number> Set<T> getNumIds(Class<?> entityCls,String clientId,int idNum) {
		
		String entityId = ClassScannerUtils.getInstance().getEntityID(entityCls);
		IDStrategy idstrategy = this.cacheSizeMap.get(entityId);
		Integer cacheSize1 = idstrategy.cacheSize();
		String key = this.getKey(entityId, clientId);
		Queue<Object> q = this.idcacheMap.get(key);
		int cacheSize = cacheSize1 == null ? 0 : cacheSize1;
		if(q == null) {
			cacheSize = cacheSize > this.maxCacheSize ? cacheSize:  this.maxCacheSize;
			q = new LinkedBlockingQueue<Object>();
			this.idcacheMap.put(key, q);
		}		
		if(q.size() < idNum) {
			cacheSize = cacheSize > this.maxCacheSize ? this.maxCacheSize:cacheSize;
			int needIdNum =  cacheSize < 1 ? 1: cacheSize;
			needIdNum = needIdNum < idNum ? idNum : needIdNum;
			Set<T> cacheIds = null;
			cacheIds =  this.generator.getNumIds(entityCls,clientId, needIdNum);
			q.addAll(cacheIds);
		}
		Set<T> ids = new HashSet<T>();
	    for(;idNum >0;idNum--) {
	    	ids.add((T)q.poll());
	    }
		return ids;
	}
	
	private String getKey(String entityId,String clientId) {
		String key = null;
		if(clientId == null || "".equals(clientId.trim())) {
			key = entityId+"_0";
		}else {
			key = entityId+"_" + clientId;
		}
		return key;
	}

	public void resetID() {
		
	}
}
