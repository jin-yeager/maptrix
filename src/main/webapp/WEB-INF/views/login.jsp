<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>로그인</title>
</head>
<body>
<h2>로그인</h2>

<!-- 로그인 실패 시 -->
<c:if test="${not empty param.error}">
    <p style="color: red;">아이디 또는 비밀번호가 올바르지 않습니다.</p>
</c:if>

<!-- 로그아웃 시 -->
<c:if test="${not empty param.logout}">
    <p style="color: green;">정상적으로 로그아웃되었습니다.</p>
</c:if>

<form action="<c:url value='/authenticate'/>" method="post">
    <div>
        <label for="email">이메일:</label>
        <input type="text" id="email" name="email" required />
    </div>
    <div>
        <label for="password">비밀번호:</label>
        <input type="password" id="password" name="password" required />
    </div>
    <div>
        <button type="submit">로그인</button>
    </div>
</form>
</body>
</html>
