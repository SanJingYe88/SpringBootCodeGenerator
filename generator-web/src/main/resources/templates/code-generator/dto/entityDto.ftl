import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * ${classInfo.classComment}
 * @author ${authorName} ${.now?string('yyyy-MM-dd')}
 */
@Data
@ApiModel("${classInfo.classComment}")
public class ${classInfo.className}DTO {

<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0>
<#list classInfo.fieldInfoList as fieldInfo >
    @ApiModelProperty("${fieldInfo.fieldComment}")
    private ${fieldInfo.fieldClass} ${fieldInfo.fieldName};
</#list>

    public ${classInfo.className}() {
    }
</#if>

}