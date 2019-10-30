package com.softdev.system.generator.util;

/**
 * string tool
 *
 * @author xuxueli 2018-05-02 20:43:25
 */
public class StringUtils {

    /**
     * 字符串不为 null 不为空
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str){
        return str == null || str.trim().length() <= 0;
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    public static String lowerCaseFirst(String str) {
        //2019-2-10 解决StringUtils.lowerCaseFirst潜在的NPE异常@liutf
        return (str!=null&&str.length()>1)?str.substring(0, 1).toLowerCase() + str.substring(1):"";
    }

    /**
     * 下划线，转换为驼峰式
     * 不论是连续的一个下划线,还是多个下划线,都当做一个下划线处理
     * @param underscoreName 带有下划线的字符串,需要全是小写字母
     * @return 驼峰命名
     */
    public static String underlineToCamelCase(String underscoreName) {
        String result = "";
        if (StringUtils.isNullOrEmpty(underscoreName)) {
            throw new CodeGenerateException("转为驼峰命名时出错.输入参数 underscoreName :" + underscoreName);
        }
        if(!underscoreName.contains("_")){  //没有下划线,不做处理
            return underscoreName;
        }
        String[] strings = underscoreName.split("_");
        for (int i = 0; i < strings.length; i++){
            result += StringUtils.upperCaseFirst(strings[i]);
        }
        return StringUtils.lowerCaseFirst(result);
    }
//    public static String underlineToCamelCase(String underscoreName) {
//        StringBuilder result = new StringBuilder();
//        if (!StringUtils.isNullOrEmpty(underscoreName)) {
//            boolean flag = false;
//            for (int i = 0; i < underscoreName.length(); i++) {
//                char ch = underscoreName.charAt(i);
//                if ("_".charAt(0) == ch) {
//                    flag = true;
//                } else {
//                    if (flag) {
//                        result.append(Character.toUpperCase(ch));
//                        flag = false;
//                    } else {
//                        result.append(ch);
//                    }
//                }
//            }
//        }
//        return result.toString();
//    }
}
