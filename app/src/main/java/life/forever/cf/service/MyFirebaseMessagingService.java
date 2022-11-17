package life.forever.cf.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.activtiy.SplashActivity;
import life.forever.cf.activtiy.TaskCenterActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    String image_url = null;
    Bitmap image_bitmap = null;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null) {
            //Get Image from Firebase
            if (remoteMessage.getNotification().getImageUrl() != null) {
                image_url = remoteMessage.getNotification().getImageUrl().toString();
                image_bitmap = getBitmapFromURL(image_url);
            }




            showMessage(PlotRead.getApplication(), SplashActivity.class, remoteMessage,remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), 0);
        }
    }

    private Bitmap getBitmapFromURL(String image_url) {

        try {
            URL url = new URL(image_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
//        Log.i("ceshi", "你删除了消息哦");
    }

    /**
     * 消息通知栏
     * @param context
     *      上下文
     * @param cl
     *      需要跳转的Activity
     * @param tittle
     *      通知栏标题
     * @param content
     *      通知栏内容
     * @param i
     *      通知的标识符
     */
    public static void showMessage(Context context, Class cl,RemoteMessage remoteMessage, String tittle, String content, int i) {
        Intent intent = new Intent(context, cl);
        if(!remoteMessage.getData().isEmpty()){
            Map<String, String> myData = remoteMessage.getData();
            Log.d("BX====qqq",remoteMessage.getData().toString());

            if (!TextUtils.isEmpty(myData.get("push"))){
                intent.putExtra("push",myData.get("push"));
            }else {
                intent.putExtra("push",true);
            }
            if (!TextUtils.isEmpty(myData.get("novelId"))){
                intent.putExtra("novelId",Integer.parseInt(myData.get("novelId").trim()));
            }
            if (!TextUtils.isEmpty(myData.get("chapterOrder"))){
                intent.putExtra("chapterOrder", Integer.parseInt(myData.get("chapterOrder").trim()));
            }

            //签到消息推送跳转任务中心
            if (myData.get("type") != null && myData.get("type").equals("taskcenter")){
                intent = new Intent(context, TaskCenterActivity.class);
            }

        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String id = context.getPackageName();//频道的ID。每个包必须是唯一的
        //渠道名字
        String name = context.getString(R.string.app_name);//频道的用户可见名称
        //创建一个通知管理器
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id,name,NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setContentTitle(tittle)//设置通知标题
                    .setContentText(content)//设置通知内容
                    .setSmallIcon(R.drawable.logo_novel_star)//设置小图标
                    .setLargeIcon(BitmapFactory.decodeResource
                            (context.getResources(), R.drawable.logo_novel_star))//设置大图标
                    .setContentIntent(pendingIntent)//打开消息跳转到这儿
                    .setAutoCancel(true)// 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失
                    .setOngoing(true)//将Ongoing设为true 那么notification将不能滑动删除
                    // 从Android4.1开始，可以通过以下方法，设置notification的优先级，优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
                    //.setPriority(NotificationCompat.PRIORITY_MAX)

                    // Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
                    // Notification.DEFAULT_SOUND：系统默认铃声。
                    // Notification.DEFAULT_VIBRATE：系统默认震动。
                    // Notification.DEFAULT_LIGHTS：系统默认闪光。
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(tittle)
                            .setContentText(content)
                            .setSmallIcon(R.drawable.logo_novel_star)
                            .setLargeIcon(BitmapFactory.decodeResource
                                    (context.getResources(), R.drawable.logo_novel_star))//设置大图标
//                            .setVibrate(vibrate)//震动
                            .setContentIntent(pendingIntent)//打开消息跳转到这儿
                            .setAutoCancel(true)
                            .setOngoing(true)
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
//                            .setVisibility(Notification.VISIBILITY_PUBLIC)//在锁屏上的显示

                            .setOngoing(true)
                            .setChannelId(id);
            notification = notificationBuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }
        notificationManager.notify(i, notification);
    }

}