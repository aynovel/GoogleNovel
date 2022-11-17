package life.forever.cf.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import life.forever.cf.entry.PayInfo;

import java.util.ArrayList;
import java.util.List;


public class PaymentSQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "PaymentSQLiteHelper";
    private static final String GOOGLE_PAY = "google_pay";


    private static final String PAY_ID = "DeveloperPayload";
    private static final String PAY_INFO = "OriginalJson";
    private static final String PAY_SIGN = "Signature";
    private static final String AMOUNT = "AMount";
    private static final String EXPEND = "Expend";
    private static final String ORDER_NAME = "Order_name";


    private static PaymentSQLiteHelper instance;
    private static SQLiteDatabase database;

    public static PaymentSQLiteHelper get(Context context) {
        if (instance == null) {
            instance = new PaymentSQLiteHelper(context);
        }
        database = instance.getWritableDatabase();
        return instance;
    }

    private PaymentSQLiteHelper(Context context) {
        super(context, "PAY", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String shelf = "CREATE TABLE IF NOT EXISTS " + GOOGLE_PAY + " (" + PAY_ID + " varchar NOT NULL," + PAY_INFO + " varchar NOT NULL," + PAY_SIGN + " varchar NOT NULL ," + AMOUNT + " varchar NOT NULL ," + EXPEND + " varchar NOT NULL ," + ORDER_NAME + " varchar NOT NULL)";
        db.execSQL(shelf);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2){
            db.execSQL("drop table if exists " + GOOGLE_PAY);
            onCreate(db);
        }
    }

    /**
     * 清空记录
     */
    public void clearRecord() {
        String clear = "delete from " + GOOGLE_PAY;
        database.execSQL(clear);
    }

    /**
     * 插入一条支付信息
     *
     * @param mPayInfo
     */
    public void insertRecord(PayInfo mPayInfo) {
        String insert = "insert or replace into " + GOOGLE_PAY + "("
                + PAY_ID + ","
                + PAY_INFO + ","
                + PAY_SIGN + ","
                + AMOUNT + ","
                + EXPEND + ","
                + ORDER_NAME  + ") "
                + "values(?,?,?,?,?,?)";
        database.execSQL(insert, new Object[]{mPayInfo.pay_id, mPayInfo.pay_originalJson, mPayInfo.signature,mPayInfo.aMount,mPayInfo.expend,mPayInfo.order_name});
    }

    /**
     * 删除一条支付信息
     *
     * @param pay_id
     */
    public void delete(String pay_id) {
        String sql = "delete from " + GOOGLE_PAY + " where " + PAY_ID + " = ?";
        database.execSQL(sql, new Object[]{pay_id});
    }

    /**
     * 删除一条支付信息
     *
     * @param payInfo
     */
    public void update(PayInfo payInfo) {
        ContentValues values = new ContentValues();
        values.put(PAY_INFO, payInfo.pay_originalJson);//key为字段名，value为值
        values.put(PAY_SIGN, payInfo.signature);
        values.put(AMOUNT, payInfo.aMount);
        values.put(EXPEND, payInfo.expend);
        values.put(ORDER_NAME, payInfo.order_name);
        database.update(GOOGLE_PAY, values, PAY_ID+"=?", new String[]{payInfo.pay_id});
//        database.close();
    }

    /**
     * 查询所有掉单信息
     */
    public List<PayInfo> query() {
        List<PayInfo> mPayInfos = new ArrayList<>();
        Cursor cursor = database.query(GOOGLE_PAY, null, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            PayInfo mPayInfo = new PayInfo();
            mPayInfo.pay_id = cursor.getString(cursor.getColumnIndex(PAY_ID));
            mPayInfo.pay_originalJson = cursor.getString(cursor.getColumnIndex(PAY_INFO));
            mPayInfo.signature = cursor.getString(cursor.getColumnIndex(PAY_SIGN));
            mPayInfo.aMount = cursor.getString(cursor.getColumnIndex(AMOUNT));
            mPayInfo.expend = cursor.getString(cursor.getColumnIndex(EXPEND));
            mPayInfo.order_name = cursor.getString(cursor.getColumnIndex(ORDER_NAME));
            mPayInfos.add(mPayInfo);
        }

        if (cursor != null) {
            cursor.close();
        }
        return mPayInfos;
    }
}
