package life.forever.cf.entry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Comment implements Parcelable {


    public int id;

    public int type;

    public int floor;


    public int pid;


    public int puid;

    public int is_ban_user;

    public int is_author_comment;

    public int is_author_user;

    public int relateId;


    public int toUid;


    public String toName;

    public int contentType;

    public String title;


    public String content;


    public int replyCount;

    public int likeCount;

    public int addtime;

    public List<Comment> replays = new ArrayList<>();

    public int isLike;

    public int status;
    public int isLong;
    public int isFine;
    public int isTop;
    public int isBanUser;
    public int topTime;
    public int pread;
    public int isRead;
    public int lastReplyTime;
    public int lowCount;


    public int uid;


    public String head;


    public String nickname;


    public int level;

    public int fansLevel;


    public String fansName;


    public int wid;


    public int cid;


    public int score;

    public String from;


    public int fromType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeInt(this.floor);
        dest.writeInt(this.pid);
        dest.writeInt(this.puid);
        dest.writeInt(this.relateId);
        dest.writeInt(this.toUid);
        dest.writeString(this.toName);
        dest.writeInt(this.is_author_comment);
        dest.writeInt(this.is_author_user);

        dest.writeInt(this.contentType);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeInt(this.replyCount);
        dest.writeInt(this.likeCount);
        dest.writeInt(this.addtime);
        dest.writeList(this.replays);
        dest.writeInt(this.isLike);
        dest.writeInt(this.status);
        dest.writeInt(this.isLong);
        dest.writeInt(this.isFine);
        dest.writeInt(this.isTop);
        dest.writeInt(this.topTime);
        dest.writeInt(this.pread);
        dest.writeInt(this.isRead);
        dest.writeInt(this.lastReplyTime);
        dest.writeInt(this.lowCount);
        dest.writeInt(this.uid);
        dest.writeString(this.head);
        dest.writeString(this.nickname);
        dest.writeInt(this.level);
        dest.writeInt(this.fansLevel);
        dest.writeInt(this.wid);
        dest.writeInt(this.cid);
        dest.writeInt(this.score);
        dest.writeString(this.from);
        dest.writeInt(this.fromType);
    }

    public Comment() {
    }

    protected Comment(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.floor = in.readInt();
        this.pid = in.readInt();
        this.puid = in.readInt();
        this.relateId = in.readInt();
        this.toUid = in.readInt();
        this.toName = in.readString();
        this.is_author_comment = in.readInt();
        this.is_author_user = in.readInt();
        this.contentType = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.replyCount = in.readInt();
        this.likeCount = in.readInt();
        this.addtime = in.readInt();
        this.replays = new ArrayList<Comment>();
        in.readList(this.replays, Comment.class.getClassLoader());
        this.isLike = in.readInt();
        this.status = in.readInt();
        this.isLong = in.readInt();
        this.isFine = in.readInt();
        this.isTop = in.readInt();
        this.isBanUser = in.readInt();
        this.topTime = in.readInt();
        this.pread = in.readInt();
        this.isRead = in.readInt();
        this.lastReplyTime = in.readInt();
        this.lowCount = in.readInt();
        this.uid = in.readInt();
        this.head = in.readString();
        this.nickname = in.readString();
        this.level = in.readInt();
        this.fansLevel = in.readInt();
        this.wid = in.readInt();
        this.cid = in.readInt();
        this.score = in.readInt();
        this.from = in.readString();
        this.fromType = in.readInt();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        return obj != null
                && obj instanceof Comment
                && ((Comment) obj).wid == wid
                && ((Comment) obj).id == id;
    }
}
