package com.softdev.system.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 字段结构的定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FieldInfo {

    private String fieldName;       //字段名(需要驼峰命名)
    private String fieldClass;      //字段类型
    private String fieldComment;    //字段注释
    private ColumnInfo columnInfo;      //列名
}
