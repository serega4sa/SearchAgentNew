import com.chmihun.searchagent.Server;
import com.chmihun.searchagent.agents.AgentFactory;
import com.chmihun.searchagent.agents.AgentTypes;
import com.chmihun.searchagent.agents.ExportController;
import com.chmihun.searchagent.databases.GoogleDB;
import com.chmihun.searchagent.agents.GoogleObj;
import com.chmihun.searchagent.agents.GoogleSearchAgent;
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
        GoogleDB gDB = new GoogleDB();
        GoogleObj obj = new GoogleObj("Test", "http://gLink.ua/test.html", "http://sLink.ua/test.html", gDB);
        gDB.insertDataToDB(obj);
    }

    @Test
    public void googleSearchTest() {
        GoogleSearchAgent app = ((GoogleSearchAgent) AgentFactory.getAgent(AgentTypes.GOOGLE));
        app.setqDuration("year");
        app.setvDuration("long");
        app.setLocalization("ua");
        app.setNumberOfPages(5);
        app.getListOfRequests().add("Метод Фрейда");
        app.run();
    }

    @Test
    public void googleSearchStatisticsTest() {
        new Thread(Server.getServerInstance()).start();
        ExportController.generateStatisticsForPeriod("Метод Фрейда", "2018-08-30 00:21", "2018-08-30 00:25", "C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\webapps\\search-agent-new");
    }

    @After
    public void close() {

    }
}
