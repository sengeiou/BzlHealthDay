package com.bozlun.healthday.android.bi8i.b18irenderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.bozlun.healthday.android.bi8i.b18ibean.Axis;
import com.bozlun.healthday.android.bi8i.b18ibean.SlidingLine;
import com.bozlun.healthday.android.bi8i.b18isupport.LeafUtil;


/**
 * 描述：
 * </br>
 */

public class B18ISlideSelectLineRendererLeafLineRenderer extends B18ILeafLineRenderer {
    /**移动标尺线**/
    private Paint slidePaint;

    public B18ISlideSelectLineRendererLeafLineRenderer(Context context, View view) {
        super(context, view);
    }

    @Override
    protected void initPaint() {
        super.initPaint();
        slidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    /**
     * 竖直滑动标尺线
     * @param canvas
     */
    public void drawSlideLine(Canvas canvas, Axis axisX, SlidingLine slidingLine, float moveX, float moveY) {
        slidePaint.setStrokeWidth(LeafUtil.dp2px(mContext, 1));
        slidePaint.setColor(slidingLine.getSlideLineColor());
        if(slidingLine.isDash()){
            float dash = LeafUtil.dp2px(mContext, 2);
            slidePaint.setPathEffect(new DashPathEffect(new float[]{dash, dash, dash, dash}, 0));
        }
        Path path = new Path();
        path.moveTo(moveX, moveY);
        path.lineTo(moveX, axisX.getStartY());
        canvas.drawPath(path, slidePaint);

        slidePaint.setPathEffect(null);
        slidePaint.setStyle(Paint.Style.FILL);
        slidePaint.setColor(Color.WHITE);
        float slidePointRadius = slidingLine.getSlidePointRadius();
        canvas.drawCircle(moveX, moveY, LeafUtil.dp2px(mContext, slidePointRadius) , slidePaint);
        slidePaint.setStyle(Paint.Style.STROKE);
        slidePaint.setStrokeWidth(LeafUtil.dp2px(mContext, 2));
        slidePaint.setColor(slidingLine.getSlidePointColor());
        canvas.drawCircle(moveX, moveY, LeafUtil.dp2px(mContext, slidePointRadius) , slidePaint);
        if(slidingLine.getSlidePointColor() != 0){
            slidePaint.setAlpha(100);
            canvas.drawCircle(moveX, moveY, LeafUtil.dp2px(mContext, slidePointRadius + 2) , slidePaint);
        }
    }

}
