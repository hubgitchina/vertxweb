<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>数据列表页面</title>
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

        <div class="layui-col-md9">
            <div class="layui-card">
                <div class="layui-card-header"><h2>${msg}</h2></div>
                <div class="layui-card-body">
                    <blockquote class="layui-elem-quote layui-quote-nm">
                        订餐情况：
                        <#--<span class="layui-badge-rim">未选</span>-->
                        <span class="layui-badge layui-bg-cyan">未选</span>
                        &nbsp;
                        <#--<span class="layui-badge layui-bg-gray">已选</span>-->
                        <span class="layui-badge layui-bg-blue">已选</span>
                    </blockquote>

                    <table class="layui-hide" id="data_table" lay-filter="data_table"></table>
                </div>
            </div>
        </div>

        <div class="layui-col-md3">
            <div class="layui-card">
                <div class="layui-card-header">菜单跟踪</div>
                <div class="layui-card-body">
                    <ul class="layui-timeline" id="ul_timeline">

                    </ul>
                </div>
            </div>
        </div>

        <div class="layui-col-md9">
            <div class="layui-card">
                <div class="layui-card-header">留言板</div>
                <div class="layui-card-body">

                    <div id="view"></div>

                    <#--<ul class="layuiadmin-card-status layuiadmin-home2-usernote">-->
                        <#--<li>-->
                            <#--<h3 style="color: #337ab7;">张爱玲</h3>-->
                            <#--<p>于千万人之中遇到你所要遇到的人，于千万年之中，时间的无涯的荒野中，没有早一步，也没有晚一步，刚巧赶上了，那也没有别的话好说，唯有轻轻的问一声：“噢，原来你也在这里？”</p>-->
                            <#--<span>4月11日 09:10</span>-->
                            <#--<a href="javascript:;" layadmin-event="replyNote" data-id="1"-->
                               <#--class="layui-btn layui-btn-xs layuiadmin-reply">回复</a>-->
                        <#--</li>-->
                    <#--</ul>-->

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


