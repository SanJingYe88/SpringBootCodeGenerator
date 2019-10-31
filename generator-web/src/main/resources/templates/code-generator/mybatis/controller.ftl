import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
* ${classInfo.classComment}Controller
* @author ${authorName}
* @date ${.now?string('yyyy/MM/dd')}
*/
@Slf4j
@RestController
@RequestMapping(value = "/${classInfo.className?uncap_first}")
public class ${classInfo.className}Controller {

    @Autowired
    private ${classInfo.className}Service ${classInfo.className?uncap_first}Service;

    /**
    * 新增
    */
    @PostMapping("/insert")
    public ReturnT<String> insert(@RequestBody ${classInfo.className} ${classInfo.className?uncap_first}){
        ${classInfo.className?uncap_first}Service.insert(${classInfo.className?uncap_first});
        return new Result(true, StatusCode.OK, "新增成功");
    }

    /**
    * 根据主键刪除
    **/
    @DeleteMapping("/{${classInfo.primaryName}}/delete")
    public ReturnT<String> delete(@PathVariable int ${classInfo.primaryName}){
        return ${classInfo.className?uncap_first}Service.delete(${classInfo.primaryName});
    }

    /**
    * 更新
    */
    @PutMapping("/{${classInfo.primaryName}}")
    public ReturnT<String> update(${classInfo.className} ${classInfo.className?uncap_first}){
        ${classInfo.className?uncap_first}Service.update(${classInfo.className?uncap_first});
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
    * 根据主键查询
    */
    @GetMapping("/{${classInfo.primaryName}}")
    public ReturnT<String> queryById(@PathVariable int ${classInfo.primaryName}){
        ${classInfo.className} ${classInfo.className?uncap_first} = ${classInfo.className?uncap_first}Service.queryById(${classInfo.primaryName});
        if{${classInfo.className?uncap_first} == null}{
            return new Result(false, StatusCode.FAIL, "查询成功",null);
        }
        return new Result(true, StatusCode.OK, "查询成功",${classInfo.className?uncap_first});
    }

    <#--/**-->
    <#--* 分页查询-->
    <#--**/-->
    <#--@RequestMapping("/page/{pageNum}/{pageSize}")-->
    <#--public Map<String, Object> pageList(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) {-->
        <#--if(pageNum == null || pageNum.trim().length == 0){-->
            <#--pageNum = 1;-->
        <#--}-->
        <#--if(pageSize == null || pageSize.trim().length == 0){-->
            <#--pageSize = 10;-->
        <#--}-->
        <#--Page<${classInfo.className}> data = ${classInfo.className?uncap_first}Service.pageList(pageNum, pageSize);-->
        <#--PageResult<${classInfo.className}> pageResult = new PageResult<>(data.getTotalElements(),data.getContent());-->
        <#--return new Result(true, StatusCode.OK, "查询成功",pageResult);-->
    <#--}-->

    <#--/**-->
    <#--* 条件查询-->
    <#--**/-->
    <#--@PostMapping("/query")-->
    <#--public ReturnT<String> query(@RequestBody ${classInfo.className} ${classInfo.className?uncap_first}){-->
        <#--return ${classInfo.className?uncap_first}Service.query(${classInfo.className?uncap_first});-->
    <#--}-->

    /**
    * 分页条件查询
    **/
    @PostMapping("/query/{pageNum}/{pageSize}")
    public ReturnT<String> query(@RequestBody(required = false) ${classInfo.className} ${classInfo.className?uncap_first}, @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize){
        if(pageNum == null || pageNum.trim().length == 0){
           pageNum = 1;
        }
        if(pageSize == null || pageSize.trim().length == 0){
           pageSize = 10;
        }
        Page<${classInfo.className}> data = ${classInfo.className?uncap_first}Service.queryPage(${classInfo.className?uncap_first},pageNum, pageSize);
        PageResult<${classInfo.className}> pageResult = new PageResult<>(data.getTotalElements(),data.getContent());
        return new Result(true, StatusCode.OK, "查询成功",pageResult);
    }
}
