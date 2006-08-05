
<%@ page language="java" import="com.mobiletech.imageconverter.*" pageEncoding="UTF-8"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>ImageConverter Status</title>    
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
  </head>
  
  <body>
    <%
		out.println(ImageConverter.getVersionInformation());		
	%>
  </body>
</html>
