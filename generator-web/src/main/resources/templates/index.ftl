<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>代码自动生成</title>
    <meta name="keywords" content="sql转实体类,sql转DAO,SQL转service,SQL转JPA实现,SQL转MYBATIS实现">
    <#import "common/common-import.ftl" as netCommon>
    <@netCommon.commonStyle />

    <@netCommon.commonScript />
<#--<script src="${request.contextPath}/static/js/index-new.js"></script>-->
    <script>
        $(function () {
            /**
             * 初始化 table sql 3
             */
            var ddlSqlArea = CodeMirror.fromTextArea(document.getElementById("ddlSqlArea"), {
                lineNumbers: true,
                matchBrackets: true,
                mode: "text/x-sql",
                lineWrapping: false,
                readOnly: false,
                foldGutter: true,
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
            });
            ddlSqlArea.setSize('auto', 'auto');
            // controller_ide
            var genCodeArea = CodeMirror.fromTextArea(document.getElementById("genCodeArea"), {
                lineNumbers: true,
                matchBrackets: true,
                mode: "text/x-java",
                lineWrapping: true,
                readOnly: true,
                foldGutter: true,
                gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"]
            });
            genCodeArea.setSize('auto', 'auto');

            var codeData;

            /**
             * 生成代码
             */
            $('#btnGenCode').click(function () {
                var tableSql = ddlSqlArea.getValue();
                $.ajax({
                    type: 'POST',
                    url: base_url + "/genCode",
                    data: JSON.stringify({
                        "tableSql": tableSql,
                        "packageName": $("#packageName").val(),
                        "returnUtil": $("#returnUtil").val(),
                        "authorName": $("#authorName").val(),
                        "canUnderLineToCamelCase": $("#isUnderLineToCamelCase").val(),
                        "databaseType": $("#databaseType").val()
                    }),
                    dataType: "json",
                    contentType: "application/json;charset-UTF-8",
                    success: function (data) {
                        if (data.code == 200) {
                            layer.open({
                                icon: '1',
                                content: "代码生成成功",
                                end: function () {
                                    codeData = data.data;
                                    genCodeArea.setValue(codeData.beetlentity);
                                    genCodeArea.setSize('auto', 'auto');
                                }
                            });
                        } else {
                            layer.open({
                                icon: '2',
                                content: (data.msg || '代码生成失败')
                            });
                        }
                    }
                });
            });
            /**
             * 按钮事件组
             */
            $('.generator').bind('click', function () {
                if (!$.isEmptyObject(codeData)) {
                    var id = this.id;
                    genCodeArea.setValue(codeData[id]);
                    genCodeArea.setSize('auto', 'auto');
                }
            });
            function donate() {
                layer.open({
                    type: 1,
                    area: ['712px', '480px'],
                    shadeClose: true, //点击遮罩关闭
                    content: '<img src="http://upyun.bejson.com/img/zhengkai.png"></img>'
                });
            }
            $('#donate1').on('click', function () {
                donate();
            });
            $('#donate2').on('click', function () {
                donate();
            });
        });
    </script>
