class Statistics {

    private timeoutBeforeReset = 10000;

    private startBtn;
    private loadingCircle;
    private statistics;
    private statisticsStatus;
    private statisticsPath;

    private agentType;
    private query;
    private startDate;
    private endDate;

    private initPageElements(): void {
        this.startBtn = document.getElementById('startBtn');
        this.loadingCircle = document.getElementById('loadingCircle');
        this.statistics = document.getElementById('statistics');
        this.statisticsStatus = document.getElementById('statisticsStatus');
        this.statisticsPath = document.getElementById('statisticsPath');
        this.agentType = document.getElementById('agentType');
        this.query = document.getElementById('query');
        this.startDate = document.getElementById('startDate');
        this.endDate = document.getElementById('endDate');
    }

    /**
     * Checks whether parameters aren't empty
     * @return {boolean} - true if any field is empty, otherwise false
     */
    private isParamsEmpty(): boolean {
        return this.query.value.length == 0 || this.startDate.value.length == 0 || this.endDate.value.length == 0;
    }

    /**
     * Checks whether dates are correctly specified (end date is after start date)
     * @return {boolean} - true if correct, otherwise false
     */
    private isCorrectDates(): boolean {
        let sDate: Date = new Date(this.startDate.value);
        let eDate: Date = new Date(this.endDate.value);
        return eDate > sDate;
    }

    private changeBtnState(eventType: Main.EventType, success?: boolean): void {
        Main.Common.changeBtnState(eventType, this.startBtn, this.loadingCircle, success);
    }

    /**
     * Sends request with parameters to the server to execute search of query and add results to the DB
     * */
    public getStatistics(): void {
        this.initPageElements();
        let agent = this.agentType.options[this.agentType.selectedIndex].value;

        let isEmpty: boolean = this.isParamsEmpty();
        let isCorrect: boolean = this.isCorrectDates();

        if (!isEmpty && isCorrect) {
            let xhr = Main.Common.createRequest();
            let data = JSON.stringify({
                "action": "getStatistics",
                "agentType": agent,
                "query": this.query.value,
                "startDate": this.startDate.value,
                "endDate": this.endDate.value
            });

            xhr.onreadystatechange = () => {
                if (xhr.readyState == 4) {
                    let response = JSON.parse(xhr.responseText);
                    let isSuccessful: boolean = response.result.toString() == 1;
                    this.changeBtnState(Main.EventType.RESPONSE, isSuccessful);
                    this.displayResults(isSuccessful);
                }
            };

            xhr.send(data);

            // Getting statistics in progress indication
            this.changeBtnState(Main.EventType.START);
        } else {
            let warningMessage: string;
            if (isEmpty && !isCorrect) {
                warningMessage = "Please specify query and correct dates";
            } else {
                warningMessage = isEmpty ? "Please fill all fields" : "Please specify correct dates interval"
            }
            window.alert(warningMessage);
        }
    }

    private displayResults(isSuccessful: boolean): void {
        if (isSuccessful) {
            this.statisticsStatus.textContent = "Results has been successfully saved to excel file. You can find them by the following path:";
            this.statisticsPath.style.visibility = "visible";
        } else {
            this.statistics.style.color = "#F93D3D";
            this.statisticsStatus.textContent = "Requested content wasn't found for the specified time interval.";
            this.statisticsPath.style.visibility = "hidden";
        }
        this.statisticsStatus.style.visibility = "visible";
        setTimeout(() => {
            this.clearToDefaultState();
        }, this.timeoutBeforeReset);
    }

    private clearToDefaultState(): void {
        this.changeBtnState(Main.EventType.END);

        // Reset all fields
        this.query.value = "";
        this.startDate.value = "";
        this.endDate.value = "";
        this.statisticsStatus.style.visibility = "hidden";
        this.statisticsPath.style.visibility = "hidden";
    }
}