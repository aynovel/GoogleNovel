package life.forever.cf.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import life.forever.cf.entry.Work;
import life.forever.cf.publics.Constant;

import java.util.ArrayList;
import java.util.List;


public class ShelfSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "ShelfSQLiteHelper";

    private static final String TAB_SHELF = "shelf";


    private static final String TAB_READ_RECORD = "record";

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

    private static final String RECORD_WID = "wid";
    private static final String RECORD_PUSH = "push";
    private static final String RECORD_WTYPE = "wtype";
    private static final String RECORD_COVER = "cover";
    private static final String RECORD_TITLE = "title";
    private static final String RECORD_AUTHOR = "author";
    private static final String RECORD_COUNTS = "counts";
    private static final String RECORD_IS_FINISH = "is_finish";
    private static final String RECORD_UPDATE_TIME = "update_time";
    private static final String RECORD_LAST_TIME = "last_time";
    private static final String RECORD_LAST_ORDER = "last_order";
    private static final String RECORD_LAST_ID = "last_id";
    private static final String RECORD_LAST_POSITION = "last_position";

    private static ShelfSQLiteHelper instance;
    private static SQLiteDatabase database;

    public static ShelfSQLiteHelper get(Context context) {
        if (instance == null) {
            instance = new ShelfSQLiteHelper(context);
        }
        database = instance.getWritableDatabase();
        return instance;
    }

    private ShelfSQLiteHelper(Context context) {
        super(context, "SHELF", null, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String shelf = "CREATE TABLE IF NOT EXISTS " + TAB_SHELF + " (" + SHELF_WID + " integer PRIMARY KEY NOT NULL," + SHELF_PUSH + " varchar NOT NULL," + SHELF_WTYPE + " integer NOT NULL," + SHELF_COVER + " varchar NOT NULL," + SHELF_TITLE + " varchar NOT NULL," + SHELF_AUTHOR + " varchar NOT NULL," + SHELF_COUNTS + " integer NOT NULL," + SHELF_IS_FINISH + " integer NOT NULL," + SHELF_UPDATE_TIME + " integer NOT NULL," + SHELF_UPDATE_FLAG + " integer NOT NULL," + SHELF_DELETE_FLAG + " integer NOT NULL," + SHELF_LAST_TIME + " integer NOT NULL," + SHELF_LAST_ORDER + " integer NOT NULL," + SHELF_LAST_ID + " integer NOT NULL," + SHELF_LAST_POSITION + " integer NOT NULL)";
        db.execSQL(shelf);
        String record = "CREATE TABLE IF NOT EXISTS " + TAB_READ_RECORD + " (" + RECORD_WID + " integer PRIMARY KEY NOT NULL," + RECORD_PUSH + " varchar NOT NULL," + RECORD_WTYPE + " integer NOT NULL," + RECORD_COVER + " varchar NOT NULL," + RECORD_TITLE + " varchar NOT NULL," + RECORD_AUTHOR + " varchar NOT NULL," + RECORD_COUNTS + " integer NOT NULL," + RECORD_IS_FINISH + " integer NOT NULL," + RECORD_UPDATE_TIME + " integer NOT NULL," + RECORD_LAST_TIME + " integer NOT NULL," + RECORD_LAST_ORDER + " integer NOT NULL," + SHELF_LAST_ID + " integer NOT NULL," + RECORD_LAST_POSITION + " integer NOT NULL)";
        db.execSQL(record);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion<2){
            db.beginTransaction();
            try {
                String shelf = "Alter table "+TAB_SHELF+" add " + SHELF_PUSH + " varchar";
                db.execSQL(shelf);
                String record = "Alter table "+TAB_READ_RECORD+" add " + RECORD_PUSH + " varchar";
                db.execSQL(record);
                db.setTransactionSuccessful();
            } catch (Throwable ex) {
//                    Log.e(TAG, ex.getMessage(), ex);
            } finally {
                db.endTransaction();
            }
        }


    }

    /**
     * 清空记录
     */
    public void clearRecord() {
        String clear = "delete from " + TAB_READ_RECORD;
        database.execSQL(clear);
    }

    /**
     * 插入阅读记录
     *
     * @param work
     */
    public void insertRecord(Work work) {
        String insert = "insert or replace into " + TAB_READ_RECORD + "("
                + SHELF_WID + ","
                + SHELF_PUSH + ","
                + SHELF_WTYPE + ","
                + SHELF_COVER + ","
                + SHELF_TITLE + ","
                + SHELF_AUTHOR + ","
                + SHELF_COUNTS + ","
                + SHELF_IS_FINISH + ","
                + SHELF_UPDATE_TIME + ","
                + SHELF_LAST_TIME + ","
                + SHELF_LAST_ORDER + ","
                + SHELF_LAST_ID + ","
                + SHELF_LAST_POSITION + ") "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        if (TextUtils.isEmpty(work.push)){
            work.push = "0";
        }
        if(work.cover == null)
        {
            work.cover = "";
        }

        if(work.push == null)
        {
            work.push = "";
        }
        database.execSQL(insert, new Object[]{work.wid, work.push ,work.wtype, work.cover,
                work.title, work.author, work.totalChapter, work.isfinish,
                work.updatetime, work.lasttime, work.lastChapterOrder, work.lastChapterId, work.lastChapterPosition});
    }

    /**
     * 获取阅读历史的书籍
     */
    public List<Work> queryRecord() {
        String query = "select * from " + TAB_READ_RECORD + " order by " + SHELF_LAST_TIME + " desc";
        List<Work> HRWorks = new ArrayList<>();
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
            mWork.lasttime = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_TIME));
            mWork.lastChapterOrder = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ORDER));
            mWork.lastChapterId = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ID));
            mWork.lastChapterPosition = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_POSITION));
            HRWorks.add(mWork);
        }
        if (cursor != null) {
            cursor.close();
        }
        return HRWorks;
    }


    /**
     * 查询阅读记录是否存在
     *
     * @param wid
     * @return
     */
    public boolean existRecord(int wid) {
        String query = "select * from " + TAB_READ_RECORD + " where " + RECORD_WID + " = " + wid;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * 查询阅读进度
     *
     * @param wid
     * @return
     */
    public Work queryRecord(int wid) {
        String query = "select * from " + TAB_READ_RECORD + " where " + RECORD_WID + " = " + wid;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToNext()) {
            Work work = new Work();
            work.lastChapterId = cursor.getInt(cursor.getColumnIndex(RECORD_LAST_ID));
            work.lasttime = cursor.getInt(cursor.getColumnIndex(RECORD_LAST_TIME));
            work.lastChapterOrder = cursor.getInt(cursor.getColumnIndex(RECORD_LAST_ORDER));
            work.lastChapterPosition = cursor.getInt(cursor.getColumnIndex(RECORD_LAST_POSITION));
            cursor.close();
            return work;
        }
        return null;
    }

    /**
     * 批量插入/更新作品
     *
     * @param works
     */
    public void insert(List<Work> works) {
        try {
            String insert = "insert or replace into " + TAB_SHELF + "("
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
                    + SHELF_LAST_POSITION + ") "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            database.beginTransaction();
            for (Work work : works) {
                if (TextUtils.isEmpty(work.push)){
                    work.push = "0";
                }
                database.execSQL(insert, new Object[]{work.wid, work.push, work.wtype, work.cover,
                        work.title, work.author, work.totalChapter, work.isfinish,
                        work.updatetime, work.updateflag, work.deleteflag,
                        work.lasttime, work.lastChapterOrder, work.lastChapterId, work.lastChapterPosition});
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
     * 插入/更新单部作品
     *
     * @param work
     */
    public void insert(Work work) {
        if (TextUtils.isEmpty(work.push)){
            work.push = "0";
        }
        String insert = "insert or replace into " + TAB_SHELF + "("
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
                + SHELF_LAST_POSITION + ") "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            database.execSQL(insert, new Object[]{work.wid, work.push, work.wtype, work.cover,
                    work.title, work.author, work.totalChapter, work.isfinish,
                    work.updatetime, work.updateflag, work.deleteflag,
                    work.lasttime, work.lastChapterOrder, work.lastChapterId,
                    work.lastChapterPosition});
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 清理书架，删除deleteflag为1的行
     */
    public void cleanShelf() {
        String delete = "delete from " + TAB_SHELF + " where " + SHELF_DELETE_FLAG + " = 1";
        database.execSQL(delete);
    }

    /**
     * 获取书架作品
     */
    public List<Work> queryShelf() {
        String query = "select * from " + TAB_SHELF + " where " + SHELF_DELETE_FLAG + " <> 1 order by " + SHELF_LAST_TIME + " desc";
        List<Work> works = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);
        while (cursor != null && cursor.moveToNext()) {
            Work work = new Work();
            work.wid = cursor.getInt(cursor.getColumnIndex(SHELF_WID));
            work.push = cursor.getString(cursor.getColumnIndex(SHELF_PUSH));
            work.wtype = cursor.getInt(cursor.getColumnIndex(SHELF_WTYPE));
            work.cover = cursor.getString(cursor.getColumnIndex(SHELF_COVER));
            work.title = cursor.getString(cursor.getColumnIndex(SHELF_TITLE));
            work.author = cursor.getString(cursor.getColumnIndex(SHELF_AUTHOR));
            work.totalChapter = cursor.getInt(cursor.getColumnIndex(SHELF_COUNTS));
            work.isfinish = cursor.getInt(cursor.getColumnIndex(SHELF_IS_FINISH));
            work.updatetime = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_TIME));
            work.updateflag = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_FLAG));
            work.deleteflag = cursor.getInt(cursor.getColumnIndex(SHELF_DELETE_FLAG));
            work.lasttime = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_TIME));
            work.lastChapterOrder = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ORDER));
            work.lastChapterId = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ID));
            work.lastChapterPosition = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_POSITION));
            works.add(work);
