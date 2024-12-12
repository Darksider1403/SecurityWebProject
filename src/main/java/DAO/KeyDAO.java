package DAO;

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

    public static boolean deactivateKey(int userId, String publicKey) {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle ->
                handle.createUpdate("UPDATE user_keys SET is_active = false WHERE user_id = ? AND public_key = ?")
                        .bind(0, userId)
                        .bind(1, publicKey)
                        .execute()
        ) > 0;
    }

    public static String getActiveKey(int userId) {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle ->
                handle.createQuery("SELECT public_key FROM user_keys WHERE user_id = ? AND is_active = true")
                        .bind(0, userId)
                        .mapTo(String.class)
                        .findFirst()
                        .orElse(null)
        );
    }

    public static List<Map<String, Object>> getKeysByUserId(int userId) {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle ->
                handle.createQuery("SELECT public_key, private_key, key_change_time FROM user_keys WHERE user_id = ?")
                        .bind(0, userId)
                        .mapToMap()
                        .list()
        );
    }

    public static void insertUserKeys(int userId, String publicKey, String privateKey) {
        JDBI = ConnectJDBI.connector();
        JDBI.useHandle(handle ->
                handle.createUpdate("INSERT INTO user_keys (user_id, public_key, private_key) VALUES (?, ?, ?)")
                        .bind(0, userId)
                        .bind(1, publicKey)
                        .bind(2, privateKey)
                        .execute()
        );
    }

    public static void updateUserKeys(int userId, String publicKey, String privateKey) {
        JDBI = ConnectJDBI.connector();
        JDBI.useHandle(handle ->
                handle.createUpdate("UPDATE user_keys SET public_key = ?, private_key = ?, key_change_time = CURRENT_TIMESTAMP WHERE user_id = ?")
                        .bind(0, publicKey)
                        .bind(1, privateKey)
                        .bind(2, userId)
                        .execute()
        );
    }

    public static boolean isKeyExpired(int userId, int maxDaysValid) {
        JDBI = ConnectJDBI.connector();
        return JDBI.withHandle(handle -> {
            List<Map<String, Object>> results = handle.createQuery(
                            "SELECT key_change_time FROM user_keys WHERE user_id = ?")
                    .bind(0, userId)
                    .mapToMap()
                    .list();

            if (!results.isEmpty()) {
                Timestamp keyChangeTime = (Timestamp) results.get(0).get("key_change_time");
                Timestamp now = new Timestamp(System.currentTimeMillis());

                long diffInMillies = Math.abs(now.getTime() - keyChangeTime.getTime());
                long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);

                return diffInDays > maxDaysValid;
            }
            return true; // No key found, consider as expired
        });
    }
}
