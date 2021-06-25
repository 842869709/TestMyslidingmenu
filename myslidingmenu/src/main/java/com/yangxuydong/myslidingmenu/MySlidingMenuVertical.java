package com.yangxuydong.myslidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 创建时间：2020/7/13
 * 编写人：czy_yangxudong
 * 功能描述：自定义view 上下滑动菜单布局
 */
public class MySlidingMenuVertical extends ViewGroup {

    private LinearLayout bottom;
    private LinearLayout top;
    private int topHeight;
    //private int rightHeight;
    private int downY;
    private Scroller scroller;
    private LinearLayout.LayoutParams params;

    //用include填充进来布局是需要时间的
    public MySlidingMenuVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //当布局完全填充进来的时候回调
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        top = (LinearLayout) getChildAt(0);
        bottom = (LinearLayout)getChildAt(1);

        topHeight = top.getLayoutParams().height;

        params = (LinearLayout.LayoutParams) top.getChildAt(0).getLayoutParams();

        scroller = new Scroller(getContext());
    }

    //排版
    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        top.layout(i, i1, i2, i3);
        bottom.layout(i, topHeight, i2, i3+topHeight);
    }

    //在ViewGroup中布局是没有测量的，需要调用测量方法，否则会match_parent尽可能铺满屏幕
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        top.measure(widthMeasureSpec, heightMeasureSpec);
        bottom.measure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) event.getY();
                int dY = moveY - downY;

                //Log.i("test","dx="+dX);

                //防止滚动越界
                //此方法获取的是上次滚动到的y坐标
                int scrollY = getScrollY();

                //此值表示的是下次将要滚动到的x坐标  scrollY是个负数 dy是正数
                int y = scrollY - dY;

                if (y < topHeight && y >= 0) {
                    //相对运动  这个方法移动的是手机屏幕 而不是屏幕内的布局 所以向右滑布局会向左动 所以要用-dY
                    scrollBy(0, -dY);
                }

                //Log.i("test","p="+1.0f*Math.abs(getScrollX())/leftWith);
                //Log.i("test","leftWith="+leftWith);
                //Log.i("test","getX()="+Math.abs(event.getX()));
                //Log.i("test","getScrollX()="+Math.abs(getScrollX()));
                upPrecent(1.0f*Math.abs(getScrollY())/topHeight*100);

                downY = moveY;
                break;
            case MotionEvent.ACTION_UP:
               //此处逻辑也可以实现闭合和展开，但是太生硬
               //获取抬手后滚动到的x坐标  负值
               /* int upScrollY=getScrollY();
                Log.i("test","upScrollY="+upScrollY);
                if (upScrollY>topHeight/2){
                    scrollTo(0,topHeight);
                    Log.i("test","闭合");
                }else{
                    scrollTo(0,0);
                    Log.i("test","展开");
                }*/

                //获取抬手后滚动到的y坐标  负值
                int upScrollY = getScrollY();
                Log.i("test","upScrollY="+upScrollY);
                //展开或者闭合 需要滚动的y的距离
                int dScrolly;

                if (upScrollY > topHeight / 2) {
                    //闭合
                    dScrolly = topHeight - upScrollY;
                    isOpen = false;
                } else {
                    //展开
                    dScrolly = -upScrollY;
                    isOpen = true;
                }
                scroller.startScroll(0, upScrollY, 0, dScrolly);
                invalidate();

                break;
            default:
                break;
        }

        return true;
    }

    private boolean isOpen = true;

    //Scroller 需要配合此方法使用
    @Override
    public void computeScroll() {
        super.computeScroll();
        //获取新的滚动位置，如果返回值为true表示还没有滚动结束
        //该方法每调用一次就会获取一个新的位置
        if (scroller.computeScrollOffset()) {
            //获取 期望位置
            int currY= scroller.getCurrY();
            //Log.i("test","currX="+currX);
            //Log.i("test","p2="+1.0f*Math.abs(getScrollX())/leftWith);
            upPrecent(1.0f*Math.abs(getScrollY())/topHeight*100);

            //滚动到期望位置
            scrollTo(0, currY);

            //重绘  为了在还没滚动到最终位置前持续调用本方法
            invalidate();

            if (Math.abs(getScrollY())==topHeight){
                upState(false);
            }else if (Math.abs(getScrollX())==0){
                upState(true);
            }

        }
    }


    public void setOpen() {
        if (isOpen) {
            //闭合
            scroller.startScroll(0, topHeight, 0, 0);
            isOpen = false;
        } else {
            //展开
            scroller.startScroll(0, 0, 0, 0);
            isOpen = true;
        }
        invalidate();
        upState(isOpen);
    }


    /**
     * 得到屏幕高度
     *
     * @return
     */
    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }




    private OnStateChangeListening onStateChangeListening;
    public interface OnStateChangeListening{
        void OnStateChange(boolean isExpand);
        void OnScroll(int precent);
    }
    public void setOnStateChangeListening(OnStateChangeListening onStateChangeListening) {
        this.onStateChangeListening = onStateChangeListening;
    }



    private boolean currentState=true;
    private void upState(Boolean isExpand){
        if (isExpand==currentState){
            return;
        }
        if (onStateChangeListening!=null){
            onStateChangeListening.OnStateChange(isExpand);
        }
        currentState=isExpand;
    }

    private float currentPrencent=0;
    private void upPrecent(float precent){
        if ((int)precent==currentPrencent){
            return;
        }
        if (onStateChangeListening!=null){
            onStateChangeListening.OnScroll((int)precent);
        }
        currentPrencent=(int)precent;
    }

}
