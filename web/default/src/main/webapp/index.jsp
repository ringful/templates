
<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Ringful Health</title>
        <jsp:include page="header_meta.jsp"/>
    </head>
    
    <body>
        
<div class="navbar navbar-fixed-top navbar-inverse">
    <div class="navbar-inner">
      <div class="container"> 
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span> </a> 
          <a class="brand brandempty" href="/index.jsp">Home</a>
          <!--
          <div class="nav-collapse collapse">
              <ul class="nav">
                  <li class="orange"><a href="contact.jsp">Contact</a></li>
                  <li class="green"><a href="contact_es.jsp">Contacto</a></li>
              </ul>
          </div>
          -->
      </div>
    </div>
</div>
        
<article class="container">
    
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
        
        <p class="lead">This is a demo app</p>
        
        <p>
            <a href="login.jsp" class="btn btn-primary">Login</a>
            or
            <a href="register.jsp" class="btn btn-primary">Register</a>
        </p>
        
    </div>  <!--/.row-fluid!-->
    
    <jsp:include page="footer.jsp"/>
</article><!--/.container!--> 

    </body>
</html>
