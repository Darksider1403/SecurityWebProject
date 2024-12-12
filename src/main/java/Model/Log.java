package Model;

import java.sql.Date;
import java.sql.Timestamp;

public class Log {
    private int id;

    private Log_Level Level;
    private String address;
    private String ip;
    private String preValue;
    private String value;
    private String country;
    private String status;
    private Date date;

    public Log() {
    }

    public Log(int id, Log_Level level, String address, String ip, String preValue, String value, String country, String status, Date date) {
        this.id = id;
        Level = level;
        this.address = address;
        this.ip = ip;
        this.preValue = preValue;
        this.value = value;
        this.country = country;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Log_Level getLevel() {
        return Level;
    }

    public void setLevel(Log_Level level) {
        Level = level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPreValue() {
        return preValue;
    }

    public void setPreValue(String preValue) {
        this.preValue = preValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", Level=" + Level +
                ", address='" + address + '\'' +
                ", ip='" + ip + '\'' +
                ", preValue='" + preValue + '\'' +
                ", value='" + value + '\'' +
                ", country='" + country + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}

