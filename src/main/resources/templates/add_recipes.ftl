<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>新增-菜谱</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/static/layui/css/layui.css" media="all">
</head>

<style>
    {
        # 设置table每一行的height #
    }
    .layui-table-cell {
        height: auto;
        line-height: 28px;
    }

    .text-right {
        text-align: center;
    }
</style>

<body>

<div class="layui-container" style="width: 100%;">
    <div class="layui-col-md12">
        <form lay-filter="othersForm" class="layui-form model-form">
            <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
                <legend>日期范围（默认以周为单位）</legend>
            </fieldset>
            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label">开始日期<b style="color:red">*</b></label>
                    <div class="layui-input-inline">
                        <input type="text" name="startDate" maxlength="200" class="layui-input"
                               placeholder="请输入开始日期" value="${monday!}"
                               lay-verify="required" required readonly>
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label">结束日期<b style="color:red">*</b></label>
                    <div class="layui-input-inline">
                        <input type="text" name="endDate" maxlength="200" class="layui-input"
                               placeholder="请输入结束日期" value="${sunday!}"
                               lay-verify="required" required readonly>
                    </div>
                </div>
            </div>

            <fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
                <legend>菜谱详情<b style="color:red">*</b></legend>
            </fieldset>
            <div class="layui-form-item">
                <table class="layui-hide" id="data_table" lay-filter="data_table"></table>
            </div>

            <div class="layui-form-item text-right ">
                <button class="layui-btn" lay-filter="formSubmit" lay-submit>保存</button>
                <button class="layui-btn layui-btn-primary" type="button" ew-event="cancelBtn" id="cancelBtn">
                    取消
                </button>
            </div>
        </form>
    </div>
</div>

<script src="/static/layui/layui.js"></script>
<script src="/static/js/form-verify.js" charset="utf-8"></script>

