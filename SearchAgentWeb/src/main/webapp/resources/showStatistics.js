var startBtn;
var loader;
var statistics;
var statisticsStatus;
var statisticsPath;

var agentType;
var query;
var startDate;
var endDate;

function initPageElements() {
    startBtn = document.getElementById('startBtn');
    loader = document.getElementById('loader');
    statistics = document.getElementById('statistics');
    statisticsStatus = document.getElementById('statisticsStatus');
    statisticsPath = document.getElementById('statisticsPath');
    agentType = document.getElementById('agentType');
    query = document.getElementById('query');
    startDate = document.getElementById('startDate');
    endDate = document.getElementById('endDate');
}

function showStatistics() {
    let url = (location.href).substr(0, (location.href).lastIndexOf('/')).concat("/statistics.jsp");
    window.location.replace(url);
}

function returnToMain() {
    window.location.replace((location.href).substr(0, (location.href).lastIndexOf('/')));
}

/**
 * Checks whether parameters aren't empty
 * @return boolean - true if any field is empty, otherwise false
 */
function isParamsEmpty() {
    return query.value === "" || startDate.value === "" || endDate.value === "";
}

/**
 * Checks whether dates are correctly specified (end date is after start date)
 * @return boolean - true if correct, otherwise false
 */
function isCorrectDates() {
    let sDate = new Date(startDate.value);
    let eDate = new Date(endDate.value);
    return eDate > sDate;
}

/**
 * Creates request to the server
 * */
function createRequest() {
    let xhr = new XMLHttpRequest();
    let url = (location.href).substr(0, (location.href).lastIndexOf('/')).concat("/action");
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");

    return xhr;
}

/**
 * Sends request with parameters to the server to execute search of query and add results to the DB
 * */
function getStatistics() {
    initPageElements();
    let agent = agentType.options[agentType.selectedIndex].value;

    let isEmpty = isParamsEmpty();
    let isCorrect = isCorrectDates();

    if (!isEmpty && isCorrect) {
        let xhr = createRequest();
        let data = JSON.stringify({
            "action": "getStatistics",
            "agentType": agent,
            "query": query.value,
            "startDate": startDate.value,
            "endDate": endDate.value
        });

        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                let response = JSON.parse(xhr.responseText);
                let result = response.result.toString();
                if (result == 1) {
                    loader.style.visibility = "hidden";
                    startBtn.style.backgroundColor = "#4CAF50";
                    startBtn.style.cursor = "non-allowed";
                    startBtn.innerHTML = "Done";
                } else {
                    loader.style.visibility = "hidden";
                    startBtn.style.backgroundColor = "#F93D3D";
                    startBtn.style.cursor = "non-allowed";
                    startBtn.innerHTML = "Failed";
                }
                displayResults(result);
            }
        };

        xhr.send(data);

        // Getting statistics in progress indication
        startBtn.style.cursor = "none";
        startBtn.style.backgroundColor = "orange";
        startBtn.innerHTML = "Wait...";
        loader.style.visibility = "visible";
    } else {
        let warningMessage;
        if (isEmpty && !isCorrect) {
            warningMessage = "Please specify query and correct dates";
        } else {
            warningMessage = isEmpty ? "Please fill all fields" : "Please specify correct dates interval"
        }
        window.alert(warningMessage);
    }
}

function displayResults(result) {
    if (result == 1) {
        statisticsStatus.textContent = "Results has been successfully saved to excel file. You can find them by the following path:";
        statisticsPath.style.visibility = "visible";
    } else if (result == 0) {
        statistics.style.color = "#F93D3D";
        statisticsStatus.textContent = "Requested content wasn't found for the specified time interval.";
        statisticsPath.style.visibility = "hidden";
    }
    statisticsStatus.style.visibility = "visible";
    setTimeout(function () {
        clearToDefaultState();
    }, 10000);
}

function clearToDefaultState() {
    startBtn.style.backgroundColor = "cornflowerblue";
    startBtn.style.cursor = "pointer";
    startBtn.innerHTML = "Start";
    query.value = "";
    startDate.value = "";
    endDate.value = "";
    statistics.style.color = "#4CAF50";
    statisticsStatus.style.visibility = "hidden";
    statisticsPath.style.visibility = "hidden";
}
