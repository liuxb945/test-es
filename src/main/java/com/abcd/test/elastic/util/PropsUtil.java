package com.abcd.test.elastic.util;

import java.util.*;
import java.io.*;

/**
 * 配置文件读取工具类
 * @author zuoguodong
 */
public class PropsUtil {

	private static Properties props = new Properties();
	
	static {
		InputStream ins = PropsUtil.class.getClass().getResourceAsStream("/application.properties");
		try {
			props.load(ins);
		} catch (IOException e) {
			System.err.println("初始化资源文件出错");;
		}finally{
			try {
				ins.close();
			} catch (IOException e) {}
		}
	}

    /**
     * 读取属性文件中的属性值
     * @param attr
     * @return
     */
    public static String readProps(String attr){
       return props.getProperty(attr);
    }
}
