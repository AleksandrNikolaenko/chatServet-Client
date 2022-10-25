<%@ page import="academy.prog.Users" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Prog Academy chat server</title>
</head>
<body>
<% Users users = Users.getInstance(); %>
<% File[] usersFiles = new File("users\\").listFiles(); %>
<% Long timeNow = new Date().getTime(); %>
<% if (usersFiles != null) { %>
<%      for (File userFile : usersFiles) { %>
<%          users.setTime(userFile.getName().substring(0, userFile.getName().length() -4), timeNow); %>
<%      } %>
<% } %>
<h1>Server works!</h1>
</body>
</html>