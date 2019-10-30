import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* ${classInfo.classComment} 实体类
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
public class ${classInfo.className} implements Serializable {
    private static final long serialVersionUID = 1L;

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};    //${fieldItem.fieldComment}
</#list>
</#if>

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    public ${classInfo.className}() {
    }

<#list classInfo.fieldList as fieldItem>
    public ${fieldItem.fieldClass} get${fieldItem.fieldName?cap_first}() {
        return ${fieldItem.fieldName};
    }
    public void set${fieldItem.fieldName?cap_first}(${fieldItem.fieldClass} ${fieldItem.fieldName}) {
        this.${fieldItem.fieldName} = ${fieldItem.fieldName};
    }
</#list>
</#if>
}