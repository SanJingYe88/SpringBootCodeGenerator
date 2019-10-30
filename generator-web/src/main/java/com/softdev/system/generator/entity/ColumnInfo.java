package com.softdev.system.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据表中的列的定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ColumnInfo {

    private String columnName;      //列名
    private String columnType;      //列类型
    private String comment;         //列注释
    private boolean canNull;         //是否允许为 null
    private boolean canUnique;       //是否唯一
    private boolean canPrimary;     //是否是主键
    private String primaryType;     //主键生成类型
    private String defaultVaule;    //默认值
}
