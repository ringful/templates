<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Ringful Health</title>
        <jsp:include page="header_meta.jsp"/>
    </head>
    
    <body>
<jsp:include page="header.jsp"/>
        
<article class="container">
    
    <c:if test="${!empty param.error}">
        <div class="alert alert-error">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <p class="lead">${param.error}</p>
        </div>
    </c:if>
    <c:if test="${!empty param.msg}">
        <div class="alert alert-info">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <p class="lead">${param.msg}</p>
        </div>
    </c:if>
    <c:if test="${!empty param.success}">
        <div class="alert alert-success">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <p class="lead">${param.success}</p>
        </div>
    </c:if>
    
    <jsp:include page="footer.jsp"/>
</article><!--/.container!--> 

    </body>
</html>
