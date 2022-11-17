package life.forever.cf.weight;

import static life.forever.cf.publics.Constant.ZERO;

import android.graphics.Rect;
import android.view.MotionEvent;

import life.forever.cf.activtiy.LogUtils;
import life.forever.cf.activtiy.ScreenUtils;

import java.util.List;

public class PageTouch {

    public boolean touchInStatusBtns(MotionEvent event, boolean isScrollFlag, Rect scollerRect, Rect preRect,
                                     List<TxtPage> mPrePageList,
                                     List<TxtPage> mCurPageList,
                                     List<TxtPage> mNextPageList,
                                     TxtPage mCurPage,
                                     int mDrawTopBottomMargin)
    {
        TxtPage prePage = null;
        if (mPrePageList != null && mPrePageList.size() > 0) {
            prePage = mPrePageList.get(mPrePageList.size() - 1);
        }

        TxtPage mTempCurPage = null;
        if (mCurPageList != null && mCurPageList.size() > 0) {
            mTempCurPage = mCurPageList.get(mCurPageList.size() - 1);
        }

        TxtPage mNextPage = null;
        if (mNextPageList != null && mNextPageList.size() > 0) {
            mNextPage = mNextPageList.get(0);
        }

        boolean hasPreFlag = false;
        if (prePage != null) {
            hasPreFlag = true;
        }

        boolean hasCurFlag = false;
        if (mTempCurPage != null) {
            hasCurFlag = true;
        }


        boolean hasNextFlag = false;
        if (mNextPage != null) {
            hasNextFlag = true;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (isScrollFlag) {
            boolean isStatusShow = false;

            Rect preFrame = new Rect(ZERO, ZERO, ZERO, ZERO);

            if (hasCurFlag && mCurPage.chapterOrder != mTempCurPage.chapterOrder) {
                scollerRect = preRect;
            }
            int destY = scollerRect.top;

//            int destY = scollerRect.top - scollerRect.height();
//            if(hasPreFlag && hasCurFlag && hasNextFlag)
//            {
//                destY = scollerRect.top - scollerRect.height();
//
//                if(hasCurFlag && mCurPage.chapterOrder != mTempCurPage.chapterOrder)
//                {
//                    destY = scollerRect.top;
//                }
//            }else if(hasCurFlag && hasNextFlag)
//            {
//                if(scollerRect.top <=0)
//                {
//                    destY = scollerRect.top;
//                }else{
//                    destY = scollerRect.top - scollerRect.height();
//                }
//
//                destY = scollerRect.top;
//
//            }else if(hasCurFlag&&!hasNextFlag)
//            {
//                destY = scollerRect.top;
//            }else if(hasNextFlag)
//            {
//                destY = scollerRect.top;
//            }

            destY += mDrawTopBottomMargin * 2;


            if (prePage != null &&
                    (prePage.mLoginFrame != null ||
                            prePage.mPayFrame != null ||
                            prePage.mPayOneMoreFrame != null ||
                            prePage.mReloadFrame != null)) {
//                destY = scollerRect.top - scollerRect.height();

                if (prePage.mLoginFrame != null) {
                    preFrame = new Rect(prePage.mLoginFrame.getX(),
                            prePage.mLoginFrame.getY() + destY,
                            prePage.mLoginFrame.getX() + prePage.mLoginFrame.getWidth(),
                            prePage.mLoginFrame.getY() + destY + prePage.mLoginFrame.getHeight());
                }

                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                if (isStatusShow == false) {
                    if (prePage.mPayFrame != null) {
                        preFrame = new Rect(prePage.mPayFrame.getX(),
                                prePage.mPayFrame.getY() + destY,
                                prePage.mPayFrame.getX() + prePage.mPayFrame.getWidth(),
                                prePage.mPayFrame.getY() + destY + prePage.mPayFrame.getHeight());
                    }

                    isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                    if (isStatusShow == false) {
                        if (prePage.mPayOneMoreFrame != null) {
                            preFrame = new Rect(prePage.mPayOneMoreFrame.getX(),
                                    prePage.mPayOneMoreFrame.getY() + destY,
                                    prePage.mPayOneMoreFrame.getX() + prePage.mPayOneMoreFrame.getWidth(),
                                    prePage.mPayOneMoreFrame.getY() + destY + prePage.mPayOneMoreFrame.getHeight());


                        }
                        isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                        if (isStatusShow == false) {
                            if (prePage.mReloadFrame != null) {
                                preFrame = new Rect(prePage.mReloadFrame.getX(),
                                        prePage.mReloadFrame.getY() + destY,
                                        prePage.mReloadFrame.getX() + prePage.mReloadFrame.getWidth(),
                                        prePage.mReloadFrame.getY() + destY + prePage.mReloadFrame.getHeight());
                            }

                            isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);
                        }
                    }
                }

                LogUtils.adD("event ====" + prePage.title + " isStatusShow ==== " + isStatusShow);

                destY += scollerRect.height();
            }

            Rect mCurFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mTempCurPage != null &&
                    (mTempCurPage.mLoginFrame != null ||
                            mTempCurPage.mPayFrame != null ||
                            mTempCurPage.mPayOneMoreFrame != null ||
                            mTempCurPage.mReloadFrame != null)
                    && isStatusShow == false) {
                if (mTempCurPage.mLoginFrame != null) {
                    mCurFrame = new Rect(mTempCurPage.mLoginFrame.getX(),
                            mTempCurPage.mLoginFrame.getY() + destY,
                            mTempCurPage.mLoginFrame.getX() + mTempCurPage.mLoginFrame.getWidth(),
                            mTempCurPage.mLoginFrame.getY() + destY + mTempCurPage.mLoginFrame.getHeight());

                }

                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                if (isStatusShow == false) {
                    if (mTempCurPage.mPayFrame != null) {
                        mCurFrame = new Rect(mTempCurPage.mPayFrame.getX(),
                                mTempCurPage.mPayFrame.getY() + destY,
                                mTempCurPage.mPayFrame.getX() + mTempCurPage.mPayFrame.getWidth(),
                                mTempCurPage.mPayFrame.getY() + destY + mTempCurPage.mPayFrame.getHeight());
                    }

                    isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                    if (isStatusShow == false) {
                        if (mTempCurPage.mPayOneMoreFrame != null) {
                            mCurFrame = new Rect(mTempCurPage.mPayOneMoreFrame.getX(),
                                    mTempCurPage.mPayOneMoreFrame.getY() + destY,
                                    mTempCurPage.mPayOneMoreFrame.getX() + mTempCurPage.mPayOneMoreFrame.getWidth(),
                                    mTempCurPage.mPayOneMoreFrame.getY() + destY + mTempCurPage.mPayOneMoreFrame.getHeight());
                        }
                        isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                        if (isStatusShow == false) {
                            if (mTempCurPage.mReloadFrame != null) {
                                mCurFrame = new Rect(mTempCurPage.mReloadFrame.getX(),
                                        mTempCurPage.mReloadFrame.getY() + destY,
                                        mTempCurPage.mReloadFrame.getX() + mTempCurPage.mReloadFrame.getWidth(),
                                        mTempCurPage.mReloadFrame.getY() + destY + mTempCurPage.mReloadFrame.getHeight());
                            }

                            isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);
                        }
                    }
                }


                LogUtils.adD("event ====" + mTempCurPage.title + " isStatusShow ==== " + isStatusShow);

                destY += scollerRect.height();
            }

