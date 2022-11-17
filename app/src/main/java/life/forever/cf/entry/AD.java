package life.forever.cf.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class AD implements Parcelable {

    public int type;
    public int recId;
    public String image;
    public int during;

    public int wid;

    public int readflag;
    public int cid;

    public String index; // ht
    public String path; // path
    public boolean pagefresh; // ps 0：true 1：false
    public boolean share; // is
    public String shareUrl; // su
    public int shareType; // st
    public boolean sharefresh; // ifreash
    public String shareTitle; // title
    public String shareDesc; // desc
    public String shareImg;

    public String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.recId);
        dest.writeString(this.image);
        dest.writeInt(this.during);
        dest.writeInt(this.wid);
        dest.writeInt(this.readflag);
        dest.writeInt(this.cid);
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

    public AD() {
    }

    protected AD(Parcel in) {
        this.type = in.readInt();
        this.recId = in.readInt();
        this.image = in.readString();
        this.during = in.readInt();
        this.wid = in.readInt();
        this.readflag = in.readInt();
        this.cid = in.readInt();
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

    public static final Parcelable.Creator<AD> CREATOR = new Parcelable.Creator<AD>() {
        @Override
        public AD createFromParcel(Parcel source) {
            return new AD(source);
        }

        @Override
        public AD[] newArray(int size) {
            return new AD[size];
        }
    };
}
