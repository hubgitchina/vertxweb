<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>菜谱发布页面</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/static/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="/static/css/admin.css" media="all">
</head>

<style type="text/css">
    {
        # 设置table每一行的height #
    }
    .layui-table-cell {
        height: auto;
        line-height: 28px;
    }
</style>

<body>

<div class="layui-fluid" style="padding: 15px; background-color: #F2F2F2;">
    <#--<div class="layui-container">-->
    <div class="layui-row layui-col-space15">

        <div class="layui-col-md12">
            <div class="layui-card">
                <div class="layui-card-header"><h2>菜谱记录</h2></div>
                <div class="layui-card-body">
                    <table class="layui-hide" id="data_table" lay-filter="data_table"></table>
                </div>
            </div>
        </div>

    </div>

    <#--<hr class="layui-bg-green">-->

    <#--<div class="layui-col-md12">-->
    <#--<ul class="layui-timeline" id="ul_timeline">-->

    <#--</ul>-->
    <#--</div>-->
</div>

<script id="recipesToolbar" type="text/html">
    <button class="layui-btn" type="button" lay-event="toAdd">新增</button>
</script>

<#-- 操作 -->
<script id="recipesBar" type="text/html">
    <button class="layui-btn layui-btn-xs layui-btn-primary" lay-event="look">查看</button>

    {{# if(d.status == 0){ }}
    <button class="layui-btn layui-btn-xs" lay-event="edit">编辑</button>
    <button class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del">删除</button>
    <button class="layui-btn layui-btn-xs layui-btn-normal" lay-event="publish">发布</button>
    {{# } else { }}
    <#--<button class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del">删除</button>-->
    {{# } }}
</script>

<script src="/static/layui/layui.js"></script>
<script charset="utf-8">

    layui.use(['layer', 'form', 'table', 'util'], function () {
        var $ = layui.jquery;
        var layer = layui.layer;
        var form = layui.form;
        var table = layui.table;
        var util = layui.util;

        table.render({
            limit: 10,
            elem: '#data_table',
            url: "/recipes/queryRecipesPublishPage",
            // where: {
            //     voucherId: voucherId
            // },
            title: '数据列表',
            toolbar: '#recipesToolbar',
            defaultToolbar: [],
            method: 'post',
            // height: '489',
            // skin: 'nob',
            // even: true,
            page: true,
            cols: [[
                {type: 'numbers', title: '序号', align: 'center'}
                , {field: 'id', title: 'ID', align: 'center', hide: true}
                , {field: 'create_by', title: '发布人', align: 'center'}
                , {
                    field: 'create_date', title: '创建时间', align: 'center'
                    , templet: function (d) {
                        if (d.create_date) {
                            return util.toDateString(d.create_date, 'yyyy-MM-dd HH:mm:ss');
                        } else {
                            return '';
                        }
                    }
                }
                , {
                    field: 'publish_time', title: '发布时间', align: 'center'
                    , templet: function (d) {
                        if (d.publish_time) {
                            return util.toDateString(d.publish_time, 'yyyy-MM-dd HH:mm:ss');
                        } else {
                            return '';
                        }
                    }
                }
                , {
                    field: 'status', title: '发布状态', align: 'center'
                    , templet: function (d) {
                        if (d.status == 0) {
                            return '<del style="color: #FF5722;"><span class="layui-btn layui-btn-xs layui-btn-radius layui-btn-danger">未发布</span></del>';
                        } else if (d.status == 1) {
                            return '<span class="layui-btn layui-btn-xs layui-btn-radius layui-btn-normal">已发布</span>';
                        } else {
                            return '';
                        }
                    }
                }
                , {
                    title: '标识', align: 'center'
                    , templet: function (d) {
                        if (d.contained == 0) {
                            return '<span style="color: #FF5722;">本周</span>';
                        } else if (d.contained == 1) {
                            return '<span style="color: #5FB878;">未来</span>';
                        } else {
                            return '<del style="color: #666;">历史</del>';
                        }
                    }
                }
                , {field: 'start_date', title: '开始日期', align: 'center'}
                , {field: 'end_date', title: '结束日期', align: 'center'}
                , {fixed: 'right', title: '操作', minWidth: 220, align: 'center', toolbar: '#recipesBar'}
            ]]
            , response: {
                statusCode: 200 //重新规定成功的状态码为 200，table 组件默认为 0
            }
            , parseData: function (res) { //将原始数据解析成 table 组件所规定的数据
                return {
                    "code": res.code, //解析接口状态
                    "msg": res.msg, //解析提示文本
                    "count": res.total, //解析数据长度
                    "data": res.data //解析数据列表
                };
            }
        });

        //监听表头工具栏按钮事件
        table.on('toolbar(data_table)', function (obj) {
            // var checkRow = table.checkStatus(obj.config.id).data;
            // if (!checkRow.length) {
            //     layer.msg("请先选择数据");
            //     return;
            // }
            switch (obj.event) {
                case 'toAdd':
                    doAdd();
                    break;
                case 'toDelete':

                    break;
                case 'toChange':

                    break;
            }
            ;
        });

        //监听表格行操作栏工具按钮事件
        table.on('tool(data_table)', function (obj) {
            var f = obj.data;
            if (obj.event === 'look') {
                lookRecipes(f.id);
            } else if (obj.event === 'edit') {
                editRecipes(f.id);
            } else if (obj.event === 'del') {
                deleteRecipes(f.id);
            } else if (obj.event === 'publish') {
                publishRecipes(f.id);
            }
        });

        function doAdd() {
            parent.layer.msg("开发中，请等待");
            return;
        }

        function publishRecipes(id) {
            layer.confirm("发布之后，菜谱将不能再做修改或删除操作，<br />确定进行发布操作吗？", {btnAlign: 'c', title:'提示'}, function (index) {

                var param = {
                    id: id
                }

                var msgIndex = layer.msg('系统处理中，请等待...', {shade: [0.8, '#393D49'], icon: 16, time: false});

                $.ajax({
                    type: "POST",
                    url: "/recipes/publishRecipes",
                    contentType: "application/json; charset=utf-8",
                    async: true,
                    data: JSON.stringify(param),
                    dataType: "json",
                    success: function (result) {
                        layer.close(msgIndex);
                        if (result.code == 200) {
                            layer.msg('操作成功');

                            table.reload('data_table');
                        } else {
                            layer.alert("操作失败，" + result.msg, {
                                icon: 5,
                                btnAlign: 'c', //按钮居中
                                title: "提示"
                            });
                        }
                    },
                    error: function (msg) {
                        layer.close(msgIndex);

                        layer.alert("操作失败: " + msg.responseText, {
                            icon: 5,
                            btnAlign: 'c', //按钮居中
                            title: "提示"
                        });
                    }
                });

                layer.close(index);
            });
        }
    });
</script>
</body>
</html>