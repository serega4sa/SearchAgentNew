<%@ page import="com.chmihun.searchagent.agents.GoogleSearch" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Search Agent</title>
    <script src="resources/webapp.js"></script>
    <script src="resources/showStatistics.js"></script>
    <link rel="stylesheet" type="text/css" href="resources/style.css">
    <link rel="icon" href="images/spy_icon.png">
</head>
<body>
<div id="menu">
    <br/>
    <b style="font-size:300%;color:white;">Menu</b>
    <br/><br/>
    <a href="#">
        <img src="images/google_logo.png" onclick="returnToMain()"/>
    </a>
    <br/><br/>
    <a href="#">
        <img src="images/mailru_logo.png"/>
    </a>
    <br/><br/>
    <a href="#">
        <img src="images/mail_logo.png"/>
    </a>
    <br/><br/>
    <a href="#">
        <img class="buttonOfPage" src="images/statistics_logo.png"/>
    </a>
</div>
<div id="content">
    <div id="header">
        <br/>
        <b style="font-size:300%;">Statistics</b>
    </div>
    <div id="inside">
        <div id="searchForm">
            <br/>
            <p>Choose agent:</p>
            <select id="agentType">
                <option value="google">Google Agent</option>
                <option value="mailru">Mail.ru Agent</option>
            </select>
            <br/>
            <p>Enter query:</p>
            <input id="query" type="text" >
            <br/>
            <p>Enter start day (format YYYY-MM-DD HH:mm):</p>
            <input id="startDate" type="text" >
            <br/>
            <p>Enter end day (format YYYY-MM-DD HH:mm):</p>
            <input id="endDate" type="text" >
        </div>
        <div id="statistics">
            <p>Results has been successfully saved to excel file. </br>You can find them by the following path:</p> </br><%= System.getProperty("user.dir") + "\\Statistics\\" %>
        </div>
        <div id="progressBar">
            <button id="startBtn" class="button buttonStart" onclick="getStatistics()">Get</button>
            <div id="loader"></div>
        </div>
    </div>
</div>
</body>
</html>
