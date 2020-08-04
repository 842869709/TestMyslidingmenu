package com.yangxuydong.myslidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * 创建时间：2020/7/13
 * 编写人：czy_yangxudong
 * 功能描述：自定义view 侧滑菜单布局
 */
public class MySlidingMenu extends ViewGroup {

    private View left;
    private LinearLayout right;
    private int leftWith;
    //private int rightHeight;
    private int downX;
    private Scroller scroller;
    private LinearLayout.LayoutParams params;

    //用include填充进来布局是需要时间的
    public MySlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //当布局完全填充进来的时候回调
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        right = (LinearLayout) getChildAt(0);
        left = getChildAt(1);

        leftWith = left.getLayoutParams().width;
        //rightHeight = right.getLayoutParams().height;

        //Log.i("test", "leftWith=" + leftWith);
        //Log.i("test","rightHeight="+rightHeight);
        //Log.i("test", "rightHeight=" + getScreenHeight());

        params = (LinearLayout.LayoutParams) right.getChildAt(0).getLayoutParams();

        scroller = new Scroller(getContext());
    }

    //排版
    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        right.layout(i, i1, i2, i3);
        left.layout(-leftWith, i1, 0, i3);
    }

    //在ViewGroup中布局是没有测量的，需要调用测量方法，否则会match_parent尽可能铺满屏幕
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        right.measure(widthMeasureSpec, heightMeasureSpec);
        left.measure(widthMeasureSpec, heightMeasureSpec);
    }

    //用于设置右边布局距离顶部的距离
    private int marginTop = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();

                //控制右边布局上下滑动的逻辑
                if (!isOpen) {
                    marginTop = 0;
                } else {
                    marginTop = leftWith;
                }

                Log.i("test", "down marginTop=" + marginTop);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int dX = moveX - downX;

                //Log.i("test","dx="+dX);

                //防止滚动越界
                //此方法获取的是上次滚动到的x坐标
                int scrollX = getScrollX();

                //此值表示的是下次将要滚动到的x坐标  scrollX是个负数 dx是正数
                int x = scrollX - dX;

                if (x >= -leftWith && x <= 0) {
                    //相对运动  这个方法移动的是手机屏幕 而不是屏幕内的布局 所以向右滑布局会向左动 所以要用-dx
                    scrollBy(-dX, 0);
                    //right.scrollBy(0,-dX/2);

                    //控制右边布局上下滑动的逻辑
                    if (!isOpen) {
                        marginTop += dX;
                        //Log.i("test", "marginTop+=" + marginTop);
                    } else {
                        marginTop -= Math.abs(dX);
                        //Log.i("test", "marginTop-=" + marginTop);
                    }
                    setMargin(marginTop / 2);
                }

                //Log.i("test","p="+1.0f*Math.abs(getScrollX())/leftWith);
                //Log.i("test","leftWith="+leftWith);
                //Log.i("test","getX()="+Math.abs(event.getX()));
                //Log.i("test","getScrollX()="+Math.abs(getScrollX()));
                upPrecent(1.0f*Math.abs(getScrollX())/leftWith*100);

                downX = moveX;
                break;
            case MotionEvent.ACTION_UP:
               /*
               *此处逻辑也可以实现闭合和展开，但是太生硬
               //获取抬手后滚动到的x坐标  负值
                int upScrollX=getScrollX();
                Log.i("test","upScrollX="+upScrollX);
                if (upScrollX<-leftWith/2){
                    scrollTo(-leftWith,0);
                    Log.i("test","闭合");
                }else{
                    scrollTo(0,0);
                    Log.i("test","展开");
                }*/

                //获取抬手后滚动到的x坐标  负值
                int upScrollX = getScrollX();
                //Log.i("test","upScrollX="+upScrollX);
                //展开或者闭合 需要滚动的x的距离
                int dScrollx;

                if (upScrollX > -leftWith / 2) {
                    //闭合
                    dScrollx = -upScrollX;
                    isOpen = false;
                } else {
                    //展开
                    dScrollx = -leftWith - upScrollX;
                    isOpen = true;
                }
                scroller.startScroll(upScrollX, 0, dScrollx, 0);
                //scroller.startScroll(upScrollX,0,dScrollx,0,2000);
                invalidate();

                break;
            default:
                break;
        }

        return true;
    }

    private boolean isOpen = false;

    //Scroller 需要配合此方法使用
    @Override
    public void computeScroll() {
        super.computeScroll();
        //获取新的滚动位置，如果返回值为true表示还没有滚动结束
        //该方法每调用一次就会获取一个新的位置
        if (scroller.computeScrollOffset()) {
            //获取 期望位置
            int currX = scroller.getCurrX();
            //Log.i("test","currX="+currX);
            //Log.i("test","p2="+1.0f*Math.abs(getScrollX())/leftWith);
            upPrecent(1.0f*Math.abs(getScrollX())/leftWith*100);

            //滚动到期望位置
            scrollTo(currX, 0);

            //控制右边布局上下滑动的逻辑
            //right.scrollTo(0,currX/2);
            setMargin(Math.abs(currX) / 2);

            //重绘  为了在还没滚动到最终位置前持续调用本方法
            invalidate();

            if (Math.abs(getScrollX())==leftWith){
                upState(true);
            }else if (Math.abs(getScrollX())==0){
                upState(false);
            }

        }
    }


    public void setOpen() {
        if (isOpen) {
            //闭合
            scroller.startScroll(-leftWith, 0, leftWith, 0);
            isOpen = false;
        } else {
            //展开
            scroller.startScroll(0, 0, -leftWith, 0);
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


    //设置右边布局margin top
    private void setMargin(int top) {
        if (isRightDown){
            params.setMargins(0, top, 0, 0);
            right.setLayoutParams(params);
        }
    }


    //控制右边布局是否可以上下滑动
    private boolean isRightDown=true;
    public void setRightDown(boolean b){
        isRightDown=b;
    }


    private OnStateChangeListening onStateChangeListening;
    public interface OnStateChangeListening{
        void OnStateChange(boolean isExpand);
        void OnScroll(int precent);
    }
    public void setOnStateChangeListening(OnStateChangeListening onStateChangeListening) {
        this.onStateChangeListening = onStateChangeListening;
    }



    private boolean currentState=false;
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
