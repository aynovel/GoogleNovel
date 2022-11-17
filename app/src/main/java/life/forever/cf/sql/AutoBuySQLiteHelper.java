package life.forever.cf.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import life.forever.cf.entry.AutoBuy;

import java.util.ArrayList;
import java.util.List;


public class AutoBuySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "AutoBuySQLiteHelper";

    private static final String TAB_AUTO = "auto";

    private static final String KEY_WID = "wid";
    private static final String KEY_COVER = "cover";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CHECK = "open";
    private static final String KEY_TIME = "time";


    private static AutoBuySQLiteHelper instance;
    private static SQLiteDatabase database;

    public static AutoBuySQLiteHelper get(Context context) {
        if (instance == null) {
            instance = new AutoBuySQLiteHelper(context);
        }
        database = instance.getWritableDatabase();
        return instance;
    }

    private AutoBuySQLiteHelper(Context context) {
        super(context, "AUTO_BUY", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TAB_AUTO + " (" + KEY_WID + " integer PRIMARY KEY NOT NULL," + KEY_COVER + " varchar NOT NULL," + KEY_TITLE + " varchar NOT NULL," + KEY_CHECK + " integer NOT NULL," + KEY_TIME + " integer NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(AutoBuy autoBuy) {
        String sql = "insert or replace into " + TAB_AUTO + "("
                + KEY_WID + ","
                + KEY_COVER + ","
                + KEY_TITLE + ","
                + KEY_CHECK + ","
                + KEY_TIME + ") "
                + "values(?,?,?,?,?)";
        database.execSQL(sql, new Object[]{autoBuy.wid, autoBuy.cover, autoBuy.title, autoBuy.check, autoBuy.timestamp});
    }



    public void insert(List<AutoBuy> autoBuys) {
        try {
            String sql = "insert or replace into " + TAB_AUTO + "("
                    + KEY_WID + ","
                    + KEY_COVER + ","
                    + KEY_TITLE + ","
                    + KEY_CHECK + ","
                    + KEY_TIME + ") "
                    + "values(?,?,?,?,?)";
            database.beginTransaction();
            for (AutoBuy autoBuy : autoBuys) {
                database.execSQL(sql, new Object[]{autoBuy.wid, autoBuy.cover, autoBuy.title, autoBuy.check, autoBuy.timestamp});
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != database) {
                    database.endTransaction();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<AutoBuy> query() {
        List<AutoBuy> keys = new ArrayList<>();
        String sql = "select * from " + TAB_AUTO + " order by " + KEY_TIME + " desc";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor != null && cursor.moveToNext()) {
            AutoBuy key = new AutoBuy();
            key.wid = cursor.getInt(cursor.getColumnIndex(KEY_WID));
            key.cover = cursor.getString(cursor.getColumnIndex(KEY_COVER));
            key.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            key.check = cursor.getInt(cursor.getColumnIndex(KEY_CHECK));
            key.timestamp = cursor.getInt(cursor.getColumnIndex(KEY_TIME));
            keys.add(key);
        }
        if (cursor != null) {
            cursor.close();
        }
        return keys;
    }

    public AutoBuy query(int wid) {
        AutoBuy key = new AutoBuy();
        key.wid = wid;
        String sql = "select * from " + TAB_AUTO + " where " + KEY_WID + " = " + wid;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor != null && cursor.moveToNext()) {
            key.cover = cursor.getString(cursor.getColumnIndex(KEY_COVER));
            key.title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
            key.check = cursor.getInt(cursor.getColumnIndex(KEY_CHECK));
            key.timestamp = cursor.getInt(cursor.getColumnIndex(KEY_TIME));
            cursor.close();
        }
        return key;
    }

    public boolean exist(int wid) {
        boolean exist = false;
        String sql = "select * from " + TAB_AUTO + " where " + KEY_WID + " = " + wid;
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor != null && cursor.moveToNext()) {
            exist = true;
            cursor.close();
        }
        return exist;
    }

}
