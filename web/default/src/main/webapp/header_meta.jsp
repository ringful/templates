

<%@page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <!-- Bootstrap -->
        <link href="css/bootstrap.min.css" rel="stylesheet"/>
        <link href="css/bootstrap-responsive.min.css" rel="stylesheet"/>
        <link href="css/style.css" rel="stylesheet"/>

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
          <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
          <style type="text/css">
ul.dropdown-menu {
    position: relative;
    z-index: 10000;
}
          </style>
        <![endif]-->

        <!-- Le fav and touch icons -->
        <link rel="shortcut icon" href="ico/favicon.ico"/>
        <link rel="apple-touch-icon" sizes="114x114" href="ico/apple-touch-icon-114.png"/>
        <link rel="apple-touch-icon" sizes="72x72" href="ico/apple-touch-icon-72.png"/>
        <link rel="apple-touch-icon" href="ico/apple-touch-icon-57.png"/>
        
        <script src="js/jquery.js"></script>
        <script src="js/jquery.placeholder.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/custom-ivamos.js"></script>
        
        <script>
// Multiple ready functions are allowed. So each page can also has its own rwady function.
// However, the execution sequence is that this one will get called first.
$(document).ready(function() {
    var width = (window.innerWidth > 0) ? window.innerWidth : screen.width;
    if (width < 500 && !location.hash) {
        setTimeout (function () {
            if (!pageYOffset) window.scrollTo(0, 1);
        }, 5);
    }
    
    $('input, textarea').placeholder();
});
    </script>
        