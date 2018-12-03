<#assign ctx=request.contextPath />
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Bootstrap 实例 - 缩略图</title>
    <link rel="stylesheet" href="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>

<form action="${ctx}/" >
    <input type="text" name="username" placeholder="用户名">
    <input type="password" name="pwd" placeholder="密码">
    <button onclick="submit()">登录</button>
</form>

<script>
    $(function () {
    });
</script>

</body>
</html>