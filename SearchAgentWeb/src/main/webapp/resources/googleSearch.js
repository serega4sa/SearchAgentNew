var startBtn;
var loader;

var query;
var vDur;
var qDur;
var local;
var numOfPages;

function initPageElements() {
    startBtn = document.getElementById('startBtn');
    loader = document.getElementById('loader');
    query = document.getElementById('query');
    vDur = document.getElementById('vDuration');
    qDur = document.getElementById('qDuration');
    local = document.getElementById('localization');
    numOfPages = document.getElementById('numOfPages');
}

/**
 * Checks whether parameters aren't empty
 * @return boolean - true if any field is empty, otherwise false
 */
function isParamsEmpty() {
    return (query.value.trim() === "" && numOfPages.value.trim() === "");
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
function getGoogleSearchResults() {
    initPageElements();
    if (isParamsEmpty()) {
        let xhr = createRequest();
        let data = JSON.stringify({
            "action": "getGoogleSearchResults",
            "query": query.value,
            "vDuration": vDur.options[vDur.selectedIndex].value,
            "qDuration": qDur.options[qDur.selectedIndex].value,
            "localization": local.options[local.selectedIndex].value,
            "numOfPages": numOfPages.value
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
                    setTimeout(function () {
                        clearToDefaultState();
                    }, 3000);
                } else {
                    loader.style.visibility = "hidden";
                    startBtn.style.backgroundColor = "#F93D3D";
                    startBtn.style.cursor = "non-allowed";
                    startBtn.innerHTML = "Failed";
                }
            }
        };

        xhr.send(data);

        // Searching in progress indication
        startBtn.style.cursor = "none";
        startBtn.style.backgroundColor = "orange";
        startBtn.innerHTML = "Wait...";
        loader.style.visibility = "visible";
    } else {
        window.alert("Please fill all data");
    }
}

function clearToDefaultState() {
    startBtn.style.backgroundColor = "cornflowerblue";
    startBtn.style.cursor = "pointer";
    startBtn.innerHTML = "Start";
    query.value = "";
    vDur.selectedIndex = 0;
    qDur.selectedIndex = 0;
    local.selectedIndex = 0;
    numOfPages.value = "";
}