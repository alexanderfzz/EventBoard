package Spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import Scraping.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


public class ProgramDAO extends JdbcDaoSupport {

    @Autowired
    DataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;



    public ProgramDAO(DataSource _dataSource, JdbcTemplate _jbdcTemplate){
        dataSource = _dataSource;
        jdbcTemplate = _jbdcTemplate;

    }

    //https://www.w3schools.com/sql/sql_insert.asp
    //


    long checkUniversity(Program program){
        String checkForUniSQL = "SELECT * FROM Universities WHERE '" + program.getUniversity() + "' in (UniversityName)";
        long universityID = -1;
        List<Map<String, Object>> uniRows = jdbcTemplate.queryForList(checkForUniSQL);
        if (uniRows.isEmpty()) {
            universityID = addUniversity(program.getUniversity());
            System.out.println("Created New Uni...");
        } else {
            //Since there should one one of every kind, we know there will only be one row
            universityID = (long) uniRows.get(0).get("Id");
            System.out.println("Uni found, ID collected...");
        }
        return universityID;
    }

    long addUniversity(String uniName){
        String addUniSQL = "INSERT INTO Universities (UniversityName)\nVALUES ('" + uniName + "')";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement(addUniSQL, new String[] {"Id"});
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    void removePrograms(List<Map<String, Object>> curProgramRows, List<Program> newPrograms){
        Set<String> receivedPrograms = newPrograms.stream()
                .map(e->e.getTitle())
                .collect(Collectors.toSet());

        Set<String> removedPrograms = curProgramRows.stream().map(e -> e.get("Title").toString())
                .filter(t -> !receivedPrograms.contains(t))
                .collect(Collectors.toSet());

        for(String p : removedPrograms){
            String sql = "DELETE FROM PROGRAMS WHERE Title='" + p + "'";
            jdbcTemplate.update(sql);
        }
    }


    public void addPrograms(List<Program> programList){
        
        for(Program program : programList) {

            long universityID = checkUniversity(program);

            String checkForProgramSQL = "SELECT * FROM Programs WHERE '" + program.getTitle() + "' in (Title)";
            List<Map<String, Object>> programRows = jdbcTemplate.queryForList(checkForProgramSQL);
            if (programRows.isEmpty()) {
                String insertProgramSQL = "INSERT INTO Programs (UniversityId, Topic, Title, Link, Audience, Description, Dates) VALUES ('"
                        + universityID +
                        "', '" +
                        program.getTopic() +
                        "', '" +
                        program.getTitle() +
                        "', '" +
                        program.getLink() +
                        "', '" +
                        program.getAudiences() +
                        "', '" +
                        program.getOverview() +
                        "', '" +
                        program.getDates() +
                        "')";
                jdbcTemplate.update(insertProgramSQL);
                System.out.println("Program not found... Added to Database...");
            }
            else { //ERROR, PLZ FIX SOON :) (Specifically, when updating you update everything and not the specific program that requires the update :( )
                String updateProgramSQL = "UPDATE Programs SET Topic = '" +
                        program.getTopic() +
                        "', Link = '" +
                        program.getLink() +
                        "', Audience = '" +
                        program.getAudiences() +
                        "', Description = '" +
                        program.getOverview() +
                        "', Dates = '" +
                        program.getDates() +
                        "'";
                jdbcTemplate.update(updateProgramSQL);
                System.out.println("Program found... Updated Database with new data...");
            }

            //removePrograms(programRows, programList);

        }
    }

    public List<Program> getAllPrograms(){
        String sql = "SELECT * FROM Programs";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        String uniSQL = "SELECT * FROM Universities";
        List<Map<String, Object>> uniRows = jdbcTemplate.queryForList(uniSQL);

        List<Program> result = new ArrayList<>();
        for(Map<String, Object> row:rows){
            Program program = new Program(
                    (String)uniRows.get(Integer.parseInt(String.valueOf((long)row.get("UniversityID")-1))).get("UniversityName"),
                    (String)row.get("Topic"),
                    (String)row.get("Title"),
                    (String)row.get("Link"),
                    (String)row.get("Audience"),
                    (String)row.get("Dates"),
                    (String)row.get("Description"));
            result.add(program);
        }
        return result;

    }



}