<script charset="utf-8">

    var tableData = new Array();

    layui.use(['form', 'table', 'layer', 'util', 'laydate'], function () {
        var $ = layui.jquery;
        var form = layui.form;
        var table = layui.table;
        var layer = layui.layer;
        var util = layui.util;
        var laydate = layui.laydate;

        // 初始化时间控件
        laydate.render({
            elem: '#entryDate',
            type: 'datetime',
            trigger: 'click' //加上该属性防止表单进行非空验证时，点击日历控件不显示的bug
        });

        var uuid = function () {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        }

        function initTableBase() {
            var recordOne = {
                id: uuid()
                , type: "早餐"
                , monday: []
                , tuesday: []
                , wednesday: []
                , thursday: []
                , friday: []
                , saturday: []
                , sunday: []
            };

            var recordTwo = {
                id: uuid()
                , type: "午餐"
                , monday: []
                , tuesday: []
                , wednesday: []
                , thursday: []
                , friday: []
                , saturday: []
                , sunday: []
            };

            var recordThree = {
                id: uuid()
                , type: "晚餐"
                , monday: []
                , tuesday: []
                , wednesday: []
                , thursday: []
                , friday: []
                , saturday: []
                , sunday: []
            };

            tableData.push(recordOne);
            tableData.push(recordTwo);
            tableData.push(recordThree);
        }

        initTableBase();

        table.render({
            limit: 10,
            elem: '#data_table',
            // url: "/main/getList",
            data: tableData,
            // where: {
            //     voucherId: voucherId
            // },
            title: '数据列表',
            method: 'post',
            height: '489',
            // skin: 'nob',
            // even: true,
            page: true,
            cols: [[
                {field: 'id', title: 'ID', align: 'center', hide: true}
                // , {field: 'type', title: '餐别', align: 'center', style: 'background-color: #5792c6; color: #fff;'}
                , {field: 'type', title: '餐别', align: 'center', style: 'color: #F581B1;'}
                , {
                    field: 'monday', title: '${monday}<br/>星期一', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.monday && d.monday.length > 0) {
                            foodHtml = '<button class="layui-btn layui-btn-normal" type="button" onclick="editRecipesFood()">编辑</button>';
                            for (var i = 0; i < d.monday.length; i++) {
                                var tempFood = d.monday[i];
                                foodHtml += '<div style="margin-top: 5px;background-color: #d2d2d2">' + tempFood.setMealName;
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += '<br/>' + tempFood.food[j].dishName;
                                }
                                foodHtml += '</div>';
                            }
                            return foodHtml;
                        }
                        return '<button class="layui-btn" type="button" onclick="addRecipesFood()">新增</button>';
                    }
                }
                , {
                    field: 'tuesday', title: '${tuesday}<br/>星期二', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.tuesday && d.tuesday.length > 0) {
                            foodHtml = '<button class="layui-btn layui-btn-normal" type="button" onclick="editRecipesFood()">编辑</button>';
                            for (var i = 0; i < d.tuesday.length; i++) {
                                var tempFood = d.tuesday[i];
                                foodHtml += '<div style="margin-top: 5px;background-color: #d2d2d2">' + tempFood.setMealName;
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += '<br/>' + tempFood.food[j].dishName;
                                }
                                foodHtml += '</div>';
                            }
                            return foodHtml;
                        }
                        return '<button class="layui-btn" type="button" onclick="addRecipesFood()">新增</button>';
                    }
                }
                , {
                    field: 'wednesday', title: '${wednesday}<br/>星期三', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.wednesday && d.wednesday.length > 0) {
                            foodHtml = '<button class="layui-btn layui-btn-normal" type="button" onclick="editRecipesFood()">编辑</button>';
                            for (var i = 0; i < d.wednesday.length; i++) {
                                var tempFood = d.wednesday[i];
                                foodHtml += '<div style="margin-top: 5px;background-color: #d2d2d2">' + tempFood.setMealName;
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += '<br/>' + tempFood.food[j].dishName;
                                }
                                foodHtml += '</div>';
                            }
                            return foodHtml;
                        }
                        return '<button class="layui-btn" type="button" onclick="addRecipesFood()">新增</button>';
                    }
                }
                , {
                    field: 'thursday', title: '${thursday}<br/>星期四', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.thursday && d.thursday.length > 0) {
                            foodHtml = '<button class="layui-btn layui-btn-normal" type="button" onclick="editRecipesFood()">编辑</button>';
                            for (var i = 0; i < d.thursday.length; i++) {
                                var tempFood = d.thursday[i];
                                foodHtml += '<div style="margin-top: 5px;background-color: #d2d2d2">' + tempFood.setMealName;
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += '<br/>' + tempFood.food[j].dishName;
                                }
                                foodHtml += '</div>';
                            }
                            return foodHtml;
                        }
                        return '<button class="layui-btn" type="button" onclick="addRecipesFood()">新增</button>';
                    }
                }
                , {
                    field: 'friday', title: '${friday}<br/>星期五', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.friday && d.friday.length > 0) {
                            foodHtml = '<button class="layui-btn layui-btn-normal" type="button" onclick="editRecipesFood()">编辑</button>';
                            for (var i = 0; i < d.friday.length; i++) {
                                var tempFood = d.friday[i];
                                foodHtml += '<div style="margin-top: 5px;background-color: #d2d2d2">' + tempFood.setMealName;
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += '<br/>' + tempFood.food[j].dishName;
                                }
                                foodHtml += '</div>';
                            }
                            return foodHtml;
                        }
                        return '<button class="layui-btn" type="button" onclick="addRecipesFood()">新增</button>';
                    }
                }
                , {
                    field: 'saturday', title: '${saturday}<br/>星期六', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.saturday && d.saturday.length > 0) {
                            foodHtml = '<button class="layui-btn layui-btn-normal" type="button" onclick="editRecipesFood()">编辑</button>';
                            for (var i = 0; i < d.saturday.length; i++) {
                                var tempFood = d.saturday[i];
                                foodHtml += '<div style="margin-top: 5px;background-color: #d2d2d2">' + tempFood.setMealName;
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += '<br/>' + tempFood.food[j].dishName;
                                }
                                foodHtml += '</div>';
                            }
                            return foodHtml;
                        }
                        return '<button class="layui-btn" type="button" onclick="addRecipesFood()">新增</button>';
                    }
                }
                , {
                    field: 'sunday', title: '${sunday}<br/>星期天', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.sunday && d.sunday.length > 0) {
                            foodHtml = '<button class="layui-btn layui-btn-normal" type="button" onclick="editRecipesFood()">编辑</button>';
                            for (var i = 0; i < d.sunday.length; i++) {
                                var tempFood = d.sunday[i];
                                foodHtml += '<div style="margin-top: 5px;background-color: #d2d2d2">' + tempFood.setMealName;
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += '<br/>' + tempFood.food[j].dishName;
                                }
                                foodHtml += '</div>';
                            }
                            return foodHtml;
                        }
                        return '<button class="layui-btn" type="button" onclick="addRecipesFood()">新增</button>';
                    }
                }
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
                /** 设置表头背景色和文字样式 */
                // $('th').css({'background-color': '#009688', 'color': '#fff', 'font-weight': 'bold'});
            }
        });

        deleteTableData = function (id) {
            tableData = tableData.filter(function (item) {
                return item.id != id
            });
        }

        addRecipesFood = function () {
            var cellIndex = event.target.parentNode.parentNode.cellIndex;
            var rowIndex = event.target.parentNode.parentNode.parentNode.rowIndex;
            layer.open({
                type: 2,
                area: ['40%', '70%'],
                // offset: '65px',
                title: '新增-菜谱菜品',
                content: '/recipes/addRecipesFood?cellIndex=' + cellIndex + '&rowIndex=' + rowIndex,
                success: function (layero, index) {

                }
            });
        }

        editRecipesFood = function () {
            layer.open({
                type: 2,
                area: ['80%', '95%'],
                // offset: '65px',
                title: '编辑-菜谱菜品',
                content: '/recipes/addRecipes',
                success: function (layero, index) {

                }
            });
        }

        $(document).on('click', '#cancelBtn', function () {
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
        });
    });
</script>
</body>
</html>