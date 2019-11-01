<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
UPDATE ${classInfo.tableInfo.tableName} SET
<#list classInfo.fieldInfoList as fieldInfo >
    ${fieldInfo.columnInfo.columnName} = ''<#if fieldInfo_has_next>,</#if>
</#list>
WHERE
<#list classInfo.fieldInfoList as fieldInfo >
    ${fieldInfo.columnInfo.columnName} = ''<#if fieldInfo_has_next>,</#if>
</#list>;
</#if>
