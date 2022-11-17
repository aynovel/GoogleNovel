package life.forever.cf.adapter.person.pay;

import life.forever.cf.entry.PayInfo;
import life.forever.cf.sql.PaymentSQLiteHelper;
import life.forever.cf.sql.SQLiteManager;
import life.forever.cf.publics.Constant;

import java.util.List;


public class PaymentUtil implements Constant {



    public static List<PayInfo> queryPayLists() {
        PaymentSQLiteHelper helper = (PaymentSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.PAY_HELPER);
        return helper.query();
    }

    public static void updatePay(PayInfo payInfo) {
        PaymentSQLiteHelper helper = (PaymentSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.PAY_HELPER);
        helper.update(payInfo);
    }



    public static void insertRecord(PayInfo mPayInfo) {
        PaymentSQLiteHelper helper = (PaymentSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.PAY_HELPER);
        helper.insertRecord(mPayInfo);
    }

    public static void deletePayInfo(String pay_id) {
        PaymentSQLiteHelper helper = (PaymentSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.PAY_HELPER);
        helper.delete(pay_id);
    }
}
