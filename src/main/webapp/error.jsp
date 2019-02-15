<%--
  Created by IntelliJ IDEA.
  User: w
  Date: 2019/2/15
  Time: 20:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>SpringBoot模版渲染</title>
    <link rel="icon" type="image/x-icon" href="/images/study.ico"/>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
</head>
<body>
<p th:text="${url}"/>
<p th:text="${exception.message}"/>
</body>
</html>
