package com.softdev.system.generator.util;


import com.softdev.system.generator.entity.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表结构解析工具类
 */
@Slf4j
public class TableParseUtil {

    /**
     * 解析建表SQL
     *
     * @param createInfo 生成条件
     * @return 返回实体类
     */
    public static ClassInfo processTableIntoClassInfo(CreateInfo createInfo) {

        ClassInfo classInfo = new ClassInfo();
        TableInfo tableInfo = new TableInfo();

        String tableSql = createInfo.getTableSql();
        tableSql = tableSql.trim()
                .replaceAll("'", "`")
                .replaceAll("\"", "`")
                .replaceAll("\n","")
                .replaceAll("\t","")
                .trim().toLowerCase();          //注意:sql 现在全是小写的了
        log.info("tableSql:{}",tableSql);
        createInfo.setTableSql(tableSql);

        // 解析出表名
        String tableName = TableParseUtil.parseTableName(tableSql);
        log.info("解析出表名:{}",tableName);
        if (StringUtils.isNullOrEmpty(tableName)){
            throw new CodeGenerateException("表名解析失败,请检查SQL语句.");
        }
        tableInfo.setTableName(tableName);
        tableSql = tableSql.substring(tableSql.indexOf("(") + 1).trim();

        // 通过表名得到类名
        String className = TableParseUtil.parseClassName(tableName);
        if (StringUtils.isNullOrEmpty(className)){
            throw new CodeGenerateException("类名解析失败.");
        }
        classInfo.setClassName(className);

        //通过类名设置实体类的名字
        classInfo.setEntityName(StringUtils.lowerCaseFirst(className));

        // 通过表名注释解析出类名注释
        String classComment = TableParseUtil.parseClassComment(className,createInfo);
        log.info("解析出类名注释:{}",classComment);
        classInfo.setClassComment(classComment);
        tableInfo.setTableComment(classComment);

        if(!className.equals(classComment)){     //如果有表注释
            tableSql = tableSql.substring(0,tableSql.lastIndexOf("comment")).trim();
        }
        tableSql = tableSql.substring(0,tableSql.lastIndexOf(")") - 1).trim();
        log.info("tableSql:{}",tableSql);
        createInfo.setTableSql(tableSql);

        // 通过表结构,解析出属性列表
        List<FieldInfo> fieldList = TableParseUtil.parseFieldInfo(createInfo);
        if (fieldList.size() < 1) {
            throw new CodeGenerateException("表结构分析失败，请检查语句或者提交issue给我");
        }

        for (FieldInfo fieldInfo : fieldList){

            if(fieldInfo.getColumnInfo().isCanPrimary()){
                tableInfo.setPrimaryKey(fieldInfo.getColumnInfo().getColumnName());
                classInfo.setPrimaryName(StringUtils.lowerCaseFirst(fieldInfo.getFieldName()));
                classInfo.setPrimaryType(fieldInfo.getColumnInfo().getColumnType());
            }
            log.info("解析出的属性信息:{}",fieldInfo);
            log.info("解析出的表字段信息:{}",fieldInfo.getColumnInfo());
        }

        classInfo.setFieldInfoList(fieldList);
        classInfo.setTableInfo(tableInfo);
        return classInfo;
    }

    /**
     * 通过表结构,解析出属性
     * @param createInfo 生成条件
     * @return 属性列表
     */
    private static List<FieldInfo> parseFieldInfo(CreateInfo createInfo) {
        if ("oracle".equals(createInfo.getDatabaseType()) || "pgsql".equals(createInfo.getDatabaseType())){
            return TableParseUtil.parseFieldInfo4Oracle(createInfo);
        }
        //mysql
        return TableParseUtil.parseFieldInfo4MySQL(createInfo);
    }

    /**
     * 通过表结构,解析出属性,针对 oracle,pgsql
     * @param createInfo 生成条件
     * @return 属性列表
     */
    private static List<FieldInfo> parseFieldInfo4Oracle(CreateInfo createInfo) {
        return null;
    }

