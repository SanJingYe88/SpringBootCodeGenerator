import java.io.Serializable;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
* ${classInfo.classComment} 实体类
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ${classInfo.className} implements Serializable {
    private static final long serialVersionUID = 1L;

<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
<#list classInfo.fieldInfoList as fieldInfo >
    private ${fieldInfo.fieldClass} ${fieldInfo.fieldName};    //${fieldInfo.fieldComment}<#if fieldInfo.columnInfo.canPrimary> 主键</#if>
</#list>
</#if>
}