package net.techgy.idgenerator;

import java.util.Set;

/**
 * ID生成器，实现可以基于数据库表，文件，Redis等存储ID生成记录
 * @author yyl
 */
public interface IIDGenerator {
	/**
	 * @param entityCls IDStrategy注解的实体类
	 * @param prefixStrs　ID的前缀
	 * @return
	 */
	String getStringId(Class<?> entityCls, String...prefixStrs);
    
	/**
     * 
     * @param entityCls
     * @param idLen ID长度，包括前缀长度，如果前缀长度+ID长度《idLen，则以0补齐
     * @param prefixStrs
     * @return
     */
	String getStringId(Class<?> entityCls, int idLen, String...prefixStrs);
   
	
	/**
	 * @param entityCls IDStrategy注解的实体类
	 * @param prefixStrs　ID的前缀
	 * @param clientId　租户ID
	 * @return
	 */
	String getStringId(Class<?> entityCls,String clientId, String...prefixStrs);
    
	/**
     * 
     * @param entityCls
     * @param idLen ID长度，包括前缀长度，如果前缀长度+ID长度《idLen，则以0补齐
     * @param prefixStrs
     * * @param clientId　租户ID
     * @return
     */
	String getStringId(Class<?> entityCls,String clientId, int idLen, String...prefixStrs);
	
	/**
	 * 
	 * @param entityCls
	 * @param clientId
	 * @param idNum
	 * @param idLen
	 * @param prefixStrs
	 * @return
	 */
	Set<String> getStringIds(Class<?> entityCls,String clientId, int idNum, int idLen, String... prefixStrs);
	/**
	 * 重置ID配置
	 * 当resetByTimer=true时，此方法起作用
	 */
	void resetID();
	
	/**
	 * 生成数字型ID
	 * @param entityCls
	 * @return
	 */
	<T extends Number> T getNumId(Class<?> entityCls);
	<T extends Number> T getNumId(Class<?> entityCls,String clientId);
	<T extends Number> Set<T> getNumIds(Class<?> entityCls,String clientId,int idNum);
	
	IIDAssignment createNewPrefixIDAssignment(IIDAssignment pa);
	
    static final int DEFAULT_INIT_VALUE = 1;
	
    static final String RADIX_2 = "radix2";
    
	static final String RADIX_8 = "radix8";
	
	static final String RADIX_10 = "radix10";
	
	static final String RADIX_16 = "radix16";
	
	static final String TABLE_NAME = "tableName";
	
	public static enum IDType{
		
		STRING("string",128),
		BYTE("byte",8),
		SHORT("short",16),
		INT("int",32),
		LONG("long",64);
		
		private String type;
		private int bitLen;
		
		IDType(String type,int bitLen) {
			this.type=type;
			this.bitLen = bitLen;
		}

		public String getType() {
			return type;
		}

		public int getBitLen() {
			return bitLen;
		}
		
		public static int getBitLenByType(String type) {
			for(IDType t : values()) {
				if(type.equals(t.type)) {
					return t.bitLen;
				}
			}
			throw new IllegalArgumentException("ID type Name is invalid: " + type);
		}
	};
	
	  public static enum IDStatu{
			
		    USING("using"),
		    USED("USED");
		   
			private String statu;
			
			IDStatu(String statu) {
				this.statu=statu;
			}

			public String getStatu() {
				return statu;
			}
			
		};
		
		/**
		 * 0到16bit前缀作为32位整形ID分段占位符，剩余的作为ID分配值
		 * @author yyl
		 *
		 */
		public static enum IntegerMask32{
			
