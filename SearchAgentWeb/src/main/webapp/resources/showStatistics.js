var startBtn;
var loader;
var agentType;
var query;
var startDate;
var endDate;
var searchForm;

function showStatistics() {
    var url = (location.href).substr(0, (location.href).lastIndexOf('/')).concat("/statistics.jsp");
    window.location.replace(url);
}

function returnToMain() {
    window.location.replace((location.href).substr(0, (location.href).lastIndexOf('/')));
}

/** Creates request to the server */
function createRequest() {
    var xhr = new XMLHttpRequest();
    var url = (location.href).substr(0, (location.href).lastIndexOf('/')).concat("/action");
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");

    return xhr;
}

/** Sends request with parameters to the server to execute search of query and add results to the DB */
function getStatistics() {
    startBtn = document.getElementById('startBtn');
    loader = document.getElementById('loader');
    searchForm = document.getElementById('searchForm');

    var temp = document.getElementById('agentType');
    agentType = temp.options[temp.selectedIndex].value;
    query = document.getElementById('query');
    startDate = document.getElementById('startDate');
    endDate = document.getElementById('endDate');
    var xhr = createRequest();
    var data = JSON.stringify({"action":"getStatistics", "agentType": agentType, "query": query.value, "startDate": startDate.value, "endDate": endDate.value});

    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4) {
            var response = JSON.parse(xhr.responseText);
            var result = response.result.toString();
            if (result == 1) {
                loader.style.visibility = "hidden";
                startBtn.style.backgroundColor = "#4CAF50";
                startBtn.style.cursor = "non-allowed";
                startBtn.innerHTML = "Done";
                setTimeout(displayResults(), 5000);
            } else {
                loader.style.visibility = "hidden";
                startBtn.style.backgroundColor = "#F93D3D";
                startBtn.style.cursor = "non-allowed";
                startBtn.innerHTML = "Failed";
            }
        }
    };

    xhr.send(data);

    /** Getting statistics in progress indication */
    startBtn.style.cursor = "none";
    startBtn.style.backgroundColor = "orange";
    startBtn.innerHTML = "Wait...";
    loader.style.visibility = "visible";
}

function displayResults() {
    document.getElementById('statistics').style.visibility = "visible";
    setTimeout(clearToDefault(), 5000);
}

function clearToDefault() {
    startBtn.style.backgroundColor = "cornflowerblue";
    startBtn.style.cursor = "pointer";
    startBtn.innerHTML = "Start";
    query.value = "";
    startDate.value = "";
    endDate.value = "";
    searchForm.style.visibility = "visible";
}
