package life.forever.cf.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class Push implements Parcelable {


    public int type;


    public int wid;

    public int readflag;
    public int chapterOrder;


    public String index; // ht
    public String path; // pt
    public boolean pagefresh; // ps 0：true 1：false
    public boolean share; // is
    public String shareUrl; // su
    public int shareType; // st
    public boolean sharefresh; // if
    public String shareTitle; // title
    public String shareDesc; // desc
    public String shareImg; // image


    public String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.wid);
        dest.writeInt(this.readflag);
        dest.writeInt(this.chapterOrder);
        dest.writeString(this.index);
        dest.writeString(this.path);
        dest.writeByte(this.pagefresh ? (byte) 1 : (byte) 0);
        dest.writeByte(this.share ? (byte) 1 : (byte) 0);
        dest.writeString(this.shareUrl);
        dest.writeInt(this.shareType);
        dest.writeByte(this.sharefresh ? (byte) 1 : (byte) 0);
        dest.writeString(this.shareTitle);
        dest.writeString(this.shareDesc);
        dest.writeString(this.shareImg);
        dest.writeString(this.url);
    }


    protected Push(Parcel in) {
        this.type = in.readInt();
        this.wid = in.readInt();
        this.readflag = in.readInt();
        this.chapterOrder = in.readInt();
        this.index = in.readString();
        this.path = in.readString();
        this.pagefresh = in.readByte() != 0;
        this.share = in.readByte() != 0;
        this.shareUrl = in.readString();
        this.shareType = in.readInt();
        this.sharefresh = in.readByte() != 0;
        this.shareTitle = in.readString();
        this.shareDesc = in.readString();
        this.shareImg = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Push> CREATOR = new Parcelable.Creator<Push>() {
        @Override
        public Push createFromParcel(Parcel source) {
            return new Push(source);
        }

        @Override
        public Push[] newArray(int size) {
            return new Push[size];
        }
    };

}
