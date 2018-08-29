package net.techgy.idgenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.techgy.idgenerator.dbbase.impl.IdAssignment;

public class BaseIDGenerator implements IIDGenerator {

	private final static Logger logger = LoggerFactory.getLogger(BaseIDGenerator.class);
	
	private Map<String,IIDAssignment> assignments = new ConcurrentHashMap<String,IIDAssignment>(100);
	
	//just use for performance to cache max value for bit length
	private Map<Integer,Long> maxValueMap = new HashMap<Integer,Long>();
	
	private Lock prefixIdLock = new ReentrantLock();
	
	private boolean isInit = false;
	
	private int oneReqMaxNum = 100;
	
	private boolean isInitConfig = false;
	
	//@Value("#{configProperties['entity.package']}")
	//@ZooKeeper("/WIAIR/electric/entity.package")
	private String entityPackage;
	
	//@Value("#{configProperties['reloadIdStragety']}")
	//@ZooKeeper("/WIAIR/electric/reloadIdStragety")
	private boolean reloadIdStragety=true;
	
	private IDAssignmentMapper idMapper;
	
	//use for IOC container to automatic inject the dependencies
	public BaseIDGenerator() {}
	
	public BaseIDGenerator(IDAssignmentMapper idMapper,String entityPackages,boolean reloadIdStragety) {
		this.idMapper = idMapper;
		this.entityPackage = entityPackages;
		this.reloadIdStragety = reloadIdStragety;
	}
	
	private void refleshCache() {
		logger.debug("init()");
		this.assignments.clear();
		Set<IIDAssignment> pre = this.idMapper.loadAllUsingAssigment();	
		for(IIDAssignment ida : pre) {
			this.assignments.put(ida.getEntityId()+"_" + ida.getClientId(), ida);
		}
		logger.debug("init() assignments size "+assignments.size());
	}
	
	private synchronized  void updateIdDatabase() {
		if(!isInitConfig && reloadIdStragety) {
			logger.debug("init()");
			initEntiryConfig();
			isInitConfig=true;
		}
		if(!isInit) {
			refleshCache();
			isInit = true;
		} 
	}
	
	/**
	 * do when the application start up
	 */
	private void initEntiryConfig() {
		
		if(entityPackage == null || this.entityPackage.trim().equals("")) {
			this.entityPackage="net.techgy";
		}
		Set<Class<?>> classes = ClassScannerUtils.getInstance().getClasses(entityPackage.split(","));
		for(Class<?> c : classes) {
			if(!c.isAnnotationPresent(IDStrategy.class)) {
				continue;
			}
			//System.out.println(c.getName());
			this.initIDAssignment(c);
		}
	
	}

	@Override
	public String getStringId(Class<?> entityCls,String clientId,int idLen,String...prefixStrs) {		
		return this.getStringIds(entityCls,clientId,1,idLen,prefixStrs).iterator().next();
	}
	

	@Override
	public String getStringId(Class<?> entityCls,String clientId,String...prefixStrs) {		
		return this.getStringId(entityCls,clientId,0,prefixStrs);
	}
	
	private String getIdKey(Class<?> entityCls,String clientId) {
		String idkey = ClassScannerUtils.getInstance().getEntityID(entityCls);	;
		if(entityCls.isAnnotationPresent(IDStrategy.class)) {
			IDStrategy s = entityCls.getAnnotation(IDStrategy.class);
			if(clientId != null && !"".equals(clientId.trim()) && s.useClient()) {
				idkey = idkey + "_" + clientId;
			}else {
				idkey = idkey + "_0";
			}
		}
		return idkey;
	}
	
	/*@Override
	public String getStringId(Class<?> entityCls, String... prefixStrs) {
		return this.getStringId(entityCls, "", prefixStrs);
	}*/

	@Override
	public String getStringId(Class<?> entityCls, int idLen, String... prefixStrs) {
		return this.getStringId(entityCls, "", idLen, prefixStrs);
	}

