package life.forever.cf.activtiy;

import android.app.Activity;
import android.os.Message;

import life.forever.cf.entry.TaskReword;
import life.forever.cf.entry.TaskDetailBean;
import life.forever.cf.entry.TaskItemBean;
import life.forever.cf.entry.UserAllTasksPackage;
import life.forever.cf.entry.UserDiscountTaskRewardPackage;
import life.forever.cf.entry.UserDiscountTaskRewardResult;
import life.forever.cf.entry.UserReadingTimeTaskRewardPackage;
import life.forever.cf.entry.UserRecevieTaskRewardPackage;
import life.forever.cf.entry.UserRecevieTaskRewardResult;
import life.forever.cf.internet.ReaderRemoteRepository;
import life.forever.cf.publics.tool.TaskCompleteDialog;
import life.forever.cf.publics.tool.TimeUtil;
import life.forever.cf.interfaces.ReceviedRewardCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import life.forever.cf.publics.Constant;

public class UserTaskReceiveManager {
    private static volatile UserTaskReceiveManager sInstance;

    private CompositeDisposable mTaskDisposable;

    private boolean autoReceiveCompele = true;

    private TaskDetailBean mAutoReceiveBean = null;

    private Activity mShowPopActivity;

    public UserTaskReceiveManager() {
        if (this.mTaskDisposable == null) {
            this.mTaskDisposable = new CompositeDisposable();
        }
    }

    private void addDisposable(Disposable disposable) {
        if (mTaskDisposable != null) {
            mTaskDisposable.add(disposable);
        }
    }


    public static UserTaskReceiveManager getInstance() {
        if (sInstance == null) {
            synchronized (UserTaskReceiveManager.class) {
                if (sInstance == null) {
                    sInstance = new UserTaskReceiveManager();
                }
            }
        }
        return sInstance;
    }

    public void getAutoBuyTaskRecevice(String taskID, ReceviedRewardCallBack callBack) {
        if (!PlotRead.getAppUser().login()) {
            return;
        }

        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderDiscountTaskReward(taskID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (UserDiscountTaskRewardPackage discountTaskRewardPackage) -> {

                            if (discountTaskRewardPackage != null && discountTaskRewardPackage.getResult() != null) {

                                UserDiscountTaskRewardResult result = discountTaskRewardPackage.getResult();
                                if(callBack != null)
                                {
                                    callBack.getReceviedRewardResult(result.status != 0);
                                }

                            }
                        },
                        (e) -> {
                            LogUtils.e("获取用户任务  失败====== " + e);
                        }
                );

        addDisposable(disposable);

    }


    public void freashAllAutoReceiveTasks(Activity showActivity, int taskType) {

        if (!PlotRead.getAppUser().login()) {
            return;
        }

        if(autoReceiveCompele  == false)
        {
            return;
        }

        mShowPopActivity = showActivity;

        autoReceiveCompele = false;
        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderUserAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (UserAllTasksPackage allTasksPackage) -> {

                            if (allTasksPackage != null && allTasksPackage.getResult() != null) {

                                mAutoReceiveBean = allTasksPackage.getResult().lists;
                                freashAutoReceiveTask(taskType);
                            }else{
                                autoReceiveCompele = true;
                            }
                        },
                        (e) -> {
                            LogUtils.e("获取用户任务  失败====== " + e);
                            autoReceiveCompele = true;
                        }
                );

