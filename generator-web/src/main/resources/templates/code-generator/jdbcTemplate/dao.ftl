
import java.util.List;
import java.util.Map;

/**
* ${classInfo.classComment} Dao 接口
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
public interface ${classInfo.className}DAO {

    /**
     * 新增
     */
    int add(${classInfo.className} ${classInfo.className?uncap_first});

    /**
     * 修改
     */
    int update(${classInfo.className} ${classInfo.className?uncap_first});

    /**
     * 删除
     */
    int delete(${classInfo.primaryType} ${classInfo.primaryName});

    /**
     * 主键查询
     */
    ${classInfo.className} findById(${classInfo.primaryType} ${classInfo.primaryName});

    /**
     * 条件查询+分页
     */
    List<${classInfo.className}> pageQuery(Map<String,Object> param, int PageNum, int pageSize);
}