    /**
     * 通过表结构,解析出属性,针对 mysql
     * @param createInfo 生成条件
     * @return 属性列表
     */
    private static List<FieldInfo> parseFieldInfo4MySQL(CreateInfo createInfo) {
        List<FieldInfo> fieldList = new ArrayList<>();     //属性列表
        String tableSql = createInfo.getTableSql();

        // 对 字段注释 comment 中有英文逗号的特殊情况的处理, 防止不小心被当成切割符号切割
        tableSql = TableParseUtil.changeFieldComment(tableSql);

        // 对 double(10, 2) 等类型中有英文逗号的特殊情况的处理, 防止不小心被当成切割符号切割
        tableSql = TableParseUtil.changeFieldType(tableSql);

        // 对 联合主键 中有英文逗号的特殊情况的处理, 防止不小心被当成切割符号切割
        tableSql = TableParseUtil.changeMorePrimary(tableSql);

        // 不相干的英文逗号处理完毕,进行字段的切分
        String[] fieldLineList = tableSql.split(",");

        if (fieldLineList.length > 0) {

            String primaryColumn = "";          //可能是主键的字段, 支持联合主键

            for (String columnLine : fieldLineList) {
                FieldInfo fieldInfo = new FieldInfo();      //属性
                ColumnInfo columnInfo = new ColumnInfo();   //字段

                columnLine = columnLine.trim();

                // 对于一些特殊语句的处理.
                if (primaryColumn.trim().length() == 0){
                    if (columnLine.contains("primary key (") || columnLine.contains("primary key(")){
                        primaryColumn = columnLine.substring(columnLine.indexOf("(") + 1, columnLine.lastIndexOf(")")).trim();
                        continue;
                    }
                }

                //`user_id` int(11) not null auto_increment comment `用户id`

                // 字段注释的处理
                if(columnLine.contains("comment")){
                    String comment = columnLine.split("comment")[1].trim();     //`用户id`
                    comment = comment.substring(1,comment.length()-1).trim();   //用户id
                    fieldInfo.setFieldComment(comment);
                    columnLine = columnLine.split("comment")[0].trim();
                }else {
                    fieldInfo.setFieldComment("");
                }
                columnInfo.setComment(fieldInfo.getFieldComment());

                //`user_id` int(11) not null auto_increment

                // 列名的处理
                if(columnLine.contains("`")){
                    columnLine = columnLine.replaceAll("`", "").trim();
                }
                String columnName = columnLine.split(" ",2)[0];
                columnInfo.setColumnName(columnName);       //user_id

                columnLine = columnLine.replace(columnName,"").trim();

                // 字段名的处理
                String fieldName = columnName.toLowerCase();
                if(createInfo.isCanUnderLineToCamelCase() && fieldName.contains("_")){
                    fieldName = StringUtils.underlineToCamelCase(fieldName);
                }
                fieldName = StringUtils.lowerCaseFirst(fieldName);
                fieldInfo.setFieldName(fieldName);

                //int(11) not null auto_increment

                String type = columnLine.split(" ",2)[0].trim();

                if (type.contains("，")){        //之前转化过,现在转回来
                    type = type.replace("，",",");
                }

                // 字段类型的处理
                String fieldClass = String.class.getSimpleName();
                if (type.startsWith("int") || type.startsWith("smallint")) {
                    fieldClass = Integer.class.getSimpleName();
                } else if (type.startsWith("varchar") || type.startsWith("char")) {
                    fieldClass = String.class.getSimpleName();
                } else if (type.startsWith("datetime") || type.startsWith("timestamp")) {
                    fieldClass = Date.class.getSimpleName();
                } else if (type.startsWith("double")) {
                    fieldClass = Double.class.getSimpleName();
                } else if (type.startsWith("boolean") || type.startsWith("tinyint")) {
                    fieldClass = Boolean.class.getSimpleName();
                } else if (type.startsWith("bigint")) {
                    fieldClass = Long.class.getSimpleName();
                } else if (type.startsWith("float")) {
                    fieldClass = Float.class.getSimpleName();
                } else if (type.startsWith("decimal") || type.startsWith("number")) {
                    fieldClass = BigDecimal.class.getSimpleName();
                } else {
                    throw new CodeGenerateException("字段类型解析失败.请检查SQL");
                }
                columnInfo.setColumnType(type.replace("，",","));

                columnLine = columnLine.replace(type, "");

                //not null auto_increment

                // 其他关键字的处理
                if (columnLine.contains("auto_increment")){
                    columnInfo.setPrimaryType("auto_increment");
                    columnLine = columnLine.replace("auto_increment","");
                }
                if (columnLine.contains("not null")){
                    columnInfo.setCanNull(false);
                    columnLine = columnLine.replace("not null","");
                }else {
                    columnInfo.setCanNull(true);
                }
                if(columnLine.contains("unique")){
                    columnInfo.setCanUnique(true);
                    columnLine = columnLine.replace("unique","");
                }else {
                    columnInfo.setCanUnique(false);
                }
                if(columnLine.contains("primary key")){
                    columnInfo.setCanPrimary(true);
                    columnLine = columnLine.replace("primary key","");
                }else {
                    columnInfo.setCanPrimary(false);
                }
                if (columnLine.contains("default")){
                    columnLine = columnLine.replace("default","").trim();
                    columnInfo.setDefaultVaule(columnLine);
                }
                fieldInfo.setFieldClass(fieldClass);
                fieldInfo.setColumnInfo(columnInfo);
                fieldList.add(fieldInfo);
            }

            // 如果是联合主键
            primaryColumn = primaryColumn.replace("，",",");
            if(primaryColumn.contains(",")){
                String[] strings = primaryColumn.split(",");
                for (String str : strings){
                    for (FieldInfo fieldInfo : fieldList){
                        if (fieldInfo.getColumnInfo().getColumnName().equals(str)){
                            fieldInfo.getColumnInfo().setCanPrimary(true);
                            fieldInfo.getColumnInfo().setCanUnique(true);
                            break;
                        }
                    }
                }
            }else {
                for (FieldInfo fieldInfo : fieldList){
                    if (fieldInfo.getColumnInfo().getColumnName().equals(primaryColumn)){
                        fieldInfo.getColumnInfo().setCanPrimary(true);
                        fieldInfo.getColumnInfo().setCanUnique(true);
                    }
                }

            }
        }

        return fieldList;
    }


//        //2018-10-18 zhengkai 新增支持double(10, 2)等类型中有英文逗号的特殊情况
//        Matcher matcher3 = Pattern.compile("\\((.*?)\\)").matcher(fieldListTmp);     // "\\{(.*?)\\}"
//        while (matcher3.find()) {
//            String commentTmp3 = matcher3.group();
//            if (commentTmp3.contains(",")) {
//                String commentTmpFinal = commentTmp3.replaceAll(",", "，");
//                fieldListTmp = fieldListTmp.replace(matcher3.group(), commentTmpFinal);
//            }
//        }

