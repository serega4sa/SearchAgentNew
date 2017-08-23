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
        <img class="buttonOfPage" src="images/google_logo.png"/>
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
        <img src="images/statistics_logo.png" onclick="showStatistics()"/>
    </a>
</div>
<div id="content">
    <div id="header">
        <br/>
        <b style="font-size:300%;">Google Search Agent</b>
    </div>
    <div id="inside">
        <div id="searchForm">
            <br/>
            <p>Enter query(s):</p>
            <input id="query" type="text" >
            <br/>
            <p>Choose video duration:</p>
            <select id="vDuration">
                <option value="any">Any</option>
                <option value="short">Short</option>
                <option value="medium">Medium</option>
                <option value="long">Long</option>
            </select>
            <br/>
            <p>Choose time:</p>
            <select id="qDuration">
                <option value="any">Any</option>
                <option value="hour">Past hour</option>
                <option value="day">Past 24 hours</option>
                <option value="week">Past week</option>
                <option value="month">Past month</option>
                <option value="year">Past year</option>
            </select>
            <br/>
            <p>Choose search localization:</p>
            <select id="localization">
                <option value="ua">UA</option>
                <option value="ru">RU</option>
            </select>
            <br/>
            <p>Specify number of pages with results:</p>
            <input id="numOfPages" type="text">
        </div>
        <div id="progressBar">
            <button id="startBtn" class="button buttonStart" onclick="getGoogleSearchResults()">Start</button>
            <div id="loader"></div>
        </div>
    </div>
</div>
</body>
</html>
