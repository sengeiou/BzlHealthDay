package com.bozlun.healthday.android.bi8i.b18isupport;


import com.bozlun.healthday.android.bi8i.b18ibean.Axis;

/**
 * 描述：
 * </br>
 */
public interface Chart {

    void setAxisX(Axis axisX);

    void setAxisY(Axis axisY);

    Axis getAxisX();

    Axis getAxisY();
}
