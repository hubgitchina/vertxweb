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

    <link rel="stylesheet" href="/static/css/style.css">
    <link rel="stylesheet" href="/static/css/comment.css">
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

                    <div class="commentAll">
                        <!--评论区域 begin-->
                        <div class="reviewArea clearfix">
                            <textarea class="content comment-input" placeholder="请输入神评妙论&hellip;"
                                      onkeyup="keyUP(this)"></textarea>
                            <a href="javascript:;" class="plBtn">评论</a>
                        </div>
                        <!--评论区域 end-->
                        <!--回复区域 begin-->
                        <div class="comment-show">
                            <#--<div class="comment-show-con clearfix">-->
                            <#--<div class="comment-show-con-img pull-left"><img-->
                            <#--src="/static/images/header-img-comment_03.png" alt=""></div>-->
                            <#--<div class="comment-show-con-list pull-left clearfix">-->
                            <#--<div class="pl-text clearfix">-->
                            <#--<a href="#" class="comment-size-name">张三 : </a>-->
                            <#--<span class="my-pl-con">&nbsp;来啊 造作啊!</span>-->
                            <#--</div>-->
                            <#--<div class="date-dz">-->
                            <#--<span class="date-dz-left pull-left comment-time">2017-5-2 11:11:39</span>-->
                            <#--<div class="date-dz-right pull-right comment-pl-block">-->
                            <#--<a href="javascript:;" class="removeBlock">删除</a>-->
                            <#--<a href="javascript:;"-->
                            <#--class="date-dz-pl pl-hf hf-con-block pull-left">回复</a>-->
                            <#--<span class="pull-left date-dz-line">|</span>-->
                            <#--<a href="javascript:;" class="date-dz-z pull-left"><i-->
                            <#--class="date-dz-z-click-red"></i>赞 (<i class="z-num">0</i>)</a>-->
                            <#--</div>-->
                            <#--</div>-->
                            <#--<div class="hf-list-con"></div>-->
                            <#--</div>-->
                            <#--</div>-->
                        </div>
                        <!--回复区域 end-->
                    </div>

                    <div id="div_laypage"></div>

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

<script src="/static/layui/layui.js"></script>

<script type="text/javascript" src="/static/js/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="/static/js/jquery.flexText.js"></script>

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

<!--textarea高度自适应-->
<script type="text/javascript">
    $(function () {
        $('.content').flexText();
    });
</script>
<!--textarea限制字数-->
<script type="text/javascript">
    function keyUP(t) {
        var len = $(t).val().length;
        if (len > 139) {
            $(t).val($(t).val().substring(0, 140));
        }
    }
</script>
<!--初始化评论区数据-->
<script type="text/javascript">
    function initRootCommnet(page, limit) {
        var param = {
            page: page,
            limit: limit,
            recipesId: recipesId
        }

        $.ajax({
            type: "POST",
            url: "/comment/queryRecipesCommentRootList",
            contentType: "application/json; charset=utf-8",
            async: true,
            data: JSON.stringify(param),
            dataType: "json",
            success: function (result) {
                if (result.code == 200) {
                    var data = result.data;
                    if (data) {
                        for (var i = 0, len = data.length; i < len; i++) {
                            var comment = data[i];
                            //动态创建评论模块
                            var oHtml = '<div class="comment-show-con clearfix"><div class="comment-show-con-img pull-left"><img src="/static/images/header-img-comment_03.png" alt=""></div><div class="comment-show-con-list pull-left clearfix"><div class="pl-text clearfix"><a href="#" class="comment-size-name">' + comment.login_name + ' : </a><span class="my-pl-con">&nbsp;' + comment.reply_content + '</span></div><div class="date-dz"><span class="date-dz-left pull-left comment-time">' + comment.reply_time + '</span><div class="date-dz-right pull-right comment-pl-block"><a href="javascript:;" class="removeBlock">删除</a><a href="javascript:;" class="date-dz-pl pl-hf hf-con-block pull-left">回复</a><span class="pull-left date-dz-line">|</span> <a href="javascript:;" class="date-dz-z pull-left"><i class="date-dz-z-click-red"></i>赞 (<i class="z-num">' + comment.fabulous_num + '</i>)</a></div></div><div class="hf-list-con"></div></div></div>';
                            $('.comment-show').append(oHtml);

                            if(comment.childTotal){
                                $('.comment-show').find('.hf-list-con').eq(i).css('display', 'block');
                                var divIndex = i;
                                if (comment.childTotal > 10) {
                                    var divId = "div_laypage_reply_" + i;
                                    var laypageDivHtml = '<div id="' + divId + '"></div>';
                                    $('.comment-show').find('.hf-list-con').eq(i).after(laypageDivHtml);

                                    layui.laypage.render({
                                        elem: divId
                                        , count: comment.childTotal //数据总数，从服务端得到
                                        , jump: function (obj, first) {
                                            //obj包含了当前分页的所有参数，比如：
                                            console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                                            console.log(obj.limit); //得到每页显示的条数

                                            //首次不执行
                                            if (!first) {
                                                //do something
                                            }
                                            queryCommnetReply(obj.curr, obj.limit, comment.id, divIndex);
                                        }
                                    });
                                }else{
                                    queryCommnetReply(1, 10, comment.id, divIndex);
                                }
                            }
                        }
                    }
                } else {
                    layer.alert("获取留言板数据失败，" + result.msg, {
                        icon: 5,
                        btnAlign: 'c', //按钮居中
                        title: "提示"
                    });
                }
            },
            error: function (msg) {
                layer.alert("获取留言板数据失败: " + msg.responseText, {
                    icon: 5,
                    btnAlign: 'c', //按钮居中
                    title: "提示"
                });
            }
        });
    }
