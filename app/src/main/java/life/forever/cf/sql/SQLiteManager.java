package life.forever.cf.sql;

import android.database.sqlite.SQLiteOpenHelper;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.publics.Constant;

import java.util.HashMap;
import java.util.Map;


public class SQLiteManager implements Constant {

    public static final int SHELF_HELPER = -1;
    public static final int SEARCH_HELPER = -2;
    public static final int AUTO_BUY_HELPER = -3;
    public static final int MODIFY_HELPER = -4;
    public static final int PAY_HELPER = -5;
    public static final int SHELF_FIRST_HELPER = -7;
    private static final Map<Integer, SQLiteOpenHelper> helpers = new HashMap<>();


    public static SQLiteOpenHelper getHelper(int type) {
        SQLiteOpenHelper helper = helpers.get(type);
        if (helper == null) {
            if (type == SHELF_HELPER) {
                helper = ShelfSQLiteHelper.get(PlotRead.getApplication());
            } else if (type == SEARCH_HELPER) {
                helper = SearchSQLiteHelper.get(PlotRead.getApplication());
            } else if (type == AUTO_BUY_HELPER) {
                helper = AutoBuySQLiteHelper.get(PlotRead.getApplication());
            } else if (type == MODIFY_HELPER) {
                helper = ModifySQLiteHelper.get(PlotRead.getApplication());
            } else if (type == PAY_HELPER) {
                helper = PaymentSQLiteHelper.get(PlotRead.getApplication());
            } else if (type == SHELF_FIRST_HELPER) {
                helper = FirstShelfSQLiteHelper.get(PlotRead.getApplication());
            } else {
                throw new RuntimeException("不支持的数据库类型");
            }
            helpers.put(type, helper);
        }
        return helper;
    }

    public static ModifySQLiteHelper getModifyHelper() {
        return (ModifySQLiteHelper) getHelper(MODIFY_HELPER);
    }


    public static void close(int type) {
        SQLiteOpenHelper helper = helpers.get(type);
        if (helper != null) {
            helper.close();
            helpers.remove(type);
        }
    }


}
