package Spring.Controllers;

import Scraping.*;
import Spring.Configs.DataBaseConfig;
import Spring.ProgramDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.io.IOException;
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

        WaterlooScraper waterlooScraper = new WaterlooScraper();
        Thread waterlooThread = new Thread(waterlooScraper);
        waterlooThread.start();

        UofTScraper uofTScraper = new UofTScraper();
        Thread uOfTThread = new Thread(uofTScraper);
        uOfTThread.start();

        McMasterScraper mcMasterScraper = new McMasterScraper();
        Thread mcMasterThread = new Thread(mcMasterScraper);
        mcMasterThread.start();

        UBCScraper uBCScraper = new UBCScraper();
        Thread ubcScraperThread = new Thread(uBCScraper);
        ubcScraperThread.start();

        while(waterlooThread.isAlive() || uOfTThread.isAlive() || mcMasterThread.isAlive() || ubcScraperThread.isAlive()){

        }

        //Adds everything (WILL BE MOVED SERVERSIDE LATER)
        //programDAO.addPrograms(waterlooScraper.getPrograms()); //This will not work until I fix something with SQL
        //programDAO.addPrograms(McMasterScraper.getPrograms());
        //programDAO.addPrograms(UofTScraper.getPrograms());
        //programDAO.addPrograms(uBCScraper.getPrograms());

        List<Program> allPrograms = programDAO.getAllPrograms();



        return allPrograms;
    }
}

