<%@page contentType="text/html; charset=utf-8" %>
<%@page trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request" />
<c:set var="resourcePath" value="${contextPath}/resources" scope="request" />
<c:set var="jsPath" value="${resourcePath}/js" scope="request" />
<c:set var="cssPath" value="${resourcePath}/css" scope="request" />
<c:set var="extJs" value="${resourcePath}/extjs" scope="request"/>
<c:set var="extCss" value="${resourcePath}/extjs/resources/css" scope="request"/>

<c:set var="locale" value="${pageContext.response.locale}" scope="request" />
<c:set var="lang" value="${pageContext.response.locale.language}" scope="request" />
<c:set var="theme" value="default" scope="request" />
<c:set var="themePath" value="${resourcePath}/css/theme/${lang}/${theme}" scope="request" />


<c:set var="appVersion" value="${applicationScope.appVersion}" scope="request"/>

<c:if test="${cookie.debug.value == 'true' or param['_debug'] == 'true'}">
<c:set var="debug" value="true" scope="request"/>
</c:if>

<s:eval expression="@appProps['app.env']" var="app_env"/>
