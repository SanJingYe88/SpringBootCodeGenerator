<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
    SELECT
    <#list classInfo.fieldInfoList as fieldInfo >
        ${fieldInfo.columnInfo.columnName}<#if fieldInfo_has_next>,</#if>
    </#list>
    FROM
        ${classInfo.tableInfo.tableName}
    WHERE
    <#list classInfo.fieldInfoList as fieldInfo >
        <#if fieldInfo_index != 0> AND </#if>${fieldInfo.columnInfo.columnName} = ''
    </#list>;
</#if>

