package life.forever.cf.entry;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class AutoPayBookBean {

    @Id
    public String wid;

    public boolean isAutoPay;

    public int addTime;

    @Generated(hash = 2112328762)
    public AutoPayBookBean(String wid, boolean isAutoPay, int addTime) {
        this.wid = wid;
        this.isAutoPay = isAutoPay;
        this.addTime = addTime;
    }

    @Generated(hash = 997573015)
    public AutoPayBookBean() {
    }

    public String getWid() {
        return this.wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public boolean getIsAutoPay() {
        return this.isAutoPay;
    }

    public void setIsAutoPay(boolean isAutoPay) {
        this.isAutoPay = isAutoPay;
    }

    public int getAddTime() {
        return this.addTime;
    }

    public void setAddTime(int addTime) {
        this.addTime = addTime;
    }



}
