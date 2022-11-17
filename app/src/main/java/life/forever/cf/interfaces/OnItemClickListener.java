package life.forever.cf.interfaces;


import life.forever.cf.entry.Image;

public interface OnItemClickListener {

    int onCheckedClick(int position, Image image);

    void onImageClick(int position, Image image);
}
