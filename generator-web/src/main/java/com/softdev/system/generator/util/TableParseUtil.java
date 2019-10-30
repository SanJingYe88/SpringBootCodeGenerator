package com.softdev.system.generator.util;


import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.CreateInfo;
import com.softdev.system.generator.entity.FieldInfo;
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
        String tableSql = createInfo.getTableSql();
        tableSql = tableSql.trim()
                .replaceAll("'", "`")
                .replaceAll("\"", "`")
                .replaceAll("\n","")
                .replaceAll("\t","")
                .trim().toLowerCase();          //注意:sql 现在全是小写的了
        createInfo.setTableSql(tableSql);
        log.info("tableSql:{}",tableSql);

        // 解析出表名
        String tableName = TableParseUtil.parseTableName(tableSql);
        log.info("解析出表名:{}",tableName);
        if (StringUtils.isNullOrEmpty(tableName)){
            throw new CodeGenerateException("表名解析失败,请检查SQL语句.");
        }

        tableSql = tableSql.substring(tableSql.indexOf("(") + 1).trim();
        createInfo.setTableSql(tableSql);

        // 通过表名得到类名
        String className = TableParseUtil.parseClassName(tableName);
        if (StringUtils.isNullOrEmpty(className)){
            throw new CodeGenerateException("类名解析失败.");
        }

        // 通过表名注释解析出类名注释
        String classComment = TableParseUtil.parseClassComment(className,createInfo);
        log.info("解析出类名注释:{}",classComment);

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

        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName(tableName);
        codeJavaInfo.setClassName(className);
        codeJavaInfo.setClassComment(classComment);
        codeJavaInfo.setFieldList(fieldList);

        return codeJavaInfo;
    }

    /**
     * 通过表结构,解析出属性
     * @param createInfo 生成条件
     * @return 属性列表
     */
    private static List<FieldInfo> parseFieldInfo1(CreateInfo createInfo) {
        List<FieldInfo> fieldList = new ArrayList<>();     //属性列表
        String tableSql = createInfo.getTableSql();

        // 正常的 sql 建表语句中 ( ) 内的一定是字段相关的定义
        String fieldListTmp = tableSql.substring(tableSql.indexOf("(") + 1, tableSql.lastIndexOf(")"));

        // 对 字段注释 comment 中有英文逗号的特殊情况的处理, 防止不小心被当成切割符号切割
        fieldListTmp = TableParseUtil.changeFieldComment(fieldListTmp);

        // 对 double(10, 2) 等类型中有英文逗号的特殊情况的处理, 防止不小心被当成切割符号切割
        fieldListTmp = TableParseUtil.changeFieldType(fieldListTmp);

        // 不相干的英文逗号处理完毕,进行字段的切分
        String[] fieldLineList = fieldListTmp.split(",");
        if (fieldLineList.length > 0) {
            int i = 0;
            //i为了解决primary key关键字出现的地方，出现在前3行，一般和id有关
            for (String columnLine : fieldLineList) {
                i++;
                columnLine = columnLine.replaceAll("\n", "").replaceAll("\t", "").trim();
                // `userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                // 2018-9-18 zhengk 修改为contains，提升匹配率和匹配不按照规矩出牌的语句
                // 2018-11-8 zhengkai 修复tornadoorz反馈的KEY FK_permission_id (permission_id),KEY FK_role_id (role_id)情况
                // 2019-2-22 zhengkai 要在条件中使用复杂的表达式
                // 2019-4-29 zhengkai 优化对普通和特殊storage关键字的判断（感谢@AhHeadFloating的反馈 ）
                boolean specialFlag = (!columnLine.contains("key ") && !columnLine.contains("constraint") && !columnLine.contains("using") && !columnLine.contains("unique")
                        && !(columnLine.contains("primary") && columnLine.indexOf("storage") + 3 > columnLine.indexOf("("))
                        && !columnLine.contains("pctincrease")
                        && !columnLine.contains("buffer_pool") && !columnLine.contains("tablespace")
                        && !(columnLine.contains("primary") && i > 3));

                if (specialFlag) {
                    //如果是oracle的number(x,x)，可能出现最后分割残留的,x)，这里做排除处理
                    if (columnLine.length() < 5) {
                        continue;
                    }
                    //2018-9-16 zhengkai 支持'符号以及空格的oracle语句// userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String columnName = "";
                    columnLine = columnLine.replaceAll("`", " ").replaceAll("\"", " ").replaceAll("'", "").replaceAll("  ", " ").trim();
                    //如果遇到username varchar(65) default '' not null,这种情况，判断第一个空格是否比第一个引号前
                    columnName = columnLine.substring(0, columnLine.indexOf(" "));

                    // field Name
//                    2019-09-08 yj 添加是否下划线转换为驼峰的判断
                    String fieldName;
                    if (createInfo.isCanUnderLineToCamelCase()) {
                        fieldName = StringUtils.lowerCaseFirst(StringUtils.underlineToCamelCase(columnName));
                        if (fieldName.contains("_")) {
                            fieldName = fieldName.replaceAll("_", "");
                        }
                    } else {
                        fieldName = StringUtils.lowerCaseFirst(columnName);
                    }

                    // field class
                    columnLine = columnLine.substring(columnLine.indexOf("`") + 1).trim();
                    // int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String fieldClass = Object.class.getSimpleName();
                    //2018-9-16 zhengk 补充char/clob/blob/json等类型，如果类型未知，默认为String
                    //2018-11-22 lshz0088 处理字段类型的时候，不严谨columnLine.contains(" int") 类似这种的，可在前后适当加一些空格之类的加以区分，否则当我的字段包含这些字符的时候，产生类型判断问题。
                    if (columnLine.contains(" int") || columnLine.contains("smallint")) {
                        fieldClass = Integer.class.getSimpleName();
                    } else if (columnLine.contains("bigint")) {
                        fieldClass = Long.class.getSimpleName();
                    } else if (columnLine.contains("float")) {
                        fieldClass = Float.class.getSimpleName();
                    } else if (columnLine.contains("double")) {
                        fieldClass = Double.class.getSimpleName();
                    } else if (columnLine.contains("datetime") || columnLine.contains("timestamp")) {
                        fieldClass = Date.class.getSimpleName();
                    } else if (columnLine.contains("varchar") || columnLine.contains(" text") || columnLine.contains("char")
                            || columnLine.contains("clob") || columnLine.contains("blob") || columnLine.contains("json")) {
                        fieldClass = String.class.getSimpleName();
                    } else if (columnLine.contains("decimal") || columnLine.contains(" number")) {
                        //2018-11-22 lshz0088 建议对number类型增加int，long，BigDecimal的区分判断
                        //如果startKh大于等于0，则表示有设置取值范围
                        int startKh = columnLine.indexOf("(");
                        if (startKh >= 0) {
                            int endKh = columnLine.indexOf(")", startKh);
                            String[] fanwei = columnLine.substring(startKh + 1, endKh).split("，");
                            //2019-1-5 zhengk 修复@arthaschan反馈的超出范围错误
                            //System.out.println("fanwei"+ JSON.toJSONString(fanwei));
                            //                            //number(20,6) fanwei["20","6"]
                            //                            //number(0,6) fanwei["0","6"]
                            //                            //number(20,0) fanwei["20","0"]
                            //                            //number(20) fanwei["20"]
                            //如果括号里是1位或者2位且第二位为0，则进行特殊处理。只有有小数位，都设置为BigDecimal。
                            if ((fanwei.length > 1 && "0".equals(fanwei[1])) || fanwei.length == 1) {
                                int length = Integer.parseInt(fanwei[0]);
                                if (fanwei.length > 1) {
                                    length = Integer.valueOf(fanwei[1]);
                                }
                                //数字范围9位及一下用Integer，大的用Long
                                if (length <= 9) {
                                    fieldClass = Integer.class.getSimpleName();
                                } else {
                                    fieldClass = Long.class.getSimpleName();
                                }
                            } else {
                                //有小数位数一律使用BigDecimal
                                fieldClass = BigDecimal.class.getSimpleName();
                            }
                        } else {
                            fieldClass = BigDecimal.class.getSimpleName();
                        }
                    } else if (columnLine.contains("boolean") || columnLine.contains("tinyint")) {
                        //20190910 MOSHOW.K.ZHENG 新增对boolean的处理（感谢@violinxsc的反馈）以及修复tinyint类型字段无法生成boolean类型问题（感谢@hahaYhui的反馈）
                        fieldClass = Boolean.class.getSimpleName();
                    } else {
                        fieldClass = String.class.getSimpleName();
                    }

                    // field comment，MySQL的一般位于field行，而pgsql和oralce多位于后面。
                    String fieldComment = null;
                    if (tableSql.contains("comment on column") && (tableSql.contains("." + columnName + " is ") || tableSql.contains(".`" + columnName + "` is"))) {
                        //新增对pgsql/oracle的字段备注支持
                        //COMMENT ON COLUMN public.check_info.check_name IS '检查者名称';
                        //2018-11-22 lshz0088 正则表达式的点号前面应该加上两个反斜杠，否则会认为是任意字符
                        //2019-4-29 zhengkai 优化对oracle注释comment on column的支持（@liukex）
                        tableSql = tableSql.replaceAll(".`" + columnName + "` is", "." + columnName + " is");
                        Matcher columnCommentMatcher = Pattern.compile("\\." + columnName + " is `").matcher(tableSql);
                        fieldComment = columnName;
                        while (columnCommentMatcher.find()) {
                            String columnCommentTmp = columnCommentMatcher.group();
                            System.out.println(columnCommentTmp);
                            fieldComment = tableSql.substring(tableSql.indexOf(columnCommentTmp) + columnCommentTmp.length()).trim();
                            fieldComment = fieldComment.substring(0, fieldComment.indexOf("`")).trim();
                        }
                    } else if (columnLine.contains("comment")) {
                        String commentTmp = columnLine.substring(columnLine.indexOf("comment") + 7).trim();
                        // '用户ID',
                        if (commentTmp.contains("`") || commentTmp.indexOf("`") != commentTmp.lastIndexOf("`")) {
                            commentTmp = commentTmp.substring(commentTmp.indexOf("`") + 1, commentTmp.lastIndexOf("`"));
                        }
                        //解决最后一句是评论，无主键且连着)的问题:album_id int(3) default '1' null comment '相册id：0 代表头像 1代表照片墙')
                        if (commentTmp.contains(")")) {
                            commentTmp = commentTmp.substring(0, commentTmp.lastIndexOf(")") + 1);
                        }
                        fieldComment = commentTmp;
                    } else {
                        //修复comment不存在导致报错的问题
                        fieldComment = columnName;
                    }

                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setColumnName(columnName);
                    fieldInfo.setFieldName(fieldName);
                    fieldInfo.setFieldClass(fieldClass);
                    fieldInfo.setFieldComment(fieldComment);

                    fieldList.add(fieldInfo);
                }
            }
        }
        return fieldList;
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

        String fieldListTmp = tableSql;

        // 对 字段注释 comment 中有英文逗号的特殊情况的处理, 防止不小心被当成切割符号切割
        fieldListTmp = TableParseUtil.changeFieldComment(fieldListTmp);

        // 对 double(10, 2) 等类型中有英文逗号的特殊情况的处理, 防止不小心被当成切割符号切割
        fieldListTmp = TableParseUtil.changeFieldType(fieldListTmp);

        // 不相干的英文逗号处理完毕,进行字段的切分
        String[] fieldLineList = fieldListTmp.split(",");
        if (fieldLineList.length > 0) {

            for (String columnLine : fieldLineList) {
                FieldInfo fieldInfo = new FieldInfo();
                columnLine = columnLine.trim();

                // 对于一些特殊语句的处理.
                if (columnLine.contains("primary key (") || columnLine.contains("primary key(")){
                    continue;
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

                //`user_id` int(11) not null auto_increment

                // 列名的处理
                if(columnLine.contains("`")){
                    columnLine = columnLine.replaceAll("`", "").trim();
                }
                String columnName = columnLine.split(" ",2)[0];
                fieldInfo.setColumnName(columnName);        //user_id

                columnLine = columnLine.replace(columnName,"").trim();

                // 字段名的处理
                String fieldName = columnName.toLowerCase();
                if(createInfo.isCanUnderLineToCamelCase() && fieldName.contains("_")){
                    fieldName = StringUtils.underlineToCamelCase(fieldName);
                }
                fieldName = StringUtils.lowerCaseFirst(fieldName);
                fieldInfo.setFieldName(fieldName);

                // 字段类型的处理
                String fieldClass = String.class.getSimpleName();
                if (columnLine.contains("int") || columnLine.contains("smallint")) {
                    fieldClass = Integer.class.getSimpleName();
                } else if (columnLine.contains("bigint")) {
                    fieldClass = Long.class.getSimpleName();
                } else if (columnLine.contains("float")) {
                    fieldClass = Float.class.getSimpleName();
                } else if (columnLine.contains("double")) {
                    fieldClass = Double.class.getSimpleName();
                } else if (columnLine.contains("datetime") || columnLine.contains("timestamp")) {
                    fieldClass = Date.class.getSimpleName();
                } else if (columnLine.contains("varchar") || columnLine.contains("char")) {
                    fieldClass = String.class.getSimpleName();
                } else if (columnLine.contains("decimal") || columnLine.contains("number")) {
                    fieldClass = BigDecimal.class.getSimpleName();
                } else if (columnLine.contains("boolean") || columnLine.contains("tinyint")) {
                    fieldClass = Boolean.class.getSimpleName();
                } else {
                    throw new CodeGenerateException("字段类型解析失败.请检查SQL");
                }
                fieldInfo.setFieldClass(fieldClass);

                log.info("解析出的字段信息:{}",fieldInfo);

/*                boolean specialFlag = (!columnLine.contains("key ") && !columnLine.contains("constraint")
                        && !columnLine.contains("unique")
                        && !columnLine.contains("primary")
                        && !(columnLine.contains("primary")));

                if (specialFlag) {
                        //2018-11-22 lshz0088 建议对number类型增加int，long，BigDecimal的区分判断
                        //如果startKh大于等于0，则表示有设置取值范围
                        int startKh = columnLine.indexOf("(");
                        if (startKh >= 0) {
                            int endKh = columnLine.indexOf(")", startKh);
                            String[] fanwei = columnLine.substring(startKh + 1, endKh).split("，");
                            //2019-1-5 zhengk 修复@arthaschan反馈的超出范围错误
                            //System.out.println("fanwei"+ JSON.toJSONString(fanwei));
                            //                            //number(20,6) fanwei["20","6"]
                            //                            //number(0,6) fanwei["0","6"]
                            //                            //number(20,0) fanwei["20","0"]
                            //                            //number(20) fanwei["20"]
                            //如果括号里是1位或者2位且第二位为0，则进行特殊处理。只有有小数位，都设置为BigDecimal。
                            if ((fanwei.length > 1 && "0".equals(fanwei[1])) || fanwei.length == 1) {
                                int length = Integer.parseInt(fanwei[0]);
                                if (fanwei.length > 1) {
                                    length = Integer.valueOf(fanwei[1]);
                                }
                                //数字范围9位及一下用Integer，大的用Long
                                if (length <= 9) {
                                    fieldClass = Integer.class.getSimpleName();
                                } else {
                                    fieldClass = Long.class.getSimpleName();
                                }
                            } else {
                                //有小数位数一律使用BigDecimal
                                fieldClass = BigDecimal.class.getSimpleName();
                            }*/
                    fieldList.add(fieldInfo);
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
        String tableName = null;

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
