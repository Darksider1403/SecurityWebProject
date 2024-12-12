package DAO;

import DAO.LogDAO;
import Model.Log;
import Model.Log_Level;
import org.jdbi.v3.core.Jdbi;
import java.util.List;



public class LogDAOImp implements LogDAO {
    private static Jdbi JDBI;

    public LogDAOImp() {
        JDBI = ConnectJDBI.connector();
    }

    @Override
    public int add(Log log) {
        try {
            return JDBI.withHandle(handle ->
                    handle.createUpdate("INSERT INTO log(ip, level, address, preValue, value,date,country,status) VALUES (?, ?, ?, ?, ?,?,?,?)")
                            .bind(0, log.getIp())
                            .bind(1, log.getLevel().toString())  // Use the integer value for LogLevel
                            .bind(2, log.getAddress())
                            .bind(3, log.getPreValue() != null ? log.getPreValue() : "")  // Ensure preValue is not null
                            .bind(4, log.getValue())
                            .bind(5, log.getDate())
                            .bind(6, log.getCountry())
                            .bind(7, log.getStatus())
                            .execute());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public List<Log> findAll() {
        return JDBI.withHandle(handle ->
                handle.createQuery("SELECT * FROM log")
                        .mapToBean(Log.class).list());
    }

    public static void main(String[] args) {
        LogDAO logDAO = new LogDAOImp();
        Log log = new Log();


        logDAO.findAll().forEach(System.out::println);
    }
}

