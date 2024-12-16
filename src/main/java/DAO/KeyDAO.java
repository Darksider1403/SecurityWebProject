package DAO;

import Model.KeyReport;
import org.jdbi.v3.core.Jdbi;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class KeyDAO {
    private static Jdbi JDBI;

    public static int saveKey(int userId, String publicKey, boolean isActive) {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle -> {
            if (isActive) {
                handle.createUpdate("UPDATE user_keys SET is_active = false WHERE user_id = ?")
                        .bind(0, userId)
                        .execute();
            }

            return handle.createUpdate("INSERT INTO user_keys(user_id, public_key, is_active, created_date, key_change_time) VALUES (?, ?, ?, ?, ?)")
                    .bind(0, userId)
                    .bind(1, publicKey)
                    .bind(2, isActive)
                    .bind(3, LocalDateTime.now())
                    .bind(4, LocalDateTime.now())
                    .execute();
        });
    }

    public static List<Map<String, Object>> getKeysByUserId(int userId) {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle ->
        
                handle.createQuery("""
                SELECT public_key, 
                       created_date,
                       is_active, 
                       key_change_time 
                FROM user_keys 
                WHERE user_id = ? 
                ORDER BY created_date DESC""")
                        .bind(0, userId)
                        .mapToMap()
                        .list()
        );
    }

    public static boolean hasActiveKey(int userId) {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle ->
                handle.createQuery("SELECT COUNT(*) > 0 FROM user_keys WHERE user_id = ? AND is_active = true")
                        .bind(0, userId)
                        .mapTo(Boolean.class)
                        .one()
        );
    }

    public static int createKeyReport(int userId) {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle ->
                handle.createUpdate("INSERT INTO key_reports(user_id, status, created_date) VALUES (?, 'PENDING', ?)")
                        .bind(0, userId)
                        .bind(1, LocalDateTime.now())
                        .execute()
        );
    }

    public List<KeyReport> getAllKeyReports() {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle ->
                handle.createQuery("""
            SELECT kr.id, kr.user_id, kr.status, 
                   kr.created_date as report_date, kr.resolved_date,
                   a.username
            FROM key_reports kr 
            JOIN accounts a ON kr.user_id = a.id 
            ORDER BY kr.created_date DESC
            """)
                        .mapToBean(KeyReport.class)
                        .list()
        );
    }

    public static void approveKeyReport(int reportId, int userId) {
        JDBI.useTransaction(handle -> {
            // Deactivate current key
            handle.createUpdate("UPDATE user_keys SET is_active = false WHERE user_id = ?")
                    .bind(0, userId)
                    .execute();

            // Update report status
            handle.createUpdate("UPDATE key_reports SET status = 'Approved', resolved_date = ? WHERE id = ?")
                    .bind(0, LocalDateTime.now())
                    .bind(1, reportId)
                    .execute();
        });
    }

    public static void main(String[] args) {
        int userId = 19;
        boolean active = hasActiveKey(userId);

        System.out.println(active);
    }
}