//            Log.d("BX====00",work.updatetime+"--ddddddddddddd--"+work.lasttime);
        }
        if (cursor != null) {
            cursor.close();
        }
        return works;
    }

    public List<Work> queryAllWithDeleted() {
        String query = "select * from " + TAB_SHELF + " order by " + SHELF_LAST_TIME + " desc";
        List<Work> works = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);
        while (cursor != null && cursor.moveToNext()) {
            Work work = new Work();
            work.wid = cursor.getInt(cursor.getColumnIndex(SHELF_WID));
            work.push = cursor.getString(cursor.getColumnIndex(SHELF_PUSH));
            work.wtype = cursor.getInt(cursor.getColumnIndex(SHELF_WTYPE));
            work.cover = cursor.getString(cursor.getColumnIndex(SHELF_COVER));
            work.title = cursor.getString(cursor.getColumnIndex(SHELF_TITLE));
            work.author = cursor.getString(cursor.getColumnIndex(SHELF_AUTHOR));
            work.totalChapter = cursor.getInt(cursor.getColumnIndex(SHELF_COUNTS));
            work.isfinish = cursor.getInt(cursor.getColumnIndex(SHELF_IS_FINISH));
            work.updatetime = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_TIME));
            work.updateflag = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_FLAG));
            work.deleteflag = cursor.getInt(cursor.getColumnIndex(SHELF_DELETE_FLAG));
            work.lasttime = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_TIME));
            work.lastChapterOrder = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ORDER));
            work.lastChapterId = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ID));
            work.lastChapterPosition = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_POSITION));
            works.add(work);
        }
        if (cursor != null) {
            cursor.close();
        }
        return works;
    }

    public boolean exist(int wid) {
        String query = "select * from " + TAB_SHELF + " where " + SHELF_WID + " = " + wid;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToNext()) {
            int flag = cursor.getInt(cursor.getColumnIndex(SHELF_DELETE_FLAG));
            cursor.close();
            return flag == Constant.ZERO;
        }
        return false;
    }

    /**
     * 清空书架
     */
    public void clearShelf() {
        String clear = "delete from " + TAB_SHELF;
        database.execSQL(clear);
    }


    /**
     * 批量插入/更新作品
     *
     * @param works
     */
    public void insertSingleWork(List<Work> works) {
        try {
            String insert = "insert or replace into " + TAB_SHELF + "("
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
                    + SHELF_LAST_POSITION + ") "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            database.beginTransaction();
            for (Work work : works) {
                if (TextUtils.isEmpty(work.push)){
                    work.push = "0";
                }
                database.execSQL(insert, new Object[]{work.wid, work.push, work.wtype, work.cover,
                        work.title, work.author, work.totalChapter, work.isfinish,
                        work.updatetime, work.updateflag, work.deleteflag,
                        work.lasttime, work.lastChapterOrder, work.lastChapterId, work.lastChapterPosition});
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
     *  单一条件查询多个
     */
    public List<Work> query(String param ,String paramName){
        String query = "select * from " + TAB_SHELF + " where " + paramName + " = " + param;
        Cursor cursor = database.rawQuery(query, null);
        List<Work> workList = new ArrayList<>();
        if (cursor != null && cursor.moveToNext()) {
            Work work = new Work();
            work.wid = cursor.getInt(cursor.getColumnIndex(SHELF_WID));
            work.push = cursor.getString(cursor.getColumnIndex(SHELF_PUSH));
            work.wtype = cursor.getInt(cursor.getColumnIndex(SHELF_WTYPE));
            work.cover = cursor.getString(cursor.getColumnIndex(SHELF_COVER));
            work.title = cursor.getString(cursor.getColumnIndex(SHELF_TITLE));
            work.author = cursor.getString(cursor.getColumnIndex(SHELF_AUTHOR));
            work.totalChapter = cursor.getInt(cursor.getColumnIndex(SHELF_COUNTS));
            work.isfinish = cursor.getInt(cursor.getColumnIndex(SHELF_IS_FINISH));
            work.updatetime = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_TIME));
            work.updateflag = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_FLAG));
            work.deleteflag = cursor.getInt(cursor.getColumnIndex(SHELF_DELETE_FLAG));
            work.lasttime = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_TIME));
            work.lastChapterOrder = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ORDER));
            work.lastChapterId = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ID));
            work.lastChapterPosition = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_POSITION));
            workList.add(work);
        }
        if (cursor != null){
            cursor.close();
        }
        return workList;
    }



    /**
     * 单一条件查询多个
     */
    public Work queryForFirst(String param,String paramName) {
        String query = "select * from " + TAB_SHELF + " where " + paramName + " = " + param;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToNext()) {
            Work work = new Work();
            work.wid = cursor.getInt(cursor.getColumnIndex(SHELF_WID));
            work.push = cursor.getString(cursor.getColumnIndex(SHELF_PUSH));
            work.wtype = cursor.getInt(cursor.getColumnIndex(SHELF_WTYPE));
            work.cover = cursor.getString(cursor.getColumnIndex(SHELF_COVER));
            work.title = cursor.getString(cursor.getColumnIndex(SHELF_TITLE));
            work.author = cursor.getString(cursor.getColumnIndex(SHELF_AUTHOR));
            work.isfinish = cursor.getInt(cursor.getColumnIndex(SHELF_IS_FINISH));
            work.updatetime = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_TIME));
            work.updateflag = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_FLAG));
            work.deleteflag = cursor.getInt(cursor.getColumnIndex(SHELF_DELETE_FLAG));
            work.lasttime = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_TIME));
            work.lastChapterOrder = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ORDER));
            work.lastChapterId = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ID));
            work.lastChapterPosition = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_POSITION));
            cursor.close();
            return work;
        }
        return null;
    }


    /**
     * 查询所有数据（无用）
     */
    public List<Work> queryAll() {
        String query = "select * from " + TAB_SHELF + " where " + SHELF_DELETE_FLAG + " <> 1 order by " + SHELF_LAST_TIME + " desc";
        Cursor cursor = database.rawQuery(query, null);
        List<Work> workList = new ArrayList<>();
        if (cursor != null && cursor.moveToNext()) {
            Work work = new Work();
            work.wid = cursor.getInt(cursor.getColumnIndex(SHELF_WID));
            work.push = cursor.getString(cursor.getColumnIndex(SHELF_PUSH));
            work.wtype = cursor.getInt(cursor.getColumnIndex(SHELF_WTYPE));
            work.cover = cursor.getString(cursor.getColumnIndex(SHELF_COVER));
            work.title = cursor.getString(cursor.getColumnIndex(SHELF_TITLE));
            work.author = cursor.getString(cursor.getColumnIndex(SHELF_AUTHOR));
            work.isfinish = cursor.getInt(cursor.getColumnIndex(SHELF_IS_FINISH));
            work.updatetime = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_TIME));
            work.updateflag = cursor.getInt(cursor.getColumnIndex(SHELF_UPDATE_FLAG));
            work.deleteflag = cursor.getInt(cursor.getColumnIndex(SHELF_DELETE_FLAG));
            work.lasttime = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_TIME));
            work.lastChapterOrder = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ORDER));
            work.lastChapterId = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_ID));
            work.lastChapterPosition = cursor.getInt(cursor.getColumnIndex(SHELF_LAST_POSITION));
           workList.add(work);
        }
        if (cursor != null){
            cursor.close();
        }
        return workList;
    }


    /**
     * 根据作品id更新表
     * @param work
     */
    public void updateWorkByWid(Work work){
        try {
            ContentValues values = new ContentValues();
            values.put(SHELF_WID,work.wid);
            values.put(SHELF_PUSH,work.push);
            values.put(SHELF_WTYPE,work.wtype);
            values.put(SHELF_COVER,work.cover);
            values.put(SHELF_TITLE,work.title);
            values.put(SHELF_AUTHOR,work.author);
            values.put(SHELF_COUNTS,work.totalChapter);
            values.put(SHELF_IS_FINISH,work.isfinish);
            values.put(SHELF_UPDATE_TIME,work.updatetime);
            values.put(SHELF_UPDATE_FLAG,work.updateflag);
            values.put(SHELF_DELETE_FLAG,work.deleteflag);
            values.put(SHELF_LAST_TIME,work.lasttime);
            values.put(SHELF_LAST_ORDER,work.lastChapterOrder);
            values.put(SHELF_LAST_ID,work.lastChapterId);
            values.put(SHELF_LAST_POSITION,work.lastChapterPosition);
            database.update(TAB_SHELF,values,"wid=?",new String[]{work.wid + ""});
        }catch (SQLException e){
            e.printStackTrace();
        }

    }


}
