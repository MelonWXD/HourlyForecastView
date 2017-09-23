package com.dongua.hourlyforecastview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;


import com.dongua.hourlyforecastview.R;
import com.dongua.hourlyforecastview.Utils;
import com.dongua.hourlyforecastview.bean.HourlyWeatherBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dongua on 17-9-7.
 */

public class HourlyForecastView extends View implements ScrollWatcher {


    private static final String TAG = "HourlyForecastView";

    private Context mContext;


    //折线
    private Paint foldLinePaint;
    //底线
    private Paint baseLinePaint;
    //虚线
    private Paint dashPaint;
    //文字
    private Paint textPaint;
    //圆
    private Paint circlePaint;
    //图片
    private Paint bitmapPaint;

    //文本的大小
    private int textSize;

    //数据
    private List<HourlyWeatherBean> hourlyWeatherList;

    //画虚线的点的index
    private List<Integer> dashLineWidth;


    private int screenWidth;
    private int screenHeight;
    //每个item的宽度
    private int itemWidth;
    //温度基准高度
    private int lowestTempHeight;
    //温度基准高度
    private int highestTempHeight;
    //最低温
    private int lowestTemp;


    //最高温
    private int highestTemp;


    //默认图片绘制位置
    float bitmapHeight;
    //默认图片宽高
    float bitmapXY;


    //View宽高
    private int mWidth;
    private int mHeight;
    //默认高
    private int defHeightPixel = 0;
    private int defWidthPixel = 0;

    //默认圆宽度
    private int defRadius;

    private int paddingL = 0;
    private int paddingT = 0;
    private int paddingR = 0;
    private int paddingB = 0;

    private int mScrollX = 0;


    public HourlyForecastView(Context context) {
        super(context);
        init(context);
    }

