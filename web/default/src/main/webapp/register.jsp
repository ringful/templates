
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
    <h1 class="text-red">Register</h1>
    
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
        <form action="/register" method="POST" class="form-horizontal">
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
                    <label class="control-label" for="firstname">Your name</label>
                    <div class="controls">
                        <input type="text" name="firstname" id="firstname" placeholder="Firstname" class="input-medium" value="${sessionScope.signup.teenFirstname}" required />
                        <input type="text" name="lastname"  id="lastname"  placeholder="Lastname"  class="input-medium" value="${sessionScope.signup.teenLastname}" required />
                    </div>
                </div>
                    
                <div class="control-group"> 
                    <label class="control-label" for="dob">Date of birth</label>
                    <div class="controls">
                        <%= com.ringfulhealth.demoapp.services.Util.createHtmlFields(null) %>
                    </div>
                </div>
                        
                <div class="control-group"> 
                    <label class="control-label" for="email">Email</label>
                    <div class="controls">
                        <input type="email" name="email" id="email" class="input-xxlarge" value="${sessionScope.signup.teenEmail}" required />
                    </div>
                </div>
                    
                <div class="control-group"> 
                    <label class="control-label" for="phone">Phone</label>
                    <div class="controls">
                        <input type="tel" name="phone" id="phone" class="input-xxlarge" value="${sessionScope.signup.teenPhone}" required />
                    </div>
                </div>
                
                <div class="control-group">
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary btn-green">Register</button>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    
    <jsp:include page="footer.jsp"/>
</article><!--/.container!--> 

    </body>
</html>
