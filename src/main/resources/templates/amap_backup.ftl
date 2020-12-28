<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>高德地图页面</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <#--<meta name="viewport" content="initial-scale=1.0, user-scalable=no">-->
    <#--<meta name="viewport" content="initial-scale=1.0, user-scalable=no, width=device-width">-->
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/static/layui/css/layui.css" media="all">

    <#--<link rel="stylesheet" href="/static/amap/indoormap-1.0.css" media="all">-->
    <link rel="stylesheet" href="/static/amap/demo-center.css" media="all">
</head>

<style type="text/css">
    html,body,#container{
        height:100%;
    }
    .info{
        width:26rem;
    }
</style>

<body>

<#--<div class="layui-fluid" style="padding: 15px; background-color: #F2F2F2;">-->
    <#--&lt;#&ndash;<div class="layui-container">&ndash;&gt;-->
    <#--<div class="layui-row layui-col-space15">-->

        <#--<div class="layui-col-md12">-->
            <#--<div class="layui-card">-->
                <#--<div class="layui-card-header"><h2>地图</h2></div>-->
                <#--<div class="layui-card-body">-->
                    <div id="container"></div>

                    <div class="info">
                        <h4 id='status'></h4><hr>
                        <p id='result'></p><hr>
                    </div>
                <#--</div>-->
            <#--</div>-->
        <#--</div>-->

    <#--</div>-->
<#--</div>-->

<script src="/static/layui/layui.js"></script>

<#--<script src="/static/amap/indoormap-1.0.js"></script>-->
<script type="text/javascript"
        src="https://webapi.amap.com/maps?v=1.4.15&key=3a88fe902c7cf008982fe2efe2334d06"></script>

<script charset="utf-8">

    layui.use(['layer', 'form', 'table', 'util'], function () {
        var $ = layui.jquery;
        var layer = layui.layer;
        var form = layui.form;
        var table = layui.table;
        var util = layui.util;

        var map = new AMap.Map('container', {
            // zoom:11,//级别
            // center: [116.397428, 39.90923],//中心点坐标
            // viewMode:'3D',//使用3D视图
            resizeEnable: true
        });

        AMap.plugin(['AMap.Geolocation','AMap.ToolBar','AMap.Walking'],function(){//异步同时加载多个插件
            var geolocation = new AMap.Geolocation({
                enableHighAccuracy: true,//是否使用高精度定位，默认:true
                timeout: 10000,          //超过10秒后停止定位，默认：5s
                buttonPosition:'RB',    //定位按钮的停靠位置
                buttonOffset: new AMap.Pixel(10, 20),//定位按钮与设置的停靠位置的偏移量，默认：Pixel(10, 20)
                zoomToAccuracy: true,   //定位成功后是否自动调整地图视野到定位点
            });
            map.addControl(geolocation);
            geolocation.getCurrentPosition(function(status,result){
                if(status=='complete'){
                    onComplete(result)
                }else{
                    onError(result)
                }
            });

            var toolbar = new AMap.ToolBar();
            map.addControl(toolbar);
            //
            // var walker = new AMap.Walking();//步行路线规划
            // walker.search([116.399028, 39.845042])
        });

        //解析定位结果
        function onComplete(data) {
            $('#status').html('定位成功');
            var str = [];
            str.push('定位结果：' + data.position);
            str.push('定位类别：' + data.location_type);
            if(data.accuracy){
                str.push('精度：' + data.accuracy + ' 米');
            }//如为IP精确定位结果则没有精度信息
            str.push('是否经过偏移：' + (data.isConverted ? '是' : '否'));
            $('#result').html(str.join('<br>'));
        }
        //解析定位错误信息
        function onError(data) {
            $('#status').html('定位失败');
            $('#result').html('失败原因排查信息:'+data.message);
        }
    });
</script>
</body>
</html>