    public HourlyForecastView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public HourlyForecastView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public HourlyForecastView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);

    }


    private void init(Context context) {
        mContext = context;

//        initData();
//        initDefValue();
    }


    public void initData(List<HourlyWeatherBean> weatherData) {

        hourlyWeatherList = weatherData;

        dashLineWidth = new ArrayList<>();
        Iterator iterator = hourlyWeatherList.iterator();
        HourlyWeatherBean tmp;
        String lastText = "";
        int idx = 0;
        while (iterator.hasNext()) {
            tmp = (HourlyWeatherBean) iterator.next();
            if (!tmp.getText().equals(lastText)) {
                dashLineWidth.add(idx);//从0开始添加虚线位置的索引值idx
                lastText = tmp.getText();
            }
            idx++;
        }
        dashLineWidth.add(hourlyWeatherList.size() - 1);//添加最后一条虚线位置的索引值idx

        initDefValue();
        initPaint();

    }


    private void initDefValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        itemWidth = Utils.dp2px(mContext, 70);

        defWidthPixel = itemWidth * (hourlyWeatherList.size() - 1);
        defHeightPixel = Utils.dp2px(mContext, 150);

        defRadius = 8;
        lowestTempHeight = Utils.dp2px(mContext, 100);//长度  非y轴值
        highestTempHeight = Utils.dp2px(mContext, 140);
        //defPadding
        paddingT = Utils.dp2px(mContext, 30);
        paddingL = Utils.dp2px(mContext, 30);
        paddingR = Utils.dp2px(mContext, 30);

        textSize = Utils.sp2px(mContext, 16);


        bitmapHeight = 1 / 2f * (2*defHeightPixel - lowestTempHeight) - textSize/2;//- 给文字留地方
        bitmapXY = 32;//32dp

    }

    private void initPaint() {
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速

        foldLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foldLinePaint.setStyle(Paint.Style.STROKE);
        foldLinePaint.setStrokeWidth(6);
        foldLinePaint.setColor(getResources().getColor(R.color.dodgerblue));

        dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setColor(getResources().getColor(R.color.gray));
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeWidth(2);
        DashPathEffect pathEffect = new DashPathEffect(new float[]{8, 8}, 1);
        dashPaint.setPathEffect(pathEffect);

        circlePaint = new Paint();
        circlePaint.setStrokeWidth(3);
        circlePaint.setColor(getResources().getColor(R.color.dodgerblue));
        circlePaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.slategray));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(textSize);


        baseLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        baseLinePaint.setStrokeWidth(6);
        baseLinePaint.setStyle(Paint.Style.STROKE);
        baseLinePaint.setColor(getResources().getColor(R.color.slategray));


        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setFilterBitmap(true);//图像滤波处理
        bitmapPaint.setDither(true);//防抖动
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //当设置的padding值小于默认值是设置为默认值
        paddingL = Math.max(paddingL, getPaddingLeft());
        paddingT = Math.max(paddingT, getPaddingTop());
        paddingR = Math.max(paddingR, getPaddingRight());
        paddingB = Math.max(paddingB, getPaddingBottom());

        //获取测量模式
        //注意 HorizontalScrollView的子View 在没有明确指定dp值的情况下 widthMode总是MeasureSpec.UNSPECIFIED
        //同理 ScrollView的子View的heightMode
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //获取测量大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize + paddingL + paddingR;
            mHeight = heightSize;
        }

        //如果为wrap_content 那么View大小为默认值
        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.AT_MOST) {
            mWidth = defWidthPixel + paddingL + paddingR;
            mHeight = defHeightPixel + paddingT + paddingB;
        }

        //设置视图的大小
        setMeasuredDimension(mWidth, mHeight);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hourlyWeatherList.size() != 0) {
            drawLines(canvas);
            drawBitmaps(canvas);
        }

    }

    private void drawBitmaps(Canvas canvas) {

        int scrollX = mScrollX;
        Log.i(TAG, "drawBitmaps 滑动距离: " + scrollX);
        boolean leftHide;
        boolean rightHide;
        for (int i = 0; i < dashLineWidth.size() - 1; i++) {
            leftHide = true;
            rightHide = true;

            int left = itemWidth * dashLineWidth.get(i) + paddingL;
            int right = itemWidth * dashLineWidth.get(i + 1) + paddingL;
            float drawPoint = 0;//图的中间位置  drawBitmap是左边开始画
            if (left > scrollX && left < scrollX + screenWidth) {
                leftHide = false;//左边缘显示
            }
            if (right > scrollX && right < scrollX + screenWidth) {
                rightHide = false;
            }


            if (!leftHide && !rightHide) {//左右边缘都显示
                drawPoint = (left + right) / 2f;

            } else if (leftHide && !rightHide) {//右边缘与屏幕左边

                drawPoint = (scrollX + right) / 2f;
            } else if (!leftHide) {//左边缘与屏幕右边
                //rightHide is True when reach this statement
                drawPoint = (left + screenWidth + scrollX) / 2f;

            } else {//左右边缘都不显示
                if (right < scrollX + screenWidth) { //左右边缘都在屏幕左边
                    continue;
                } else if (left > scrollX + screenWidth) {//左右边缘都在屏幕右边
                    continue;
                } else {
                    drawPoint = (screenWidth) / 2f + scrollX;
                }
            }


            int code = hourlyWeatherList.get(dashLineWidth.get(i)).getIntCode();
            Bitmap bitmap = getBitmapByCode(code);

            //越界判断
            if (drawPoint >= right - bitmap.getWidth() / 2f) {
                drawPoint = right - bitmap.getWidth() / 2f;
            }
            if (drawPoint <= left + bitmap.getWidth() / 2f) {
                drawPoint = left + bitmap.getWidth() / 2f;
            }

            drawBitmap(canvas, bitmap, drawPoint, bitmapHeight);
            String text = hourlyWeatherList.get(dashLineWidth.get(i)).getText();
            textPaint.setTextSize(50);
            canvas.drawText(text, drawPoint, bitmapHeight + bitmap.getHeight() + 100 / 3f, textPaint);

        }

    }

    private void drawBitmap(Canvas canvas, Bitmap bitmap, float left, float top) {
        Log.i(TAG, "dddrawBitmaps: left " + left);
        canvas.save();
        canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2, top, bitmapPaint);
        canvas.restore();
    }

    private void drawBitmap(Canvas canvas, int code, float left, float top) {
        Log.i(TAG, "dddrawBitmaps: left " + left);
        Bitmap bitmap = getBitmapByCode(code);
        canvas.save();
        canvas.drawBitmap(bitmap, left - bitmap.getWidth() / 2, top, bitmapPaint);
        canvas.restore();
    }

    private Bitmap getBitmapByCode(int code) {
        BitmapDrawable bd = null;
        switch (code) {
            case 0:
            case 1:
            case 2:
            case 3:
                bd = (BitmapDrawable) getResources().getDrawable(R.drawable.qing);
                break;
            case 4:
            case 5:
            case 6:
            case 7:
                bd = (BitmapDrawable) getResources().getDrawable(R.drawable.duoyun);
                break;
            case 8:
            case 9:
            case 10:
            case 11:
                bd = (BitmapDrawable) getResources().getDrawable(R.drawable.yu);
                break;
            default:
                bd = (BitmapDrawable) getResources().getDrawable(R.drawable.shape);
                break;
        }

        //压缩图片
        Bitmap bitmap = Utils.bitmapResize(bd.getBitmap(), Utils.dp2px(mContext, 32), Utils.dp2px(mContext, 32));

        return bitmap;
    }

    private void drawLines(Canvas canvas) {
        //底部的线的高度 高度为控件高度减去text高度的1.5倍
        float baseLineHeight = mHeight - 1.5f * textSize;
        Path p = new Path();

        for (int i = 0; i < hourlyWeatherList.size(); i++) {
            float temp = Integer.parseInt(hourlyWeatherList.get(i).getTemperature());

            float w = itemWidth * i + paddingL;
            float h = tempHeightPixel(temp) + paddingT;

            if (i == 0) {
                p.moveTo(w, h);

            } else {
                p.lineTo(w, h);
            }


            //画虚线
            if (dashLineWidth.contains(i)) {

                canvas.drawLine(w, h, w, baseLineHeight, dashPaint);
            }

        }

        //画折线
        canvas.drawPath(p, foldLinePaint);
        //画底线
        canvas.drawLine(paddingL, baseLineHeight, mWidth - paddingR, baseLineHeight, baseLinePaint);

        for (int i = 0; i < hourlyWeatherList.size(); i++) {
            float temp = Integer.parseInt(hourlyWeatherList.get(i).getTemperature());

            float w = itemWidth * i + paddingL;
            float h = tempHeightPixel(temp) + paddingT;

            //画大白圆
            circlePaint.setColor(getResources().getColor(R.color.white));
            circlePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(w, h, defRadius + 6, circlePaint);
            //画小蓝圆
            circlePaint.setColor(getResources().getColor(R.color.dodgerblue));
            circlePaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(w, h, defRadius, circlePaint);

            //画温度值  y轴是文本基线 故除2处理
            canvas.drawText(hourlyWeatherList.get(i).getTemperature(), w, h - textSize / 2f, textPaint);
            //画时间
            canvas.drawText(hourlyWeatherList.get(i).getTime(), w, baseLineHeight + textSize, textPaint);
        }

    }

    private void drawCircles(Canvas canvas){

    }


    public float tempHeightPixel(float tmp) {
        float res = ((tmp - lowestTemp) / (highestTemp - lowestTemp)) * (highestTempHeight - lowestTempHeight) + lowestTempHeight;
        return defHeightPixel - res;//y从上到下
    }

    @Override
    public void update(int scrollX) {
        mScrollX = scrollX;
//        invalidate();
    }


    public void setLowestTemp(int lowestTemp) {
        this.lowestTemp = lowestTemp;
    }

    public void setHighestTemp(int highestTemp) {
        this.highestTemp = highestTemp;
    }


}