        addDisposable(disposable);

    }

    private void freashAutoReceiveTask(int taskType)
    {
        if(mAutoReceiveBean != null)
        {
            boolean receiveFlag = false;
            if(mAutoReceiveBean.first_list.size()>0)
            {
                receiveFlag = checkTaskGetReward(mAutoReceiveBean.first_list,taskType);
            }

            if(receiveFlag == false)
            {
                if(mAutoReceiveBean.daily_list.size()>0)
                {
                    receiveFlag = checkTaskGetReward(mAutoReceiveBean.daily_list,taskType);
                }
            }

            if(receiveFlag == false)
            {
                if(mAutoReceiveBean.read_list.size()>0)
                {
                    receiveFlag = checkTaskGetReward(mAutoReceiveBean.read_list,taskType);
                }
            }

            if(receiveFlag)
            {
                return;
            }

        }

        autoReceiveCompele = true;
    }

    /**
     * 判断任务是否能够领取奖励
     * @param dataList
     */
    private boolean checkTaskGetReward(List<TaskItemBean> dataList, int taskType){
        boolean hasAutoFlag = false;
        for (TaskItemBean itemBean : dataList){
            //充值
            if (Integer.parseInt(itemBean.task_type) == taskType){
                //已完成
                if (itemBean.status == 1){
                    //自动
                    if (itemBean.auto.equals("1")){
                        //领取
                        hasAutoFlag = true;
                        autoReceviceTask(itemBean,taskType);
                    }
                }
            }
        }
        return hasAutoFlag;
    }



    public void autoReceviceTask(TaskItemBean itemBean,int taskType) {

        if (!PlotRead.getAppUser().login()) {
            return;
        }


        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderUserRecevieTaskRewad(itemBean.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (UserRecevieTaskRewardPackage recevieTaskRewardPackage) -> {

                            if (recevieTaskRewardPackage != null && recevieTaskRewardPackage.getResult() != null) {
                                UserRecevieTaskRewardResult result = recevieTaskRewardPackage.getResult();
                                if(result.task != null)
                                {

                                    if(mShowPopActivity != null && !mShowPopActivity.isFinishing())
                                    {
                                        //后续操作  显示领取奖励dialog
                                        TaskCompleteDialog mTaskCompleteDialog =  new TaskCompleteDialog(mShowPopActivity, result.task, new ReceviedRewardCallBack() {
                                            @Override
                                            public void getReceviedRewardResult(boolean isReceived) {
                                                freashAutoReceiveTask(taskType);
                                            }
                                        });
                                        mTaskCompleteDialog.show();
                                    }


                                    //书币更改通知个人中心
                                    Message message = Message.obtain();
                                    message.what = Constant.ADD_BOUNS_SUCCESS;
                                    EventBus.getDefault().post(message);
                                }

                            }
                        },
                        (e) -> {
                            LogUtils.e("自动领取任务奖励  失败====== " + e);
                        }
                );
        addDisposable(disposable);
    }

    public void autoReceviceRewordTask(TaskReword itemBean) {

        if (!PlotRead.getAppUser().login()) {
            return;
        }


        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderUserRecevieTaskRewad(itemBean.task_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (UserRecevieTaskRewardPackage recevieTaskRewardPackage) -> {

                            if (recevieTaskRewardPackage != null && recevieTaskRewardPackage.getResult() != null) {
                                UserRecevieTaskRewardResult result = recevieTaskRewardPackage.getResult();
                                if(result.task != null)
                                {
                                    if(mShowPopActivity != null && !mShowPopActivity.isFinishing())
                                    {
                                        //后续操作  显示领取奖励dialog
                                        TaskCompleteDialog mTaskCompleteDialog =  new TaskCompleteDialog(mShowPopActivity, result.task, new ReceviedRewardCallBack() {
                                            @Override
                                            public void getReceviedRewardResult(boolean isReceived) {
                                            }
                                        });
                                        mTaskCompleteDialog.show();
                                    }


                                    //书币更改通知个人中心
                                    Message message = Message.obtain();
                                    message.what = Constant.ADD_BOUNS_SUCCESS;
                                    EventBus.getDefault().post(message);
                                }

                            }
                        },
                        (e) -> {
                            LogUtils.e("自动领取任务奖励  失败====== " + e);
                        }
                );
        addDisposable(disposable);
    }

    /**
     * 上传阅读时长
     * @param readTime 分钟
     */
    public void updateUserReadTimeAndGetTaskRecevice(Activity showActivity, int readTime) {

        if (!PlotRead.getAppUser().login()) {
            return;
        }

        mShowPopActivity = showActivity;

        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderUploadReadTimeTaskReward(TimeUtil.currentYMDDate(),readTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (UserReadingTimeTaskRewardPackage timeTaskRewardPackage) -> {

                            if (timeTaskRewardPackage != null && timeTaskRewardPackage.getResult() != null) {

                                if(timeTaskRewardPackage.getResult().task != null)
                                {
                                    if (timeTaskRewardPackage.getResult().task != null){
                                        if (timeTaskRewardPackage.getResult().task.auto.equals("1")){
                                            autoReceviceRewordTask(timeTaskRewardPackage.getResult().task);
                                        }
                                    }
                                }
                            }
                        },
                        (e) -> {
                            LogUtils.e("上传阅读时长任务  失败====== " + e);
                        }
                );

        addDisposable(disposable);
    }

}
