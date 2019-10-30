package com.softdev.system.generator.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * freemarker 操作工具类
 *
 * @author xuxueli 2018-05-02 19:56:00
 */
@Component
public class FreemarkerTool {

    @Autowired
    private Configuration configuration;

    /**
     * 根据模板名解析生成字符串
     *
     * @param templateName 模板名
     * @param params 参数
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String processString(String templateName, Map<String, Object> params)  throws IOException, TemplateException {
        Template template = configuration.getTemplate(templateName);
        return this.processTemplateIntoString(template, params);
    }

    /**
     * 根据模板解析生成字符串
     *
     * @param template 模板
     * @param model 参数
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    private String processTemplateIntoString(Template template, Object model) throws IOException, TemplateException {
        StringWriter result = new StringWriter();
        template.process(model, result);
        return result.toString();
    }
}
