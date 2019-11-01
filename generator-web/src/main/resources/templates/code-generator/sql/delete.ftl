
<!--根据主键删除-->
DELETE FROM ${classInfo.tableInfo.tableName}
WHERE ${classInfo.tableInfo.primaryKey} = ''

<!--条件删除-->
<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
DELETE FROM ${classInfo.tableInfo.tableName}
WHERE
<#list classInfo.fieldInfoList as fieldInfo >
    ${fieldInfo.columnInfo.columnName} = ''<#if fieldInfo_has_next>,</#if>
</#list>;
</#if>