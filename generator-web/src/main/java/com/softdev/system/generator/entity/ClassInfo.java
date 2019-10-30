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

    private String tableName;       //所属表名
    private String className;       //类名
	private String classComment;    //类名注释
	private List<FieldInfo> fieldList;  //字段列表
}