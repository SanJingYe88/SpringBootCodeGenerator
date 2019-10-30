package com.softdev.system.generator.entity;

import com.softdev.system.generator.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 生成条件实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CreateInfo {

    private String tableSql;    //建表sql
    private String authorName;  //作者名
    private String packageName; //包名
    private String returnUtil;  //Controller返回值类型
    private boolean canUnderLineToCamelCase = true; //是否下划线转为驼峰
    private String databaseType;    //数据库类型

    public static CreateInfo defaultConfig(CreateInfo createInfo){
        if (StringUtils.isNullOrEmpty(createInfo.getAuthorName())){
            createInfo.setAuthorName("SanJinYe");
        }
        if (StringUtils.isNullOrEmpty(createInfo.getPackageName())){
            createInfo.setPackageName("it.com");
        }
        if (StringUtils.isNullOrEmpty(createInfo.getDatabaseType())){
            createInfo.setDatabaseType("mysql");
        }
        return createInfo;
    }
}
