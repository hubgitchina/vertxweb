<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>新增-菜品</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/static/layui/css/layui.css" media="all">
</head>

<style>
    .text-right {
        text-align: center;
    }
</style>

<body>

<div class="layui-container" style="width: 100%;">
    <div class="layui-row" style="margin-top: 20px;">
        <form class="layui-form">
            <div class="layui-form-item">
                <div class="layui-inline" style="width: 80%">
                    <label class="layui-form-label">菜名</label>
                    <div class="layui-input-block">
                        <input type="hidden" id="oldId" name="oldId" value="${oldId!}">
                        <input type="text" name="dish_name" required lay-verify="required" placeholder="请输入菜名"
                               autocomplete="off" class="layui-input">
                    </div>
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-inline" style="width: 80%">
                    <label class="layui-form-label">规格</label>
                    <div class="layui-input-block">
                        <select name="category" id="category" lay-filter="category" lay-search=""
                                lay-verify="required">
                            <option value="">请选择规格</option>
                            <option value="1">大份</option>
                            <option value="2">中份</option>
                            <option value="3">小份</option>
                        </select>
                    </div>
                </div>
            </div>

            <div class="layui-form-item text-right ">
                <button class="layui-btn" lay-filter="addFood" lay-submit>保存</button>
                <button class="layui-btn layui-btn-primary" type="button" ew-event="cancelBtn" id="cancelBtn">
                    取消
                </button>
            </div>
        </form>
    </div>
</div>

<script src="/static/layui/layui.js"></script>

<script charset="utf-8">

    layui.use(['form', 'layer', 'util'], function () {
        var $ = layui.jquery;
        var form = layui.form;
        var layer = layui.layer;
        var util = layui.util;

        var uuid = function () {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        }

        form.on('submit(addFood)', function (d) {
            // 组装数据
            var record = {
                id: uuid()
                , dishName: d.field.dish_name
                , category: d.field.category
            };

            var oldId = d.field.oldId;
            if(oldId){
                parent.deleteTableData(oldId);
            }

            for(var i=0; i<parent.tableData.length; i++){
                var tempData = parent.tableData[i];
                if(tempData.dishName == record.dishName){
                    parent.layer.alert("菜品已存在", {
                        icon: 5,
                        btnAlign: 'c', //按钮居中
                        title: "提示"
                    });

                    return false;
                }
            }

            // parent.layui.customTableData.push(record);
            // parent.layui.table.reload('custom_table', {data: parent.layui.customTableData});

            parent.tableData.push(record);
            parent.layui.table.reload('data_table', {data: parent.tableData});

            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
            parent.layer.msg("新增成功！", {icon: 1});

            return false;
        });

        $(document).on('click', '#cancelBtn', function () {
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
        });
    });
</script>
</body>
</html>