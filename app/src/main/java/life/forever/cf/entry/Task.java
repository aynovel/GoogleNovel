package life.forever.cf.entry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 任务类
 *
 * @author haojie
 */
public class Task implements Parcelable {

    public int id;
    public int type;
    public String title;
    public String description;
    public String reward;
    public int giving;
    public int givingType;
    public int experience;

    /**
     * 0：未完成  1：完成未领取   2：完成已领取
     */
    public int status;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.reward);
        dest.writeInt(this.giving);
        dest.writeInt(this.givingType);
        dest.writeInt(this.experience);
        dest.writeInt(this.status);
    }

    public Task() {
    }

    protected Task(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.reward = in.readString();
        this.giving = in.readInt();
        this.givingType = in.readInt();
        this.experience = in.readInt();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel source) {
            return new Task(source);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
