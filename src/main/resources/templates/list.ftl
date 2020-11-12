<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>数据列表页面</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/static/layui/css/layui.css" media="all">
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

<div class="layui-container">
    <div class="layui-row">
        <div class="layui-col-md12">
            <h1>${msg}</h1>
        </div>
        <div class="layui-col-md12">
            <table class="layui-hide" id="data_table" lay-filter="data_table"></table>
        </div>
    </div>
</div>

<script src="/static/layui/layui.js"></script>
<script charset="utf-8">

    layui.use(['layer', 'form', 'table'], function () {
        var $ = layui.jquery;
        var layer = layui.layer;
        var form = layui.form;
        var table = layui.table;

        table.render({
            limit: 10,
            elem: '#data_table',
            url: "/main/getList",
            // where: {
            //     voucherId: voucherId
            // },
            title: '数据列表',
            method: 'post',
            height: '489',
            page: true,
            cols: [[
                {field: 'id', title: 'ID', align: 'center', hide: true}
                , {field: 'type', title: '餐别', align: 'center'}
                , {
                    field: 'monday', title: '${monday}<br/>星期一', align: 'center'
                    , templet: function (d) {
                        return d.monday;
                    }
                }
                , {field: 'tuesday', title: '${tuesday}<br/>星期二', align: 'center'}
                , {field: 'wednesday', title: '${wednesday}<br/>星期三', align: 'center'}
                , {field: 'thursday', title: '${thursday}<br/>星期四', align: 'center'}
                , {field: 'friday', title: '${friday}<br/>星期五', align: 'center'}
                , {field: 'saturday', title: '${saturday}<br/>星期六', align: 'center'}
                , {field: 'sunday', title: '${sunday}<br/>星期天', align: 'center'}
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
            }, done: function (res, curr, count) {
                $('th').css({'background-color': '#009688', 'color': '#fff', 'font-weight': 'bold'});
                res.data.forEach(function (item, index) {
                    //如果是已选择，则设置单元格背景色
                    if (item.isMonday == 1) {
                        var tr = $(".layui-table-body tbody tr[data-index='" + index + "']");
                        tr.find(".laytable-cell-1-0-2").css("background-color", "#eee");
                    }
                    if (item.isTuesday == 1) {
                        var tr = $(".layui-table-body tbody tr[data-index='" + index + "']");
                        tr.find(".laytable-cell-1-0-3").css("background-color", "#eee");
                    }
                    if (item.isWednesday == 1) {
                        var tr = $(".layui-table-body tbody tr[data-index='" + index + "']");
                        tr.find(".laytable-cell-1-0-4").css("background-color", "#eee");
                    }
                    if (item.isThursday == 1) {
                        var tr = $(".layui-table-body tbody tr[data-index='" + index + "']");
                        tr.find(".laytable-cell-1-0-5").css("background-color", "#eee");
                    }
                    if (item.isFriday == 1) {
                        var tr = $(".layui-table-body tbody tr[data-index='" + index + "']");
                        tr.find(".laytable-cell-1-0-6").css("background-color", "#eee");
                    }

                });
            }
        });
    });
</script>
</body>
</html>