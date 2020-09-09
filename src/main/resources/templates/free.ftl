<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>FreeMarker页面</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <#--<link rel="stylesheet" href="/root/layui/css/layui.css" media="all">-->
    <!-- 注意：如果你直接复制所有代码到本地，上述css路径需要改成你本地的 -->
</head>
<body>
<h1>${msg}</h1>
<img src="${path}" />
<form action="/file/upload" method="post" enctype="multipart/form-data">
    <div>
        <label for="name">选择上传文件</label>
        <input type="file" name="file">
    </div>
    <div class="button">
        <button type="submit">提交</button>
    </div>
</form>

<form action="/file/download" method="post">
    <div class="button">
        <button type="submit">下载</button>
    </div>
</form>
</body>
</html>