			MASK_INTEGER32_ZERO0(0XFFFFFFFF),
			MASK_INTEGER32_ZERO1(0X7FFFFFFF),
			MASK_INTEGER32_ZERO2(0X3FFFFFFF),
			MASK_INTEGER32_ZERO3(0X1FFFFFFF),
			MASK_INTEGER32_ZERO4(0X0FFFFFFF),
			MASK_INTEGER32_ZERO5(0X07FFFFFF),
			MASK_INTEGER32_ZERO6(0X03FFFFFF),
			MASK_INTEGER32_ZERO7(0X01FFFFFF),
			MASK_INTEGER32_ZERO8(0X00FFFFFF),
			MASK_INTEGER32_ZERO9(0X007FFFFF),
			MASK_INTEGER32_ZERO1O(0X003FFFFF),
			MASK_INTEGER32_ZERO11(0X001FFFFF),
			MASK_INTEGER32_ZERO12(0X000FFFFF),
			MASK_INTEGER32_ZERO13(0X0007FFFF),
			MASK_INTEGER32_ZERO14(0X0003FFFF),
			MASK_INTEGER32_ZERO15(0X0001FFFF),
			MASK_INTEGER32_ZERO16(0X0000FFFF);
			
			private int maskValue;	
			
			IntegerMask32(int maskValue) {
				this.maskValue= maskValue;
			}
			
			public static int getMask(int bitLen) {
				if(bitLen >= 0 && bitLen < values().length) {
					return values()[bitLen].maskValue;
				}else {
					throw new ArrayIndexOutOfBoundsException();
				}
			}
			public long getMaskValue() {
				return maskValue;
			}
		};
		
		/**
		 * 0到32bit前缀作为64位整形ID分段占位符，剩余的作为ID分配值
		 * @author yyl
		 *
		 */
	  public static enum LongMask64{
			
			MASK_LONG64_ZERO0(0XFFFFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO1(0X7FFFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO2(0X3FFFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO3(0X1FFFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO4(0X0FFFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO5(0X07FFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO6(0X03FFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO7(0X01FFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO8(0X00FFFFFFFFFFFFFFL),
			MASK_LONG64_ZERO9(0X007FFFFFFFFFFFFFL),
			MASK_LONG64_ZERO1O(0X003FFFFFFFFFFFFFL),
			MASK_LONG64_ZERO11(0X001FFFFFFFFFFFFFL),
			MASK_LONG64_ZERO12(0X000FFFFFFFFFFFFFL),
			MASK_LONG64_ZERO13(0X0007FFFFFFFFFFFFL),
			MASK_LONG64_ZERO14(0X0003FFFFFFFFFFFFL),
			MASK_LONG64_ZERO15(0X0001FFFFFFFFFFFFL),
			MASK_LONG64_ZERO16(0X0000FFFFFFFFFFFFL),
		    MASK_LONG64_ZERO17(0X00007FFFFFFFFFFFL),
			MASK_LONG64_ZERO18(0X00003FFFFFFFFFFFL),
			MASK_LONG64_ZERO19(0X00001FFFFFFFFFFFL),
			MASK_LONG64_ZERO20(0X00000FFFFFFFFFFFL),
			MASK_LONG64_ZERO21(0X000007FFFFFFFFFFL),
			MASK_LONG64_ZERO22(0X000003FFFFFFFFFFL),
		    MASK_LONG64_ZERO23(0X000001FFFFFFFFFFL),
			MASK_LONG64_ZERO24(0X000000FFFFFFFFFFL),
			MASK_LONG64_ZERO25(0X0000007FFFFFFFFL),
			MASK_LONG64_ZERO26(0X0000003FFFFFFFFFL),
			MASK_LONG64_ZERO27(0X0000001FFFFFFFFFL),
			MASK_LONG64_ZERO28(0X0000000FFFFFFFFFL),
		    MASK_LONG64_ZERO29(0X00000007FFFFFFFFL),
			MASK_LONG64_ZERO30(0X00000003FFFFFFFFL),
			MASK_LONG64_ZERO31(0X00000001FFFFFFFFL);

			private long maskValue;	
			
			LongMask64(long mask) {
				this.maskValue= mask;
			}
			
			public static long getMask(int bitLen) {
				if(bitLen >= 0 && bitLen < values().length) {
					return values()[bitLen].maskValue;
				}else {
					throw new ArrayIndexOutOfBoundsException();
				}
			}

			public long getMaskValue() {
				return maskValue;
			}
			
		};
}
