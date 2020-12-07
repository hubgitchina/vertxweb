<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>新增-菜谱菜品</title>
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
                <div class="layui-inline">
                    <label class="layui-form-label">是否套餐<b style="color:red">*</b></label>
                    <div class="layui-input-block">
                        <input type="radio" name="is_set_meal" value="0" title="否">
                        <input type="radio" name="is_set_meal" value="1" title="是" checked>
                    </div>
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label">套餐名称</label>
                    <div class="layui-input-block">
                        <input type="text" name="set_meal_name" required lay-verify="required" placeholder="请输入套餐名称"
                               autocomplete="off" class="layui-input">
                    </div>
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-inline">
                    <label class="layui-form-label">套餐价格</label>
                    <div class="layui-input-block">
                        <input type="text" name="price" required lay-verify="required|number" placeholder="￥"
                               autocomplete="off" class="layui-input">
                    </div>
                </div>
            </div>

            <div class="layui-form-item">
                <table class="layui-hide" id="data_table" lay-filter="data_table"></table>
            </div>

            <input type="hidden" id="cellIndex" name="cellIndex" value="${cellIndex!}">
            <input type="hidden" id="rowIndex" name="rowIndex" value="${rowIndex!}">

            <div class="layui-form-item text-right ">
                <button class="layui-btn" lay-filter="addRecipesFood" lay-submit>保存</button>
                <button class="layui-btn layui-btn-primary" type="button" ew-event="cancelBtn" id="cancelBtn">
                    取消
                </button>
            </div>
        </form>
    </div>
</div>

<script id="foodToolbar" type="text/html">
    <button class="layui-btn" type="button" lay-event="toAdd">新增</button>
</script>

<script id="foodBar" type="text/html">
    <button type="button" class="layui-btn layui-btn-xs" lay-event="edit">编辑</button>
    <button type="button" class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del">删除</button>
</script>

<script src="/static/layui/layui.js"></script>
<script src="/static/js/form-verify.js" charset="utf-8"></script>

<script charset="utf-8">

    var tableData = new Array();

    layui.use(['form', 'table', 'layer', 'util'], function () {
        var $ = layui.jquery;
        var form = layui.form;
        var table = layui.table;
        var layer = layui.layer;
        var util = layui.util;

        var uuid = function () {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        }

        table.render({
            limit: 10,
            elem: '#data_table',
            data: tableData,
            title: '菜品列表',
            method: 'post',
            // width: '500',
            // height: '472',
            contentType: 'application/json',
            toolbar: '#foodToolbar',
            defaultToolbar: [],
            page: true,
            cols: [[
                {type: 'numbers', title: '序号', align: 'center'}
                , {field: 'id', title: 'ID', align: 'center', hide: true}
                , {field: 'dishName', title: '菜品名称', minWidth: 100, align: 'center'}
                , {
                    field: 'category', title: '规格', align: 'center'
                    , templet: function (d) {
                        if (d.category == 1) {
                            return '大份';
                        } else if (d.category == 2) {
                            return '中份';
                        } else if (d.category == 3) {
                            return '小份';
                        } else {
                            return '';
                        }
                    }
                }
                , {fixed: 'right', title: '操作', minWidth: 100, align: 'center', toolbar: '#foodBar'}
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
            switch (obj.event) {
                case 'toAdd':
                    doAdd('');
                    break;
            }
        });

        function doAdd(id) {
            layer.open({
                type: 2,
                area: ['80%', '60%'],
                // offset: '65px',
                title: '新增-菜品',
                content: '/recipes/addFood?id=' + id,
                success: function (layero, index) {

                }
            });
        }

        deleteTableData = function (id) {
            tableData = tableData.filter(function (item) {
                return item.id != id
            });
        }

        form.on('submit(addRecipesFood)', function (d) {
            if (tableData.length == 0) {
                layer.msg("请添加菜品");
                return false;
            }

            // 组装数据
            var record = {
                id: uuid()
                , isSetMeal: d.field.is_set_meal
                , setMealName: d.field.set_meal_name
                , price: d.field.price
                , food: tableData
            };

            var oldId = d.field.oldId;
            if (oldId) {
                parent.deleteTableData(oldId);
            }

            for (var i = 0; i < parent.tableData.length; i++) {
                var tempData = parent.tableData[i];
                if (tempData.setMealName == record.setMealName) {
                    parent.layer.alert("套餐已存在", {
                        icon: 5,
                        btnAlign: 'c', //按钮居中
                        title: "提示"
                    });

                    return false;
                }
            }

            var cellIndex = d.field.cellIndex;
            var rowIndex = d.field.rowIndex;
            switch (cellIndex - 1) {
                case 1:
                    if (rowIndex == 0) {
                        parent.tableData[0].monday.push(record);
                    } else if (rowIndex == 1) {
                        parent.tableData[1].monday.push(record);
                    } else if (rowIndex == 2) {
                        parent.tableData[2].monday.push(record);
                    }
                    break;
                case 2:
                    if (rowIndex == 0) {
                        parent.tableData[0].tuesday.push(record);
                    } else if (rowIndex == 1) {
                        parent.tableData[1].tuesday.push(record);
                    } else if (rowIndex == 2) {
                        parent.tableData[2].tuesday.push(record);
                    }
                    break;
                case 3:
                    if (rowIndex == 0) {
                        parent.tableData[0].wednesday.push(record);
                    } else if (rowIndex == 1) {
                        parent.tableData[1].wednesday.push(record);
                    } else if (rowIndex == 2) {
                        parent.tableData[2].wednesday.push(record);
                    }
                    break;
                case 4:
                    if (rowIndex == 0) {
                        parent.tableData[0].thursday.push(record);
                    } else if (rowIndex == 1) {
                        parent.tableData[1].thursday.push(record);
                    } else if (rowIndex == 2) {
                        parent.tableData[2].thursday.push(record);
                    }
                    break;
                case 5:
                    if (rowIndex == 0) {
                        parent.tableData[0].friday.push(record);
                    } else if (rowIndex == 1) {
                        parent.tableData[1].friday.push(record);
                    } else if (rowIndex == 2) {
                        parent.tableData[2].friday.push(record);
                    }
                    break;
                case 6:
                    if (rowIndex == 0) {
                        parent.tableData[0].saturday.push(record);
                    } else if (rowIndex == 1) {
                        parent.tableData[1].saturday.push(record);
                    } else if (rowIndex == 2) {
                        parent.tableData[2].saturday.push(record);
                    }
                    break;
                case 7:
                    if (rowIndex == 0) {
                        parent.tableData[0].sunday.push(record);
                    } else if (rowIndex == 1) {
                        parent.tableData[1].sunday.push(record);
                    } else if (rowIndex == 2) {
                        parent.tableData[2].sunday.push(record);
                    }
            }

            // parent.tableData.push(record);
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