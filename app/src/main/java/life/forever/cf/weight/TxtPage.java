package life.forever.cf.weight;


import life.forever.cf.entry.ParaInPageBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TxtPage {
    int position;
    String title;
    int titleLines; //当前 lines 中为 title 的行数。
    List<String> lines;
    List<LineInfo> lineInfos;
    private List<LineInfo> firstLines;

    private Map<Integer, ParaInPageBean> paraList;

    private int speechingPara = -1;

    private int paraCount = -1;

    private LineInfo mAdLine = null;

    private int adHashCode = 0;



    public TxtPage() {
        lineInfos = new ArrayList<>();
        lines = new ArrayList<>();
    }


//    public ChapterPageStatusInfo findChapterPageStatus(){
//        if(mPageStatus != null)
//        {
//            return mPageStatus;
//        }
//
//        if(showStatusFlag)
//        {
//            mPageStatus = new ChapterPageStatusInfo();
//            return mPageStatus;
//        }
//
//        return null;
//    }

    //生成广告相关信息
    public LineInfo findAdLine() {
        if (mAdLine != null) {
            return mAdLine;
        }
        if (lineInfos != null && lineInfos.size() > 0) {
            for (LineInfo line : lineInfos) {
                if (line.getmLineType() == LineInfo.LineType.LineTypeAdView || line.getmLineType() == LineInfo.LineType.LineTypeAdPage) {
                    mAdLine = line;
                    return line;
                }
            }
        }
        return null;
    }

    public boolean bHaveAd() {
        return findAdLine() != null;
    }

    public LineInfo.LineAdType getAdType() {
        LineInfo adLine = findAdLine();
        if (adLine != null) {
            return adLine.getLineAdType();
        }
        return LineInfo.LineAdType.LineAdTypeNone;
    }


    public YYFrame getAdFrame() {
        LineInfo adLine = findAdLine();
        if (adLine != null) {
            return adLine.getmAdView().getAdFrame();
        }
        return YYFrame.YYFrameZero();
    }

    public int pageCharCount() {
        int wordCount = 0;
        if (lineInfos == null) {
            return 0;
        }
        for (LineInfo line : lineInfos) {
            wordCount += line.getmCharCount();
        }
        return wordCount;
    }

    public List<LineInfo> getStringList() {
        return lineInfos;
    }

    public int getAdHashCode() {
        return adHashCode;
    }

    public void setAdHashCode(int adHashCode) {
        this.adHashCode = adHashCode;
    }

    public String getMarkDesc() {
        if (lineInfos.isEmpty()) {
            return "";
        }

        String desc = lineInfos.get(0).getmLineText();
        if (lineInfos.size() >= 2) {
            desc = desc + lineInfos.get(1).getmLineText();
        }
        return desc;
    }

    public int getTitleLines() {
        return titleLines;
    }

    public void setSpeechingPara(int paraIndex) {
        speechingPara = paraIndex;
    }

    public int getSpeechingPara() {
        return speechingPara;
    }

    public void setSpeechingParaToLastPara() {
        speechingPara = paraCount;
    }

    public void setSpeechingParaToFirstPara() {
        speechingPara = 0;
    }

    public int getPageParaCount() {
        if (paraList == null) {
            return 0;
        }
        return paraList.size();
    }

    public void addLineToLastPara(LineInfo lineInfo,boolean isFirstLineInPage) {

        if (paraList == null) {
            paraList = new HashMap<>();
            paraCount = -1;
        }


        if (lineInfo.getmLineType() == LineInfo.LineType.LineTypeFirstLine || isFirstLineInPage) {
            paraCount++;
            ParaInPageBean paraInPageBean = new ParaInPageBean();
            paraInPageBean.setParaIndex(paraCount);
            paraInPageBean.setLineCount(1);
            String stringContent = paraInPageBean.getTextContent();
            paraInPageBean.setTextContent(stringContent+lineInfo.getmLineText());
            paraInPageBean.setStartCharPos(lineInfo.getmStartPos());
            paraInPageBean.setEndCharPos(lineInfo.getmStartPos()+lineInfo.getmCharCount());
            paraList.put(paraCount, paraInPageBean);
        } else {
            if (paraList.get(paraCount)!=null) {
                String stringContent = paraList.get(paraCount).getTextContent();
                paraList.get(paraCount).setTextContent(stringContent + lineInfo.getmLineText());
                paraList.get(paraCount).addLineCount();
            }else {
                String stringContent = "";
                if (paraCount >= 0 && paraCount < paraList.size()) {
                    paraList.get(paraCount).setTextContent(stringContent + lineInfo.getmLineText());
                    paraList.get(paraCount).addLineCount();
                    paraList.get(paraCount).setStartCharPos(lineInfo.getmStartPos());
                    paraList.get(paraCount).setEndCharPos(lineInfo.getmStartPos()+lineInfo.getmCharCount());
                }
            }
        }
    }

    public ParaInPageBean getPara(int paraIndex) {
        if (paraList == null) {
            paraList = new HashMap<>();
        }
        if (paraList.size() <= paraIndex) {
            ParaInPageBean paraInPageBean = new ParaInPageBean();
            paraInPageBean.setParaIndex(paraIndex);
            paraList.put(paraIndex,paraInPageBean);
        }
        return paraList.get(paraIndex);
    }

    public List<LineInfo> getParaLines(int paraIndex) {
        List<LineInfo> paraLines = new ArrayList<>();
        if (lineInfos == null) {
            return paraLines;
        }
        for (int i = 0; i < lineInfos.size(); i++) {
            if (lineInfos.get(i).getmParaIndex() == paraIndex) {
                paraLines.add(lineInfos.get(i));
            }
        }
        return paraLines;
    }

    public float getCurrentParaStartY(int paraIndex) {
        float currentParaStartY = 0;
        if (speechingPara!=-1&&paraList.get(speechingPara)!=null) {
            currentParaStartY = paraList.get(speechingPara).getStartY();
        }
        return currentParaStartY;

    }


    //页面状态的定义
    private Boolean showStatusFlag  = false;

    public Boolean getShowStatusFlag() {
        return showStatusFlag;
    }

    public void setShowStatusFlag(Boolean showStatusFlag) {
        this.showStatusFlag = showStatusFlag;
    }

    private ChapterPageStatusInfo mPageStatus = null;

    public ChapterPageStatusInfo getmPageStatus() {
        return mPageStatus;
    }

    public void setmPageStatus(ChapterPageStatusInfo mPageStatus) {
        this.mPageStatus = mPageStatus;
    }

    //提前算好  按钮布局位置
    // TODO:1.8.1 2021/9/30 1.8.1提前算好按钮位置
    public YYFrame mLoginFrame;
    public YYFrame mReloadFrame;
    public YYFrame mPayFrame;
    public YYFrame mAutoPayFrame;
    public YYFrame mPayOneMoreFrame;
    public YYFrame mRewardFrame;
    public YYFrame mRecommendFrame;

    public int touchType = 0; //1：登录 2：购买 3：购买更多 4：重新加载 5：自动购买
    public int chapterOrder = -1;
}
