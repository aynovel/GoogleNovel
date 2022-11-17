package life.forever.cf.interfaces;


public interface IPagerNavigator {

    ///////////////////////// ViewPager的3个回调
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onAttachToMagicIndicator();

    void onDetachFromMagicIndicator();

    void notifyDataSetChanged();
}