</script>
<!--获取评论回复区分页数据-->
<script type="text/javascript">
    function queryCommnetReply(page, limit, rootCommentId, index) {
        var param = {
            page: page,
            limit: limit,
            rootCommentId: rootCommentId
        }

        $.ajax({
            type: "POST",
            url: "/comment/queryRecipesCommentChildList",
            contentType: "application/json; charset=utf-8",
            async: true,
            data: JSON.stringify(param),
            dataType: "json",
            success: function (result) {
                if (result.code == 200) {
                    var data = result.data;
                    $('.comment-show').find('.hf-list-con').eq(index).empty();
                    if (data) {
                        for (var i = 0, len = data.length; i < len; i++) {
                            var reply = data[i];
                            var oAt = '回复<a href="#" class="atName">@' + reply.replyLoginName + '</a> : ' + reply.reply_content;
                            var rHtml = '<div class="all-pl-con"><div class="pl-text hfpl-text clearfix"><a href="#" class="comment-size-name">' + reply.loginName + ' : </a><span class="my-pl-con">' + oAt + '</span></div><div class="date-dz"> <span class="date-dz-left pull-left comment-time">' + reply.reply_time + '</span> <div class="date-dz-right pull-right comment-pl-block"> <a href="javascript:;" class="removeBlock">删除</a> <a href="javascript:;" class="date-dz-pl pl-hf hf-con-block pull-left">回复</a> <span class="pull-left date-dz-line">|</span> <a href="javascript:;" class="date-dz-z pull-left"><i class="date-dz-z-click-red"></i>赞 (<i class="z-num">' + reply.fabulous_num + '</i>)</a> </div> </div></div>';
                            $('.comment-show').find('.hf-list-con').eq(index).append(rHtml);
                        }
                    }
                } else {
                    layer.alert("获取评论回复数据失败，" + result.msg, {
                        icon: 5,
                        btnAlign: 'c', //按钮居中
                        title: "提示"
                    });
                }
            },
            error: function (msg) {
                layer.alert("获取评论回复板数据失败: " + msg.responseText, {
                    icon: 5,
                    btnAlign: 'c', //按钮居中
                    title: "提示"
                });
            }
        });
    }
