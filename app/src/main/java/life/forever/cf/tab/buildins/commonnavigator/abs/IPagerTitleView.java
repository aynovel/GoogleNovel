package life.forever.cf.tab.buildins.commonnavigator.abs;

public interface IPagerTitleView {

    void onSelected(int index, int totalCount);


    void onDeselected(int index, int totalCount);


    void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight);


    void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight);
}
