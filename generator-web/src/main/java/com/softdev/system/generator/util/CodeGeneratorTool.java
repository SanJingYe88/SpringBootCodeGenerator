package com.softdev.system.generator.util;

import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.CreateInfo;

/**
 * 代码生成工具类
 */
public class CodeGeneratorTool {

    /**
     * process Table Into ClassInfo
     *
     * @param createInfo 生成条件实体类
     * @return ClassInfo 类的结构信息
     */
    public static ClassInfo processTableIntoClassInfo(CreateInfo createInfo) {
        return TableParseUtil.processTableIntoClassInfo(createInfo);
    }
}