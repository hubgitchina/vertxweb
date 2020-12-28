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
    <link rel="stylesheet" href="https://indoorweb.amap.com/indoormap-1.2.css" media="all">
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
</style>

<body>

<div id="container"></div>

<script src="/static/layui/layui.js"></script>

<#--<script src="/static/amap/indoormap-1.0.js"></script>-->
<script type="text/javascript"
        src="https://indoorweb.amap.com/indoormap-1.2.js"></script>

<script charset="utf-8">

    layui.use(['layer', 'form', 'table', 'util'], function () {
        var $ = layui.jquery;
        var layer = layui.layer;
        var form = layui.form;
        var table = layui.table;
        var util = layui.util;

        var map = new Indoor.Map('container',{
            key:'3a88fe902c7cf008982fe2efe2334d06',
            buildingId:'B000A856LJ'
        });

        //由于地图数据使用了异步加载，为避免出错请把所有的逻辑放在mapready事件内
        map.once('mapready',function(){
            console.log(map.getFloor());
        })



    });
</script>
</body>
</html>