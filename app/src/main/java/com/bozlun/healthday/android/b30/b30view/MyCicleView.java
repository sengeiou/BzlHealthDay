package com.bozlun.healthday.android.b30.b30view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;


public class MyCicleView extends View {
    private Paint paint;
    private int now = 0; //当前进度
    private int max = 100;//最大进度
    private int rundwidth = 25;//圆弧宽度
    private int measuredWidth;
    private int prossColor = Color.parseColor("#F3C9D4");
    private String textShow = "空";
    private boolean isHeart = true;

    public MyCicleView(Context context) {
        this(context, null);
    }

    public MyCicleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCicleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        initView();
    }


    private ForceStopListenter forceStopListenter;

    public void setForceStopListenter(ForceStopListenter forceStopListenter) {
        this.forceStopListenter = forceStopListenter;
    }

    public interface ForceStopListenter{
        void forcesStop();
    }

    private void initView() {
        paint = new Paint();//创建笔
//        this.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                now++;
//                if (now == 101) {
//                    now = 0;
//                }
//                invalidate();//强制重绘
//            }
//        });
    }


    public void startTestAction() {
        if (handler != null && (now == 0 || now == 100)) {
            textShow = "空";
            handler.sendEmptyMessage(0x01);
        }
    }

    /**
     * 停止时设置当前的进度
     *
     * @param textShow
     */
    public void stopTestAction(String textShow) {
        if (handler != null) handler.removeMessages(0x01);
        now = 0;
        setTextShow(textShow);
        invalidate();//强制重绘
    }

    void setTextShow(String textShow) {
        this.textShow = textShow;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x01:
                    now++;
                    invalidate();//强制重绘
                    if (now == 100) {
                        now = 0;
                        handler.sendEmptyMessage(0x02);
                    } else {
                        textShow = "空";
                        handler.sendEmptyMessageDelayed(0x01, 1000);
                    }
                    break;
                case 0x02:
                    forceStopListenter.forcesStop();
                    stopTestAction("0");
                    break;
            }
            return false;
        }
    });

    public void setProssColor(int prossColor) {
        this.prossColor = prossColor;
    }

    public void setHeart(boolean heart) {
        isHeart = heart;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //MeasureSpec.
        measuredWidth = getMeasuredWidth();//测量当前画布大小
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.STROKE);//设置为空心圆
        paint.setStrokeWidth(rundwidth);
        paint.setColor(Color.parseColor("#F0F2F5"));
        float x = measuredWidth / 2;
        float y = measuredWidth / 2;
        int rd = measuredWidth / 2 - rundwidth / 2;
        canvas.drawCircle(x, y, rd, paint);
        //绘制圆弧
        RectF rectF = new RectF(rundwidth / 2, rundwidth / 2, measuredWidth - rundwidth / 2, measuredWidth - rundwidth / 2);
        paint.setColor(prossColor);
        canvas.drawArc(rectF, 0, now * 360 / max, false, paint);


        paint.setStrokeWidth(0);

        Rect rect = new Rect();
        paint.setColor(Color.BLACK);

        //设置当前文字
        if ((textShow.equals("空"))) {
            textShow = now * 100 / max + "%";


            paint.setTextSize(90);
            paint.getTextBounds(textShow, 0, textShow.length(), rect);
            canvas.drawText(textShow, measuredWidth / 2 - rect.width() / 2, measuredWidth / 2 + rect.height() / 2, paint);
        } else {

            if (isHeart){
                paint.setTextSize(90);
                paint.getTextBounds(textShow, 0, textShow.length(), rect);
                canvas.drawText(textShow, measuredWidth / 2 - rect.width() / 2, measuredWidth / 2 + rect.height() / 2, paint);

                paint.setTextSize(50);
                //paint.getTextBounds("BPM", 0, textShow.length(), rect);
                canvas.drawText("BPM", (measuredWidth / 2 + rect.width() / 2)+5 , measuredWidth / 2 + rect.height() / 2, paint);
            }else {
                paint.setTextSize(90);
                paint.getTextBounds(textShow, 0, textShow.length(), rect);
                canvas.drawText(textShow, measuredWidth / 2 - rect.width() / 2, measuredWidth / 2 + rect.height() / 2, paint);
            }

        }


    }


}
