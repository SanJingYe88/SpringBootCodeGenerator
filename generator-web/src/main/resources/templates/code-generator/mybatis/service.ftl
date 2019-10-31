import java.util.Map;

/**
* ${classInfo.classComment}Service 接口
* @author ${authorName}
* @date ${.now?string('yyyy/MM/dd')}
*/
public interface ${classInfo.className}Service {

    /**
    * 新增
    */
    public ReturnT<String> insert(${classInfo.className} ${classInfo.className?uncap_first});

    /**
    * 根据主键刪除
    **/
    public ReturnT<String> deleteById(int ${classInfo.primaryName});

    /**
    * 更新
    */
    public ReturnT<String> update(${classInfo.className} ${classInfo.className?uncap_first});

    /**
    * 根据主键查询
    */
    public ${classInfo.className} queryById(int ${classInfo.primaryName});

    <#--/**-->
    <#--* 分页查询-->
    <#--**/-->
    <#--public List<${classInfo.className}> pageAll(int pageNum,int pageSize);-->

    <#--/**-->
    <#--* 条件查询-->
    <#--**/-->
    <#--public List<${classInfo.className}> query(${classInfo.className} ${classInfo.entityName});-->

    /**
    * 分页条件查询
    **/
    public List<${classInfo.className}> queryPage(${classInfo.className} ${classInfo.entityName},int pageNum,int pageSize);

    /**
    * 统计
    **/
    public Long countAll();

    /**
    * 条件统计
    **/
    public Long countAll(${classInfo.className} ${classInfo.entityName});
}
