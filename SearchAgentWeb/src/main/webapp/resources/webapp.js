var startBtn = document.getElementById('startBtn');
var loader = document.getElementById('loader');
var query = document.getElementById('query');
var vDur = document.getElementById('vDuration');
var qDur = document.getElementById('qDuration');
var local = document.getElementById('localization');
var numOfPages = document.getElementById('numOfPages');

/** Creates request to the server */
function createRequest() {
    var xhr = new XMLHttpRequest();
    var url = (location.href).substr(0, (location.href).lastIndexOf('/')).concat("/action");
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");

    return xhr;
}

/** Sends request with parameters to the server to execute search of query and add results to the DB */
function getGoogleSearchResults() {
    startBtn = document.getElementById('startBtn');
    loader = document.getElementById('loader');
    query = document.getElementById('query');
    vDur = document.getElementById('vDuration');
    qDur = document.getElementById('qDuration');
    local = document.getElementById('localization');
    numOfPages = document.getElementById('numOfPages');
    var xhr = createRequest();
    var data = JSON.stringify({"action":"getGoogleSearchResults", "query": query.value, "vDuration": vDur.options[vDur.selectedIndex].value,
        "qDuration": qDur.options[qDur.selectedIndex].value, "localization": local.options[local.selectedIndex].value, "numOfPages": numOfPages.value});

    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4) {
            var response = JSON.parse(xhr.responseText);
            var result = response.result.toString();
            if (result == 1) {
                loader.style.visibility = "hidden";
                startBtn.style.backgroundColor = "#4CAF50";
                startBtn.style.cursor = "non-allowed";
                startBtn.innerHTML = "Done";
                setTimeout(function(){clearToDefaultGSA();}, 3000);
            } else {
                loader.style.visibility = "hidden";
                startBtn.style.backgroundColor = "#F93D3D";
                startBtn.style.cursor = "non-allowed";
                startBtn.innerHTML = "Failed";
            }
        }
    };

    xhr.send(data);

    /** Searching in progress indication */
    startBtn.style.cursor = "none";
    startBtn.style.backgroundColor = "orange";
    startBtn.innerHTML = "Wait...";
    loader.style.visibility = "visible";
}

function clearToDefaultGSA() {
    startBtn.style.backgroundColor = "cornflowerblue";
    startBtn.style.cursor = "pointer";
    startBtn.innerHTML = "Start";
    query.value = "";
    vDur.selectedIndex = 0;
    qDur.selectedIndex = 0;
    local.selectedIndex = 0;
    numOfPages.value = "";
}