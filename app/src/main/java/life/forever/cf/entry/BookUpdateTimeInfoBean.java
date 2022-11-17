package life.forever.cf.entry;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class BookUpdateTimeInfoBean {
    @Id
    public String wid;

    public int counts;

    public int update_time;

    @Generated(hash = 1002426960)
    public BookUpdateTimeInfoBean(String wid, int counts, int update_time) {
        this.wid = wid;
        this.counts = counts;
        this.update_time = update_time;
    }

    @Generated(hash = 707193653)
    public BookUpdateTimeInfoBean() {
    }

    public String getWid() {
        return this.wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public int getCounts() {
        return this.counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    public int getUpdate_time() {
        return this.update_time;
    }

    public void setUpdate_time(int update_time) {
        this.update_time = update_time;
    }
}
