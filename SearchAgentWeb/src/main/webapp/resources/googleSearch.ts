import EventType = Main.EventType;
import AppCommon = Main.Common;

class GSResults {

    private timeoutBeforeReset = 3000;

    private startBtn;
    private loadingCircle;

    private query;
    private vDur;
    private qDur;
    private local;
    private numOfPages;

    private initPageElements(): void {
        this.startBtn = document.getElementById('startBtn');
        this.loadingCircle = document.getElementById('loadingCircle');
        this.query = document.getElementById('query');
        this.vDur = document.getElementById('vDuration');
        this.qDur = document.getElementById('qDuration');
        this.local = document.getElementById('localization');
        this.numOfPages = document.getElementById('numOfPages');
    }

    /**
     * Checks whether parameters aren't empty
     * @return {boolean} - true if any field is empty, otherwise false
     */
    private isParamsEmpty(): boolean {
        return this.query.value.length == 0 || this.numOfPages.value.length == 0;
    }

    /**
     * Checks whether number of pages parameter is numeric
     * @return {boolean} - true if numeric, otherwise false
     */
    private isCorrectNumberOfPages(): boolean {
        return isNaN(Number(this.numOfPages.value));
    }

    private changeBtnState(eventType: EventType, success?: boolean): void {
        AppCommon.changeBtnState(eventType, this.startBtn, this.loadingCircle, success);
    }

    /**
     * Sends request with parameters to the server to execute search of query and add results to the DB
     * */
    public getGoogleSearchResults(): void {
        this.initPageElements();

        let isEmpty: boolean = this.isParamsEmpty();
        let isCorrectData: boolean = this.isCorrectNumberOfPages();

        if (!isEmpty && isCorrectData) {
            let xhr = AppCommon.createRequest();
            let data = JSON.stringify({
                "action": "getGoogleSearchResults",
                "query": this.query.value,
                "vDuration": this.vDur.options[this.vDur.selectedIndex].value,
                "qDuration": this.qDur.options[this.qDur.selectedIndex].value,
                "localization": this.local.options[this.local.selectedIndex].value,
                "numOfPages": this.numOfPages.value
            });

            xhr.onreadystatechange = () => {
                if (xhr.readyState == 4) {
                    let response = JSON.parse(xhr.responseText);
                    let isSuccessful: boolean = response.result.toString() == 1;
                    this.changeBtnState(EventType.RESPONSE, isSuccessful);
                    if (isSuccessful) {
                        setTimeout(() => {
                            this.clearToDefaultState();
                        }, this.timeoutBeforeReset);
                    }
                }
            };

            xhr.send(data);

            // Searching in progress indication
            this.changeBtnState(EventType.START);
        } else {
            let warningMessage: string;
            if (isEmpty && !isCorrectData) {
                warningMessage = "Please specify query and correct number of pages";
            } else {
                warningMessage = isEmpty ? "Please fill all fields" : "Please specify correct number of pages";
            }
            window.alert(warningMessage);
        }
    }

    private clearToDefaultState(): void {
        this.changeBtnState(EventType.END);

        // Reset all fields
        this.query.value = "";
        this.vDur.selectedIndex = 0;
        this.qDur.selectedIndex = 0;
        this.local.selectedIndex = 0;
        this.numOfPages.value = "";
    }
}