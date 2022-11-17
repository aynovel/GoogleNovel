package life.forever.cf.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import life.forever.cf.entry.Work;

import java.util.ArrayList;
import java.util.List;


public class FirstShelfSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAB_FIRST_SHELF = "firstshelf";

    private static final String SHELF_WID = "wid";
    private static final String SHELF_PUSH = "push";
    private static final String SHELF_WTYPE = "wtype";
    private static final String SHELF_COVER = "cover";
    private static final String SHELF_TITLE = "title";
    private static final String SHELF_AUTHOR = "author";
    private static final String SHELF_COUNTS = "counts";
    private static final String SHELF_IS_FINISH = "is_finish";
    private static final String SHELF_UPDATE_TIME = "update_time";
    private static final String SHELF_UPDATE_FLAG = "update_flag";
    private static final String SHELF_DELETE_FLAG = "delete_flag";
    private static final String SHELF_LAST_TIME = "last_time";
    private static final String SHELF_LAST_ORDER = "last_order";
    private static final String SHELF_LAST_ID = "last_id";
    private static final String SHELF_LAST_POSITION = "last_position";
    private static final String SHELF_IS_REC = "is_rec";

    private static FirstShelfSQLiteHelper instance;
    private static SQLiteDatabase database;

    public static FirstShelfSQLiteHelper get(Context context) {
        if (instance == null) {
            instance = new FirstShelfSQLiteHelper(context);
        }
        database = instance.getWritableDatabase();
        return instance;
    }

    private FirstShelfSQLiteHelper(Context context) {
        super(context, "FIRST_SHELF", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String firstshelf = "CREATE TABLE IF NOT EXISTS " + TAB_FIRST_SHELF + " (" + SHELF_WID + " integer PRIMARY KEY NOT NULL," + SHELF_PUSH + " varchar NOT NULL," + SHELF_WTYPE + " integer NOT NULL," + SHELF_COVER + " varchar NOT NULL," + SHELF_TITLE + " varchar NOT NULL," + SHELF_AUTHOR + " varchar NOT NULL," + SHELF_COUNTS + " integer NOT NULL," + SHELF_IS_FINISH + " integer NOT NULL," + SHELF_UPDATE_TIME + " integer NOT NULL," + SHELF_UPDATE_FLAG + " integer NOT NULL," + SHELF_DELETE_FLAG + " integer NOT NULL," + SHELF_LAST_TIME + " integer NOT NULL," + SHELF_LAST_ORDER + " integer NOT NULL," + SHELF_LAST_ID + " integer NOT NULL," + SHELF_LAST_POSITION + " integer NOT NULL," + SHELF_IS_REC + " integer NOT NULL)";
        db.execSQL(firstshelf);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){
            db.beginTransaction();
            try {
                String firstshelf = "Alter table " + TAB_FIRST_SHELF + " add " + SHELF_PUSH + " varchar";
                db.execSQL(firstshelf);
                db.setTransactionSuccessful();
            } catch (Throwable ignored) {
            } finally {
                db.endTransaction();
            }
        }


    }

    /**
     * 首推批量插入/更新作品
     *
     * @param Works
     */
    public void firstinsert(List<Work> Works) {
        try {
            String insert = "insert or replace into " + TAB_FIRST_SHELF + "("
                    + SHELF_WID + ","
                    + SHELF_PUSH + ","
                    + SHELF_WTYPE + ","
                    + SHELF_COVER + ","
                    + SHELF_TITLE + ","
                    + SHELF_AUTHOR + ","
                    + SHELF_COUNTS + ","
                    + SHELF_IS_FINISH + ","
                    + SHELF_UPDATE_TIME + ","
                    + SHELF_UPDATE_FLAG + ","
                    + SHELF_DELETE_FLAG + ","
                    + SHELF_LAST_TIME + ","
                    + SHELF_LAST_ORDER + ","
                    + SHELF_LAST_ID + ","
                    + SHELF_LAST_POSITION + ","
                    + SHELF_IS_REC + ") "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            database.beginTransaction();
            for (Work mWork : Works) {
                if (TextUtils.isEmpty(mWork.push)) {
                    mWork.push = "0";
                }
                database.execSQL(insert, new Object[]{mWork.wid, mWork.push, mWork.wtype, mWork.cover,
                        mWork.title, mWork.author, mWork.totalChapter, mWork.isfinish,
                        mWork.updatetime, mWork.updateflag, mWork.deleteflag,
                        mWork.lasttime, mWork.lastChapterOrder, mWork.lastChapterId, mWork.lastChapterPosition, mWork.is_rec});
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

    /**
     * 获取书架首推作品
     */
    public List<Work> firstqueryShelf() {
        String query = "select * from " + TAB_FIRST_SHELF + " where " + SHELF_DELETE_FLAG + " <> 1 order by " + SHELF_LAST_TIME + " desc";
        List<Work> Works = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);
        while (cursor != null && cursor.moveToNext()) {
            Work mWork = new Work();
            mWork.wid = cursor.getInt(cursor.getColumnIndex(SHELF_WID));
            mWork.push = cursor.getString(cursor.getColumnIndex(SHELF_PUSH));
            mWork.wtype = cursor.getInt(cursor.getColumnIndex(SHELF_WTYPE));
            mWork.cover = cursor.getString(cursor.getColumnIndex(SHELF_COVER));
            mWork.title = cursor.getString(cursor.getColumnIndex(SHELF_TITLE));
            mWork.author = cursor.getString(cursor.getColumnIndex(SHELF_AUTHOR));
            mWork.totalChapter = cursor.getInt(cursor.getColumnIndex(SHELF_COUNTS));
            mWork.isfinish = cursor.getInt(cursor.getColumnIndex(SHELF_IS_FINISH));
            mWork.updatetime = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_TIME));
            mWork.updateflag = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_FLAG));
            mWork.deleteflag = cursor.getInt(cursor.getColumnIndex(SHELF_DELETE_FLAG));
            mWork.lasttime = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_TIME));
            mWork.lastChapterOrder = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ORDER));
            mWork.lastChapterId = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ID));
            mWork.lastChapterPosition = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_POSITION));
            mWork.is_rec = cursor.getInt(cursor.getColumnIndex(SHELF_IS_REC));
            Works.add(mWork);
        }
        if (cursor != null) {
            cursor.close();
        }
        return Works;
    }

    /**
     * 清空书架
     */
    public void clearShelf() {
        String clear = "delete from " + TAB_FIRST_SHELF;
        database.execSQL(clear);
    }

    /**
     * 清理书架，删除deleteflag为1的行
     */
    public void firstDeleteShelf(int wid) {
        String delete = "delete from " + TAB_FIRST_SHELF + " where " + SHELF_WID + " = " + wid;
        database.execSQL(delete);
    }
}
