namespace Main {

    const gsResults: GSResults = new GSResults();
    const statistics: Statistics = new Statistics();

    /**
     * Redirects to the main page
     */
    export function returnToMain(): void {
        window.location.replace((location.href).substr(0, (location.href).lastIndexOf('/')));
    }

    /**
     * Redirects to the statistics page
     */
    export function showStatisticsPage(): void {
        let url = (location.href).substr(0, (location.href).lastIndexOf('/')).concat("/statistics.jsp");
        window.location.replace(url);
    }

    /**
     * Runs logic of generating Google search agent results
     */
    export function getGoogleSearchResults(): void {
        gsResults.getGoogleSearchResults();
    }

    /**
     * Runs logic of generating statistic
     */
    export function getStatistics(): void {
        statistics.getStatistics();
    }

    export enum EventType {
        START,
        RESPONSE,
        END
    }

    export class Common {
        /**
         * Creates request to the server
         * */
        public static createRequest(): XMLHttpRequest {
            let xhr = new XMLHttpRequest();
            let url = (location.href).substr(0, (location.href).lastIndexOf('/')).concat("/action");
            xhr.open("POST", url, true);
            xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");

            return xhr;
        }

        /**
         *  Changes button state
         * @param {boolean} eventType - indicates when change is performed on: Start / Response / End
         * @param startBtn - start button element on current page
         * @param loadingCircle - loading circle around startBtn element on current page
         * @param {boolean} success - indicates which result state is expected
         */
        public static changeBtnState(eventType: EventType, startBtn: HTMLElement, loadingCircle: HTMLElement, success?: boolean) {
            let bgColor: string;
            let text: string;
            let visibility: string;
            let cursor: string;

            switch (eventType) {
                case EventType.START:
                    bgColor = "orange";
                    text = "Wait...";
                    visibility = "visible";
                    cursor = "none";
                    break;
                case EventType.RESPONSE:
                    bgColor = success ? "#4CAF50" : "#F93D3D";
                    text = success ? "Done" : "Failed";
                    visibility = "hidden";
                    cursor = "not-allowed";
                    break;
                case EventType.END:
                    bgColor = "cornflowerblue";
                    text = "Start";
                    visibility = "hidden";
                    cursor = "pointer";
                    break;
            }

            startBtn.style.backgroundColor = bgColor;
            startBtn.innerHTML = text;
            loadingCircle.style.visibility = visibility;
            startBtn.style.cursor = cursor;
        }
    }
}