    /**
     * 转换 联合主键 中可能存在的英文逗号(,) 为中文逗号(，)
     * @param fieldListTmp 字段注释
     * @return 字段注释
     */
    private static String changeMorePrimary(String fieldListTmp) {
        if(fieldListTmp.contains("primary key(")){
            String str = fieldListTmp.substring(fieldListTmp.indexOf("primary key("));
            int length = str.length();
            str = str.replace("`","").replace(",","，");
            fieldListTmp = fieldListTmp.substring(0,fieldListTmp.length()-length) + str;
        } else if(fieldListTmp.contains("primary key (")){
            String str = fieldListTmp.substring(fieldListTmp.indexOf("primary key ("));
            int length = str.length();
            str = str.replace("`","").replace(",","，");
            fieldListTmp = fieldListTmp.substring(0,fieldListTmp.length()-length) + str;
        }
        return fieldListTmp;
    }

    /**
     * 转换 字段 double(10, 2) 等类型 中可能存在的英文逗号(,) 为中文逗号(，)  英文括号 () 为中文括号 （）
     * @param fieldListTmp 字段注释
     * @return 字段注释
     */
    private static String changeFieldType(String fieldListTmp) {
        Matcher matcher = Pattern.compile("\\`(.*?)\\`").matcher(fieldListTmp);     // "\\{(.*?)\\}"
        while (matcher.find()) {
            String commentTmp = matcher.group();
            if (commentTmp.contains(",")) {
                String commentTmpFinal = commentTmp.replaceAll(",", "，")
                                                    .replaceAll("\\(", "（")
                                                    .replaceAll("\\)", "）");
                fieldListTmp = fieldListTmp.replace(commentTmp, commentTmpFinal);
            }
        }
        return fieldListTmp;
    }

    /**
     * 转换 字段 注释 comment 中可能存在的英文逗号(,) 为中文逗号(，)
     * @param fieldListTmp 字段注释
     * @return 字段注释
     */
    private static String changeFieldComment(String fieldListTmp){
        Matcher matcher = Pattern.compile("comment `(.*?)\\`").matcher(fieldListTmp);     // "\\{(.*?)\\}"
        while (matcher.find()) {
            String commentTmp = matcher.group();
            if (commentTmp.contains(",")) {
                String commentTmpFinal = commentTmp.replaceAll(",", "，");
                fieldListTmp = fieldListTmp.replace(commentTmp, commentTmpFinal);
            }
        }
        return fieldListTmp;
    }

