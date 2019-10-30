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

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};    //${fieldItem.fieldComment}
</#list>
</#if>
}