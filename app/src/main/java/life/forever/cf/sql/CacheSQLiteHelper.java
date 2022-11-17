package life.forever.cf.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.entry.Catalog;
import life.forever.cf.entry.Mark;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CacheSQLiteHelper extends SQLiteOpenHelper {


    private static final String TAB_CATALOG = "catalog";
    private static final String CATALOG_ID = "id";
    private static final String CATALOG_TITLE = "title";
    private static final String CATALOG_SORT = "sort";
    private static final String CATALOG_IS_VIP = "is_vip";
    private static final String CATALOG_Y_VIP = "vip";
    private static final String CATALOG_CREATE_TIME = "create_time";
    private static final String CATALOG_UPDATE_TIME = "update_time";
    private static final String CATALOG_ORDER_ID = "order_id";

    private static final String TAB_CACHE = "cache";
    private static final String CACHE_ID = "id";
    private static final String CACHE_CONTENT = "content";


    private static final String TAB_MARK = "mark";
    private static final String MARK_TIME = "mark_date";
    private static final String MARK_CHAPTER_ID = "chapter_id";
    private static final String MARK_CHAPTER_ORDER = "chapter_order";
    private static final String MARK_CHAPTER_POSITION = "chapter_position";
    private static final String MARK_CHAPTER_TITLE = "chapter_title";
    private static final String MARK_CHAPTER_CONTENT = "chapter_content";

    private static final String TAG = "CacheSQLiteHelper";

    private static String db;
    private static CacheSQLiteHelper instance;
    private static SQLiteDatabase database;
    Cursor cursorCheck = null;
    public static CacheSQLiteHelper get(Context context, int wid) {
        if (context == null){
            context = PlotRead.getApplication();
        }
        String format = "WORK_%d";
        Locale locale = Locale.getDefault();
        if (instance == null || TextUtils.isEmpty(db) || !String.format(locale, format, wid).equals(db)) {
            db = String.format(locale, format, wid);
            instance = new CacheSQLiteHelper(context);
        }
        if (instance == null){
            instance = new CacheSQLiteHelper(PlotRead.getApplication());
        }
        database = instance.getWritableDatabase();
        return instance;
    }

    private CacheSQLiteHelper(Context context) {
        super(context, db, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String catalog = "CREATE TABLE IF NOT EXISTS " + TAB_CATALOG + " (" + CATALOG_ID + " integer PRIMARY KEY NOT NULL," + CATALOG_TITLE + " varchar NOT NULL," + CATALOG_SORT + " integer NOT NULL," + CATALOG_IS_VIP + " integer NOT NULL," + CATALOG_Y_VIP +" integer NOT NULL,"  + CATALOG_CREATE_TIME + " integer NOT NULL," + CATALOG_UPDATE_TIME + " integer NOT NULL," + CATALOG_ORDER_ID +" integer NOT NULL)";
        db.execSQL(catalog);
        String content = "CREATE TABLE IF NOT EXISTS " + TAB_CACHE + " (" + CACHE_ID + " integer PRIMARY KEY NOT NULL," + CACHE_CONTENT + " varchar NOT NULL)";
        db.execSQL(content);
        String mark = "CREATE TABLE IF NOT EXISTS " + TAB_MARK + " (" + MARK_TIME + " integer PRIMARY KEY NOT NULL," + MARK_CHAPTER_ID + " integer NOT NULL," + MARK_CHAPTER_ORDER + " integer NOT NULL," + MARK_CHAPTER_POSITION + " integer NOT NULL," + MARK_CHAPTER_TITLE + " varchar NOT NULL," + MARK_CHAPTER_CONTENT + " varchar NOT NULL)";
        db.execSQL(mark);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion<2){
            db.beginTransaction();
            try {
                String shelf = "alter table "+TAB_CATALOG+" add " + CATALOG_ORDER_ID + " integer";
                db.execSQL(shelf);
//                String record = "Alter table "+TAB_READ_RECORD+" add " + RECORD_PUSH + " varchar";
//                db.execSQL(record);
//                db.setTransactionSuccessful();
            } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage(), ex);
            } finally {
                db.endTransaction();
            }
        }
    }


    /*------------------------ 操作方法 ------------------------*/

    /**
     * 查询目录列表，并插入到{@code catalogs}中
     *
     * @param catalogs
     */
    public void query(List<Catalog> catalogs) {
        try {
//            String catalog1 = "CREATE TABLE IF NOT EXISTS " + TAB_CATALOG + " (" + CATALOG_ID + " integer PRIMARY KEY NOT NULL," + CATALOG_TITLE + " varchar NOT NULL," + CATALOG_SORT + " integer NOT NULL," + CATALOG_IS_VIP + " integer NOT NULL," + CATALOG_Y_VIP +" integer NOT NULL," + CATALOG_ORDER_ID +" integer NOT NULL,"  + CATALOG_CREATE_TIME + " integer NOT NULL," + CATALOG_UPDATE_TIME + " integer NOT NULL)";
//            database.execSQL(catalog1);
            cursorCheck = database.query(TAB_CATALOG, null, null, null, null, null, null);
            while (cursorCheck != null && cursorCheck.moveToNext()) {
                Catalog catalog = new Catalog();
                catalog.id = cursorCheck.getInt(cursorCheck.getColumnIndex(CATALOG_ID));
                catalog.title = cursorCheck.getString(cursorCheck.getColumnIndex(CATALOG_TITLE));
                catalog.sort = cursorCheck.getInt(cursorCheck.getColumnIndex(CATALOG_SORT));
                catalog.isvip = cursorCheck.getInt(cursorCheck.getColumnIndex(CATALOG_IS_VIP));
                catalog.vip = cursorCheck.getInt(cursorCheck.getColumnIndex(CATALOG_Y_VIP));

                catalog.createtime = cursorCheck.getInt(cursorCheck.getColumnIndex(CATALOG_CREATE_TIME));
                catalog.updatetime = cursorCheck.getInt(cursorCheck.getColumnIndex(CATALOG_UPDATE_TIME));
                catalog.order = cursorCheck.getInt(cursorCheck.getColumnIndex(CATALOG_ORDER_ID));
                catalogs.add(catalog);

            }
            if (cursorCheck != null) {
                cursorCheck.close();
            }
        }
        catch (Exception e){
            if (cursorCheck != null) {
                cursorCheck.close();
            }
        }  finally{
            if (cursorCheck != null) {
                cursorCheck.close();
            }
        }
    }

    /**
     * 批量插入目录
     *
     * @param catalogs
     */
    public void insert(List<Catalog> catalogs) {
        if (null == catalogs || catalogs.size() <= 0) {
            return;
        }
        try {
            String sql = "insert or replace into " + TAB_CATALOG + "("
                    + CATALOG_ID + ","
                    + CATALOG_TITLE + ","
                    + CATALOG_SORT + ","
                    + CATALOG_IS_VIP + ","
                    + CATALOG_Y_VIP + ","
                    + CATALOG_CREATE_TIME + ","
                    + CATALOG_UPDATE_TIME + ","
                    + CATALOG_ORDER_ID + ") "
                    + "values(?,?,?,?,?,?,?,?)";
            database.beginTransaction();
            for (Catalog catalog : catalogs) {
                database.execSQL(sql, new Object[]{catalog.order, catalog.title, catalog.sort,
                        catalog.isvip, catalog.vip, catalog.createtime, catalog.updatetime, catalog.id});
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
     * 清空目录缓存
     */
    public void clearCatalogs() {
        database.delete(TAB_CATALOG, null, null);
    }

    /**
     * 清空目录缓存
     */
    //删除某一个表
    public void dropTable(SQLiteDatabase db){
        db.execSQL("drop table tab_name");
    }



    /* -------------------------- 巨丑无比的分割线 --------------------------- */

    /**
     * 查询章节缓存
     *
     * @param cid
     * @return
     */
    Cursor cursorCache;
    public String query(int cid) {
        try {
            String result = "";
            String sql = "select " + CACHE_CONTENT + " from " + TAB_CACHE + " where " + CACHE_ID + "= ?";
            cursorCache = database.rawQuery(sql, new String[]{String.valueOf(cid)});
            if (cursorCache != null && cursorCache.moveToNext()) {
                result = cursorCache.getString(0);
                cursorCache.close();
            }
            return result;
        } catch (Exception e){

            if (cursorCache != null) {
                cursorCache.close();
            }
            return "";
        }

    }

    /**
     * 插入章节缓存
     *
     * @param cid
     * @param chapter
     */
    public void insert(int cid, String chapter) {
        if (TextUtils.isEmpty(chapter)) {
            return;
        }
        String sql = "insert or replace into " + TAB_CACHE + "("
                + CACHE_ID + ","
                + CACHE_CONTENT + ") "
                + "values(?,?)";
        database.execSQL(sql, new Object[]{cid, chapter});
    }

    /**
     * 删除章节缓存
     *
     * @param cid
     */
    public void delete(int cid) {
        String sql = "delete from " + TAB_CACHE + " where " + CACHE_ID + " = ?";
        database.execSQL(sql, new Object[]{cid});
    }

    /**
     * 删除所有章节缓存
     */
    public void clearCache() {
        database.delete(TAB_CACHE, null, null);
    }

    /* -------------------------- 巨丑无比的分割线 --------------------------- */

    /**
     * 获取所有书签
     *
     * @return
     */
    public List<Mark> getAllMarks() {
        String sql = "select * from " + TAB_MARK + " order by " + MARK_TIME + " desc";
        Cursor cursor = database.rawQuery(sql, null);
        List<Mark> marks = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            Mark mark = new Mark();
            mark.timestamp = cursor.getInt(cursor.getColumnIndex(MARK_TIME));
            mark.chapterId = cursor.getInt(cursor.getColumnIndex(MARK_CHAPTER_ID));
            mark.chapterOrder = cursor.getInt(cursor.getColumnIndex(MARK_CHAPTER_ORDER));
            mark.chapterPos = cursor.getInt(cursor.getColumnIndex(MARK_CHAPTER_POSITION));
            mark.chapterTitle = cursor.getString(cursor.getColumnIndex(MARK_CHAPTER_TITLE));
            mark.chapterContent = cursor.getString(cursor.getColumnIndex(MARK_CHAPTER_CONTENT));
            marks.add(mark);
        }
        if (cursor != null) {
            cursor.close();
        }
        return marks;
    }

    /**
     * 判断某个章节的指定位置范围是否有书签
     *
     * @param cid
     * @param pageStart
     * @param pageEnd
     * @return
     */
    public boolean hasMark(int cid, int pageStart, int pageEnd) {
        boolean exist = false;
        String sql = "select * from " + TAB_MARK + " where " + MARK_CHAPTER_ID + " = ? and " + MARK_CHAPTER_POSITION + " >= ? and " + MARK_CHAPTER_POSITION + " < ?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(cid), String.valueOf(pageStart), String.valueOf(pageEnd)});
        if (cursor != null && cursor.moveToNext()) {
            exist = true;
            cursor.close();
        }
        return exist;
    }

    /**
     * 删除某个章节指定位置范围内的书签
     *
     * @param cid
     * @param pageStart
     * @param pageEnd
     */
    public void deleteMark(int cid, int pageStart, int pageEnd) {
        String sql = "delete from " + TAB_MARK + " where " + MARK_CHAPTER_ID + " = ? and " + MARK_CHAPTER_POSITION + " >= ? and " + MARK_CHAPTER_POSITION + " < ?";
        database.execSQL(sql, new Object[]{cid, pageStart, pageEnd});
    }

    /**
     * 删除指定书签
     *
     * @param mark
     */
    public void deleteMark(Mark mark) {
        String sql = "delete from " + TAB_MARK + " where " + MARK_TIME + " = ?";
        database.execSQL(sql, new Object[]{mark.timestamp});
    }

    /**
     * 插入书签
     *
     * @param mark
     */
    public void insertMark(Mark mark) {
        String sql = "insert or replace into " + TAB_MARK + "("
                + MARK_TIME + ","
                + MARK_CHAPTER_ID + ","
                + MARK_CHAPTER_ORDER + ","
                + MARK_CHAPTER_POSITION + ","
                + MARK_CHAPTER_TITLE + ","
                + MARK_CHAPTER_CONTENT + ") "
                + "values(?,?,?,?,?,?)";
        database.execSQL(sql, new Object[]{mark.timestamp, mark.chapterId, mark.chapterOrder, mark.chapterPos, mark.chapterTitle, mark.chapterContent});
    }
}
