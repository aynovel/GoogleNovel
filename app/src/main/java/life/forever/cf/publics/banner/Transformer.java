package life.forever.cf.publics.banner;

import androidx.viewpager.widget.ViewPager.PageTransformer;


import life.forever.cf.publics.banner.transformer.DepthPageTransformer;
import life.forever.cf.publics.banner.transformer.ScaleTransformer;
import life.forever.cf.publics.banner.transformer.TabletTransformer;


public class Transformer {


    public static Class<? extends PageTransformer> DepthPage = DepthPageTransformer.class;
    public static Class<? extends PageTransformer> Scale = ScaleTransformer.class;
    public static Class<? extends PageTransformer> Tablet = TabletTransformer.class;

}