</head>
<body style="background-color: #e9ecef">
    <div class="container">
        <nav class="navbar navbar-dark bg-primary btn-lg">
            <a class="navbar-brand" href="http://www.bejson.com">BeJSON在线工具站</a>
            <ul class="nav navbar-nav">
                <li class="nav-item active">
                    <a class="nav-link" href=""></a>
                </li>
            </ul>
        </nav>
    </div>
    <div class="jumbotron">
        <div class="container">
            <h2>Spring Boot Code Generator!</h2>
            <p class="lead">
                基于<code>SpringBoot2</code>+<code>Freemarker</code>的代码生成器，用<code>DDL SQL</code>语句生成<code>JPA</code>/<code>JdbcTemplate</code>/<code>Mybatis</code>/<code>MybatisPlus</code>/<code>BeetlSQL</code>相关代码，支持<code>mysql</code>/<code>oracle</code>/<code>pgsql</code>三大数据库。以<code>释放双手</code>为目的，各大模板也在陆续补充和优化。欢迎大家多多提交模板和交流想法，如果发现有SQL语句不能识别，请<a
                    href="https://github.com/moshowgame/SpringBootCodeGenerator/issues">留言</a>给我分析，同时欢迎大家进行<a
                    href="https://github.com/moshowgame/SpringBootCodeGenerator/pulls">PullRequest</a>和<a href="#" id="donate1">赞赏</a>，谢谢！
            </p>
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">作者名称</span>
                </div>
                <input type="text" class="form-control" id="authorName" name="authorName" placeholder="">
                <div class="input-group-prepend">
                    <span class="input-group-text">包名路径</span>
                </div>
                <input type="text" class="form-control" id="packageName" name="packageName" placeholder="it.com">
            </div>
            <div class="input-group mb-3">
                <div class="input-group-prepend">
                    <span class="input-group-text">返回封装</span>
                </div>
                <input type="text" class="form-control" id="returnUtil" name="returnUtil" placeholder="ApiReturnObject">
                <div class="input-group-prepend">
                    <span class="input-group-text">是否下划线转换为驼峰</span>
                </div>
                <select type="text" class="form-control" id="isUnderLineToCamelCase" name="isUnderLineToCamelCase">
                    <option value="true">转换</option>
                    <option value="false">不转换</option>
                </select>
                <div class="input-group-prepend">
                    <span class="input-group-text">使用的数据库类型</span>
                </div>
                <select type="text" class="form-control" id="databaseType" name="databaseType">
                    <option value="mysql">MySQL</option>
                    <option value="oracle">Oracle</option>
                    <option value="pgsql">PgSQL</option>
                </select>
            </div>
            <textarea id="ddlSqlArea" placeholder="请输入表结构信息..." class="form-control btn-lg" style="height: 250px;">
                CREATE TABLE `userinfo` (
                  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                  `username` varchar(255) NOT NULL COMMENT '用户名',
                  `addtime` datetime NOT NULL COMMENT '创建时间',
                  PRIMARY KEY (`user_id`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息'
            </textarea><br>
            <p><button class="btn btn-primary btn-lg disabled" id="btnGenCode" role="button">开始生成 »</button></p>
            <hr>
            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">Entity</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="entitySetGet">entity(set/get)</button>
                        <button type="button" class="btn btn-default generator" id="entityByLombok">entity(lombok)</button>
                    </div>
                </div>
                <div class="btn-toolbar col-md-7" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">Mybatis</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="mybatisXml">mybatis.xml</button>
                        <button type="button" class="btn btn-default generator" id="mapper">mapper接口</button>
                        <button type="button" class="btn btn-default generator" id="service">service接口</button>
                        <button type="button" class="btn btn-default generator" id="serviceImpl">serviceImpl</button>
                        <button type="button" class="btn btn-default generator" id="controller">controller</button>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">MybatisPlus</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="plusmapper">mapper</button>
                        <button type="button" class="btn btn-default generator" id="pluscontroller">controller</button>
                    </div>
                </div>

                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">UI</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="swagger-ui">swagger-ui</button>
                        <button type="button" class="btn btn-default generator" id="element-ui">element-ui</button>
                        <button type="button" class="btn btn-default generator" id="bootstrap-ui">bootstrap-ui</button>
                    </div>
                </div>
            </div>

            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">BeetlSQL</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="beetlmd">beetlmd</button>
                        <button type="button" class="btn btn-default generator" id="beetlcontroller">beetlcontroller
                        </button>
                    </div>
                </div>
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">JPA</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="entity">jpa-entity</button>
                        <button type="button" class="btn btn-default generator" id="repository">repository</button>
                        <button type="button" class="btn btn-default generator" id="jpacontroller">controller</button>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">JdbcTemplate</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="jtdaoimpl">daoimpl</button>
                        <button type="button" class="btn btn-default generator" id="jtdao">dao</button>
                    </div>
                </div>
                <div class="btn-toolbar col-md-7" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">SQL</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="select">select</button>
                        <button type="button" class="btn btn-default generator" id="insert">insert</button>
                        <button type="button" class="btn btn-default generator" id="update">update</button>
                        <button type="button" class="btn btn-default generator" id="delete">delete</button>
                    </div>
                </div>
            </div>
            <div class="row" style="margin-top: 10px;">
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">DTO</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="entityDto">entityDto(lombok+swagger)</button>
                    </div>
                </div>
                <div class="btn-toolbar col-md-5" role="toolbar" aria-label="Toolbar with button groups">
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <div class="btn btn-secondary disabled" id="btnGroupAddon">Util</div>
                        </div>
                    </div>
                    <div class="btn-group" role="group" aria-label="First group">
                        <button type="button" class="btn btn-default generator" id="util">bean get set</button>
                    </div>
                </div>
            </div>
            <hr>
            <textarea id="genCodeArea" class="form-control btn-lg"></textarea>
        </div>
    </div>
    <div class="container">
        <hr>
        <footer>
            <footer class="bd-footer text-muted" role="contentinfo">
                <div class="container">
                    <strong>Copyright &copy; ${.now?string('yyyy')}-2022 &nbsp;
                   <p><a href="https://github.com/moshowgame/SpringBootCodeGenerator">SpringBootCodeGenerator</a>
                            由<a href="https://blog.csdn.net/moshowgame" target="_blank">@Moshow/大狼狗/郑锴</a> 开发维护。
                            由<a href="https://www.bejson.com">BeJson三叔 </a> 提供在线版本。
                            点击<a href="#" id="donate2">赞赏</a>。</p>
                </div>
            </footer>
        </footer>
    </div>
</body>
</html>