<script id="msgTpl" type="text/html">
    <ul class="layuiadmin-card-status layuiadmin-home2-usernote">
        {{#  layui.each(d, function(index, item){ }}
        <li>
            <h3 style="color: #337ab7;">{{ item.userName }}</h3>
            <p>{{ item.msg }}</p>
            <span>{{ item.date || '' }}</span>
            <a href="javascript:;" id="test" onclick="replyNote(event)" data-id="1"
               class="layui-btn layui-btn-xs layuiadmin-reply">回复</a>
        </li>
        {{#  }); }}
        {{#  if(d.length === 0){ }}
        无数据
        {{#  } }}
    </ul>
</script>


<script src="/static/layui/layui.js"></script>

<style>
    .order-div {
        margin-top: 5px;
        background-color: #2F4056;
        color: #fff;
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

    layui.use(['layer', 'form', 'table', 'laytpl'], function () {
        var $ = layui.jquery;
        var layer = layui.layer;
        var form = layui.form;
        var table = layui.table;
        var laytpl = layui.laytpl;

        var recipesId = '${recipesId!}';

        $.ajax({
            type: "POST",
            url: "/main/getMsg",
            contentType: "application/json; charset=utf-8",
            async: true,
            data: JSON.stringify(param),
            dataType: "json",
            success: function (result) {
                if (result.code == 200) {
                    var data = result.data;
                    var getTpl=document.getElementById('msgTpl').innerHTML
                        ,view = document.getElementById('view');
                    laytpl(getTpl).render(data, function(html){
                        view.innerHTML = html;
                    });
                } else {
                    layer.alert("获取留言板数据失败，" + result.msg, {
                        icon: 5,
                        btnAlign: 'c', //按钮居中
                        title: "提示"
                    });
                }
            },
            error: function (msg) {
                layer.alert("获取留言板数据失败: " + msg, {
                    icon: 5,
                    btnAlign: 'c', //按钮居中
                    title: "提示"
                });
            }
        });

        table.render({
            limit: 10,
            elem: '#data_table',
            // url: "/main/getList",
            url: "/recipes/getRecipesById",
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
                                if (tempFood.isChoose == 1) {
                                    foodHtml += '<div class="order-div-choose">';
                                } else {
                                    foodHtml += '<div class="order-div">';
                                }
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
                                if (tempFood.isChoose == 1) {
                                    foodHtml += '<div class="order-div-choose">';
                                } else {
                                    foodHtml += '<div class="order-div">';
                                }
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
                                if (tempFood.isChoose == 1) {
                                    foodHtml += '<div class="order-div-choose">';
                                } else {
                                    foodHtml += '<div class="order-div">';
                                }
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
                                if (tempFood.isChoose == 1) {
                                    foodHtml += '<div class="order-div-choose">';
                                } else {
                                    foodHtml += '<div class="order-div">';
                                }
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
                                if (tempFood.isChoose == 1) {
                                    foodHtml += '<div class="order-div-choose">';
                                } else {
                                    foodHtml += '<div class="order-div">';
                                }
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
                                if (tempFood.isChoose == 1) {
                                    foodHtml += '<div class="order-div-choose">';
                                } else {
                                    foodHtml += '<div class="order-div">';
                                }
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
                                if (tempFood.isChoose == 1) {
                                    foodHtml += '<div class="order-div-choose">';
                                } else {
                                    foodHtml += '<div class="order-div">';
                                }
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
                setChooseTableData(res.data);
                return {
                    "code": res.code, //解析接口状态
                    "msg": res.msg, //解析提示文本
                    "count": res.total, //解析数据长度
                    "data": res.data //解析数据列表
                };
            }, done: function (res, curr, count) {
                /** 设置表头背景色和文字样式 */
                $('th').css({'background-color': '#009688', 'color': '#fff', 'font-weight': 'bold'});
                // res.data.forEach(function (item, index) {
                //     var tr = $(".layui-table-body tbody tr[data-index='" + index + "']");
                //
                //     // tr.find(".laytable-cell-1-0-1").css("background-color", "#5792c6");
                //     // tr.find(".laytable-cell-1-0-1").css("color", "#fff");
                //
                //     //如果是已选择，则设置单元格背景色
                //     if (item.isMonday == 1) {
                //         tr.find(".laytable-cell-1-0-2").css("background-color", "#eee");
                //     }
                //     if (item.isTuesday == 1) {
                //         tr.find(".laytable-cell-1-0-3").css("background-color", "#eee");
                //     }
                //     if (item.isWednesday == 1) {
                //         tr.find(".laytable-cell-1-0-4").css("background-color", "#eee");
                //     }
                //     if (item.isThursday == 1) {
                //         tr.find(".laytable-cell-1-0-5").css("background-color", "#eee");
                //     }
                //     if (item.isFriday == 1) {
                //         tr.find(".laytable-cell-1-0-6").css("background-color", "#eee");
                //     }
                // });
            }
        });

        var chooseTableData = new Array();

        <#list orderList as order>
        var recordOrder = {
            recipesId: recipesId
            , setMealId: '${order.recipes_set_meal_id!}'
            , price: '${order.price!}'
            , type: '${order.type!}'
        };
        chooseTableData.push(recordOrder);
        </#list>

        function setChooseTableData(recipesList) {
            for (var i = 0, len1 = recipesList.length; i < len1; i++) {
                var tempRecipes = recipesList[i];
                for (var key in tempRecipes) {
                    var tempRecipesList = tempRecipes[key];
                    for (var j = 0, len2 = tempRecipesList.length; j < len2; j++) {
                        var tempRecipesDetail = tempRecipesList[j];
                        for (var k = 0, len3 = chooseTableData.length; k < len3; k++) {
                            var tempOrder = chooseTableData[k];
                            if (tempOrder.setMealId == tempRecipesDetail.id) {
                                tempRecipesDetail.isChoose = 1;
                                break;
                            }
                        }
                    }
                }
            }
        }

        var param = {
            id: '1'
        }

        $.ajax({
            type: "POST",
            url: "/main/getTimeline",
            contentType: "application/json; charset=utf-8",
            async: true,
            data: JSON.stringify(param),
            dataType: "json",
            success: function (result) {
                if (result.code == 200) {
                    var data = result.data;
                    var ulHtml = '';
                    for (var i = 0; i < data.length; i++) {
                        ulHtml += '<li class="layui-timeline-item">';
                        ulHtml += '<i class="layui-icon layui-timeline-axis">&#xe63f;</i>';
                        ulHtml += '<div class="layui-timeline-content layui-text">';
                        ulHtml += '<h3 class="layui-timeline-title">' + data[i].date + '</h3>';
                        ulHtml += '<p>';
                        ulHtml += data[i].name;
                        ulHtml += '<br>' + data[i].operation;
                        ulHtml += '</p>';
                        ulHtml += '</div>';
                        ulHtml += '</li>';
                    }
                    ulHtml += '<li class="layui-timeline-item">';
                    ulHtml += '<i class="layui-icon layui-timeline-axis">&#xe63f;</i>';
                    ulHtml += '<div class="layui-timeline-content layui-text">';
                    ulHtml += '<div class="layui-timeline-title">开始</div>';
                    ulHtml += '</div>';
                    ulHtml += '</li>';
                    $("#ul_timeline").html(ulHtml);
                } else {
                    layer.alert("获取时间线数据失败，" + result.msg, {
                        icon: 5,
                        btnAlign: 'c', //按钮居中
                        title: "提示"
                    });
                }
            },
            error: function (msg) {
                layer.alert("获取时间线数据失败: " + msg, {
                    icon: 5,
                    btnAlign: 'c', //按钮居中
                    title: "提示"
                });
            }
        });

        replyNote = function (e) {
            var a = $(e.target).data("id");
            layer.prompt({title: "回复留言 ID:" + a, formType: 2}, function (e, a) {
                layer.msg("得到：" + e), layer.close(a)
            })
        }
    });
</script>
</body>
</html>