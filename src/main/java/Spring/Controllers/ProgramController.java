package Spring.Controllers;

import Scraping.*;
import Spring.Configs.DataBaseConfig;
import Spring.ProgramDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Controller
public class ProgramController {

    ProgramDAO programDAO;

    @GetMapping
    public String allPrograms(Model model) throws IOException {
        model.addAttribute("programList", getPrograms());
        return "Programs";
    }

    private List<Program> getPrograms() throws IOException {
        programDAO = new ProgramDAO(DataBaseConfig.dataSource(), DataBaseConfig.jdbcTemplate());
        System.out.println("LAMOAMAOMAMOAOS");

        LinkedList<Thread> scraperThreads = new LinkedList<>();

        WaterlooScraper waterlooScraper = new WaterlooScraper();
        Thread waterlooThread = new Thread(waterlooScraper);
        scraperThreads.add(waterlooThread);
        waterlooThread.start();

        UofTScraper uofTScraper = new UofTScraper();
        Thread uOfTThread = new Thread(uofTScraper);
        scraperThreads.add(uOfTThread);
        uOfTThread.start();

        McMasterScraper mcMasterScraper = new McMasterScraper();
        Thread mcMasterThread = new Thread(mcMasterScraper);
        scraperThreads.add(mcMasterThread);
        mcMasterThread.start();

        UBCScraper uBCScraper = new UBCScraper();
        Thread ubcScraperThread = new Thread(uBCScraper);
        scraperThreads.add(ubcScraperThread);
        ubcScraperThread.start();

        //Shaya, if you want to you can remove this try-catch block if you want to
        //I'm just not sure if you prefer this or adding exception signature to the method
        //It's your choice
        for (Thread thread : scraperThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Adds everything (WILL BE MOVED SERVERSIDE LATER)
        //programDAO.addPrograms(waterlooScraper.getPrograms());
        //programDAO.addPrograms(McMasterScraper.getPrograms());
        //programDAO.addPrograms(UofTScraper.getPrograms());
        //programDAO.addPrograms(uBCScraper.getPrograms());

        List<Program> allPrograms = programDAO.getAllPrograms();



        return allPrograms;
    }
}

