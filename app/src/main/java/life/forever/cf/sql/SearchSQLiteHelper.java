package life.forever.cf.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import life.forever.cf.entry.SearchKey;

import java.util.ArrayList;
import java.util.List;

public class SearchSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "SearchSQLiteHelper";

    private static final String TAB_SEARCH = "search";

    private static final String KEY_WORD = "key";
    private static final String TIMESTAMP = "timestamp";


    private static SearchSQLiteHelper instance;
    private static SQLiteDatabase database;

    public static SearchSQLiteHelper get(Context context) {
        if (instance == null) {
            instance = new SearchSQLiteHelper(context);
        }
        database = instance.getWritableDatabase();
        return instance;
    }

    private SearchSQLiteHelper(Context context) {
        super(context, "SEARCH", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TAB_SEARCH + " (" + KEY_WORD + " varchar PRIMARY KEY NOT NULL," + TIMESTAMP + " integer NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(SearchKey searchKey) {
        String sql = "insert or replace into " + TAB_SEARCH + "("
                + KEY_WORD + ","
                + TIMESTAMP + ") "
                + "values(?,?)";
        database.execSQL(sql, new Object[]{searchKey.keyWord, searchKey.timestamp});
    }

    public List<SearchKey> query() {
        List<SearchKey> keys = new ArrayList<>();
        String sql = "select * from " + TAB_SEARCH + " order by " + TIMESTAMP + " desc";
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor != null && cursor.moveToNext()) {
            SearchKey key = new SearchKey();
            key.keyWord = cursor.getString(cursor.getColumnIndex(KEY_WORD));
            key.timestamp = cursor.getInt(cursor.getColumnIndex(TIMESTAMP));
            keys.add(key);
        }
        if (cursor != null) {
            cursor.close();
        }
        return keys;
    }

    public void clear() {
        database.delete(TAB_SEARCH, null, null);
    }

    public void clears(String searchkeyname) {
        String[] name = {String.valueOf(searchkeyname)};
        database.delete(TAB_SEARCH, KEY_WORD + " = ?", name);
    }

}
