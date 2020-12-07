<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>订餐页面</title>
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
                <button class="layui-btn" lay-filter="orderRecipes" lay-submit>保存</button>
                <button class="layui-btn layui-btn-primary" type="button" ew-event="cancelBtn" id="cancelBtn">
                    取消
                </button>
            </div>
        </form>
    </div>
</div>

<script src="/static/layui/layui.js"></script>
<script src="/static/js/form-verify.js" charset="utf-8"></script>

<style>
    .order-div {
        margin-top: 5px;
        background-color: #d2d2d2;
        color: #666;
        cursor: pointer;
    }

    .order-div:hover {
        background-color: #5FB878;
    }

    .order-div-choose {
        margin-top: 5px;
        background-color: #1E9FFF;
        color: #fff;
        cursor: pointer;
    }

    .order-div-choose:hover {
        background-color: #5FB878;
    }
</style>

<script charset="utf-8">

    layui.use(['form', 'table', 'layer', 'util', 'laydate'], function () {
        var $ = layui.jquery;
        var form = layui.form;
        var table = layui.table;
        var layer = layui.layer;
        var util = layui.util;
        var laydate = layui.laydate;

        var recipesId = '${recipesId!}';

        table.render({
            limit: 10,
            elem: '#data_table',
            url: "/recipes/getRecipesById",
            // data: tableData,
            where: {
                recipesId: recipesId
            },
            title: '数据列表',
            method: 'post',
            height: '489',
            // skin: 'nob',
            // even: true,
            // page: true,
            cols: [[
                {field: 'id', title: 'ID', align: 'center', hide: true}
                // , {field: 'type', title: '餐别', align: 'center', style: 'background-color: #5792c6; color: #fff;'}
                , {field: 'type', title: '餐别', align: 'center', style: 'color: #F581B1;'}
                , {
                    field: 'monday', title: '${monday}<br/>星期一', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.monday && d.monday.length > 0) {
                            for (var i = 0; i < d.monday.length; i++) {
                                var tempFood = d.monday[i];
                                foodHtml += '<div class="order-div">';
                                foodHtml += '<input type="hidden" id="setMealId" value="' + tempFood.id + '">';
                                foodHtml += '<input type="hidden" id="price" value="' + tempFood.price + '">';
                                foodHtml += '<input type="hidden" id="type" value="' + tempFood.type + '">';
                                foodHtml += '<div style="border-bottom:1px solid #c2c2c2;">' + tempFood.setMealName + '(' + tempFood.price + '元)</div>';
                                foodHtml += '<div>';
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += tempFood.food[j].dishName + '<br/>';
                                }
                                foodHtml += '</div>';
                                foodHtml += '</div>';
                            }
                        }
                        return foodHtml;
                    }
                }
                , {
                    field: 'tuesday', title: '${tuesday}<br/>星期二', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.tuesday && d.tuesday.length > 0) {
                            for (var i = 0; i < d.tuesday.length; i++) {
                                var tempFood = d.tuesday[i];
                                foodHtml += '<div class="order-div">';
                                foodHtml += '<input type="hidden" id="setMealId" value="' + tempFood.id + '">';
                                foodHtml += '<input type="hidden" id="price" value="' + tempFood.price + '">';
                                foodHtml += '<input type="hidden" id="type" value="' + tempFood.type + '">';
                                foodHtml += '<div style="border-bottom:1px solid #c2c2c2;">' + tempFood.setMealName + '(' + tempFood.price + '元)</div>';
                                foodHtml += '<div>';
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += tempFood.food[j].dishName + '<br/>';
                                }
                                foodHtml += '</div>';
                                foodHtml += '</div>';
                            }
                        }
                        return foodHtml;
                    }
                }
                , {
                    field: 'wednesday', title: '${wednesday}<br/>星期三', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.wednesday && d.wednesday.length > 0) {
                            for (var i = 0; i < d.wednesday.length; i++) {
                                var tempFood = d.wednesday[i];
                                foodHtml += '<div class="order-div">';
                                foodHtml += '<input type="hidden" id="setMealId" value="' + tempFood.id + '">';
                                foodHtml += '<input type="hidden" id="price" value="' + tempFood.price + '">';
                                foodHtml += '<input type="hidden" id="type" value="' + tempFood.type + '">';
                                foodHtml += '<div style="border-bottom:1px solid #c2c2c2;">' + tempFood.setMealName + '(' + tempFood.price + '元)</div>';
                                foodHtml += '<div>';
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += tempFood.food[j].dishName + '<br/>';
                                }
                                foodHtml += '</div>';
                                foodHtml += '</div>';
                            }
                        }
                        return foodHtml;
                    }
                }
                , {
                    field: 'thursday', title: '${thursday}<br/>星期四', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.thursday && d.thursday.length > 0) {
                            for (var i = 0; i < d.thursday.length; i++) {
                                var tempFood = d.thursday[i];
                                foodHtml += '<div class="order-div">';
                                foodHtml += '<input type="hidden" id="setMealId" value="' + tempFood.id + '">';
                                foodHtml += '<input type="hidden" id="price" value="' + tempFood.price + '">';
                                foodHtml += '<input type="hidden" id="type" value="' + tempFood.type + '">';
                                foodHtml += '<div style="border-bottom:1px solid #c2c2c2;">' + tempFood.setMealName + '(' + tempFood.price + '元)</div>';
                                foodHtml += '<div>';
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += tempFood.food[j].dishName + '<br/>';
                                }
                                foodHtml += '</div>';
                                foodHtml += '</div>';
                            }
                        }
                        return foodHtml;
                    }
                }
                , {
                    field: 'friday', title: '${friday}<br/>星期五', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.friday && d.friday.length > 0) {
                            for (var i = 0; i < d.friday.length; i++) {
                                var tempFood = d.friday[i];
                                foodHtml += '<div class="order-div">';
                                foodHtml += '<input type="hidden" id="setMealId" value="' + tempFood.id + '">';
                                foodHtml += '<input type="hidden" id="price" value="' + tempFood.price + '">';
                                foodHtml += '<input type="hidden" id="type" value="' + tempFood.type + '">';
                                foodHtml += '<div style="border-bottom:1px solid #c2c2c2;">' + tempFood.setMealName + '(' + tempFood.price + '元)</div>';
                                foodHtml += '<div>';
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += tempFood.food[j].dishName + '<br/>';
                                }
                                foodHtml += '</div>';
                                foodHtml += '</div>';
                            }
                        }
                        return foodHtml;
                    }
                }
                , {
                    field: 'saturday', title: '${saturday}<br/>星期六', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.saturday && d.saturday.length > 0) {
                            for (var i = 0; i < d.saturday.length; i++) {
                                var tempFood = d.saturday[i];
                                foodHtml += '<div class="order-div">';
                                foodHtml += '<input type="hidden" id="setMealId" value="' + tempFood.id + '">';
                                foodHtml += '<input type="hidden" id="price" value="' + tempFood.price + '">';
                                foodHtml += '<input type="hidden" id="type" value="' + tempFood.type + '">';
                                foodHtml += '<div style="border-bottom:1px solid #c2c2c2;">' + tempFood.setMealName + '(' + tempFood.price + '元)</div>';
                                foodHtml += '<div>';
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += tempFood.food[j].dishName + '<br/>';
                                }
                                foodHtml += '</div>';
                                foodHtml += '</div>';
                            }
                        }
                        return foodHtml;
                    }
                }
                , {
                    field: 'sunday', title: '${sunday}<br/>星期天', align: 'center'
                    , templet: function (d) {
                        var foodHtml = '';
                        if (d.sunday && d.sunday.length > 0) {
                            for (var i = 0; i < d.sunday.length; i++) {
                                var tempFood = d.sunday[i];
                                foodHtml += '<div class="order-div">';
                                foodHtml += '<input type="hidden" id="setMealId" value="' + tempFood.id + '">';
                                foodHtml += '<input type="hidden" id="price" value="' + tempFood.price + '">';
                                foodHtml += '<input type="hidden" id="type" value="' + tempFood.type + '">';
                                foodHtml += '<div style="border-bottom:1px solid #c2c2c2;">' + tempFood.setMealName + '(' + tempFood.price + '元)</div>';
                                foodHtml += '<div>';
                                for (var j = 0; j < tempFood.food.length; j++) {
                                    foodHtml += tempFood.food[j].dishName + '<br/>';
                                }
                                foodHtml += '</div>';
                                foodHtml += '</div>';
                            }
                        }
                        return foodHtml;
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

        function getHexBackgroundColor(rgb) {
            // var rgb = $(this).css('background-color');
            // if (!$.browser.msie) {
            rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);

            function hex(x) {
                return ("0" + parseInt(x).toString(16)).slice(-2);
            }

            rgb = "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
            // }
            return rgb;
        }

        var chooseTableData = new Array();

        $(document).on('click', '.order-div, .order-div-choose', function () {
            // var backgroundColor = getHexBackgroundColor($(this).css("background-color"));
            // alert(backgroundColor);
            var css = $(this).attr("class");
            var setMealId = $(this).find("#setMealId").val();
            var price = $(this).find("#price").val()
            var type = $(this).find("#type").val()

            var record = {
                recipesId: recipesId
                , setMealId: setMealId
                , price: price
                , type: type
            };

            if (css == "order-div-choose") {
                $(this).removeClass("order-div-choose");
                $(this).addClass("order-div");

                chooseTableData = chooseTableData.filter(function (item) {
                    return item.setMealId != setMealId
                });
            } else if (css == "order-div") {
                $(this).removeClass("order-div");
                $(this).addClass("order-div-choose");

                chooseTableData.push(record);
            } else {

            }
        });

        // 表单提交事件
        form.on('submit(orderRecipes)', function (d) {
            if (chooseTableData.length == 0) {
                layer.msg("请选择菜谱");
                return false;
            }

            var msgIndex = layer.msg('系统处理中，请等待...', {shade: [0.8, '#393D49'], icon: 16, time: false});

            $.ajax({
                type: 'POST',
                url: '/order/saveOrderRecipes',
                contentType: "application/json; charset=utf-8",
                async: true,
                data: JSON.stringify(chooseTableData),
                dataType: "json",
                success: function (res) {
                    layer.close(msgIndex);

                    if (res.code == 200) {
                        parent.layui.table.reload('data_table');
                        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        parent.layer.close(index); //再执行关闭
                        parent.layer.msg("预订成功！", {icon: 1});
                    } else {
                        layer.alert("预订失败，" + res.msg, {
                            icon: 5,
                            btnAlign: 'c', //按钮居中
                            title: "提示"
                        });
                    }
                },
                error: function (msg) {
                    layer.close(msgIndex);

                    layer.alert("预订失败: " + msg.responseText, {
                        icon: 5,
                        btnAlign: 'c', //按钮居中
                        title: "提示"
                    });
                }
            });
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