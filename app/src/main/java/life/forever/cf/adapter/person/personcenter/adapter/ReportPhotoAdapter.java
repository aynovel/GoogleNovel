package life.forever.cf.adapter.person.personcenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.adapter.AppendableAdapter;

public class ReportPhotoAdapter extends AppendableAdapter<String> {

    public ReportPhotoAdapter(Context mContext) {
        this.mContext = mContext;
    }

    private final Context mContext;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view =   LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_report_photo,parent,false);
        return new ReportPhotoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReportPhotoHolder mHolder = (ReportPhotoHolder) holder;

        String item = mDataItems.get(position);

        if (!item.equals("")){
            GlideUtil.load(mContext, item, R.drawable.default_user_logo, mHolder.mImgPhoto);
            mHolder.mImgDelete.setVisibility(View.VISIBLE);
            mHolder.mImgAdd.setVisibility(View.GONE);
            mHolder.mImgPhoto.setVisibility(View.VISIBLE);
        }else {
            mHolder.mImgDelete.setVisibility(View.GONE);
            mHolder.mImgAdd.setVisibility(View.VISIBLE);
            mHolder.mImgPhoto.setVisibility(View.GONE);
        }


        mHolder.mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickLitener.onItemClick(view,position);
            }
        });


        mHolder.mImgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickLitener.onItemClick(view,position);
            }
        });

    }

    public class ReportPhotoHolder extends RecyclerView.ViewHolder {

        private final ImageView mImgPhoto;
        private final ImageView mImgAdd;
        private final ImageView mImgDelete;

        public ReportPhotoHolder(@NonNull View itemView) {
            super(itemView);
            mImgPhoto = itemView.findViewById(R.id.imgPhoto);
            mImgAdd = itemView.findViewById(R.id.imgAdd);
            mImgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }

    //照片数量
    public String getLimitText(){
        int i = 0 ;
        for (String item : mDataItems){
            if (!item.equals("")){
                i++;
            }
        }
        return i+"/"+4;
    }
}
