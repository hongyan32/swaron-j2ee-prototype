<!doctype html>
<%@tag description="basic layout" trimDirectiveWhitespaces="true" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="jwr" uri="http://jawr.net/tags-el" %>
<%@attribute name="head" fragment="true"%>
<%@attribute name="scripts" fragment="true"%>
<%@attribute name="title" required="true" rtexprvalue="true" type="java.lang.String"%>
<%@attribute name="pageClass" required="false" rtexprvalue="true" type="java.lang.String"%>
<jsp:include page="/WEB-INF/jsp/includes/pathVars.jsp" flush="false" />
<html>
<head>
<title><s:message code="${title}" text="${title}"/></title>
<c:if test="${debug}">
<link rel="stylesheet" type="text/css" href="${extCss}/ext-all-neptune.css" />
</c:if>
<c:if test="${not debug}">
<link rel="stylesheet" type="text/css" href="${extCss}/ext-all-neptune.css" />
</c:if>
<link rel="stylesheet" type="text/css" href="${cssPath}/theme/${lang}/${theme}/locale.css?v=${appVersion}" />
<jwr:style src="/bundles/css/extux.css" useRandomParam="false"/>
<jwr:style src="/bundles/css/app.css" useRandomParam="false"/>
<jsp:invoke fragment="head"></jsp:invoke>
<jsp:include page="/WEB-INF/jsp/includes/appConfig.jsp" flush="false" />

<jwr:script src="/bundles/js/messages.js"/>
<s:eval expression="@appProps['app.env']" var="app_env"/>

<c:if test="${debug}">
<!-- dynamic loading version start -->
<script type="text/javascript" src="${extJs}/ext-dev.js"></script>
<script type="text/javascript">
	Ext.Loader.setConfig({
	    enabled: true,
	    disableCaching : false
	});
	Ext.Loader.setPath('Ext', '${extJs}/src');
	Ext.Loader.setPath('Ext.ux', '${resourcePath}/extjs-ux');
	Ext.Loader.setPath('App', '${jsPath}/app');
	Ext.Loader.setPath('Lib', '${resourcePath}/libjs/src');
</script>
<jwr:script src="/bundles/js/base.js" useRandomParam="false"/>
<!-- dynamic loading version end -->
</c:if>
<c:if test="${not debug}">
<!-- static loading version start, use cdn -->
<!-- <script type="text/javascript" src="${extJs}/ext-all.js"></script> -->
<script type="text/javascript" src="http://cdn.sencha.io/ext-4.1.1-gpl/ext-all.js"></script>
<jwr:script src="/bundles/js/base.js" useRandomParam="false"/>
<jwr:script src="/bundles/js/extux.js" useRandomParam="false"/>
<jwr:script src="/bundles/js/widget.js" useRandomParam="false"/>
<!-- static loading version end -->
</c:if>
<script type="text/javascript">
(function(){
	if(App.cfg.lang != 'en'){
		var url = Ext.util.Format.format("{0}/locale/ext-lang-{1}.js?v=${appVersion}", App.cfg.extJs, App.cfg.lang);
		Ext.Loader.loadScript(url);
	}
})();
Ext.onReady(function(){
	Ext.getBody().unmask();
});
</script>
</head>
<body id="app">
	<script type="text/javascript">
	Ext.getBody().mask("Loading Page ...");
	</script>
	<div id="wrapper" class="basic-page ${pageClass}">
		<div id="header"></div>
		<div id="side"></div>
		<div id="main">
			<div id="body">
				<jsp:doBody/>
			</div>
		</div>
		<div id="side2"></div>
		<div id="footer"></div>
	</div>
<jsp:invoke fragment="scripts" />
</body>
</html>