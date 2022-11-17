package life.forever.cf.publics.weight.poputil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.entry.TagsBean;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import life.forever.cf.publics.Constant;


public class TagPopupWindow extends PopupWindow implements View.OnClickListener {

    @SuppressLint("StaticFieldLeak")
    private static Activity mActivity;
    private AlertDialog mLoadingDialog;
    private ListView mListView;
    private List<TagsBean.ResultData.Tags> mTagsList = new ArrayList<>();

    private TagsBean.ResultData.Tags mTags;
    private String mid;
    private TagsAdapter mTagsAdapter;
    private PopupWindowOnClick listener;


    public TagPopupWindow(Activity mContext, String id) {
        super(mContext);
        mActivity = mContext;
        mid = id;
        init();
    }

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    private void init() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tag_popup_anim, null);
        setContentView(view);

        setting();
        view.findViewById(R.id.left_image).setOnClickListener(this);
        mListView = view.findViewById(R.id.layout_list);
        tagslist();
        /*mTagsAdapter = new TagsAdapter();
        mListView.setAdapter(mTagsAdapter);*/
        mListView.setOnItemClickListener((adapterView, view1, position, l) -> {
            if (listener != null) {
                mTagsAdapter.select(position);
                listener.onPopWindowClick(mTagsList.get(position).tag, mTagsList.get(position).id);
                dismiss();
            }
        });
    }

    public void setting() {
        //设置PopupWindow弹出窗体的宽高
        WindowManager mWindowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        this.setWidth(mWindowManager.getDefaultDisplay().getWidth());
        this.setHeight(mWindowManager.getDefaultDisplay().getHeight());
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置动画
        this.setAnimationStyle(R.style.dialog_style);
        //实例化一个ColorDrawable颜色为白色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        //设置PopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    /*
     * 请求全部标签列表数据
     */
    private void tagslist() {
        showLoading(mActivity.getString(R.string.loading));
        NetRequest.tagsRequest(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (Constant.SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");

                        JSONObject json = new JSONObject(resultString);
                        String strResult = json.getString("tags");

                        Type listType = new TypeToken<List<TagsBean.ResultData.Tags>>() {
                        }.getType();
                        Gson gson = new Gson();
                        mTagsList = gson.fromJson(strResult, listType);
                        mTagsAdapter = new TagsAdapter(mTagsList,mid);
                        mListView.setAdapter(mTagsAdapter);
//                        mTagsAdapter.data(mTagsList,mid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later！");
            }
        });
    }

    /*
     * 展示loading弹窗
     * @param tip
     */
    public void showLoading(String tip) {
        dismissLoading();
        mLoadingDialog = LoadingAlertDialog.show(mActivity, tip);
    }

    /*
     * 隐藏loading弹窗
     */
    public void dismissLoading() {
        LoadingAlertDialog.dismiss(mLoadingDialog);
    }

    public void show() {
        Rect rect = new Rect();
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        this.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.left_image) {
            dismiss();
        }
    }

    public interface PopupWindowOnClick {
        void onPopWindowClick(String title, String tag_id);
    }

    //设置监听事件
    public void setPopupWindowOnClick(PopupWindowOnClick listener) {
        this.listener = listener;
    }

    private class TagsAdapter extends BaseAdapter {

        private final LayoutInflater layoutInflater;

        public TagsAdapter(List<TagsBean.ResultData.Tags> List,String id) {
            layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mTagsList = List;
            mid = id;
        }

        // 选中当前选项时，让其他选项不被选中
        public void select(int position) {
            if (!mTagsList.get(position).isSelected()) {
                mTagsList.get(position).setSelected(true);
                for (int i = 0; i < mTagsList.size(); i++) {
                    if (i != position) {
                        mTagsList.get(i).setSelected(false);
                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTagsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTagsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHoler viewHoler;
            if (view == null) {
                viewHoler = new ViewHoler();
                view = layoutInflater.inflate(R.layout.item_listview, null);
                viewHoler.mTagName = view.findViewById(R.id.tag_name);
                viewHoler.mTagSelect = view.findViewById(R.id.tag_select);
                viewHoler.mTagSelect.setClickable(false);
                view.setTag(viewHoler);
            } else {
                viewHoler = (ViewHoler) view.getTag();
            }
            if (mid.equals(mTagsList.get(position).id)) {
                mTagsList.get(position).setSelected(true);
                notifyDataSetChanged();
            }
            mTags = (TagsBean.ResultData.Tags) getItem(position);
            viewHoler.mTagSelect.setChecked(mTags.isSelected());
            viewHoler.mTagName.setText(mTagsList.get(position).tag);
            return view;
        }

        private class ViewHoler {
            TextView mTagName;
            RadioButton mTagSelect;
        }
    }
}