	@Override
	public Set<String> getStringIds(Class<?> entityCls,String clientId, int idNum, int idLen, String...prefixStrs) {
		this.doCheck(entityCls);
		//String entityId = IDUtils.getInstance().getEntityID(entityCls);		
		updateIdDatabase();
		
		String radix = RADIX_10;
		if(entityCls.isAnnotationPresent(IDStrategy.class)) {
			IDStrategy s = entityCls.getAnnotation(IDStrategy.class);
			radix = s.radix();
		}
		String idkey = this.getIdKey(entityCls,clientId);
		IIDAssignment oldPa = this.assignments.get(idkey);
		if(null == oldPa) {
			throw new IDGeneratorException("ID Entity not found for entity ID: " + idkey);
		}
		
		Set<String> ids = new TreeSet<String>();
		synchronized(oldPa) {
			oldPa = this.assignments.get(idkey);
			idNum = idNum > this.oneReqMaxNum ? this.oneReqMaxNum: idNum;			
			this.idMapper.updateIdAssignmentValue(oldPa,idNum*oldPa.getIdStepLen());			
			Long v = oldPa.getIdValue() ;
			for(int num = 0; num < idNum; num++) {
				StringBuffer sb = new StringBuffer();
				if(prefixStrs == null || prefixStrs.length < 1 || (prefixStrs.length == 1 && prefixStrs[0].trim().equals(""))) {
					/*try {
						sb.append(Integer.parseInt(this.serverId));
					} catch (NumberFormatException e) {
						sb.append(this.serverId.hashCode());
					}*/
				} else {
					for(String pre: prefixStrs) {
						sb.append(pre);
					}
				}
				String subStr = "";
				if(RADIX_2.equals(radix)) {
					subStr = Long.toBinaryString(v+oldPa.getIdStepLen()*num);
				}else if(RADIX_8.equals(radix)) {
					subStr = Long.toOctalString(v+oldPa.getIdStepLen()*num);
				}else if(RADIX_16.equals(radix)) {
					subStr = Long.toHexString(v+oldPa.getIdStepLen()*num);
				} else {
					subStr = new Long(v+oldPa.getIdStepLen()*num).toString();
				}
				if(idLen >0) {
					int subLen = idLen - sb.length();
					if(subLen <=0) {
						throw new IDGeneratorException("String prefix len have been max " + idLen); 
					}
					if(subStr.length() == subLen) {
						sb.append(subStr);
					}else if(subStr.length() > subLen) {
						sb.append(subStr.substring(subStr.length() - subLen, subStr.length()));
					} else {
						for(int count = subLen-subStr.length(); count>0; count--) {
							sb.append("0");
						}
						sb.append(subStr);
					}
					
				}else {
					sb.append(subStr);
				}
				ids.add(sb.toString());
			}
			oldPa.setIdValue(v+idNum*oldPa.getIdStepLen());
		}
		return ids;
	
	}

	public Set<String> getStringIds(Class<?> entityCls,String clientId, int idNum,String... prefixStrs) {		
		return this.getStringIds(entityCls,clientId, idNum, 0, prefixStrs);
	}	
	
	@Override
	public <T extends Number> T getNumId(Class<?> entityCls,String clientId) {
		this.doCheck(entityCls);
		return (T)this.getNumIds(entityCls,clientId,1).iterator().next();
	}

	@Override
	public <T extends Number> T getNumId(Class<?> entityCls) {
		return this.getNumId(entityCls, null);
	}

