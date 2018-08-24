package net.techgy.idgenerator.dbbase.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.techgy.idgenerator.IIDGenerator;

public class JdbcUtils {  
	  
    // 定义数据库的用户名  
    private final String USERNAME = "root";  
    // 定义数据库的密码  
    private final String PASSWORD = "Admin112233^&*";  
    // 定义数据库的驱动信息  
    private final String DRIVER = "com.mysql.jdbc.Driver";  
    // 定义访问数据库的地址  
    private final String URL = "jdbc:mysql://localhost:3306/electric";  
  
    // 定义访问数据库的连接  
    private Connection connection;  
  
    private static final JdbcUtils ins = new JdbcUtils();
    public static JdbcUtils getIns() {
    	ins.getConnection();
    	return ins;
    }
    
    private JdbcUtils() {  
        // TODO Auto-generated constructor stub  
        try {  
            Class.forName(DRIVER);  
            System.out.println("注册驱动成功！！");  
        } catch (ClassNotFoundException e) {  
            // TODO Auto-generated catch block  
            System.out.println("注册驱动失败！！");  
        }  
  
    }  
  
    // 定义获得数据库的连接  
    public Connection getConnection() {  
        try {  
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);  
        } catch (Exception e) {  
            // TODO: handle exception  
            System.out.println("Connection exception !");  
        }  
        return connection;
    }  
  
    /** 
     * 完成对数据库标的增加删除和修改的操作 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public int updateByPreparedStatement(String sql, List<Object> params)  
            throws SQLException {  
        boolean flag = false;  
        int result = -1;// 表示当用户执行增加删除和修改的操作影响的行数  
        int index = 0; // 表示 占位符 ，从1开始  
        PreparedStatement pstmt = connection.prepareStatement(sql);  
        if ( params != null ) {  
            for (Iterator<Object> ite=params.iterator(); ite.hasNext(); ) {  
                pstmt.setObject(++index, ite.next()); 
            }  
        }  
  
        result = pstmt.executeUpdate();   
        return result;  
  
    }  
    
    public int insert(Object obj)  throws SQLException { 
    	Class cls = obj.getClass();
        String tableName = getTableName(cls);
        
        StringBuffer sb = new StringBuffer();
        sb.append("insert into ").append(tableName).append("( ");
        
        StringBuffer sbv = new StringBuffer();
        sbv.append(" values (");
        
        List<Object> values = new ArrayList<Object>();
        
        Field[] fs = cls.getDeclaredFields();
        int indx = 0;
        for(Field f : fs) {
        	if(f.isAnnotationPresent(Column.class)){
        		Column c = f.getAnnotation(Column.class);
        		sb.append(c.value()).append(",");
        		try {
        			f.setAccessible(true);
					values.add(indx++, f.get(obj));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
        		sbv.append("?,");
        	}
        }
        sbv.delete(sbv.length()-1, sbv.length());
        sb.delete(sb.length()-1, sb.length());
        sb.append(" ) ");
        sbv.append(" ) ");
        sb.append(sbv.toString());
    	
    	boolean flag = false;  
        int result = -1;// 表示当用户执行增加删除和修改的操作影响的行数  
        int index = 0; // 表示 占位符 ，从1开始  
        PreparedStatement pstmt = connection.prepareStatement(sb.toString());  
        if ( values != null ) {  
            for (Iterator<Object> ite = values.iterator(); ite.hasNext(); ) {  
                pstmt.setObject(++index, ite.next()); 
            }  
        }  
  
        result = pstmt.executeUpdate();  
        return result;
  
    }  
  
    private String getTableName(Class<? extends Object> cls) {
		if(cls.isAnnotationPresent(Table.class)) {
			Table an = cls.getAnnotation(Table.class);
			return an.value();
		}else {
			return cls.getName();
		}
	}

	/** 
     * 查询返回单条记录 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public  Map<String, Object> findSimpleResult(String sql, List<Object> params)  
            throws SQLException {  
        Map<String, Object> map = new HashMap<String, Object>();  
        PreparedStatement pstmt = connection.prepareStatement(sql);  
        int index = 1;  
        if (params != null && !params.isEmpty()) {  
            for (int i = 0; i < params.size(); i++) {  
                pstmt.setObject(index++, params.get(i));  
            }  
        }  
        ResultSet resultSet = pstmt.executeQuery();
  
        ResultSetMetaData metaData = pstmt.getMetaData();
        int cols_len = metaData.getColumnCount();
  
        while (resultSet.next()) {  
            for (int i = 0; i < cols_len; i++) {  
                String col_name = metaData.getColumnName(i + 1);
                Object col_value = resultSet.getObject(col_name);  
                map.put(col_name, col_value);  
            }  
  
        }  
  
        return map;  
    }  
  
    /** 
     * 查询返回多条记录 
     *  
     * @param sql 
     * @param params 
     * @return 
     * @throws SQLException 
     */  
    public List<Map<String, Object>> findMoreResult(String sql,  
            List<Object> params) throws SQLException {  
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();  
        PreparedStatement pstmt = connection.prepareStatement(sql);  
        int index = 1; // 表示占位符  
        if (params != null && !params.isEmpty()) {  
            for (int i = 0; i < params.size(); i++) {  
                pstmt.setObject(index++, params.get(i));  
            }  
        }  
        ResultSet resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
  
        while (resultSet.next()) {  
            Map<String, Object> map = new HashMap<String, Object>();  
            int cols_len = metaData.getColumnCount();
            for (int i = 0; i < cols_len; i++) {  
                String col_name = metaData.getColumnName(i + 1);
                
                Object col_value = resultSet.getObject(col_name);
                if (col_value == null) {  
                    col_value = "";  
                }  
  
                map.put(col_name, col_value);  
            }  
            list.add(map);  
        }  
  
        return list;  
  
    }  
  
    /** 
     * 查询返回单个JavaBean(使用java反射机制) 
     *  
     * @param sql 
     * @param params 
     * @param cls 
     * @return 
     * @throws Exception 
     */  
    public <T> T findSimpleRefResult(String sql, List<Object> params,  
            Class<T> cls) throws Exception {  
        T resultObject = null;  
        int index = 1;
        PreparedStatement pstmt = connection.prepareStatement(sql);  
        if (params != null && !params.isEmpty()) {  
            for (int i = 0; i < params.size(); i++) {  
                pstmt.setObject(index++, params.get(i)); 
            }  
        }  
        ResultSet resultSet = pstmt.executeQuery(); 
  
        ResultSetMetaData metaData = resultSet.getMetaData(); 
        int cols_len = metaData.getColumnCount(); 
        while (resultSet.next()) {
            resultObject = cls.newInstance();
            for (int i = 0; i < cols_len; i++) {
                String col_name = metaData.getColumnName(i + 1);
                Field field = getField(cls,col_name);
                if(field == null) {
                	continue;
                }
                
                Object col_value = getValue(resultSet,col_name,field.getType());  
                 
                field.setAccessible(true);
                field.set(resultObject, col_value);
            }  
  
        } 
        return resultObject;  
    }  
    
    
    private Object getValue(ResultSet resultSet, String col_name, Class<?> type) throws SQLException {
		
    	Object value = null;
		if(type == Double.TYPE|| type == Double.class) {
			value = resultSet.getString(col_name);
		}else if(type == Float.TYPE|| type == Float.class) {
			value = resultSet.getFloat(col_name);
		}else if(type == Long.TYPE || type == Long.class) {
			value = resultSet.getLong(col_name);
		}else if(type == Integer.TYPE|| type == Integer.class) {
			value = resultSet.getInt(col_name);
		}else if(type == Short.TYPE|| type == Short.class) {
			value = resultSet.getShort(col_name);
		}else if(type == Byte.TYPE|| type == Byte.class) {
			value = resultSet.getByte(col_name);
		}else if(type == Boolean.TYPE|| type == Boolean.class) {
			value = resultSet.getBoolean(col_name);
		}else if(type == Character.TYPE|| type == Character.class) {
			value = resultSet.getObject(col_name);
		}else if(type == Void.TYPE|| type == Void.class) {
			value = null;
		}else if(type == String.class) {
			value = resultSet.getString(col_name);
		}else if(type == BigDecimal.class) {
			value = resultSet.getBigDecimal(col_name);
		}else if(type == java.util.Date.class) {
			value = resultSet.getDate(col_name);
		}else {
			value = resultSet.getObject(col_name);
		}
		return value;
	}

	private Field getField(Class cls,String col_name) {	
    	Field[] fs = cls.getDeclaredFields();
        int indx = 0;
        for(Field f : fs) {
        	if(f.isAnnotationPresent(Column.class)){
        		Column c = f.getAnnotation(Column.class);
        		if(c.value().equals(col_name)) {
        			return f;
        		}
        	}
        } 
		return null;
	}

	/** 查询返回多个JavaBean(通过java反射机制) 
     * @param sql 
     * @param params 
     * @param cls 
     * @return 
     * @throws Exception 
     */  
    public <T> Set<T> findMoreRefResult(String sql, List<Object> params,  
            Class<T> cls) throws Exception {  
        Set<T> list = new HashSet<T>();  
        int index = 1; //占位符  
        PreparedStatement pstmt = connection.prepareStatement(sql);  
        if (params != null && !params.isEmpty()) {  
            for (int i = 0; i < params.size(); i++) {  
                pstmt.setObject(index++, params.get(i));  
            }  
        }  
        ResultSet resultSet = pstmt.executeQuery(); // 返回查询结果集合  
  
        ResultSetMetaData metaData = resultSet.getMetaData(); // 返回列的信息  
        int cols_len = metaData.getColumnCount(); // 结果集中总的列数  
        while (resultSet.next()) {  
            // 通过反射机制创建一个java实例  
            T resultObject = cls.newInstance();  
            for (int i = 0; i < cols_len; i++) {  
                String col_name = metaData.getColumnName(i + 1); // 获得第i列的名称  
                Field field = getField(cls,col_name);
                if(field == null) {
                	continue;
                }
                Object col_value = resultSet.getObject(col_name); // 获得第i列的内容   
                  
                field.setAccessible(true); // 打开JavaBean的访问private权限  
                field.set(resultObject, col_value);  
            }  
            list.add(resultObject);  
  
        }  
  
        return list;  
    }  
      
    /**关闭数据库访问 
     * @throws SQLException 
     */  
    public void releaseConn() throws SQLException{  
        /*if (resultSet!=null) {  
            resultSet.close();  
        }  
        if (pstmt!=null) {  
            pstmt.close();  
        }  */
        if (connection!=null) {  
            connection.close();  
        }  
    }  
  
      
}  
