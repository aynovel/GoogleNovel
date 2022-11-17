package life.forever.cf.entry;

import android.os.Parcel;
import android.os.Parcelable;


public class RankType implements Parcelable {

    public int id;
    public String title;
    public int pageId;
    public int cycleId;
    public int status;
    public String icon_image;
    public String icon_gray_image;
    public String icon_type;
    public String desc;
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.pageId);
        dest.writeInt(this.cycleId);
        dest.writeInt(this.status);
        dest.writeString(this.icon_image);
        dest.writeString(this.icon_gray_image);
        dest.writeString(this.icon_type);
        dest.writeString(this.desc);
    }

    public RankType() {

    }

    protected RankType(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.pageId = in.readInt();
        this.cycleId = in.readInt();
        this.status = in.readInt();
        this.icon_image = in.readString();
        this.icon_gray_image = in.readString();
        this.icon_type = in.readString();
        this.desc = in.readString();
    }

    public static final Parcelable.Creator<RankType> CREATOR = new Parcelable.Creator<RankType>() {
        @Override
        public RankType createFromParcel(Parcel source) {
            return new RankType(source);
        }

        @Override
        public RankType[] newArray(int size) {
            return new RankType[size];
        }
    };

}
