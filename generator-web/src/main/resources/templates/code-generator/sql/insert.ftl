
<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
INSERT INTO ${classInfo.tableInfo.tableName} ( <#list classInfo.fieldInfoList as fieldInfo >${fieldInfo.columnInfo.columnName}<#if fieldInfo_has_next>,</#if></#list> )
VALUES(
    <#list classInfo.fieldInfoList as fieldInfo >
    ''<#if fieldInfo_has_next>,</#if>
    </#list>
);
</#if>
