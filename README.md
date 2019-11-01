
SpringBootCodeGenerator
----
在项目 SpringBoot2+Freemarker 地址:https://github.com/moshowgame/SpringBootCodeGenerator 的基础之上,自行修改的代码生成器.
<br><br>
<table><tbody>
<tr><td>访问路径</td> <td>http://127.0.0.1:1234</td></tr>
<tr><td>20191101<td>对原有DTO和SQL分类下的模板位置及结构重新编写.</td></tr>
<tr><td>20191031<td>对原有Mapper分类下的模板位置及结构重新编写.1.扩展了类实体,表实体的结构信息<br/>2.可以解析出主键,主键类型,主键生成方式.</td></tr>
<tr><td>20191030<td>1.增加了数据表实体类,表字段实体类.<br/>2.对解析SQL的逻辑进行扩展,支持联合主键,支持 NOT NULL,UNIQUE,DEFAULT 等关键字的解析.</td></tr>
<tr><td>20191030<td>对原有Entity分类下的模板位置及结构重新编写.<br>1.调整模板位置和分类位置.<br>2.调整模板结构</td></tr>
<tr><td>20191030<td>对原有建表SQL的逻辑进行重新编写.<br>1.前端新增数据库类型选项.<br>2.新增生成条件实体类进行前端条件封装<br>3.原有SQL解析流程根据数据库类型去解析,更加清晰.</td></tr>
<tr><td>20191030<td>初始化项目.</td></tr>
</tbody></table>
