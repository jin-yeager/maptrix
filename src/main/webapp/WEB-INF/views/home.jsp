<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Insert title here</title>
</head>
<body>
<h2>Hello Spring!!</h2>

<!-- 로그인된 상태라면 -->
<c:if test="${not empty pageContext.request.userPrincipal}">
  <p>환영합니다, ${pageContext.request.userPrincipal.name}님!</p>
  <!-- Spring Security 기본 로그아웃 URL -->
  <form action="<c:url value='/logout'/>" method="post">
    <button type="submit">Logout</button>
  </form>
</c:if>

<!-- (로그인 안 된 상태라면 추가로 로그인 링크를 보여주고 싶다면) -->
<c:if test="${empty pageContext.request.userPrincipal}">
  <a href="<c:url value='/login'/>">Login</a>
</c:if>

</body>
</html>