</script>
<!--点击评论创建评论条-->
<script type="text/javascript">
    $('.commentAll').on('click', '.plBtn', function () {
        //获取输入内容
        var oSize = $(this).siblings('.flex-text-wrap').find('.comment-input').val();
        if (oSize.replace(/(^\s*)|(\s*$)/g, "") == '') {
            return false;
        }

        console.log(oSize);

        var param = {
            recipesId: recipesId,
            replyContent: oSize
        }

        $.ajax({
            type: "POST",
            url: "/comment/saveRecipesComment",
            contentType: "application/json; charset=utf-8",
            async: true,
            data: JSON.stringify(param),
            dataType: "json",
            success: function (result) {
                if (result.code == 200) {
                    var data = result.data;

                    //动态创建评论模块
                    var oHtml = '<div class="comment-show-con clearfix"><div class="comment-show-con-img pull-left"><img src="/static/images/header-img-comment_03.png" alt=""></div><div class="comment-show-con-list pull-left clearfix"><div class="pl-text clearfix"><a href="#" class="comment-size-name">' + data.commentUserName + ' : </a><span class="my-pl-con">&nbsp;' + oSize + '</span></div><div class="date-dz"><span class="date-dz-left pull-left comment-time">' + data.commentTime + '</span><div class="date-dz-right pull-right comment-pl-block"><a href="javascript:;" class="removeBlock">删除</a><a href="javascript:;" class="date-dz-pl pl-hf hf-con-block pull-left">回复</a><span class="pull-left date-dz-line">|</span> <a href="javascript:;" class="date-dz-z pull-left"><i class="date-dz-z-click-red"></i>赞 (<i class="z-num">' + data.fabulousNum + '</i>)</a></div></div><div class="hf-list-con"></div></div></div>';
                    $('.plBtn').parents('.reviewArea').siblings('.comment-show').prepend(oHtml);
                    $('.plBtn').siblings('.flex-text-wrap').find('.comment-input').prop('value', '').siblings('pre').find('span').text('');
                } else {
                    layer.alert("评论失败，" + result.msg, {
                        icon: 5,
                        btnAlign: 'c', //按钮居中
                        title: "提示"
                    });
                }
            },
            error: function (msg) {
                layer.alert("评论失败: " + msg.responseText, {
                    icon: 5,
                    btnAlign: 'c', //按钮居中
                    title: "提示"
                });
            }
        });

        // var myDate = new Date();
        // //获取当前年
        // var year=myDate.getFullYear();
        // //获取当前月
        // var month=myDate.getMonth()+1;
        // //获取当前日
        // var date=myDate.getDate();
        // var h=myDate.getHours();       //获取当前小时数(0-23)
        // var m=myDate.getMinutes();     //获取当前分钟数(0-59)
        // if(m<10) m = '0' + m;
        // var s=myDate.getSeconds();
        // if(s<10) s = '0' + s;
        // var now=year+'-'+month+"-"+date+" "+h+':'+m+":"+s;
        //
        // //动态创建评论模块
        // var oHtml = '<div class="comment-show-con clearfix"><div class="comment-show-con-img pull-left"><img src="/static/images/header-img-comment_03.png" alt=""></div> <div class="comment-show-con-list pull-left clearfix"><div class="pl-text clearfix"> <a href="#" class="comment-size-name">David Beckham : </a> <span class="my-pl-con">&nbsp;'+ oSize +'</span> </div> <div class="date-dz"> <span class="date-dz-left pull-left comment-time">'+now+'</span> <div class="date-dz-right pull-right comment-pl-block"><a href="javascript:;" class="removeBlock">删除</a> <a href="javascript:;" class="date-dz-pl pl-hf hf-con-block pull-left">回复</a> <span class="pull-left date-dz-line">|</span> <a href="javascript:;" class="date-dz-z pull-left"><i class="date-dz-z-click-red"></i>赞 (<i class="z-num">0</i>)</a> </div> </div><div class="hf-list-con"></div></div> </div>';
        //
        // $(this).parents('.reviewArea ').siblings('.comment-show').prepend(oHtml);
        // $(this).siblings('.flex-text-wrap').find('.comment-input').prop('value','').siblings('pre').find('span').text('');
    });
</script>
<!--点击回复动态创建回复块-->
<script type="text/javascript">
    $('.comment-show').on('click', '.pl-hf', function () {
        //获取回复人的名字
        var fhName = $(this).parents('.date-dz-right').parents('.date-dz').siblings('.pl-text').find('.comment-size-name').html();
        //回复@
        var fhN = '回复@' + fhName;
        //var oInput = $(this).parents('.date-dz-right').parents('.date-dz').siblings('.hf-con');
        var fhHtml = '<div class="hf-con pull-left"> <textarea class="content comment-input hf-input" placeholder="" onkeyup="keyUP(this)"></textarea> <a href="javascript:;" class="hf-pl">评论</a></div>';
        //显示回复
        if ($(this).is('.hf-con-block')) {
            $(this).parents('.date-dz-right').parents('.date-dz').append(fhHtml);
            $(this).removeClass('hf-con-block');
            $('.content').flexText();
            $(this).parents('.date-dz-right').siblings('.hf-con').find('.pre').css('padding', '6px 15px');
            //console.log($(this).parents('.date-dz-right').siblings('.hf-con').find('.pre'))
            //input框自动聚焦
            $(this).parents('.date-dz-right').siblings('.hf-con').find('.hf-input').val('').focus().val(fhN);
        } else {
            $(this).addClass('hf-con-block');
            $(this).parents('.date-dz-right').siblings('.hf-con').remove();
        }
    });
