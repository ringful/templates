
<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Columbia University &ndash; Vamos Project</title>
        <jsp:include page="header_meta.jsp"/>
    </head>
    
    <body>
<c:if test="${empty param.noheader}">
<jsp:include page="header.jsp"/>
</c:if>
        
<article class="container">
    <h1 class="text-red">Reset Password</h1>
    <p>Please fill in your username as well as the email address or cell phone number registered with the username</p>
    <hr/>
    
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
        <form action="/reset_password" method="POST" class="form-horizontal">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="username">Username</label>
                    <div class="controls">
                        <input type="text" class="input-xlarge" name="username" autocapitalize="off" autocorrect="off" autocomplete="off" id="username"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="email">Email</label>
                    <div class="controls">
                        <input type="email" class="input-xlarge" name="email" id="email"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="phone">Mobile phone</label>
                    <div class="controls">
                        <input type="tel" class="input-xlarge" name="phone" id="phone"/>
                    </div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Reset password</button>
                </div>
            </fieldset>
        </form>
    </div>
    
    <jsp:include page="footer.jsp"/>
</article><!--/.container!--> 

    </body>
</html>
