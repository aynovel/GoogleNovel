package life.forever.cf.entry;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class BookMultiDownConfigBean {
    @Id
    public String wid;

    public boolean isCanDown;

    public int addTime;

    @Generated(hash = 211084829)
    public BookMultiDownConfigBean(String wid, boolean isCanDown, int addTime) {
        this.wid = wid;
        this.isCanDown = isCanDown;
        this.addTime = addTime;
    }

    @Generated(hash = 1757419950)
    public BookMultiDownConfigBean() {
    }

    public String getWid() {
        return this.wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public boolean getIsCanDown() {
        return this.isCanDown;
    }

    public void setIsCanDown(boolean isCanDown) {
        this.isCanDown = isCanDown;
    }

    public int getAddTime() {
        return this.addTime;
    }

    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }
}