            Rect mNextFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mNextPage != null &&
                    (mNextPage.mLoginFrame != null ||
                            mNextPage.mPayFrame != null ||
                            mNextPage.mPayOneMoreFrame != null ||
                            mNextPage.mReloadFrame != null)
                    && isStatusShow == false) {
                if (mNextPage.mLoginFrame != null) {
                    mNextFrame = new Rect(mNextPage.mLoginFrame.getX(),
                            mNextPage.mLoginFrame.getY() + destY,
                            mNextPage.mLoginFrame.getX() + mNextPage.mLoginFrame.getWidth(),
                            mNextPage.mLoginFrame.getY() + destY + mNextPage.mLoginFrame.getHeight());
                }


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

                if (isStatusShow == false) {
                    if (mNextPage.mPayFrame != null) {
                        mNextFrame = new Rect(mNextPage.mPayFrame.getX(),
                                mNextPage.mPayFrame.getY() + destY,
                                mNextPage.mPayFrame.getX() + mNextPage.mPayFrame.getWidth(),
                                mNextPage.mPayFrame.getY() + destY + mNextPage.mPayFrame.getHeight());
                    }

                    isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

                    if (isStatusShow == false) {
                        if (mNextPage.mPayOneMoreFrame != null) {
                            mNextFrame = new Rect(mNextPage.mPayOneMoreFrame.getX(),
                                    mNextPage.mPayOneMoreFrame.getY() + destY,
                                    mNextPage.mPayOneMoreFrame.getX() + mNextPage.mPayOneMoreFrame.getWidth(),
                                    mNextPage.mPayOneMoreFrame.getY() + destY + mNextPage.mPayOneMoreFrame.getHeight());
                        }
                        isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

                        if (isStatusShow == false) {
                            if (mNextPage.mReloadFrame != null) {
                                mNextFrame = new Rect(mNextPage.mReloadFrame.getX(),
                                        mNextPage.mReloadFrame.getY() + destY,
                                        mNextPage.mReloadFrame.getX() + mNextPage.mReloadFrame.getWidth(),
                                        mNextPage.mReloadFrame.getY() + destY + mNextPage.mReloadFrame.getHeight());
                            }

                            isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);
                        }
                    }
                }

                LogUtils.adD("event ====" + mNextPage.title + " isStatusShow ==== " + isStatusShow);
            }


            return isStatusShow;
        }


        if (mCurPage.getmPageStatus() != null && isScrollFlag == false) {

            boolean isStatusShow = false;
            int destY = 0;

            Rect mCurFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            mTempCurPage = mCurPage;
            if (mTempCurPage != null &&
                    (mTempCurPage.mLoginFrame != null ||
                            mTempCurPage.mPayFrame != null ||
                            mTempCurPage.mPayOneMoreFrame != null ||
                            mTempCurPage.mReloadFrame != null)
                    && isStatusShow == false) {
                if (mTempCurPage.mLoginFrame != null) {
                    mCurFrame = new Rect(mTempCurPage.mLoginFrame.getX(),
                            mTempCurPage.mLoginFrame.getY() + destY,
                            mTempCurPage.mLoginFrame.getX() + mTempCurPage.mLoginFrame.getWidth(),
                            mTempCurPage.mLoginFrame.getY() + destY + mTempCurPage.mLoginFrame.getHeight());

                }

                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                if (isStatusShow == false) {
                    if (mTempCurPage.mPayFrame != null) {
                        mCurFrame = new Rect(mTempCurPage.mPayFrame.getX(),
                                mTempCurPage.mPayFrame.getY() + destY,
                                mTempCurPage.mPayFrame.getX() + mTempCurPage.mPayFrame.getWidth(),
                                mTempCurPage.mPayFrame.getY() + destY + mTempCurPage.mPayFrame.getHeight());
                    }

                    isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                    if (isStatusShow == false) {
                        if (mTempCurPage.mPayOneMoreFrame != null) {
                            mCurFrame = new Rect(mTempCurPage.mPayOneMoreFrame.getX(),
                                    mTempCurPage.mPayOneMoreFrame.getY() + destY,
                                    mTempCurPage.mPayOneMoreFrame.getX() + mTempCurPage.mPayOneMoreFrame.getWidth(),
                                    mTempCurPage.mPayOneMoreFrame.getY() + destY + mTempCurPage.mPayOneMoreFrame.getHeight());
                        }
                        isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                        if (isStatusShow == false) {
                            if (mTempCurPage.mReloadFrame != null) {
                                mCurFrame = new Rect(mTempCurPage.mReloadFrame.getX(),
                                        mTempCurPage.mReloadFrame.getY() + destY,
                                        mTempCurPage.mReloadFrame.getX() + mTempCurPage.mReloadFrame.getWidth(),
                                        mTempCurPage.mReloadFrame.getY() + destY + mTempCurPage.mReloadFrame.getHeight());
                            }

                            isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);
                        }
                    }
                }


                LogUtils.adD("event ====" + mTempCurPage.title + " isStatusShow ==== " + isStatusShow);
            }

            return isStatusShow;
        }

        return  false;
    }

    public boolean touchInAutoBuyBtn(MotionEvent event, boolean isScrollFlag, Rect scollerRect, Rect preRect,
                                     List<TxtPage> mPrePageList,
                                     List<TxtPage> mCurPageList,
                                     List<TxtPage> mNextPageList,
                                     TxtPage mCurPage,
                                     int mDrawTopBottomMargin){
        TxtPage prePage = null;
        if (mPrePageList != null && mPrePageList.size() > 0) {
            prePage = mPrePageList.get(mPrePageList.size() - 1);
        }

        TxtPage mTempCurPage = null;
        if (mCurPageList != null && mCurPageList.size() > 0) {
            mTempCurPage = mCurPageList.get(mCurPageList.size() - 1);
        }

        TxtPage mNextPage = null;
        if (mNextPageList != null && mNextPageList.size() > 0) {
            mNextPage = mNextPageList.get(0);
        }

        boolean hasPreFlag = false;
        if (prePage != null) {
            hasPreFlag = true;
        }

        boolean hasCurFlag = false;
        if (mTempCurPage != null) {
            hasCurFlag = true;
        }


        boolean hasNextFlag = false;
        if (mNextPage != null) {
            hasNextFlag = true;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (isScrollFlag) {
            boolean isStatusShow = false;

            Rect preFrame = new Rect(ZERO, ZERO, ZERO, ZERO);

            if (hasCurFlag && mCurPage.chapterOrder != mTempCurPage.chapterOrder) {
                scollerRect = preRect;
            }
            int destY = scollerRect.top;

//            int destY = scollerRect.top - scollerRect.height();
//
//            if (hasPreFlag && hasCurFlag && hasNextFlag) {
//                destY = scollerRect.top - scollerRect.height();
//
//                if(hasCurFlag && mCurPage.chapterOrder != mTempCurPage.chapterOrder)
//                {
//                    destY = scollerRect.top;
//                }
//
//            } else if (hasCurFlag && hasNextFlag) {
//                destY = scollerRect.top - scollerRect.height();
//
//            } else if(hasCurFlag&&!hasNextFlag)
//            {
//                destY = scollerRect.top;
//            }else if (hasNextFlag) {
//                destY = scollerRect.top;
//            }

            destY += mDrawTopBottomMargin * 4;

            if (prePage != null &&
                    (prePage.mAutoPayFrame != null)) {
//                destY = scollerRect.top - scollerRect.height();

                preFrame = new Rect(prePage.mAutoPayFrame.getX(),
                        prePage.mAutoPayFrame.getY() + destY,
                        prePage.mAutoPayFrame.getX() + prePage.mAutoPayFrame.getWidth(),
                        prePage.mAutoPayFrame.getY() + destY + prePage.mAutoPayFrame.getHeight());


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                LogUtils.adD("event ====" + prePage.title + " isStatusShow ==== " + isStatusShow);

                destY += scollerRect.height();
            }

            Rect mCurFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mTempCurPage != null &&
                    (mTempCurPage.mAutoPayFrame != null)
                    && isStatusShow == false) {
                mCurFrame = new Rect(mTempCurPage.mAutoPayFrame.getX(),
                        mTempCurPage.mAutoPayFrame.getY() + destY,
                        mTempCurPage.mAutoPayFrame.getX() + mTempCurPage.mAutoPayFrame.getWidth(),
                        mTempCurPage.mAutoPayFrame.getY() + destY + mTempCurPage.mAutoPayFrame.getHeight());


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);


                LogUtils.adD("event ====" + mTempCurPage.title + " isStatusShow ==== " + isStatusShow);

                destY += scollerRect.height();
            }

            Rect mNextFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mNextPage != null &&
                    (mNextPage.mAutoPayFrame != null)
                    && isStatusShow == false) {
                mNextFrame = new Rect(mNextPage.mAutoPayFrame.getX(),
                        mNextPage.mAutoPayFrame.getY() + destY,
                        mNextPage.mAutoPayFrame.getX() + mNextPage.mAutoPayFrame.getWidth(),
                        mNextPage.mAutoPayFrame.getY() + destY + mNextPage.mAutoPayFrame.getHeight());

                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

            }

            return isStatusShow;
        }


        if (mCurPage.getmPageStatus() != null && isScrollFlag == false) {
            boolean isStatusShow = false;
            int destY = 0;
            Rect mCurFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            mTempCurPage = mCurPage;
            if (mTempCurPage != null &&
                    (mTempCurPage.mAutoPayFrame != null)
                    && isStatusShow == false) {
                mCurFrame = new Rect(mTempCurPage.mAutoPayFrame.getX(),
                        mTempCurPage.mAutoPayFrame.getY() + destY,
                        mTempCurPage.mAutoPayFrame.getX() + mTempCurPage.mAutoPayFrame.getWidth(),
                        mTempCurPage.mAutoPayFrame.getY() + destY + mTempCurPage.mAutoPayFrame.getHeight());


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);


                LogUtils.adD("event ====" + mTempCurPage.title + " isStatusShow ==== " + isStatusShow);
                return isStatusShow;
            }
        }

        return false;
    }




    public TxtPage getPageTouchInStatusBtns(MotionEvent event, boolean isScrollFlag, Rect scollerRect, Rect preRect,
                                     List<TxtPage> mPrePageList,
                                     List<TxtPage> mCurPageList,
                                     List<TxtPage> mNextPageList,
                                     TxtPage mCurPage,
                                     int mDrawTopBottomMargin)
    {
        TxtPage prePage = null;
        if (mPrePageList != null && mPrePageList.size() > 0) {
            prePage = mPrePageList.get(mPrePageList.size() - 1);
            prePage.touchType = 0;
        }

        TxtPage mTempCurPage = null;
        if (mCurPageList != null && mCurPageList.size() > 0) {
            mTempCurPage = mCurPageList.get(mCurPageList.size() - 1);
            mTempCurPage.touchType = 0;
        }

        TxtPage mNextPage = null;
        if (mNextPageList != null && mNextPageList.size() > 0) {
            mNextPage = mNextPageList.get(0);
            mNextPage.touchType = 0;
        }

        boolean hasPreFlag = false;
        if (prePage != null) {
            hasPreFlag = true;
        }

        boolean hasCurFlag = false;
        if (mTempCurPage != null) {
            hasCurFlag = true;
        }


        boolean hasNextFlag = false;
        if (mNextPage != null) {
            hasNextFlag = true;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (isScrollFlag) {
            boolean isStatusShow = false;

            Rect preFrame = new Rect(ZERO, ZERO, ZERO, ZERO);

            if (hasCurFlag && mCurPage.chapterOrder != mTempCurPage.chapterOrder) {
                scollerRect = preRect;
            }
            int destY = scollerRect.top;

//            int destY = scollerRect.top - scollerRect.height();
//
//            if(hasPreFlag && hasCurFlag && hasNextFlag)
//            {
//                destY = scollerRect.top - scollerRect.height();
//
//                if(hasCurFlag && mCurPage.chapterOrder != mTempCurPage.chapterOrder)
//                {
//                    destY = scollerRect.top;
//                }
//
//            }else if(hasCurFlag && hasNextFlag)
//            {
////                destY = scollerRect.top - scollerRect.height();
//                if(scollerRect.top <=0)
//                {
//                    destY = scollerRect.top;
//                }else{
//                    destY = scollerRect.top - scollerRect.height();
//                }
//                destY = scollerRect.top;
//
//            }else if(hasCurFlag&&!hasNextFlag)
//            {
//                destY = scollerRect.top;
//            }else if(hasNextFlag)
//            {
//                destY = scollerRect.top;
//            }

            destY += mDrawTopBottomMargin * 2;


            int touchType = -1;

            if (prePage != null &&
                    (prePage.mLoginFrame != null ||
                            prePage.mPayFrame != null ||
                            prePage.mPayOneMoreFrame != null ||
                            prePage.mReloadFrame != null)) {
//                destY = scollerRect.top - scollerRect.height();

                if (prePage.mLoginFrame != null) {
                    preFrame = new Rect(prePage.mLoginFrame.getX(),
                            prePage.mLoginFrame.getY() + destY,
                            prePage.mLoginFrame.getX() + prePage.mLoginFrame.getWidth(),
                            prePage.mLoginFrame.getY() + destY + prePage.mLoginFrame.getHeight());

                }


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                LogUtils.adD("event ====" + prePage.title + " isStatusShow ==== " + isStatusShow);

                if (isStatusShow) {
                    touchType = 1;
                }

                if (isStatusShow == false) {
                    if (prePage.mPayFrame != null) {
                        preFrame = new Rect(prePage.mPayFrame.getX(),
                                prePage.mPayFrame.getY() + destY,
                                prePage.mPayFrame.getX() + prePage.mPayFrame.getWidth(),
                                prePage.mPayFrame.getY() + destY + prePage.mPayFrame.getHeight());
                    }

                    isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                    if (isStatusShow) {
                        touchType = 2;
                    }


                    if (isStatusShow == false) {
                        if (prePage.mPayOneMoreFrame != null) {
                            preFrame = new Rect(prePage.mPayOneMoreFrame.getX(),
                                    prePage.mPayOneMoreFrame.getY() + destY,
                                    prePage.mPayOneMoreFrame.getX() + prePage.mPayOneMoreFrame.getWidth(),
                                    prePage.mPayOneMoreFrame.getY() + destY + prePage.mPayOneMoreFrame.getHeight());


                        }
                        isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                        if (isStatusShow) {
                            touchType = 3;
                        }

                        if (isStatusShow == false) {
                            if (prePage.mReloadFrame != null) {
                                preFrame = new Rect(prePage.mReloadFrame.getX(),
                                        prePage.mReloadFrame.getY() + destY,
                                        prePage.mReloadFrame.getX() + prePage.mReloadFrame.getWidth(),
                                        prePage.mReloadFrame.getY() + destY + prePage.mReloadFrame.getHeight());
                            }

                            isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                            if (isStatusShow) {
                                touchType = 4;
                            }
                        }
                    }
                }


                if (isStatusShow) {
                    prePage.touchType = touchType;
                    return prePage;
                }

                destY += scollerRect.height();
            }

            Rect mCurFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mTempCurPage != null &&
                    (mTempCurPage.mLoginFrame != null ||
                            mTempCurPage.mPayFrame != null ||
                            mTempCurPage.mPayOneMoreFrame != null ||
                            mTempCurPage.mReloadFrame != null)
                    && isStatusShow == false) {
                if (mTempCurPage.mLoginFrame != null) {
                    mCurFrame = new Rect(mTempCurPage.mLoginFrame.getX(),
                            mTempCurPage.mLoginFrame.getY() + destY,
                            mTempCurPage.mLoginFrame.getX() + mTempCurPage.mLoginFrame.getWidth(),
                            mTempCurPage.mLoginFrame.getY() + destY + mTempCurPage.mLoginFrame.getHeight());

                }


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                LogUtils.adD("event ====" + mTempCurPage.title + " isStatusShow ==== " + isStatusShow);


                if (isStatusShow) {
                    touchType = 1;
                }

                if (isStatusShow == false) {
                    if (mTempCurPage.mPayFrame != null) {
                        mCurFrame = new Rect(mTempCurPage.mPayFrame.getX(),
                                mTempCurPage.mPayFrame.getY() + destY,
                                mTempCurPage.mPayFrame.getX() + mTempCurPage.mPayFrame.getWidth(),
                                mTempCurPage.mPayFrame.getY() + destY + mTempCurPage.mPayFrame.getHeight());
                    }

                    isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                    if (isStatusShow) {
                        touchType = 2;
                    }


                    if (isStatusShow == false) {
                        if (mTempCurPage.mPayOneMoreFrame != null) {
                            mCurFrame = new Rect(mTempCurPage.mPayOneMoreFrame.getX(),
                                    mTempCurPage.mPayOneMoreFrame.getY() + destY,
                                    mTempCurPage.mPayOneMoreFrame.getX() + mTempCurPage.mPayOneMoreFrame.getWidth(),
                                    mTempCurPage.mPayOneMoreFrame.getY() + destY + mTempCurPage.mPayOneMoreFrame.getHeight());
                        }
                        isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                        if (isStatusShow) {
                            touchType = 3;
                        }

                        if (isStatusShow == false) {
                            if (mTempCurPage.mReloadFrame != null) {
                                mCurFrame = new Rect(mTempCurPage.mReloadFrame.getX(),
                                        mTempCurPage.mReloadFrame.getY() + destY,
                                        mTempCurPage.mReloadFrame.getX() + mTempCurPage.mReloadFrame.getWidth(),
                                        mTempCurPage.mReloadFrame.getY() + destY + mTempCurPage.mReloadFrame.getHeight());
                            }

                            isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                            if (isStatusShow) {
                                touchType = 4;
                            }
                        }
                    }
                }

                if (isStatusShow) {
                    mTempCurPage.touchType = touchType;
                    return mTempCurPage;
                }


                destY += scollerRect.height();


            }

            Rect mNextFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mNextPage != null &&
                    (mNextPage.mLoginFrame != null ||
                            mNextPage.mPayFrame != null ||
                            mNextPage.mPayOneMoreFrame != null ||
                            mNextPage.mReloadFrame != null)
                    && isStatusShow == false) {
                if (mNextPage.mLoginFrame != null) {
                    mNextFrame = new Rect(mNextPage.mLoginFrame.getX(),
                            mNextPage.mLoginFrame.getY() + destY,
                            mNextPage.mLoginFrame.getX() + mNextPage.mLoginFrame.getWidth(),
                            mNextPage.mLoginFrame.getY() + destY + mNextPage.mLoginFrame.getHeight());

                }
                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

                if (isStatusShow) {
                    touchType = 1;
                }


                if (isStatusShow == false) {
                    if (mNextPage.mPayFrame != null) {
                        mNextFrame = new Rect(mNextPage.mPayFrame.getX(),
                                mNextPage.mPayFrame.getY() + destY,
                                mNextPage.mPayFrame.getX() + mNextPage.mPayFrame.getWidth(),
                                mNextPage.mPayFrame.getY() + destY + mNextPage.mPayFrame.getHeight());
                    }

                    isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

                    if (isStatusShow) {
                        touchType = 2;
                    }

                    if (isStatusShow == false) {
                        if (mNextPage.mPayOneMoreFrame != null) {
                            mNextFrame = new Rect(mNextPage.mPayOneMoreFrame.getX(),
                                    mNextPage.mPayOneMoreFrame.getY() + destY,
                                    mNextPage.mPayOneMoreFrame.getX() + mNextPage.mPayOneMoreFrame.getWidth(),
                                    mNextPage.mPayOneMoreFrame.getY() + destY + mNextPage.mPayOneMoreFrame.getHeight());
                        }
                        isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

                        if (isStatusShow) {
                            touchType = 3;
                        }

                        if (isStatusShow == false) {
                            if (mNextPage.mReloadFrame != null) {
                                mNextFrame = new Rect(mNextPage.mReloadFrame.getX(),
                                        mNextPage.mReloadFrame.getY() + destY,
                                        mNextPage.mReloadFrame.getX() + mNextPage.mReloadFrame.getWidth(),
                                        mNextPage.mReloadFrame.getY() + destY + mNextPage.mReloadFrame.getHeight());
                            }

                            isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

                            if (isStatusShow) {
                                touchType = 4;
                            }
                        }
                    }
                }


                if (isStatusShow) {
                    mNextPage.touchType = touchType;
                    return mNextPage;
                }


                LogUtils.adD("event ====" + mNextPage.title + " isStatusShow ==== " + isStatusShow);


            }
        }


        if (mCurPage.getmPageStatus() != null && isScrollFlag == false) {

            boolean isStatusShow = false;
            int destY = 0;
            int touchType = -1;
            mTempCurPage = mCurPage;

            Rect mCurFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mTempCurPage != null &&
                    (mTempCurPage.mLoginFrame != null ||
                            mTempCurPage.mPayFrame != null ||
                            mTempCurPage.mPayOneMoreFrame != null ||
                            mTempCurPage.mReloadFrame != null)
                    && isStatusShow == false) {
                if (mTempCurPage.mLoginFrame != null) {
                    mCurFrame = new Rect(mTempCurPage.mLoginFrame.getX(),
                            mTempCurPage.mLoginFrame.getY() + destY,
                            mTempCurPage.mLoginFrame.getX() + mTempCurPage.mLoginFrame.getWidth(),
                            mTempCurPage.mLoginFrame.getY() + destY + mTempCurPage.mLoginFrame.getHeight());

                }


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                LogUtils.adD("event ====" + mTempCurPage.title + " isStatusShow ==== " + isStatusShow);


                if (isStatusShow) {
                    touchType = 1;
                }

                if (isStatusShow == false) {
                    if (mTempCurPage.mPayFrame != null) {
                        mCurFrame = new Rect(mTempCurPage.mPayFrame.getX(),
                                mTempCurPage.mPayFrame.getY() + destY,
                                mTempCurPage.mPayFrame.getX() + mTempCurPage.mPayFrame.getWidth(),
                                mTempCurPage.mPayFrame.getY() + destY + mTempCurPage.mPayFrame.getHeight());
                    }

                    isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                    if (isStatusShow) {
                        if (mTempCurPage.getmPageStatus().getMode() == ChapterPageStatusInfo.PageStatusMode.LACK_BALANCE) {
                            touchType = 5;
                        } else {

                        }

                        touchType = 2;

                    }


                    if (isStatusShow == false) {
                        if (mTempCurPage.mPayOneMoreFrame != null) {
                            mCurFrame = new Rect(mTempCurPage.mPayOneMoreFrame.getX(),
                                    mTempCurPage.mPayOneMoreFrame.getY() + destY,
                                    mTempCurPage.mPayOneMoreFrame.getX() + mTempCurPage.mPayOneMoreFrame.getWidth(),
                                    mTempCurPage.mPayOneMoreFrame.getY() + destY + mTempCurPage.mPayOneMoreFrame.getHeight());
                        }
                        isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                        if (isStatusShow) {
                            touchType = 3;
                        }

                        if (isStatusShow == false) {
                            if (mTempCurPage.mReloadFrame != null) {
                                mCurFrame = new Rect(mTempCurPage.mReloadFrame.getX(),
                                        mTempCurPage.mReloadFrame.getY() + destY,
                                        mTempCurPage.mReloadFrame.getX() + mTempCurPage.mReloadFrame.getWidth(),
                                        mTempCurPage.mReloadFrame.getY() + destY + mTempCurPage.mReloadFrame.getHeight());
                            }

                            isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                            if (isStatusShow) {
                                touchType = 4;
                            }
                        }
                    }
                }

                if (isStatusShow) {
                    mTempCurPage.touchType = touchType;
                    return mTempCurPage;
                }
            }
        }

        return mCurPage;
    }


    public TxtPage getPageTouchInAutoBuyBtn(MotionEvent event, boolean isScrollFlag, Rect scollerRect, Rect preRect,
                                     List<TxtPage> mPrePageList,
                                     List<TxtPage> mCurPageList,
                                     List<TxtPage> mNextPageList,
                                     TxtPage mCurPage,
                                     int mDrawTopBottomMargin) {

        TxtPage prePage = null;
        if (mPrePageList != null && mPrePageList.size() > 0) {
            prePage = mPrePageList.get(mPrePageList.size() - 1);
            prePage.touchType = 0;
        }

        TxtPage mTempCurPage = null;
        if (mCurPageList != null && mCurPageList.size() > 0) {
            mTempCurPage = mCurPageList.get(mCurPageList.size() - 1);
            mTempCurPage.touchType = 0;
        }

        TxtPage mNextPage = null;
        if (mNextPageList != null && mNextPageList.size() > 0) {
            mNextPage = mNextPageList.get(0);
            mNextPage.touchType = 0;
        }

        boolean hasPreFlag = false;
        if (prePage != null) {
            hasPreFlag = true;
        }

        boolean hasCurFlag = false;
        if (mTempCurPage != null) {
            hasCurFlag = true;
        }


        boolean hasNextFlag = false;
        if (mNextPage != null) {
            hasNextFlag = true;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (isScrollFlag) {
            boolean isStatusShow = false;

            Rect preFrame = new Rect(ZERO, ZERO, ZERO, ZERO);

            if (hasCurFlag && mCurPage.chapterOrder != mTempCurPage.chapterOrder) {
                scollerRect = preRect;
            }
            int destY = scollerRect.top;

//            int destY = scollerRect.top - scollerRect.height();
//
//            if(hasPreFlag && hasCurFlag && hasNextFlag)
//            {
//                destY = scollerRect.top - scollerRect.height();
//
//                if(hasCurFlag && mCurPage.chapterOrder != mTempCurPage.chapterOrder)
//                {
//                    destY = scollerRect.top;
//                }
//
//            }else if(hasCurFlag && hasNextFlag)
//            {
//                destY = scollerRect.top - scollerRect.height();
//
//            }else if(hasCurFlag&&!hasNextFlag)
//            {
//                destY = scollerRect.top;
//            }else if(hasNextFlag)
//            {
//                destY = scollerRect.top;
//            }

            destY += mDrawTopBottomMargin * 4;


            int touchType = -1;

            if (prePage != null &&
                    (prePage.mAutoPayFrame != null)) {
//                destY = scollerRect.top - scollerRect.height();

                preFrame = new Rect(prePage.mAutoPayFrame.getX(),
                        prePage.mAutoPayFrame.getY() + destY,
                        prePage.mAutoPayFrame.getX() + prePage.mAutoPayFrame.getWidth(),
                        prePage.mAutoPayFrame.getY() + destY + prePage.mAutoPayFrame.getHeight());


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, preFrame);

                LogUtils.adD("event ====" + prePage.title + " isStatusShow ==== " + isStatusShow);

                if (isStatusShow) {
                    touchType = 5;
                }

                if (isStatusShow) {
                    prePage.touchType = touchType;
                    return prePage;
                }

                destY += scollerRect.height();
            }

            Rect mCurFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mTempCurPage != null &&
                    (mTempCurPage.mAutoPayFrame != null)
                    && isStatusShow == false) {
                mCurFrame = new Rect(mTempCurPage.mAutoPayFrame.getX(),
                        mTempCurPage.mAutoPayFrame.getY() + destY,
                        mTempCurPage.mAutoPayFrame.getX() + mTempCurPage.mAutoPayFrame.getWidth(),
                        mTempCurPage.mAutoPayFrame.getY() + destY + mTempCurPage.mAutoPayFrame.getHeight());


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                LogUtils.adD("event ====" + mTempCurPage.title + " isStatusShow ==== " + isStatusShow);


                if (isStatusShow) {
                    touchType = 5;
                }


                if (isStatusShow) {
                    mTempCurPage.touchType = touchType;
                    return mTempCurPage;
                }

                destY += scollerRect.height();


            }

            Rect mNextFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            if (mNextPage != null &&
                    (mNextPage.mAutoPayFrame != null)
                    && isStatusShow == false) {
                mNextFrame = new Rect(mNextPage.mAutoPayFrame.getX(),
                        mNextPage.mAutoPayFrame.getY() + destY,
                        mNextPage.mAutoPayFrame.getX() + mNextPage.mAutoPayFrame.getWidth(),
                        mNextPage.mAutoPayFrame.getY() + destY + mNextPage.mAutoPayFrame.getHeight());


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mNextFrame);

                if (isStatusShow) {
                    touchType = 5;
                }

                if (isStatusShow) {
                    mNextPage.touchType = touchType;
                    return mNextPage;
                }

                LogUtils.adD("event ====" + mNextPage.title + " isStatusShow ==== " + isStatusShow);
            }
        }

        if (mCurPage.getmPageStatus() != null && isScrollFlag == false) {
            boolean isStatusShow = false;
            int destY = 0;
            int touchType = -1;
            Rect mCurFrame = new Rect(ZERO, ZERO, ZERO, ZERO);
            mTempCurPage = mCurPage;
            if (mTempCurPage != null &&
                    (mTempCurPage.mAutoPayFrame != null)
                    && isStatusShow == false) {
                mCurFrame = new Rect(mTempCurPage.mAutoPayFrame.getX(),
                        mTempCurPage.mAutoPayFrame.getY() + destY,
                        mTempCurPage.mAutoPayFrame.getX() + mTempCurPage.mAutoPayFrame.getWidth(),
                        mTempCurPage.mAutoPayFrame.getY() + destY + mTempCurPage.mAutoPayFrame.getHeight());


                isStatusShow = ScreenUtils.isPointInRectFrame(x, y, mCurFrame);

                LogUtils.adD("event ====" + mTempCurPage.title + " isStatusShow ==== " + isStatusShow);


                if (isStatusShow) {
                    touchType = 5;
                }


                if (isStatusShow) {
                    mTempCurPage.touchType = touchType;
                    return mTempCurPage;
                }
            }
        }

        return mCurPage;
    }

}
