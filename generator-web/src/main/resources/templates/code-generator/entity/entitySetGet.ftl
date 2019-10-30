import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* ${classInfo.classComment} 实体类
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
public class ${classInfo.className} implements Serializable {
    private static final long serialVersionUID = 1L;

<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
<#list classInfo.fieldInfoList as fieldInfo >
    private ${fieldInfo.fieldClass} ${fieldInfo.fieldName};    //${fieldInfo.fieldComment}<#if fieldInfo.columnInfo.canPrimary> 主键</#if>
</#list>
</#if>

<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
    public ${classInfo.className}() {
    }

<#list classInfo.fieldInfoList as fieldInfo>
    public ${fieldInfo.fieldClass} get${fieldInfo.fieldName?cap_first}() {
        return ${fieldInfo.fieldName};
    }
    public void set${fieldInfo.fieldName?cap_first}(${fieldInfo.fieldClass} ${fieldInfo.fieldName}) {
        this.${fieldInfo.fieldName} = ${fieldInfo.fieldName};
    }
</#list>
</#if>
}