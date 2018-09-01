<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
    private String getTomcatPath(String currentURL) {
        String tomcatPath = System.getProperty("user.dir");
        String appName = currentURL.substring(currentURL.lastIndexOf("/") + 1);
        return tomcatPath.substring(0, tomcatPath.lastIndexOf("\\")) + "\\webapps\\" + appName + "\\statistics\\";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Search Agent</title>
    <script src="resources/googleSearch.js"></script>
    <script src="resources/showStatistics.js"></script>
    <link rel="stylesheet" type="text/css" href="resources/style.css">
    <link rel="stylesheet" type="text/css" href="resources/calendar/bootstrap-combined.min.css">
    <link rel="stylesheet" type="text/css" href="resources/calendar/bootstrap-datetimepicker.min.css">
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
            <p>Enter start day:</p>
            <div id="datetimepickerStart" class="input-append date">
                <input id="startDate" type="text"/>
                <span class="add-on">
                    <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                </span>
            </div>
            <br/>
            <p>Enter end day:</p>
            <div id="datetimepickerEnd" class="input-append date">
                <input id="endDate" type="text"/>
                <span class="add-on">
                    <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                </span>
            </div>
        </div>
        <div id="statistics" style="line-height: 35px;">
            <p id="statisticsStatus"></p>
            <p id="statisticsPath"><%= getTomcatPath(request.getContextPath()) %></p>
        </div>
        <div id="progressBar">
            <button id="startBtn" class="button buttonStart" onclick="getStatistics()">Get</button>
            <div id="loader"></div>
        </div>
    </div>
</div>
</body>
<script type="text/javascript" src="resources/calendar/jquery.min.js"></script>
<script type="text/javascript" src="resources/calendar/bootstrap.min.js"></script>
<script type="text/javascript" src="resources/calendar/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript">
    $(function() {
        $('#datetimepickerStart').datetimepicker({
            format: 'yyyy-MM-dd hh:mm',
            pickSeconds: false
        });
    });

    $(function() {
        $('#datetimepickerEnd').datetimepicker({
            format: 'yyyy-MM-dd hh:mm',
            pickSeconds: false
        });
    });
</script>
</html>
