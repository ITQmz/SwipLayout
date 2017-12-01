package com.qmz.swiplayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 15757 on 2017/11/30.
 */

public class SwipLayout extends FrameLayout {
    private List<ViewPager> viewPagerList = new ArrayList<>();
    private float downX;
    private float downY;
    private int touchSlop;
    private int dircetion=RIGHT;


    public SwipLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SwipLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mscroller=new Scroller(getContext());
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private Activity mactivity;

    private Scroller mscroller;
    public void attachToActivity(Activity activity) {

        ViewGroup decoview = (ViewGroup) activity.getWindow().getDecorView();
        View deciChild = decoview.getChildAt(0);
        decoview.removeView(deciChild);
        addView(deciChild);
        decoview.addView(this);

        this.mactivity = activity;


    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed){
            findAllViewPagers(this);
        }
    }

    private static final String TAG = "SwipLayout";
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //如果触碰在viewpager的非第一页上面，不截获事件
        ViewPager viewPager=viewPagerOfTouching(ev);
        if(viewPager!=null&&viewPager.getCurrentItem()!=0){
            return super.onInterceptTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY=ev.getY();
                return super.onInterceptTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:
                float offsetX = ev.getX() - downX;
                //如果向右滑，滑动距离大于最小滑动距离，拦截事件
                if(offsetX>touchSlop){
                    return true;
                }

        }

        return super.onInterceptTouchEvent(ev);


    }

    private void smoothScrollerTo(int destX){
        int scrollX=getScrollX();
        int deltaX=-destX-scrollX;
        mscroller.startScroll(scrollX,0,deltaX,0);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if(mscroller.computeScrollOffset()){
            scrollTo(mscroller.getCurrX(),mscroller.getCurrY());
            postInvalidate();
        }else{
            if(finshActivity) {
                mactivity.finish();
            }

        }
    }

    private boolean finshActivity;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //这里方法调用由两种情况导致的
        // 1.自身拦截
        // 2.子view无法消费事件
        //但无论哪种情况，都需要在这里消费事件
            switch (event.getAction()){
                case MotionEvent.ACTION_MOVE:
                    float offsetX=event.getX()-downX;
                    Log.d(TAG, "onTouchEvent: offsetX"+offsetX);
                    if(offsetX>=0){
                      smoothScrollerTo((int) offsetX);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if(Math.abs(getScrollX())>=getWidth()/2){
                        smoothScrollerTo(getWidth());
                        finshActivity=true;
                    }else{
                        Log.d(TAG, "onTouchEvent: ==========++++++");
                        smoothScrollerTo(0);

                    }

            }
            return true;




    }

    /**
     * 触摸点是否落在viewpager上面
     *
     * @param ev
     * @return
     */
    private ViewPager viewPagerOfTouching(MotionEvent ev) {
        Rect rect = new Rect();
        for (ViewPager viewPager : viewPagerList) {
            viewPager.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                return viewPager;
            }
        }
        return null;
    }

    private void findAllViewPagers(ViewGroup swipLayout) {
        int childCount = swipLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = swipLayout.getChildAt(i);
            if (view instanceof ViewGroup) {
                findAllViewPagers((ViewGroup) view);
            } else {
                if (view instanceof ViewPager) {
                    viewPagerList.add((ViewPager) view);
                }
            }
        }
    }
    public static final int LEFT=0;
    public static final int RIGHT=1;
    public static final int TOP=2;
    public static final int BOTTOM=3;
    public void setDirecTion(int dircetion){
        this.dircetion=dircetion;
    }

}
