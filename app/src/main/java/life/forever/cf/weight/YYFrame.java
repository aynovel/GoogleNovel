package life.forever.cf.weight;


public class YYFrame {
    private int x;
    private int y;
    private int width;
    private int height;

    public static YYFrame YYFrameZero(){
        YYFrame frame = new YYFrame();
        return frame;
    }

    public YYFrame(){
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    public YYFrame(int ax,int ay,int awidth,int aheight){
        x = ax;
        y = ay;
        width = awidth;
        height = aheight;
    }

    public boolean isZeroFrame(){
        if (x == 0 && y == 0 && width == 0 && height == 0){
            return true;
        }
        return false;
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
