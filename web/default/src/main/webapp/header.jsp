
<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="navbar navbar-fixed-top navbar-inverse">
    <div class="navbar-inner">
      <div class="container"> 
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse"> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span> </a> 
          <a class="brand brandempty" href="#"></a>
          <div class="nav-collapse collapse">
              <ul class="nav">
                  <c:if test="${empty sessionScope.user}">
                      <li class="blue"><a href="#">Home</a></li>
                  </c:if>
                  <c:if test="${!empty sessionScope.user}">
                      <c:if test="${sessionScope.user.status == 'ADMIN'}">
                          <li class="blue"><a href="admin_index.jsp">Admin</a></li>
                      </c:if>
                      <c:if test="${sessionScope.user.status != 'ADMIN'}">
                          <li class="blue"><a href="profile.jsp">Profile</a></li>
                      </c:if>
                  </c:if>
              </ul>
              <c:if test="${empty sessionScope.user}">
                  <c:if test="${param.form ne 'empty'}">
              <form action="/login" method="POST" class="navbar-form pull-right">
                  <input class="span2" type="text" name="username" placeholder="username" autocapitalize="off" autocorrect="off" autocomplete="off" required />
                  <input class="span2" type="password" name="password" placeholder="Password" required />
                  <button type="submit" class="btn">Sign in</button>
              </form>
                  </c:if>
              </c:if>
              <c:if test="${!empty sessionScope.user}">
              <div class="btn-group navbar-form pull-right">
                  <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                    <i class="icon-user icon-white"></i> ${sessionScope.user.firstname} ${sessionScope.user.lastname}
                    <span class="caret"></span>
                  </a>
                  <ul class="dropdown-menu">
                    <li><a href="profile.jsp">My Profile</a></li>
                    <li class="divider"></li>
                    <li><a href="/logout">Sign Out</a></li>
                  </ul>
              </div>
              </c:if>
          </div>
      </div>
    </div>
</div>