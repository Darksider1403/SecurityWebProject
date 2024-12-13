package Model;


import java.sql.Timestamp;

public class KeyReport {
    private int id;
    private int userID;
    private String username;
    private String status;
    private Timestamp reportDate;
    private Timestamp resolvedDate;

    public KeyReport() {
    }

    public KeyReport(int id, int userID, String username, String status, Timestamp reportDate, Timestamp resolvedDate) {
        this.id = id;
        this.userID = userID;
        this.username = username;
        this.status = status;
        this.reportDate = reportDate;
        this.resolvedDate = resolvedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getReportDate() {
        return reportDate;
    }

    public void setReportDate(Timestamp reportDate) {
        this.reportDate = reportDate;
    }

    public Timestamp getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(Timestamp resolvedDate) {
        this.resolvedDate = resolvedDate;
    }
}