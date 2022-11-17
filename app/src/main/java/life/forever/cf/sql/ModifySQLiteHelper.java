package life.forever.cf.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ModifySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "ModifySQLiteHelper";

    private static final String TAB_MODIFY = "modify";

    private static final String WID = "wid";
    private static final String UPDATE_TIME = "update_time";

    private static ModifySQLiteHelper instance;
    private static SQLiteDatabase database;

    public static ModifySQLiteHelper get(Context context) {
        if (instance == null) {
            instance = new ModifySQLiteHelper(context);
        }
        database = instance.getWritableDatabase();
        return instance;
    }

    private ModifySQLiteHelper(Context context) {
        super(context, "MODIFY", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TAB_MODIFY + " (" + WID + " integer PRIMARY KEY NOT NULL," + UPDATE_TIME + " integer NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(int wid, int time) {
        String insert = "insert or replace into " + TAB_MODIFY + "("
                + WID + ","
                + UPDATE_TIME + ") "
                + "values(?,?)";
        try {
            database.execSQL(insert, new Object[]{wid, time});
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int getLastTime(int wid) {
        String query = "select * from " + TAB_MODIFY + " where " + WID + " = " + wid;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToNext()) {
            int lastTime = cursor.getInt(cursor.getColumnIndex(UPDATE_TIME));
            cursor.close();
            return lastTime;
        }
        return 0;
    }

}
