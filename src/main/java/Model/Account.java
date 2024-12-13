
package Model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Account {
    private static final Log log = LogFactory.getLog(Account.class);
    private int ID;
    private String username;
    private String password;
    private String email;
    private String name;
    private String fullname;
    private String numberPhone;
    private int status;
    private int role;
    private boolean isVerified;


    public Account(int ID, String username, String password, String email, String fullname, String numberPhone, int status, int role) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullname = fullname;
        this.numberPhone = numberPhone;
        this.status = status;
        this.role = role;
    }

    public Account(int ID, String email, String name, boolean isVerified) {
        this.ID = ID;
        this.email = email;
        this.name = name;
        this.isVerified = isVerified;
    }

    public Account() {
    }

    public Account(String username, String password, int status) {
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Account{" +
                "ID=" + ID +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", fullname='" + fullname + '\'' +
                ", numberPhone='" + numberPhone + '\'' +
                ", status=" + status +
                ", role=" + role +
                ", isVerified=" + isVerified +
                '}';
    }
}
