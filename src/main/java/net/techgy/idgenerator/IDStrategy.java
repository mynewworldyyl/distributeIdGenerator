package net.techgy.idgenerator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 注解到类中，作为ID的配置类
 * 此类可以是对应于数据库的实体类，如JAP的@Table注解的类，或者任意Java类，如订单服务类，HTTP会话ID，登录令牌等需要唯一标识的场景
 * @author yyl
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface IDStrategy {
	 //cannot updatable, will get the server id by default
	 
	 // public String serverId() default "0";
	  /*
	   * cannot updatable, it will create a new entity if you update this value for specify Entity.
	   * default value: 
	   *     a. get this entiryId value annotation for this class;
	   *     b. if a is not specify, get the Table annotation name for JPA entity class;
	   *     c. if b not found, get entity class name.
	   *     类的ID，也就是此ID分属的Java类，如果没有配置此值，默认类的包名+类名，唯一区别此ID生成规则，不能与tableName同时存在
	   */
	  public String entiryId() default "";  
	  /**
	   * 字符串类型ID前缀长度，默认是16个字符
	   * 整数类型ID，此值无效
	   */
	  public int prefixValueLen() default 16;
	  /**
	   * ID类型，
	   * @see IIDGenerator．IDType
	   * @return
	   */
	  public String  idValueType() default "";
	  /**
	   * 两个相邻ID的差值，用于计算下一个ID值，一般为1
	   * @return
	   */
	  public int idStepLen() default 1;
	 
	  /**
	   * ID生成器可以一次性生成多少个ID放于缓存中，用于加快ID获取速度
	   * @return
	   */
	  public int cacheSize() default 1;
	  
	  /**
	   * 基于数据库表行ID，指ID所属表名，这样获取ID时，可以根行表名区别并获取ID
	   * @return
	   */
	  public String tableName() default "";
	  
	  
	  /**
	   * ID基数，用于转为字符串
	   * 
	   * @return
	   */
	  public String radix() default "radix10";
	  
	  /**
	   * 使用租户Id作为生成ID的前缀，用于多租户ID生成系统
	   * 如B2C系统中的商家1和商家2分属不同租户，并且1和2分虽作为基租户id，生成对应订单ID时，加入其租户ID作为前缀，这样就可以根据单号找到对应的商家
	   * @return
	   */
	  public boolean useClient() default false;	  
	  
	  /**
	   * ID是否根据时间做重置操作，如入库单号 20180808-001，这种以时间天作为前缀，后面跟一个编号，这种编号需要每天重置为1开始
	   * @return
	   */
	  public boolean resetByTimer() default false;
	  
	 
}
