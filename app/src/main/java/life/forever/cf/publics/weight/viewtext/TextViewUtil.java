package life.forever.cf.publics.weight.viewtext;

import static java.util.regex.Pattern.compile;

import android.graphics.Paint;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.widget.TextView;

import life.forever.cf.publics.tool.LOG;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextViewUtil {

    private final static CharSequence ELLIPSIZE = "...";

    public static CharSequence ellipsize(CharSequence text, TextView textView, int maxLines) {
        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textView.getTextSize());

        int width = textView.getWidth();
        int paddingLeft = textView.getPaddingLeft();
        int paddingRight = textView.getPaddingRight();
        int visibleWidth = width - paddingLeft - paddingRight;

        LOG.i("TextViewUtil", "visibleWidth = " + visibleWidth);
        if (visibleWidth == 0) {
            return text;
        }
        float totalWidth = paint.measureText(text, 0, text.length());
        if (totalWidth <= visibleWidth * maxLines) {
            return text;
        } else {
            int size = paint.breakText(text, 0, text.length(), true, visibleWidth * maxLines, null);
            LOG.i("TextViewUtil", "size = " + size);
            return new SpannableStringBuilder(text.subSequence(0, size - 2)).append(ELLIPSIZE);
        }
    }

    /**
     * 替换评论内容中得span标签
     *
     * @param text
     * @return
     */
    public static CharSequence replaceSpan(String text) {
        String replacement = "<font 'color='#e82217'>【%s】</font>";
        Pattern pattern = compile("<span[^>]*>([^<]*)</span>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            text = text.replace(matcher.group(), String.format(Locale.getDefault(), replacement, matcher.group(1)));
        }

        pattern = compile("&lt;span[^>]*&gt;([^<]*)&lt;/span&gt;");
        matcher = pattern.matcher(text);
        while (matcher.find()) {
            text = text.replace(matcher.group(), String.format(Locale.getDefault(), replacement, matcher.group(1)));
        }

        return Html.fromHtml(text);
    }

}
