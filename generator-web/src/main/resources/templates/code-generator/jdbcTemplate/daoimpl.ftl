import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* ${classInfo.classComment} Dao 实现
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
@Repository
public class ${classInfo.className}DaoImpl implements ${classInfo.className}Dao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 新增
     */
    @Override
    public int add(${classInfo.className} ${classInfo.className?uncap_first}) {
        String sql = "insert into ${classInfo.tableInfo.tableName} (<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0><#list classInfo.fieldInfoList as fieldInfo >${fieldInfo.columnInfo.columnName}<#if fieldInfo_has_next>,</#if></#list></#if> ) "
                      + " values (<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0><#list classInfo.fieldInfoList as fieldInfo >?<#if fieldInfo_has_next>,</#if></#list></#if> )";
        return jdbcTemplate.update(sql,<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0><#list classInfo.fieldInfoList as fieldInfo >${classInfo.className?uncap_first}.get${fieldInfo.fieldName?cap_first}()<#if fieldInfo_has_next>,</#if></#list></#if>);
    }

    /**
     * 修改
     */
    @Override
    public int update(${classInfo.className} ${classInfo.className?uncap_first}) {
        String sql = "UPDATE  ${classInfo.tableInfo.tableName}  SET <#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0><#list classInfo.fieldInfoList as fieldInfo ><#if fieldInfo_index gt 0 >${fieldInfo.columnInfo.columnName}=?<#if fieldInfo_has_next>,</#if></#if></#list></#if>"
                      +" where <#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0><#list classInfo.fieldInfoList as fieldInfo ><#if fieldInfo_index = 0>${fieldInfo.columnInfo.columnName}=?<#break ></#if></#list></#if>";
        return jdbcTemplate.update(sql,<#if classInfo.fieldInfoList?exists && classInfo.fieldInfoList?size gt 0><#list classInfo.fieldInfoList as fieldInfo ><#if fieldInfo_index gt 0 >${classInfo.className?uncap_first}.get${fieldInfo.fieldName?cap_first}(),</#if></#list><#list classInfo.fieldInfoList as fieldInfo ><#if fieldInfo_index = 0 >${classInfo.className?uncap_first}.get${fieldInfo.fieldName?cap_first}()</#if></#list></#if>);
    }

    /**
     * 删除
     */
    @Override
    public int delete(${classInfo.primaryType} ${classInfo.primaryName}) {
        String sql = "DELETE from ${classInfo.tableInfo.tableName} where ${classInfo.tableInfo.primaryKey} = ?";
        return jdbcTemplate.delete(sql,${classInfo.primaryName});
    }

    /**
     * 主键查询
     */
    @Override
    public ${classInfo.className} findById(${classInfo.primaryType} ${classInfo.primaryName}) {
        String sql = "select * from ${classInfo.tableInfo.tableName} where ${classInfo.tableInfo.primaryKey} = ?";
        List<${classInfo.className}> list = jdbcTemplate.query(sql, new Object[]{${classInfo.primaryName}}, new BeanPropertyRowMapper<${classInfo.className}>(${classInfo.className}.class));
        if(list!=null && list.size()>0){
            ${classInfo.className} ${classInfo.className?uncap_first} = list.get(0);
            return ${classInfo.className?uncap_first};
        }else{
             return null;
        }
    }

    /**
     * 条件查询
     */
    @Override
    public List<${classInfo.className}> findAllList(Map<String,Object> params) {
        String sql = "select * from ${classInfo.tableInfo.tableName}";
        List<${classInfo.className}> list = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<${classInfo.className}>(${classInfo.className}.class));
        return list != null ? list : Collections.EMPTY_LIST;
    }
}
