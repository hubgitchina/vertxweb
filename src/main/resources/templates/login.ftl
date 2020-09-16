<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>FreeMarker-登录页</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <#--<link rel="stylesheet" href="/root/layui/css/layui.css" media="all">-->
    <!-- 注意：如果你直接复制所有代码到本地，上述css路径需要改成你本地的 -->
</head>
<body>
<h1>系统登录</h1>
<form action="/user/login" method="post">
    <div>
        <input name="username" placeholder="请输入账号" class="name" required />
        <input name="password" placeholder="请输入密码" class="password" type="password" required />
    </div>
    <div class="button">
        <button type="submit">登陆</button>
    </div>
</form>
</body>
</html>