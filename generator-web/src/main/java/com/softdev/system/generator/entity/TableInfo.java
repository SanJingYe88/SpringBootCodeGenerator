package com.softdev.system.generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 数据表的结构信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TableInfo {

    private String primaryKey;      //主键字段,支持联合主键
    private String primaryType;     //主键类型
    private String primaryCreateType;     //主键生成方式
    private String tableName;       //表名
    private String tableComment;    //表名注释
    private List<ColumnInfo> columnInfoList;  //字段列表
}
