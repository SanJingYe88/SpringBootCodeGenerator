import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* ${classInfo.classComment}Service 实现类
* @author ${authorName}
* @date ${.now?string('yyyy/MM/dd')}
*/
@Slf4j
@Service
@Transactional
public class ${classInfo.className}ServiceImpl implements ${classInfo.className}Service {

	@Autowired
	private ${classInfo.className}Mapper ${classInfo.className?uncap_first}Mapper;

    /**
    * 新增
    */
	@Override
	public void insert(${classInfo.className} ${classInfo.className?uncap_first}) {
        return ${classInfo.className?uncap_first}Mapper.insert(${classInfo.className?uncap_first});
	}

    /**
    * 根据主键刪除
    **/
	@Override
	public int deleteById(int ${classInfo.primaryName}) {
		return ${classInfo.className?uncap_first}Mapper.deleteById(id);
	}

	/**
	* 更新
	*/
	@Override
	public int update(${classInfo.className} ${classInfo.className?uncap_first}) {
		return ${classInfo.className?uncap_first}Mapper.update(${classInfo.className?uncap_first});
	}

	/**
	* 根据主键查询
	*/
	@Override
	public ${classInfo.className} queryById(int ${classInfo.primaryName}) {
		return ${classInfo.className?uncap_first}Mapper.queryById(${classInfo.primaryName});
	}

	<#--/**-->
	<#--* 分页查询-->
	<#--**/-->
	<#--@Override-->
	<#--public Map<String,Object> pageAll(int pageNum,int pageSize) {-->
		<#--List<${classInfo.className}> pageList = ${classInfo.className?uncap_first}Mapper.pageAll(pageNum, pageSize);-->
		<#--int totalCount = ${classInfo.className?uncap_first}Mapper.countAll();-->
		<#--Map<String, Object> result = new HashMap<String, Object>();-->
		<#--result.put("pageList", pageList);-->
		<#--result.put("totalCount", totalCount);-->
		<#--return result;-->
	<#--}-->

	<#--/**-->
	<#--* 条件查询-->
	<#--**/-->
	<#--@Override-->
	<#--public Map<String, Object> query(${classInfo.className} ${classInfo.entityName}){-->
		<#--List<${classInfo.className}> pageList = ${classInfo.className?uncap_first}Mapper.query(${classInfo.entityName});-->
		<#--int totalCount = ${classInfo.className?uncap_first}Mapper.countAll(${classInfo.entityName});-->
		<#--Map<String, Object> result = new HashMap<String, Object>();-->
		<#--result.put("pageList", pageList);-->
		<#--result.put("totalCount", totalCount);-->
		<#--return result;-->
	<#--}-->

	/**
	* 分页条件查询
	**/
	public Map<String, Object> queryPage(${classInfo.className} ${classInfo.entityName},int pageNum,int pageSize){
		List<${classInfo.className}> pageList = ${classInfo.className?uncap_first}Mapper.queryPage(${classInfo.entityName},int pageNum,int pageSize);
		int totalCount = ${classInfo.className?uncap_first}Mapper.countAll(${classInfo.entityName});
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("pageList", pageList);
		result.put("totalCount", totalCount);
		return result;
	}
}
