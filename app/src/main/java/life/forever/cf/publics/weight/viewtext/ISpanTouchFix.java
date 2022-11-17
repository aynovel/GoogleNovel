package life.forever.cf.publics.weight.viewtext;



public interface ISpanTouchFix {

    /**
     * 记录当前 Touch 事件对应的点是不是点在了 span 上面
     */
    void setTouchSpanHit(boolean hit);
}
