package DAO;

import Model.Voucher;
import org.jdbi.v3.core.Jdbi;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class VoucherDAO {
    private static Jdbi JDBI;

    public static int createVoucher( String name, Date dateStart, Date dateEnd, double discount) {
        Voucher voucher = new Voucher();
        if (voucher.checkDateValid(dateStart, dateEnd)) {
            JDBI = ConnectJDBI.connector();
            int execute = JDBI.withHandle(handle ->
                    handle.createUpdate("INSERT INTO voucher(name, dateStart, dateEnd, discount) " +
                                    "VALUES (?, ?, ?, ?)")
                            .bind(0, name)
                            .bind(1, dateStart)
                            .bind(2, dateEnd)
                            .bind(3, discount)
                            .execute());
            return execute;
        } else {
            // Handle the case when the date validation fails
            System.out.println("fall");
            return -1; // or any other error code indicating date validation failure
        }
    }
    public static List<Voucher> getVouchers() {
        JDBI = ConnectJDBI.connector();
        String sql = "SELECT id, name, dateStart, dateEnd, discount FROM voucher";
        List<Voucher> vouchers = JDBI.withHandle(handle ->
                handle.createQuery(sql)
                        .mapToBean(Voucher.class)  // Registering a mapper for the Voucher class
                        .list()
        );
        return vouchers;
    }

    public  static Voucher getVoucherByID(int id) {
        JDBI = ConnectJDBI.connector();
        String sql = "SELECT id, name, dateStart, dateEnd, discount FROM voucher where id=?";
        Optional<Voucher> voucher = JDBI.withHandle(handle ->
                handle.createQuery(sql).bind(0,id).mapToBean(Voucher.class).stream().findFirst()
        );
        return voucher.orElse(null);
    }


    public static void main(String[] args) {
        VoucherDAO vd = new VoucherDAO();
        vd.createVoucher("Quoc", Date.valueOf("2024-07-24"), Date.valueOf("2024-08-25"), 0.8);
        vd.createVoucher("Duong", Date.valueOf("2024-07-24"), Date.valueOf("2024-08-25"), 0.7);
        vd.createVoucher("Sale", Date.valueOf("2024-09-24"), Date.valueOf("2026-08-25"), 0.2);
        vd.createVoucher("Black market", Date.valueOf("2024-10-24"), Date.valueOf("2025-12-25"), 0.5);
        System.out.println(vd.getVouchers());
//        System.out.println(vd.getVoucherByID(1));
    }
}
