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
    h3 {
        font-size: 1.17em;
        line-height: 1.4;
        font-weight: bold;
        margin: 2px;
    }

    hr {
        margin: 1px;
    }

    html, body {
        width: 100%;
        height: 100%;
        margin: 0px;
    }

    .map {
        height: 100%;
        width: 100%;
        float: left;
    }

    #mapDiv .amap-indoormap-floorbar-control {
        bottom: 10%
    }

    #panel {
        position: fixed;
        background-color: white;
        max-height: 90%;
        overflow-y: auto;
        top: 10px;
        right: 10px;
        width: 280px;
    }
    #panel .amap-call {
        background-color: #009cf9;
        border-top-left-radius: 4px;
        border-top-right-radius: 4px;
    }
    #panel .amap-lib-walking {
        border-bottom-left-radius: 4px;
        border-bottom-right-radius: 4px;
        overflow: hidden;
    }
</style>

<body>

<div id="mapDiv" class="map" tabindex="0"></div>
<div id="panel"></div>

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

        var map = new AMap.Map('mapDiv', {
            resizeEnable: true,
            center: [116.518542, 39.924677],
            zoom: 18
        });

        map.on('indoor_create', function () {
            map.indoorMap.showIndoorMap('B000A856LJ', -2);
        });

        AMap.plugin(['AMap.ToolBar','AMap.Walking'],function(){//异步同时加载多个插件
            var toolbar = new AMap.ToolBar();
            map.addControl(toolbar);

            //步行导航
            var walking = new AMap.Walking({
                map: map,
                panel: "panel"
            });

            //根据起终点坐标规划步行路线
            walking.search([116.519998,39.92421], [116.518716,39.924445], function (status, result) {
                // result即是对应的步行路线数据信息，相关数据结构文档请参考  https://lbs.amap.com/api/javascript-api/reference/route-search#m_WalkingResult
                if (status === 'complete') {
                    layer.msg('绘制步行路线完成')
                } else {
                    layer.msg('步行路线数据查询失败' + result)
                }
            });
        });



    });
</script>
</body>
</html>