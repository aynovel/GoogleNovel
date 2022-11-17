package life.forever.cf.entry;

import android.os.Parcel;
import android.os.Parcelable;


public class LatestChapter implements Parcelable {

    public int wid;
    public int cid;
    public String title;
    public int updatetime;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.wid);
        dest.writeInt(this.cid);
        dest.writeString(this.title);
        dest.writeInt(this.updatetime);
    }

    public LatestChapter() {
    }

    protected LatestChapter(Parcel in) {
        this.wid = in.readInt();
        this.cid = in.readInt();
        this.title = in.readString();
        this.updatetime = in.readInt();
    }

    public static final Parcelable.Creator<LatestChapter> CREATOR = new Parcelable.Creator<LatestChapter>() {
        @Override
        public LatestChapter createFromParcel(Parcel source) {
            return new LatestChapter(source);
        }

        @Override
        public LatestChapter[] newArray(int size) {
            return new LatestChapter[size];
        }
    };
}
