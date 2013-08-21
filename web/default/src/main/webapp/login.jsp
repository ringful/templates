
<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Ringful Health</title>
        <jsp:include page="header_meta.jsp"/>
    </head>
    
    <body>
<jsp:include page="header.jsp?form=empty"/>
        
<article class="container">
    <h1 class="text-red">Login</h1>
    
    <c:if test="${!empty param.error}">
        <div class="alert alert-error">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            ${param.error}
        </div>
    </c:if>
    <c:if test="${!empty param.msg}">
        <div class="alert alert-info">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            ${param.msg}
        </div>
    </c:if>
    
    <div class="row-fluid">
        <form action="/login" method="POST" class="form-horizontal">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="username">Username</label>
                    <div class="controls">
                        <input type="text" name="username" id="username" class="input-xxlarge" autocapitalize="off" autocorrect="off" autocomplete="off" required />
                    </div>
                </div>
                
                <div class="control-group">
                    <label class="control-label" for="password">Password</label>
                    <div class="controls">
                        <input type="password" name="password" id="password" class="input-xxlarge" required />
                    </div>
                </div>
                
                <div class="control-group">
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary btn-green">Login</button>
                        <a href="reset_password.jsp">I forgot my password!</a>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    
    <jsp:include page="footer.jsp"/>
</article><!--/.container!--> 

    </body>
</html>
