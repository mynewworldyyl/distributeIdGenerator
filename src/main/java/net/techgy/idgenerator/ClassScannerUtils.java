package net.techgy.idgenerator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.Id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassScannerUtils {

	private final static Logger logger = LoggerFactory.getLogger(ClassScannerUtils.class);
	
	private static final ClassScannerUtils instance = new ClassScannerUtils();
	private ClassScannerUtils() {
		
	}
	
	public static ClassScannerUtils getInstance() {
		return instance;
	}
	
	public String getSpecLenAsString(int value, int num) {
		String val = String.valueOf(value);	
		int len = val.length();
	    if(len == num) {
	    	return  val;
	    }
	    if(len > num) {
	    	return val.substring(len-num, len);
	    }
	    
	    int divLen = num -len;
	    String preZero = "";
	    for(; divLen > 0; divLen--) {
	    	preZero+="0";
	    }
	    val = preZero+val;
		return val;
	}
	
	public String getEntityID(Class<?> entityCls) {
		if(!entityCls.isAnnotationPresent(IDStrategy.class)) {
			return null;
		}
		IDStrategy s = entityCls.getAnnotation(IDStrategy.class);
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
		return eid;
	}
	
	public String getEntityIDType(Class<?> entityCls) {
		if(entityCls == Object.class || entityCls == null) {
			return null;
		}
		
		String type = null;
		try {
			//声明为id的字段，默认为实体的ID
			Field idField = entityCls.getDeclaredField("id");
			if(null != idField) {
				type = this.getIDTypeString(idField.getType());
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			type = this.getEntityIDType(entityCls.getSuperclass());
		}
		
		if(type == null || "".equals(type.trim())) {
			Field[] fs = entityCls.getDeclaredFields();
			for(Field f : fs) {
				if(f.isAnnotationPresent(Id.class)) {
					type = this.getIDTypeString(f.getType());
				}
			}
		}
		return type;
	}
	
	public String getIDTypeString(Class<?> type) {
		String value = null;
		if(type == Double.TYPE|| type == Double.class) {
			value = IIDGenerator.IDType.LONG.getType();
		}else if(type == Float.TYPE|| type == Float.class) {
			value = IIDGenerator.IDType.LONG.getType();
		}else if(type == Long.TYPE || type == Long.class) {
			value = IIDGenerator.IDType.LONG.getType();
		}else if(type == Integer.TYPE|| type == Integer.class) {
			value = IIDGenerator.IDType.INT.getType();
		}else if(type == Short.TYPE|| type == Short.class) {
			value = IIDGenerator.IDType.SHORT.getType();
		}else if(type == Byte.TYPE|| type == Byte.class) {
			value = IIDGenerator.IDType.BYTE.getType();
		}else if(type == Boolean.TYPE|| type == Boolean.class) {
			value = null;
		}else if(type == Character.TYPE|| type == Character.class) {
			value = null;
		}else if(type == Void.TYPE|| type == Void.class) {
			value = null;
		}else if(type == String.class) {
			value = IIDGenerator.IDType.STRING.getType();
		}else if(type == BigDecimal.class) {
			value = IIDGenerator.IDType.LONG.getType();
		}
		return value;
	}
	
	public Set<Class<?>> getClasses(String pack) {  
		  
        // 第一个class类的集合  
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();  
        // 是否循环迭代  
        boolean recursive = true;  
        // 获取包的名字 并进行替换  
        String packageName = pack;  
        String packageDirName = packageName.replace('.', '/');  
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things  
        Enumeration<URL> dirs;  
        try {  
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);  
            // 循环迭代下去  
            while (dirs.hasMoreElements()) {  
                // 获取下一个元素  
                URL url = dirs.nextElement();  
                String f = url.getFile();
                // 得到协议的名称  
                String protocol = url.getProtocol();  
                // 如果是以文件的形式保存在服务器上  
                if ("file".equals(protocol)) {
                    //System.err.println("file类型的扫描");  
                    // 获取包的物理路径  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    // 以文件的方式扫描整个包下的文件 并添加到集合中  
                    findAndAddClassesInPackageByFile(pack, filePath,recursive, classes);  
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件  
                    // 定义一个JarFile  
                    //System.err.println("jar类型的扫描");  
                    JarFile jar;  
                    try {  
                        // 获取jar  
                        jar = ((JarURLConnection) url.openConnection())  
                                .getJarFile();  
                        // 从此jar包 得到一个枚举类  
                        Enumeration<JarEntry> entries = jar.entries();  
                        // 同样的进行循环迭代  
                        while (entries.hasMoreElements()) {  
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件  
                            JarEntry entry = entries.nextElement();  
                            String name = entry.getName();  
                            // 如果是以/开头的  
                            if (name.charAt(0) == '/') {  
                                // 获取后面的字符串  
                                name = name.substring(1);  
                            }  
                            // 如果前半部分和定义的包名相同  
                            if (name.startsWith(packageDirName)) {  
                                int idx = name.lastIndexOf('/');  
                                // 如果以"/"结尾 是一个包  
                                if (idx != -1) {  
                                    // 获取包名 把"/"替换成"."  
                                    packageName = name.substring(0, idx)  
                                            .replace('/', '.');  
                                }  
                                // 如果可以迭代下去 并且是一个包  
                                if ((idx != -1) || recursive) {  
                                    // 如果是一个.class文件 而且不是目录  
                                    if (name.endsWith(".class")  
                                            && !entry.isDirectory()) {  
                                        // 去掉后面的".class" 获取真正的类名  
                                        String className = name.substring(  
                                                packageName.length() + 1, name  
                                                        .length() - 6);  
                                        try {  
                                            // 添加到classes  
                                            classes.add(Class  
                                                    .forName(packageName + '.'  
                                                            + className));  
                                        } catch (ClassNotFoundException e) {  
                                            // log  
                                            // .error("添加用户自定义视图类错误 找不到此类的.class文件");  
                                            e.printStackTrace();  
                                        }  
                                    }  
                                }  
                            }  
                        }  
                    } catch (IOException e) {  
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");  
                        e.printStackTrace();  
                    }  
                } else if ("bundleresource".equals(protocol)) {
                    //System.err.println("file类型的扫描");  
                    // 获取包的物理路径  
                   // String filePath = url.getFile(); 
                    //URL fileUrl = FileLocator.toFileURL(url);
                   // String filePath = URLDecoder.decode(fileUrl.getPath(), "UTF-8"); 
                    // 以文件的方式扫描整个包下的文件 并添加到集合中  
                    //findAndAddClassesInPackageByFile(pack, filePath,recursive, classes);  
                } 
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
        return classes;  
    }  
	
    /** 
     * 以文件的形式来获取包下的所有Class 
     *  
     * @param packageName 
     * @param packagePath 
     * @param recursive 
     * @param classes 
     */  
    public void findAndAddClassesInPackageByFile(String packageName,  String packagePath, final boolean recursive, Set<Class<?>> classes) {
    	if(classes == null) {
    		throw new NullPointerException("classes can not be null");
    	}
        // 获取此包的目录 建立一个File  
        File dir = new File(packagePath);  
        // 如果不存在或者 也不是目录就直接返回  
        if (!dir.exists() || !dir.isDirectory()) {  
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");  
            return;  
        }  
        // 如果存在 就获取包下的所有文件 包括目录  
        File[] dirfiles = dir.listFiles(new FileFilter() {  
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
            public boolean accept(File file) {  
                return (recursive && file.isDirectory())  || (file.getName().endsWith(".class"));  
            }  
        });  
        
        // 循环所有文件  
        for (File file : dirfiles) {  
            // 如果是目录 则继续扫描  
        	
            if (file.isDirectory()) {  
                findAndAddClassesInPackageByFile(packageName + "."  + file.getName(), file.getAbsolutePath(), recursive,  classes);  
            } else {  
                // 如果是java类文件 去掉后面的.class 只留下类名  
                String className = file.getName().substring(0,  file.getName().length() - 6);                 
                String cn = null;
                try {  
                    // 添加到集合中去  
                    //classes.add(Class.forName(packageName + '.' + className));  
                     //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净 
                	cn = packageName + '.' + className;
                	Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(cn);
                    classes.add(cls);    
                    } catch (ClassNotFoundException e) {  
	                    logger.error("ERROR: "+file.getAbsolutePath() +" for class " + cn,e);  
	                    //e.printStackTrace();  
	                    //logger.warn(e.getMessage());
                    }  catch (SecurityException e) {
                    	 logger.error("ERROR: "+file.getAbsolutePath() +" for class " + cn,e);  
                    }
            }  
        }  
    }

	public Set<Class<?>> getClasses(String[] ps) {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		for(String p : ps) {
			classes.addAll(this.getClasses(p.trim()));
		}
		return classes;
	}  
}
