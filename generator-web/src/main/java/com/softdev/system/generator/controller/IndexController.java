package com.softdev.system.generator.controller;

import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.CreateInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.util.CodeGeneratorTool;
import com.softdev.system.generator.util.FreemarkerTool;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class IndexController {

    @Autowired
    private FreemarkerTool freemarkerTool;

    @PostMapping("/genCode")
    @ResponseBody
    public ReturnT<Map<String, String>> codeGenerate(@RequestBody CreateInfo createInfo) {
        try {
            if (StringUtils.isBlank(createInfo.getTableSql())) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "表结构信息不可为空");
            }

            // 对没有设置的属性进行默认的配置
            createInfo = CreateInfo.defaultConfig(createInfo);
            log.info("createInfo:{}",createInfo);

            // 解析 为 实体类
            ClassInfo classInfo = CodeGeneratorTool.processTableIntoClassInfo(createInfo);

            log.info("{}",classInfo);

            // code genarete
            Map<String, Object> params = new HashMap<>();
            params.put("classInfo", classInfo);
            params.put("tableInfo", classInfo.getTableInfo());
            params.put("authorName", createInfo.getAuthorName());
            params.put("packageName", createInfo.getPackageName());
            params.put("returnUtil", createInfo.getReturnUtil());

            // result
            Map<String, String> result = new HashMap<String, String>();

            //Entity
            result.put("entityByLombok", freemarkerTool.processString("code-generator/entity/entityByLombok.ftl", params));
            result.put("entitySetGet", freemarkerTool.processString("code-generator/entity/entitySetGet.ftl", params));

            //MyBatis
            result.put("mybatisXml", freemarkerTool.processString("code-generator/mybatis/mybatisXml.ftl", params));
            result.put("mapper", freemarkerTool.processString("code-generator/mybatis/mapper.ftl", params));
            result.put("service", freemarkerTool.processString("code-generator/mybatis/service.ftl", params));
            result.put("serviceImpl", freemarkerTool.processString("code-generator/mybatis/serviceImpl.ftl", params));
            result.put("controller", freemarkerTool.processString("code-generator/mybatis/controller.ftl", params));

            //DTO
            result.put("entityDto", freemarkerTool.processString("code-generator/dto/entityDto.ftl", params));

            //SQL
            result.put("select", freemarkerTool.processString("code-generator/sql/select.ftl", params));
            result.put("insert", freemarkerTool.processString("code-generator/sql/insert.ftl", params));
            result.put("update", freemarkerTool.processString("code-generator/sql/update.ftl", params));
            result.put("delete", freemarkerTool.processString("code-generator/sql/delete.ftl", params));

//            //UI
//            result.put("swagger-ui", freemarkerTool.processString("code-generator/ui/swagger-ui.ftl", params));
//            result.put("element-ui", freemarkerTool.processString("code-generator/ui/element-ui.ftl", params));
//            result.put("bootstrap-ui", freemarkerTool.processString("code-generator/ui/bootstrap-ui.ftl", params));
//            //mybatis old
//
//            //jpa
//            result.put("entity", freemarkerTool.processString("code-generator/jpa/entity.ftl", params));
//            result.put("repository", freemarkerTool.processString("code-generator/jpa/repository.ftl", params));
//            result.put("jpacontroller", freemarkerTool.processString("code-generator/jpa/jpacontroller.ftl", params));
//            //jdbc template
//            result.put("jtdao", freemarkerTool.processString("code-generator/jdbc-template/jtdao.ftl", params));
//            result.put("jtdaoimpl", freemarkerTool.processString("code-generator/jdbc-template/jtdaoimpl.ftl", params));
//            //beetsql
//            result.put("beetlmd", freemarkerTool.processString("code-generator/beetlsql/beetlmd.ftl", params));
//
//            result.put("beetlcontroller", freemarkerTool.processString("code-generator/beetlsql/beetlcontroller.ftl", params));
//            //mybatis plus
//            result.put("pluscontroller", freemarkerTool.processString("code-generator/mybatis-plus/pluscontroller.ftl", params));
//            result.put("plusmapper", freemarkerTool.processString("code-generator/mybatis-plus/plusmapper.ftl", params));
//            //util
//            result.put("util", freemarkerTool.processString("code-generator/util/util.ftl", params));

            // 计算,生成代码行数
            int lineNum = 0;
            for (Map.Entry<String, String> item: result.entrySet()) {
                if (item.getValue() != null) {
                    lineNum += StringUtils.countMatches(item.getValue(), "\n");
                }
            }
            log.info("生成代码行数：{}", lineNum);
            //测试环境可自行开启
            //log.info("生成代码数据：{}", result);
            return new ReturnT<>(result);
        } catch (IOException | TemplateException e) {
            log.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "表结构解析失败"+e.getMessage());
        }
    }
}