    /**
     * 通过表名注释解析出类名注释,没有表注释的,使用类名代替
     * @param className 类名
     * @param createInfo 生成条件
     * @return 类名注释
     */
    private static String parseClassComment(String className,CreateInfo createInfo) {
        String tableSql = createInfo.getTableSql();
        String databaseType = createInfo.getDatabaseType();

        //不同数据库的表注释: mysql 是 comment= pgsql/oracle 是 comment on table
        if ("mysql".equals(databaseType)) {    //mysql
            int lastIndexOf = tableSql.lastIndexOf(")");    //最后一个 ) 出现的位置
            int lastIndexOfComment = tableSql.lastIndexOf("comment");    //最后一个 comment 出现的位置
            if (lastIndexOf > lastIndexOfComment){      //说明表名没有注释,返回 className 作为实体类名注释
                return className;
            }
            String temp = tableSql.substring(lastIndexOfComment, tableSql.length());
            String[] strings = temp.split("=", 2);      //只切割一次
            String classCommentTmp = strings[1];
            classCommentTmp = classCommentTmp.replaceAll("`", "").trim();
            if (classCommentTmp.trim().length() <= 0) { //虽然有 comment , 但是注释内容为空
                return className;
            }
            return classCommentTmp;
        }

        if ("oracle".equals(databaseType) || "pgsql".equals(databaseType)) {     //oracle,pgsql
            //COMMENT ON TABLE CT_BAS_FEETYPE IS 'CT_BAS_FEETYPE';
            String classCommentTmp = tableSql.substring(tableSql.lastIndexOf("comment on table") + 17).trim();
            //证明这是一个常规的COMMENT ON TABLE  xxx IS 'xxxx'
            if (classCommentTmp.contains("`")) {
                classCommentTmp = classCommentTmp.substring(classCommentTmp.indexOf("`") + 1);
                classCommentTmp = classCommentTmp.substring(0, classCommentTmp.indexOf("`"));
                return classCommentTmp;
            }
            return className;   //返回 className 作为实体类名注释
        }

        //返回 className 作为实体类名注释
        return className;
    }

    /**
     * 解析出类名
     * @param tableName 表名
     * @return 类名
     */
    private static String parseClassName(String tableName) {
        String className;
        tableName = tableName.toLowerCase();    //全部转为小写字母
        tableName = StringUtils.underlineToCamelCase(tableName);    //转为驼峰命名
        className = StringUtils.upperCaseFirst(tableName);  //首字母大写
        if (className.contains("_")) {
            className = className.replaceAll("_", "");
        }
        return className;
    }

    /**
     * 解析出表名
     * @param tableSql 建表sql
     * @return 返回表名
     */
    private static String parseTableName(String tableSql){
        String tableName = "";

        // 针对 create table if not exists 表名 ( ... ) 的解析
        if(tableSql.contains("if not exists")){
            tableSql = tableSql.replaceAll("if not exists", "");
        }

        // 针对  create table 表名 ( ... ) 的解析
        if (tableSql.contains("TABLE") && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf("TABLE") + 5, tableSql.indexOf("("));
        } else if (tableSql.contains("table") && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf("table") + 5, tableSql.indexOf("("));
        } else {
            throw new CodeGenerateException("无法解析出表名! 请检查 SQL 语句是否正确.");
        }

        if (tableName.contains("`")) {
            tableName = tableName.substring(tableName.indexOf("`") + 1, tableName.lastIndexOf("`"));
        } else {
            // 空格开头的，需要替换掉\n\t空格
            tableName = tableName.replaceAll(" ", "")
                    .replaceAll("\n", "")
                    .replaceAll("\t", "");
        }

        // 针对 数据库名`.`表名 这种命名的支持
        if (tableName.contains("`.`")) {
            tableName = tableName.substring(tableName.indexOf("`.`") + 3);
        } else if (tableName.contains(".")) {
            // 针对 数据库名.表名 这种命名的支持
            tableName = tableName.substring(tableName.indexOf(".") + 1);
        }
        return tableName;
    }
}
