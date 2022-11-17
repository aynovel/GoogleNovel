package life.forever.cf.entry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


public class Work implements Parcelable {



    public static final Parcelable.Creator<Work> CREATOR = new Parcelable.Creator<Work>() {
        @Override
        public Work createFromParcel(Parcel source) {
            return new Work(source);
        }

        @Override
        public Work[] newArray(int size) {
            return new Work[size];
        }
    };
    public String initial;
    public boolean cheak;
    public String openstate;
    public String isvip;
    public int wid;
    public int wtype;
    public String title;
    public String push;
    public int lastChapterPos;
    public String author;
    public int totalChapter;
    public int isfinish;
    public String description;
    public String cover;
    public int updatetime;
    public int totalWord;
    public int updateflag;
    public int is_rec;
    public int deleteflag;
    public int lasttime;
    public int lastChapterOrder;
    public int lastChapterId;
    public int lastChapterPosition;
    public String platform;
    public int score;
    public int recId;
    public LatestChapter latestChapter;
    public String sortTitle;
    public int sort_id;
    public int uv;
    public int pv;
    public List<TagBean> tag;
    public int totalFans;
    public int totalShare;
    public int totalCollect;
    public int award_total;
    public boolean isDiscount;
    public int discount;
    public int discount_start_time;
    public int discount_end_time;
    public boolean isMonth;
    public int month_start_time;
    public int month_end_time;
    public String solicit_logo;
    public int config_num;
    public int toReadType = 0;


    public Work() {
    }


    protected Work(Parcel in) {
        this.wid = in.readInt();
        this.wtype = in.readInt();
        this.title = in.readString();
        this.push = in.readString();
        this.author = in.readString();
        this.totalChapter = in.readInt();
        this.isfinish = in.readInt();
        this.description = in.readString();
        this.cover = in.readString();
        this.updatetime = in.readInt();
        this.totalWord = in.readInt();
        this.updateflag = in.readInt();
        this.deleteflag = in.readInt();
        this.lasttime = in.readInt();
        this.lastChapterOrder = in.readInt();
        this.lastChapterId = in.readInt();
        this.lastChapterPosition = in.readInt();
        this.platform = in.readString();
        this.score = in.readInt();
        this.recId = in.readInt();
        this.latestChapter = in.readParcelable(LatestChapter.class.getClassLoader());
        this.sortTitle = in.readString();
        this.solicit_logo = in.readString();
        this.uv = in.readInt();
        this.pv = in.readInt();
        this.totalFans = in.readInt();
        this.totalShare = in.readInt();
        this.totalCollect = in.readInt();
        this.award_total = in.readInt();

        this.isDiscount = in.readByte() != 0;
        this.discount = in.readInt();
        this.discount_start_time = in.readInt();
        this.discount_end_time = in.readInt();
        this.toReadType = in.readInt();
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public boolean isCheak() {
        return cheak;
    }

    public void setCheak(boolean cheak) {
        this.cheak = cheak;
    }

    public String getOpenstate() {
        return openstate;
    }

    public void setOpenstate(String openstate) {
        this.openstate = openstate;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Work && this.wid == ((Work) obj).wid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.wid);
        dest.writeInt(this.wtype);
        dest.writeString(this.title);
        dest.writeString(this.push);
        dest.writeString(this.author);

        dest.writeInt(this.totalChapter);
        dest.writeInt(this.isfinish);
        dest.writeString(this.description);
        dest.writeString(this.cover);
        dest.writeInt(this.updatetime);
        dest.writeInt(this.totalWord);
        dest.writeInt(this.updateflag);
        dest.writeInt(this.deleteflag);
        dest.writeInt(this.lasttime);
        dest.writeInt(this.lastChapterOrder);
        dest.writeInt(this.lastChapterId);
        dest.writeInt(this.lastChapterPosition);
        dest.writeString(this.platform);
        dest.writeInt(this.score);
        dest.writeInt(this.recId);
        dest.writeParcelable(this.latestChapter, flags);
        dest.writeString(this.sortTitle);
        dest.writeString(this.solicit_logo);
        dest.writeInt(this.uv);
        dest.writeInt(this.pv);
        dest.writeInt(this.totalFans);
        dest.writeInt(this.totalShare);
        dest.writeInt(this.totalCollect);
        dest.writeInt(this.award_total);
        dest.writeByte(this.isDiscount ? (byte) 1 : (byte) 0);
        dest.writeInt(this.discount);
        dest.writeInt(this.discount_start_time);
        dest.writeInt(this.discount_end_time);
        dest.writeInt(this.toReadType);
    }
}
