package life.forever.cf.entry;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ParaInPageBean implements Serializable {

    private int paraIndex;
    private float startY;
    private float endY;
    private int lineCount;
    private String textContent;
    private int startCharPos;
    private int endCharPos;

    public int getStartCharPos() {
        return startCharPos;
    }

    public void setStartCharPos(int startCharPos) {
        this.startCharPos = startCharPos;
    }

    public int getEndCharPos() {
        return endCharPos;
    }

    public void setEndCharPos(int endCharPos) {
        this.endCharPos = endCharPos;
    }

    public int getParaIndex() {
        return paraIndex;
    }

    public void setParaIndex(int paraIndex) {
        this.paraIndex = paraIndex;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public void addLineCount() {
        this.lineCount++;
    }

    public String getTextContent() {
        if (StringUtils.isBlank(textContent)) {
            textContent = "";
        }
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
}