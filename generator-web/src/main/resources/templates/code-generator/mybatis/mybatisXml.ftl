<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${packageName}.mapper.${classInfo.className}Mapper">

    <resultMap id="baseResultMap" type="${packageName}.entity.${classInfo.className}" >
    <#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
    <#list classInfo.fieldInfoList as fieldInfo >
        <!-- ${fieldInfo.fieldComment} -->
        <result column="${fieldInfo.columnInfo.columnName}" property="${fieldInfo.fieldName}" />
    </#list>
    </#if>
    </resultMap>

    <sql id="baseColumnList">
    <#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
     <#list classInfo.fieldInfoList as fieldInfo >
        ${fieldInfo.columnInfo.columnName}<#if fieldInfo_has_next>,</#if>
     </#list>
    </#if>
    </sql>

    <insert id="insert" useGeneratedKeys="true" keyColumn="${tableInfo.primaryKey}" parameterType="${packageName}.entity.${classInfo.className}">
        INSERT INTO ${classInfo.tableInfo.tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
        <#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
         <#list classInfo.fieldInfoList as fieldInfo >
          <#if fieldInfo.columnInfo.columnName != tableInfo.primaryKey >
            ${r"<if test ='null != "}${fieldInfo.fieldName}${r"'>"}${fieldInfo.columnInfo.columnName}<#if fieldInfo_has_next>,</#if>${r"</if>"}
          </#if>
         </#list>
        </#if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
        <#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
         <#list classInfo.fieldInfoList as fieldInfo >
          <#if fieldInfo.columnInfo.columnName != tableInfo.primaryKey >
            ${r"<if test ='null != "}${fieldInfo.fieldName}${r"'>"}${r"#{"}${fieldInfo.fieldName}${r"}"}<#if fieldInfo_has_next>,</#if>${r"</if>"}
          </#if>
         </#list>
        </#if>
        </trim>
    </insert>

    <!--根据主键删除-->
    <delete id="deleteById" >
        DELETE FROM ${classInfo.tableInfo.tableName}
        WHERE ${tableInfo.primaryKey} = ${r"#{"}${tableInfo.primaryKey}${r"}"}
    </delete>

    <update id="update" parameterType="${packageName}.entity.${classInfo.className}">
        UPDATE ${classInfo.tableInfo.tableName}
        <set>
        <#list classInfo.fieldInfoList as fieldInfo >
         <#if fieldInfo.columnInfo.columnName != tableInfo.primaryKey>
            ${r"<if test ='null != "}${fieldInfo.fieldName}${r"'>"}${fieldInfo.columnInfo.columnName} = ${r"#{"}${fieldInfo.fieldName}${r"}"}<#if fieldInfo_has_next>,</#if>${r"</if>"}
         </#if>
        </#list>
        </set>
        WHERE ${tableInfo.primaryKey} = ${r"#{"}${tableInfo.primaryKey}${r"}"}
    </update>

    <!--根据主键查询-->
    <select id="queryById" resultMap="baseResultMap">
        SELECT <include refid="baseColumnList" />
        FROM ${classInfo.tableInfo.tableName}
        WHERE ${tableInfo.primaryKey} = ${r"#{"}${tableInfo.primaryKey}${r"}"}
    </select>

    <!--条件查询-->
    <select id="query" resultMap="baseResultMap" parameterType="${packageName}.entity.${classInfo.className}">
        SELECT <include refid="baseColumnList" />
        FROM ${classInfo.tableInfo.tableName}
        <where>
         ${r" 1=1 "}
         <#list classInfo.fieldInfoList as fieldInfo >
          <#if fieldInfo_has_next> AND </#if>${r"<if test ='null != "}${fieldInfo.fieldName}${r"'>"}${fieldInfo.columnInfo.columnName} = ${r"#{"}${fieldInfo.columnInfo.columnName}${r"}"} ${r"</if>"}
         </#list>
        </where>
    </select>

    <!--分页查询-->
    <select id="pageAll" resultMap="baseResultMap">
        SELECT <include refid="baseColumnList" />
        FROM ${classInfo.tableInfo.tableName}
        LIMIT ${r"#{pageNum}"}, ${r"#{pageSize}"}
    </select>

    <!-- 分页条件查询 -->
    <select id="queryPage" resultMap="baseResultMap" parameterType="${packageName}.entity.${classInfo.className}">
        SELECT <include refid="baseColumnList" />
        FROM ${classInfo.tableInfo.tableName}
        <where>
        ${r" 1=1 "}
        <#list classInfo.fieldInfoList as fieldInfo >
         <#if fieldInfo_has_next> AND </#if>${r"<if test ='null != "}${fieldInfo.fieldName}${r"'>"}${fieldInfo.columnInfo.columnName} = ${r"#{"}${fieldInfo.columnInfo.columnName}${r"}"} ${r"</if>"}
        </#list>
        </where>
        ${r" "}LIMIT ${r"#{pageNum}"}, ${r"#{pageSize}"}
    </select>

    <!--统计-->
    <select id="countAll" resultType="java.lang.Long">
        SELECT count(1)
        FROM ${classInfo.tableInfo.tableName}
    </select>

    <!--条件统计-->
    <select id="count" resultType="java.lang.Long" parameterType="${packageName}.entity.${classInfo.className}">
        SELECT count(1)
        FROM ${classInfo.tableInfo.tableName}
        <where>
        ${r" 1=1 "}
        <#list classInfo.fieldInfoList as fieldInfo >
            <#if fieldInfo_has_next> AND </#if>${r"<if test ='null != "}${fieldInfo.fieldName}${r"'>"}${fieldInfo.columnInfo.columnName} = ${r"#{"}${fieldInfo.columnInfo.columnName}${r"}"} ${r"</if>"}
        </#list>
        </where>
    </select>

</mapper>