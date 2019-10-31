import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
* ${classInfo.classComment}
* @author ${authorName}
* @date ${.now?string('yyyy/MM/dd')}
*/
@Mapper
@Repository
public interface ${classInfo.className}Mapper {

    /**
    * 新增
    **/
    int insert(${classInfo.className} ${classInfo.className?uncap_first});

    /**
    * 根据主键刪除
    **/
    int deleteById(int ${classInfo.primaryName});

    /**
    * 更新
    **/
    int update(${classInfo.className} ${classInfo.className?uncap_first});

    /**
    * 根据主键查询
    **/
    ${classInfo.className} queryById(int ${classInfo.primaryName});

    <#--/**-->
    <#--* 分页查询-->
    <#--**/-->
    <#--List<${classInfo.className}> pageAll(int pageNum,int pageSize);-->

    <#--/**-->
    <#--* 条件查询-->
    <#--**/-->
    <#--List<${classInfo.className}> query(${classInfo.className} ${classInfo.entityName});-->

    /**
    * 分页条件查询
    **/
    List<${classInfo.className}> queryPage(${classInfo.className} ${classInfo.entityName},int pageNum,int pageSize);

    /**
    * 统计
    **/
    Long countAll();

    /**
    * 条件统计
    **/
    Long countAll(${classInfo.className} ${classInfo.entityName});
}
