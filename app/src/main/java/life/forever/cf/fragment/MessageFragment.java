package life.forever.cf.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.MsgTypeBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.activtiy.MessageDetailActivity;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MessageFragment extends BaseFragment {

    @BindView(R.id.noneView)
    View mNoneView;

    /*用户消息*/
    @BindView(R.id.layout_customer)
    RelativeLayout mLayoutCustomer;
    @BindView(R.id.view_customer)
    View mViewCustomer;
    @BindView(R.id.tv_customer)
    TextView mTvCustomer;
    @BindView(R.id.tv_customer_time)
    TextView mTvCustomerTime;
    @BindView(R.id.tv_customer_num)
    TextView mTvCustomerNum;
    /*系统消息*/
    @BindView(R.id.layout_system)
    RelativeLayout mLayoutSystem;
    @BindView(R.id.view_system)
    View mViewSystem;
    @BindView(R.id.tv_system)
    TextView mTvSystem;
    @BindView(R.id.tv_system_time)
    TextView mTvSystemTime;
    @BindView(R.id.tv_system_num)
    TextView mTvSystemNum;

    private MsgTypeBean.ResultData mTypeMsg;

    @SuppressLint("StaticFieldLeak")
    private static MessageFragment instance;

    public static MessageFragment get() {
        instance = new MessageFragment();
        return instance;
    }

    @Override
    protected void bindView() {
        mTitleBar.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        View root = LayoutInflater.from(context).inflate(R.layout.fragment_message, mContentLayout, TRUE);
        ButterKnife.bind(this, root);
        mNoneView.setOnClickListener(onRefreshClick);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void fetchData() {
        EventBus.getDefault().register(this);
        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
            //根据你的对象读取数据
//            mTypeMsg = (MsgTypeBean.ResultData) ObjectSaveUtils.getObject(getActivity(), "message_type");
//            msg();
            usermsg();
        } else {
            mNoneView.setVisibility(View.VISIBLE);
        }
    }

    private final View.OnClickListener onRefreshClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
                usermsg();
            } else {
                mNoneView.setVisibility(View.VISIBLE);
            }
        }
    };

    @SuppressLint("SetTextI18n")
    private void msg() {
        if (mTypeMsg != null) {
            switchPageBySize();
            /*用户消息*/
            if (mTypeMsg.lists.user_msg.list.size() > 0) {
                mLayoutCustomer.setVisibility(View.VISIBLE);
                mViewCustomer.setVisibility(View.VISIBLE);
                if (mTypeMsg.lists.user_msg.list.size() > 0) {
                    if (mTypeMsg.lists.user_msg.unread_count == 0) {
                        mTvCustomerNum.setVisibility(View.GONE);
                    } else if (mTypeMsg.lists.user_msg.unread_count > 99) {
                        mTvCustomerNum.setVisibility(View.VISIBLE);
                        mTvCustomerNum.setText("99+");
                    } else {
                        mTvCustomerNum.setVisibility(View.VISIBLE);
                        mTvCustomerNum.setText(mTypeMsg.lists.user_msg.unread_count + "");
                    }
                    mTvCustomer.setText(delHTMLTag(mTypeMsg.lists.user_msg.list.get(0).content));
                    String mTime = ComYou.timeFormat(Integer.parseInt(mTypeMsg.lists.user_msg.list.get(0).addtime), DATE_FORMATTER_9);
                    mTvCustomerTime.setText(mTime);
                }
            } else {
                mLayoutCustomer.setVisibility(View.GONE);
                mViewCustomer.setVisibility(View.GONE);
            }
            /*系统消息*/
            if (mTypeMsg.lists.sys_msg.list.size() > 0) {
                mLayoutSystem.setVisibility(View.VISIBLE);
                mViewSystem.setVisibility(View.VISIBLE);
                if (mTypeMsg.lists.sys_msg.list.size() > 0) {
                    if (mTypeMsg.lists.sys_msg.unread_count == 0) {
                        mTvSystemNum.setVisibility(View.GONE);
                    } else if (mTypeMsg.lists.sys_msg.unread_count > 99) {
                        mTvSystemNum.setVisibility(View.VISIBLE);
                        mTvSystemNum.setText("99+");
                    } else {
                        mTvSystemNum.setVisibility(View.VISIBLE);
                        mTvSystemNum.setText(mTypeMsg.lists.sys_msg.unread_count + "");
                    }
                    mTvSystem.setText(delHTMLTag(mTypeMsg.lists.sys_msg.list.get(0).content));
                    String mTime = ComYou.timeFormat(Integer.parseInt(mTypeMsg.lists.sys_msg.list.get(0).addtime), DATE_FORMATTER_9);
                    mTvSystemTime.setText(mTime);
                }
            } else {
                mLayoutSystem.setVisibility(View.GONE);
                mViewSystem.setVisibility(View.GONE);
            }
        } else {
            mNoneView.setVisibility(View.VISIBLE);
            /*初始化数据*/
            usermsg();
        }

    }

    /*用户消息*/
    @OnClick(R.id.layout_customer)
    void onCustomerClick() {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra("title", getString(R.string.customer_message));
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    /*系统消息*/
    @OnClick(R.id.layout_system)
    void onSystemClick() {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra("title", getString(R.string.system_messages));
        intent.putExtra("type", 2);
        startActivity(intent);
    }

    /**
     * 请求用户消息类型
     */
    private void usermsg() {
        NetRequest.usermsgRequest(new OkHttpResult() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        Gson gson = new Gson();
                        mTypeMsg = gson.fromJson(resultString, MsgTypeBean.ResultData.class);
                        switchPageBySize();
                        /*用户消息*/
                        if (mTypeMsg!=null&&mTypeMsg.lists !=null &&mTypeMsg.lists.user_msg.list.size() > 0) {
                            mLayoutCustomer.setVisibility(View.VISIBLE);
                            mViewCustomer.setVisibility(View.VISIBLE);
                            if (mTypeMsg!=null&&mTypeMsg.lists !=null &&mTypeMsg.lists.user_msg.list.size() > 0) {
                                if (mTypeMsg.lists.user_msg.unread_count == 0) {
                                    mTvCustomerNum.setVisibility(View.GONE);
                                } else if (mTypeMsg.lists.user_msg.unread_count > 99) {
                                    mTvCustomerNum.setVisibility(View.VISIBLE);
                                    mTvCustomerNum.setText("99+");
                                } else {
                                    mTvCustomerNum.setVisibility(View.VISIBLE);
                                    mTvCustomerNum.setText(mTypeMsg.lists.user_msg.unread_count + "");
                                }
                                mTvCustomer.setText(delHTMLTag(mTypeMsg.lists.user_msg.list.get(0).content));
                                String mTime = ComYou.timeFormat(Integer.parseInt(mTypeMsg.lists.user_msg.list.get(0).addtime), DATE_FORMATTER_9);
                                mTvCustomerTime.setText(mTime);
                            }
                        } else {
                            mLayoutCustomer.setVisibility(View.GONE);
                            mViewCustomer.setVisibility(View.GONE);
                        }
                        /*系统消息*/
                        if (mTypeMsg!=null&&mTypeMsg.lists !=null &&mTypeMsg.lists.sys_msg.list.size() > 0) {
                            mLayoutSystem.setVisibility(View.VISIBLE);
                            mViewSystem.setVisibility(View.VISIBLE);
                            if (mTypeMsg!=null&&mTypeMsg.lists !=null &&mTypeMsg.lists.sys_msg.list.size() > 0) {
                                if (mTypeMsg.lists.sys_msg.unread_count == 0) {
                                    mTvSystemNum.setVisibility(View.GONE);
                                } else if (mTypeMsg.lists.sys_msg.unread_count > 99) {
                                    mTvSystemNum.setVisibility(View.VISIBLE);
                                    mTvSystemNum.setText("99+");
                                } else {
                                    mTvSystemNum.setVisibility(View.VISIBLE);
                                    mTvSystemNum.setText(mTypeMsg.lists.sys_msg.unread_count + "");
                                }
                                mTvSystem.setText(delHTMLTag(mTypeMsg.lists.sys_msg.list.get(0).content));
                                String mTime = ComYou.timeFormat(Integer.parseInt(mTypeMsg.lists.sys_msg.list.get(0).addtime), DATE_FORMATTER_9);
                                mTvSystemTime.setText(mTime);
                            }
                        } else {
                            mLayoutSystem.setVisibility(View.GONE);
                            mViewSystem.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mNoneView.setVisibility(View.VISIBLE);
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                switchPageBySize();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        switch (message.what) {
            case BUS_USER_INFO_SUCCESS:
            case BUS_MSG_NUM_CHANGE:
                /*初始化消息类型数据*/
                usermsg();
                break;
//            case MSG_NUM:
//                //根据你的对象读取数据
//                mTypeMsg = (MsgTypeBean.ResultData) ObjectSaveUtils.getObject(getActivity(), "message_type");
//                msg();
//                break;
            case BUS_LOG_OUT:
                mLayoutCustomer.setVisibility(View.GONE);
                mLayoutSystem.setVisibility(View.GONE);
                mNoneView.setVisibility(View.VISIBLE);
                break;
        }

    }

    /**
     * 根据作品数量是否为0来切换页面状态
     */
    private void switchPageBySize() {
        if (mTypeMsg==null||mTypeMsg.lists ==null || mTypeMsg.lists.user_msg.list.size() == ZERO && mTypeMsg.lists.sys_msg.list.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
        } else {
            mNoneView.setVisibility(View.GONE);
        }
    }

    /**
     * 定义script的正则表达式
     */
    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    /**
     * 定义style的正则表达式
     */
    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    /**
     * 定义HTML标签的正则表达式
     */
    private static final String REGEX_HTML = "<[^>]+>";
    /**
     * 定义空格回车换行符
     */
    private static final String REGEX_SPACE = "\\s*|\t|\r|\n";
    public static String delHTMLTag(String htmlStr) {
        // 过滤script标签
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤style标签
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        // 过滤html标签
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        // 过滤空格回车标签
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
//        htmlStr = m_space.replaceAll("");
        return htmlStr.trim(); // 返回文本字符串
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