</script>
<!--评论回复块创建-->
<script type="text/javascript">
    $('.comment-show').on('click', '.hf-pl', function () {
        var oThis = $(this);
        var myDate = new Date();
        //获取当前年
        var year = myDate.getFullYear();
        //获取当前月
        var month = myDate.getMonth() + 1;
        //获取当前日
        var date = myDate.getDate();
        var h = myDate.getHours();       //获取当前小时数(0-23)
        var m = myDate.getMinutes();     //获取当前分钟数(0-59)
        if (m < 10) m = '0' + m;
        var s = myDate.getSeconds();
        if (s < 10) s = '0' + s;
        var now = year + '-' + month + "-" + date + " " + h + ':' + m + ":" + s;
        //获取输入内容
        var oHfVal = $(this).siblings('.flex-text-wrap').find('.hf-input').val();
        console.log(oHfVal)
        var oHfName = $(this).parents('.hf-con').parents('.date-dz').siblings('.pl-text').find('.comment-size-name').html();
        var oAllVal = '回复@' + oHfName;
        if (oHfVal.replace(/^ +| +$/g, '') == '' || oHfVal == oAllVal) {

        } else {
            $.getJSON("/static/json/pl.json", function (data) {
                var oAt = '';
                var oHf = '';
                $.each(data, function (n, v) {
                    delete v.hfContent;
                    delete v.atName;
                    var arr;
                    var ohfNameArr;
                    if (oHfVal.indexOf("@") == -1) {
                        data['atName'] = '';
                        data['hfContent'] = oHfVal;
                    } else {
                        arr = oHfVal.split(':');
                        ohfNameArr = arr[0].split('@');
                        data['hfContent'] = arr[1];
                        data['atName'] = ohfNameArr[1];
                    }

                    if (data.atName == '') {
                        oAt = data.hfContent;
                    } else {
                        oAt = '回复<a href="#" class="atName">@' + data.atName + '</a> : ' + data.hfContent;
                    }
                    oHf = data.hfName;
                });

                var oHtml = '<div class="all-pl-con"><div class="pl-text hfpl-text clearfix"><a href="#" class="comment-size-name">我的名字 : </a><span class="my-pl-con">' + oAt + '</span></div><div class="date-dz"> <span class="date-dz-left pull-left comment-time">' + now + '</span> <div class="date-dz-right pull-right comment-pl-block"> <a href="javascript:;" class="removeBlock">删除</a> <a href="javascript:;" class="date-dz-pl pl-hf hf-con-block pull-left">回复</a> <span class="pull-left date-dz-line">|</span> <a href="javascript:;" class="date-dz-z pull-left"><i class="date-dz-z-click-red"></i>赞 (<i class="z-num">0</i>)</a> </div> </div></div>';
                oThis.parents('.hf-con').parents('.comment-show-con-list').find('.hf-list-con').css('display', 'block').prepend(oHtml) && oThis.parents('.hf-con').siblings('.date-dz-right').find('.pl-hf').addClass('hf-con-block') && oThis.parents('.hf-con').remove();
            });
        }
    });
</script>
<!--删除评论块-->
<script type="text/javascript">
    $('.commentAll').on('click', '.removeBlock', function () {
        var oT = $(this).parents('.date-dz-right').parents('.date-dz').parents('.all-pl-con');
        if (oT.siblings('.all-pl-con').length >= 1) {
            oT.remove();
        } else {
            $(this).parents('.date-dz-right').parents('.date-dz').parents('.all-pl-con').parents('.hf-list-con').css('display', 'none')
            oT.remove();
        }
        $(this).parents('.date-dz-right').parents('.date-dz').parents('.comment-show-con-list').parents('.comment-show-con').remove();

    })
</script>
<!--点赞-->
<script type="text/javascript">
    $('.comment-show').on('click', '.date-dz-z', function () {
        var zNum = $(this).find('.z-num').html();
        if ($(this).is('.date-dz-z-click')) {
            zNum--;
            $(this).removeClass('date-dz-z-click red');
            $(this).find('.z-num').html(zNum);
            $(this).find('.date-dz-z-click-red').removeClass('red');
        } else {
            zNum++;
            $(this).addClass('date-dz-z-click');
            $(this).find('.z-num').html(zNum);
            $(this).find('.date-dz-z-click-red').addClass('red');
        }
    })
</script>

<script charset="utf-8">

    var recipesId = '${recipesId!}';

    layui.use(['layer', 'form', 'table', 'laypage'], function () {
        var $ = layui.jquery;
        var layer = layui.layer;
        var form = layui.form;
        var table = layui.table;
        var laypage = layui.laypage;

        var param = {
            recipesId: recipesId
        }

        $.ajax({
            type: "POST",
            url: "/comment/getRecipesCommentTotal",
            contentType: "application/json; charset=utf-8",
            async: true,
            data: JSON.stringify(param),
            dataType: "json",
            success: function (result) {
                if (result.code == 200) {
                    var data = result.data;

                    laypage.render({
                        elem: 'div_laypage'
                        , count: data //数据总数，从服务端得到
                        , jump: function (obj, first) {
                            //obj包含了当前分页的所有参数，比如：
                            console.log(obj.curr); //得到当前页，以便向服务端请求对应页的数据。
                            console.log(obj.limit); //得到每页显示的条数

                            //首次不执行
                            if (!first) {
                                //do something
                            }

                            initRootCommnet(obj.curr, obj.limit);
                        }
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
                layer.alert("获取留言板数据失败: " + msg.responseText, {
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

    });
</script>
</body>
</html>