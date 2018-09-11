var EventType = Main.EventType;
var AppCommon = Main.Common;
var GSResults = (function () {
    function GSResults() {
        this.timeoutBeforeReset = 3000;
    }
    GSResults.prototype.initPageElements = function () {
        this.startBtn = document.getElementById('startBtn');
        this.loadingCircle = document.getElementById('loadingCircle');
        this.query = document.getElementById('query');
        this.vDur = document.getElementById('vDuration');
        this.qDur = document.getElementById('qDuration');
        this.local = document.getElementById('localization');
        this.numOfPages = document.getElementById('numOfPages');
    };
    GSResults.prototype.isParamsNotEmpty = function () {
        return this.query.value.length > 0 && this.numOfPages.value.length > 0;
    };
    GSResults.prototype.changeBtnState = function (eventType, success) {
        AppCommon.changeBtnState(eventType, this.startBtn, this.loadingCircle, success);
    };
    GSResults.prototype.getGoogleSearchResults = function () {
        var _this = this;
        this.initPageElements();
        if (this.isParamsNotEmpty()) {
            var xhr_1 = AppCommon.createRequest();
            var data = JSON.stringify({
                "action": "getGoogleSearchResults",
                "query": this.query.value,
                "vDuration": this.vDur.options[this.vDur.selectedIndex].value,
                "qDuration": this.qDur.options[this.qDur.selectedIndex].value,
                "localization": this.local.options[this.local.selectedIndex].value,
                "numOfPages": this.numOfPages.value
            });
            xhr_1.onreadystatechange = function () {
                if (xhr_1.readyState == 4) {
                    var response = JSON.parse(xhr_1.responseText);
                    var isSuccessful = response.result.toString() == 1;
                    _this.changeBtnState(EventType.RESPONSE, isSuccessful);
                    if (isSuccessful) {
                        setTimeout(function () {
                            _this.clearToDefaultState();
                        }, _this.timeoutBeforeReset);
                    }
                }
            };
            xhr_1.send(data);
            this.changeBtnState(EventType.START);
        }
        else {
            window.alert("Please fill all data");
        }
    };
    GSResults.prototype.clearToDefaultState = function () {
        this.changeBtnState(EventType.END);
        this.query.value = "";
        this.vDur.selectedIndex = 0;
        this.qDur.selectedIndex = 0;
        this.local.selectedIndex = 0;
        this.numOfPages.value = "";
    };
    return GSResults;
}());
