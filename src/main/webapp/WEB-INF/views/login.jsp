<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>로그인</title>

    <!-- SweetAlert2 -->
    <link rel="stylesheet" href="<c:url value='/css/sweetalert2.min.css'/>" />
    <script src="<c:url value='/js/sweetalert2.min.js'/>"></script>
    <script>
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000
        });
    </script>

    <!-- Kakao JS SDK -->
    <script src="https://developers.kakao.com/sdk/js/kakao.min.js"></script>
    <script>
        Kakao.init('fee2c7b0cb94e410bf197ab10532e947');  // ← 여기에 발급받은 JS Key

        // 절대 Redirect URI: http(s)://{호스트}:{포트}/oauth/kakao/callback
        const kakaoRedirectUri = window.location.origin
            + '<c:url value="/oauth/kakao/callback"/>';
    </script>
</head>
<body>
<!-- 로그인 실패 -->
<c:if test="${not empty param.error}">
    <script>
        Toast.fire({
            icon: 'error',
            title: '이메일 또는 비밀번호가 올바르지 않습니다.'
        });
    </script>
</c:if>

<!-- 로그아웃 성공 -->
<c:if test="${not empty param.logout}">
    <script>
        Toast.fire({
            icon: 'success',
            title: '정상적으로 로그아웃되었습니다.'
        });
    </script>
</c:if>

<h2>로그인</h2>

<!-- 폼 로그인 -->
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

<hr/>

<!-- 카카오 로그인 버튼 -->
<div>
    <a href="javascript:Kakao.Auth.authorize({redirectUri: kakaoRedirectUri});">
        <img src="<c:url value='/images/kakao_login_btn.png'/>" alt="카카오 로그인" style="max-width:160px; height:auto;"/>
    </a>
    <hr/>
         <a href="<c:url value='/oauth/naver'/>">
            <img
                    src="<c:url value='/images/naver_login_btn.png'/>"
                    alt="네이버 로그인"
                    style="max-width:160px; height:auto;"
            />
        </a>
</div>

</body>
</html>
