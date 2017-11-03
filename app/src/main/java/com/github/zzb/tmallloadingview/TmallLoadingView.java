package com.github.zzb.tmallloadingview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/11/3.
 */

public class TmallLoadingView extends View {

    private static String TAG = TmallLoadingView.class.getSimpleName();

    // 画笔
    private Paint mPaint;

    // View 宽高
    private int mViewWidth;
    private int mViewHeight;

    private Path mPath;
    private Path mDstPath;

    // 测量Path 并截取部分的工具
    private PathMeasure mMeasure;

    // 动画数值(用于控制动画状态,因为同一时间内只允许有一种状态出现,具体数值处理取决于当前状态)
    private float mAnimatorValue = 0;
    // 控制各个过程的动画
    private ValueAnimator mAnimator;

    private float mPathLength;

    private int distance = dp2px(10);

    private PointF p1, p2, p3, p4, p5, c1, c2, c3, c4;

    private float mPaintWidth = dp2px(1);
    private float cellSize = dp2px(3);
    //cellSize

    private int DEFAULT_WIDTH =  (int) (12 * cellSize);
    private int DEFAULT_HEIGHT = (int) (8 * cellSize);

    public TmallLoadingView(Context context) {
        this(context, null);
    }

    public TmallLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TmallLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAll();
    }

    private void initAll() {
        initPaint();

        initPath();

        initEarsPath();

        initListener();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(mPaintWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);//圆形线帽
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setAntiAlias(true);

        //关闭当前view的硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void initPath() {
        mPath = new Path();
        mDstPath = new Path();
        mMeasure = new PathMeasure();

        p1 = new PointF(0, 0);
        p2 = new PointF(0, 0);
        p3 = new PointF(0, 0);
        p4 = new PointF(0, 0);
        p5 = new PointF(0, 0);
        c1 = new PointF(0, 0);
        c2 = new PointF(0, 0);
        c3 = new PointF(0, 0);
        c4 = new PointF(0, 0);

    }

    private void initListener() {

        mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        mAnimator.setDuration(2000);//一段path的时间
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        //加入线性插值器 计算返回的值均匀递增 如若不用 会出现卡顿现象
        mAnimator.setInterpolator(new LinearInterpolator());

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(DEFAULT_WIDTH, widthMeasureSpec);
        int height = measureDimension(DEFAULT_HEIGHT, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = (int) Math.min(defaultSize + 2 * mPaintWidth, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    private void initEarsPath() {

        p1.x = cellSize ;
        p1.y = 0;
        p2.x = 5 * cellSize ;
        p2.y = -2 * cellSize;
        p3.x = 6 * cellSize;
        p3.y = 0;
        p4.x = 6 * cellSize ;
        p4.y = 5 * cellSize ;
        p5.x = 5 * cellSize ;
        p5.y = 6 * cellSize ;
        c1.x = 3 * p1.x ;
        c1.y = 0;
        c2.x = 4 *cellSize ;
        c2.y = -2 * cellSize ;
        c3.x = 6 * cellSize;
        c3.y = -2 * cellSize ;
        c4.x = 6 * cellSize ;
        c4.y = 6 * cellSize ;
        mPath.moveTo(p1.x, p1.y);
        //三阶
        mPath.cubicTo(c1.x, c1.y, c2.x,c2.y, p2.x, p2.y);
        //二阶
        mPath.quadTo(c3.x, c3.y, p3.x, p3.y);
        //右侧直线
        mPath.lineTo(p4.x, p4.y);
        //右下角圆弧
        mPath.quadTo(c4.x, c4.y, p5.x, p5.y);

        //取反
        p1.x = -p1.x;
        p2.x = -p2.x;
        p3.x = -p3.x;
        p4.x = -p4.x;
        p5.x = -p5.x;
        c1.x = -c1.x;
        c2.x = -c2.x;
        c3.x = -c3.x;
        c4.x = -c4.x;

        mPath.lineTo(p5.x, p5.y);
        //左下角圆弧
        mPath.quadTo(c4.x, c4.y, p4.x, p4.y);
        //左侧直线
        mPath.lineTo(p3.x, p3.y);
        //二阶
        mPath.quadTo(c3.x, c3.y, p2.x, p2.y);
        //三阶
        mPath.cubicTo(c2.x, c2.y, c1.x,c1.y, p1.x, p1.y);

        mPath.close();

        mMeasure.setPath(mPath, false);
        mPathLength = mMeasure.getLength();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mViewWidth / 2, mViewHeight / 2 - 2 * cellSize);

        mDstPath.reset();
        if (mPathLength * mAnimatorValue + distance <= mPathLength) {
            mMeasure.getSegment(0, mPathLength * mAnimatorValue, mDstPath, true);
            mMeasure.getSegment(mPathLength * mAnimatorValue + distance, mPathLength, mDstPath, true);
        } else {
            mMeasure.getSegment(distance - mPathLength * (1 - mAnimatorValue), mPathLength * mAnimatorValue, mDstPath, true);
        }

        canvas.drawPath(mDstPath, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        mAnimator.end();
        super.onDetachedFromWindow();
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }
}