	private void checkReqNum(IIDAssignment pa, int idNum) {
		int maxbitLen = IIDGenerator.IDType.getBitLenByType(pa.getIdValueType());
		long maxValue =  this.getMaxValueByBitLen(maxbitLen-pa.getPrefixValueLen());
		long reqMaxValue = idNum * pa.getIdStepLen();
		if(reqMaxValue > maxValue) {
			throw new IDGeneratorException("The request max ID num not allow max to : " + (maxValue/pa.getIdStepLen()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Number> Set<T> getNumIds(Class<?> entityCls,String clientId,int idNum) {
		updateIdDatabase();
		this.doCheck(entityCls);
		//String entityId = IDUtils.getInstance().getEntityID(entityCls);	
		String idkey = this.getIdKey(entityCls,clientId);
		IIDAssignment oldPa = this.assignments.get(idkey);
		if(null == oldPa) {
			throw new IDGeneratorException("ID Entity not found for entity ID: " + idkey);
		}
		
		IIDAssignment newPa = null;
		Set<T> ids = new HashSet<T>();
		synchronized(oldPa) {
			//avoid the replace by the pre-operation
			oldPa = this.assignments.get(idkey);
			checkReqNum(oldPa,idNum);
			boolean f = this.isNeedIncrementPrefix(oldPa,idNum);
			if(f) {
				long securityNum = (this.getMaxValue(oldPa)-oldPa.getIdValue())/oldPa.getIdStepLen();
				if(securityNum>0) {
					ids.addAll((Set<T>)getSecurityNumIds(oldPa,securityNum));
				}				
				newPa = this.createNewPrefixIDAssignment(oldPa);
				ids.addAll((Set<T>)getSecurityNumIds(newPa,idNum - securityNum));	
			} else {
				newPa = oldPa;
				ids.addAll((Set<T>)getSecurityNumIds(newPa,idNum));	
			}
			if(newPa != oldPa) {
				this.assignments.remove(oldPa.getEntityId());
				this.assignments.put(newPa.getEntityId(), newPa);
			}		
		}		
		return ids;
	}
	
	private <T extends Number> Set<T> getSecurityNumIds(IIDAssignment pa,long idNum) {
		Set<T> ids = new HashSet<T>();
		this.idMapper.updateIdAssignmentValue(pa, pa.getIdStepLen()*idNum);
		for(int num = 0; num < idNum; num++) {
			T value = this.mergeNum(pa);
			pa.setIdValue(pa.getIdValue() + pa.getIdStepLen());
			ids.add(value);
		}
		return ids;
	}

	
	private void initIDAssignment(Class<?> entityCls) {
		
		IDStrategy s = entityCls.getAnnotation(IDStrategy.class);		
		IIDAssignment pa = new IdAssignment();
		//set entity ID
		String eid = s.entiryId();
		/*if(null == eid || "".equals(eid.trim())) {
			if(entityCls.isAnnotationPresent(Table.class)) {	
				Table t = entityCls.getAnnotation(Table.class);
				eid = t.name();
			}
		}*/
		if(null == eid || "".equals(eid.trim())) {
			eid = entityCls.getName();
		}
		pa.setEntityId(eid);
		
		//set prefix value length
		pa.setPrefixValueLen(s.prefixValueLen());
		
		//set id value length
		pa.setIdStepLen(s.idStepLen());
		
		pa.setIdInitValue(1l);
		pa.setIdValue(1l);
		pa.setTableName(s.tableName());
		String valueType = s.idValueType();
		if(valueType== null || "".equals(valueType.trim())) {
			valueType = ClassScannerUtils.getInstance().getEntityIDType(entityCls);
			if(valueType== null || "".equals(valueType.trim())) {
				//default ID value type is String
				valueType="string"; 
			}
		}
		pa.setIdValueType(valueType);
		pa.setStatu(IIDGenerator.IDStatu.USING.getStatu());
		pa.setClientId("0");
		
        if(s.useClient()) {
    		List<String>  l = this.idMapper.getClient();
    		for(String c : l) {
    			pa.setClientId(c);
    			if(idMapper.isExistPrefix(pa.getClientId(),pa.getEntityId())) {
    				this.idMapper.updatePrefixAssignmentConfig(pa);
    			} else {
    				pa.setPrefixValue(-1l);
    				this.createNewPrefixIDAssignment(pa);
    			}
    		}
        	
		} else {
			if(idMapper.isExistPrefix(pa.getClientId(),pa.getEntityId())) {
				this.idMapper.updatePrefixAssignmentConfig(pa);
			} else {
				pa.setPrefixValue(-1l);
				pa = this.createNewPrefixIDAssignment(pa);
			}
		}
	}

	public IIDAssignment createNewPrefixIDAssignment(IIDAssignment pa)
			throws IDGeneratorException {
		IdAssignment newPa = new IdAssignment();
		try {
			prefixIdLock.tryLock();
			Long value = this.idMapper.maxPrefixValue(pa.getClientId(),pa.getEntityId());
			if(value == null || value == 0) {
				if(pa.getPrefixValue() != -1) {
					throw new IDGeneratorException("Fail to query max prefix");
				}
				value=-1l;
			}
			if(pa.getPrefixValue() != -1) {
				pa.setStatu("used");
				this.idMapper.updateIdAssignmentStatu(pa);
			}
		
			newPa.setPrefixValue(value+1l);			
			newPa.setEntityId(pa.getEntityId());
			newPa.setIdInitValue(pa.getIdInitValue());
			newPa.setIdStepLen(pa.getIdStepLen());
			newPa.setIdValue(1l);
			newPa.setIdValueType(pa.getIdValueType());
			newPa.setPrefixValueLen(pa.getPrefixValueLen());
			newPa.setStatu("using");	
			newPa.setTableName(pa.getTableName());
			newPa.setClientId(pa.getClientId());
			this.idMapper.insert(newPa);
			
		}catch(Throwable e) {
			logger.error("", e);
		}finally{
			prefixIdLock.unlock();
		}
	
		return newPa;
	}
	

	private void doCheck(Class<?> entityCls) {
		if(!entityCls.isAnnotationPresent(IDStrategy.class)) {
			throw new IDGeneratorException("Class NOT a entiry class annotated with IDStrategy: "+ entityCls.getName());
		}
	}
	
	
	private boolean isNeedIncrementPrefix(IIDAssignment pa,int idNum) {
		if(pa.getIdValue() < 0) {
			throw new NumberFormatException("Value: " + pa.getIdValue() + "  cannot be negative number");
		}
		long maxValue = this.getMaxValue(pa);
		return maxValue - pa.getIdValue() < idNum * pa.getIdStepLen();
	}
	
	private long getMaxValueByBitLen(int bitLen) {
		Long v = this.maxValueMap.get(bitLen);
		if(v != null) {
			return v;
		}
		long val = 0;
		for(int bl = bitLen; bl > 0; bl--) {
			val= (val <<1) | 1;
		}
		this.maxValueMap.put(bitLen, val);
		return val;
	}
	
	private long getMaxValue(IIDAssignment pa) {
        int totalBitLen = IIDGenerator.IDType.getBitLenByType(pa.getIdValueType());
		long maxValue = this.getMaxValueByBitLen(totalBitLen - pa.getPrefixValueLen());
		return maxValue;
	}
	
	private void doCheck(long value, long prefix, byte preBitLen, long maxValue) {
		if(value < 0) {
			throw new NumberFormatException("Value: " + value + "  cannot be negative number");
		}
		if(prefix < 0) {
			throw new NumberFormatException("Prefix value : " + prefix + " have to be 1 or more than 1");
		}
		long maxPrefixValue = this.getMaxValueByBitLen(preBitLen);
		if(prefix > maxPrefixValue) {
			throw new NumberFormatException("Prefix Value:" + prefix + " is too max");
		}	
		if(value > maxValue) {
			throw new NumberFormatException("Value: " + value + "  is too max");
		}
	}
	
	private <T  extends Number> T mergeNum(IIDAssignment pa) {
		int totalbitLen = IIDGenerator.IDType.getBitLenByType(pa.getIdValueType());
		long prefixValue = pa.getPrefixValue() << (totalbitLen-pa.getPrefixValueLen().byteValue());
		
		long maxValue = this.getMaxValue(pa);
		this.doCheck(pa.getIdValue(), pa.getPrefixValue(), pa.getPrefixValueLen().byteValue(),maxValue);
		
		Long value = prefixValue | pa.getIdValue();
		T val  = null;
		if(totalbitLen == 8) {
			val = (T)Byte.valueOf(value.byteValue());
		}else if(totalbitLen == 16) {
			val = (T)Short.valueOf(value.shortValue());
		}else if(totalbitLen == 32) {
			val = (T)Integer.valueOf(value.intValue());
		}else if(totalbitLen == 64) {
			val = (T)Long.valueOf(value.longValue());
		}else {
			throw new IDGeneratorException("ID type Error : "+ pa.getIdValueType());
		}
		return val;
	}
	
	public void resetID() {
	
		Set<Class<?>> classes = ClassScannerUtils.getInstance().getClasses(
				entityPackage.split(","));
		for (Class<?> c : classes) {
			if (!c.isAnnotationPresent(IDStrategy.class)) {
				continue;
			}
			IDStrategy ids = c.getAnnotation(IDStrategy.class);
			if (!ids.resetByTimer()) {
				continue;
			}
			this.idMapper.resetID(c.getName(), 1l);
		}

		this.isInit = false;
		this.updateIdDatabase();
	}

	public int getOneReqMaxNum() {
		return oneReqMaxNum;
	}

	public void setOneReqMaxNum(int oneReqMaxNum) {
		this.oneReqMaxNum = oneReqMaxNum;
	}

	public String getEntityPackage() {
		return entityPackage;
	}

	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	public boolean isReloadIdStragety() {
		return reloadIdStragety;
	}

	public void setReloadIdStragety(boolean reloadIdStragety) {
		this.reloadIdStragety = reloadIdStragety;
	}

	public IDAssignmentMapper getIdMapper() {
		return idMapper;
	}

	public void setIdMapper(IDAssignmentMapper idMapper) {
		this.idMapper = idMapper;
	}
	
}
