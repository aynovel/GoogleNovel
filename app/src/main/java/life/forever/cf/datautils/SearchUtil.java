package life.forever.cf.datautils;

import android.os.Message;

import life.forever.cf.entry.SearchKey;
import life.forever.cf.sql.SQLiteManager;
import life.forever.cf.sql.SearchSQLiteHelper;
import life.forever.cf.publics.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class SearchUtil {

    public static void insert(SearchKey searchKey) {
        SearchSQLiteHelper helper = (SearchSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SEARCH_HELPER);
        helper.insert(searchKey);
        // 发送通知
        Message message = Message.obtain();
        message.what = Constant.BUS_SEARCH_CHANGE;
        EventBus.getDefault().post(message);
    }

    public static void delete() {
        SearchSQLiteHelper helper = (SearchSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SEARCH_HELPER);
        helper.clear();
        // 发送通知
        Message message = Message.obtain();
        message.what = Constant.BUS_SEARCH_CHANGE;
        EventBus.getDefault().post(message);
    }

    public static void deletes(String searchkeyname) {
        SearchSQLiteHelper helper = (SearchSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SEARCH_HELPER);
        helper.clears(searchkeyname);
        // 发送通知
        Message message = Message.obtain();
        message.what = Constant.BUS_SEARCH_CHANGE;
        EventBus.getDefault().post(message);
    }

    public static List<SearchKey> query() {
        SearchSQLiteHelper helper = (SearchSQLiteHelper) SQLiteManager.getHelper(SQLiteManager.SEARCH_HELPER);
        return helper.query();
    }
}
