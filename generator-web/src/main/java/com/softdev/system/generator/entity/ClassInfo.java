package com.softdev.system.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 类的结构信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ClassInfo {

    private String className;       //类名(首字母大写,驼峰)
    private String entityName;      //该类的实例的名字(类名首字母小写,驼峰)
    private String primaryName;     //该类对应的数据表的主键的驼峰命名
    private String primaryType;     //该类对应的数据表的主键的类型
	private String classComment;    //类名注释
    private TableInfo tableInfo;    //类对应的数据表
	private List<FieldInfo> fieldInfoList;  //属性列表
}