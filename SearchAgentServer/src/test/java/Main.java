import com.chmihun.searchagent.databases.Google;
import com.chmihun.searchagent.agents.GoogleObj;
import com.chmihun.searchagent.agents.GoogleSearch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Sergey.Chmihun on 06/27/2017.
 */
public class Main {
    @Before
    public void init() {

    }

    @Test
    public void dbTest() {
        Google gDB = new Google();
        GoogleObj obj = new GoogleObj("Test", "http://gLink.ua/test.html", "http://sLink.ua/test.html", gDB);
        gDB.insertDataToDB(obj);
    }

    @Test
    public void googleSearchTest() {
        GoogleSearch app = new GoogleSearch();
        app.setqDuration("year");
        app.setvDuration("long");
        app.setLocalization("ua");
        app.setNumberOfPages(5);
        app.getListOfRequests().add("Метод Фрейда");
        app.run();
    }

    @After
    public void close() {

    }
}
