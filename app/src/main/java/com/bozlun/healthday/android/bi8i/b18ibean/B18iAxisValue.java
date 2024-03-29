package com.bozlun.healthday.android.bi8i.b18ibean;

/**
 * 描述：坐标轴上点的值
 * </br>
 */
public class B18iAxisValue {
    /**刻度值**/
    private String label;

    /**x坐标**/
    private float pointX;

    /**y坐标**/
    private float pointY;

    /**是否显示刻度值**/
    private boolean isShowLabel = true;

    public B18iAxisValue(){

    }

    public B18iAxisValue(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getPointX() {
        return pointX;
    }

    public void setPointX(float pointX) {
        this.pointX = pointX;
    }

    public float getPointY() {
        return pointY;
    }

    public void setPointY(float pointY) {
        this.pointY = pointY;
    }

    public boolean isShowLabel() {
        return isShowLabel;
    }

    public void setShowLabel(boolean showLabel) {
        isShowLabel = showLabel;
    }

    @Override
    public String toString() {
        return "B18iAxisValue{" +
                "label='" + label + '\'' +
                ", pointX=" + pointX +
                ", pointY=" + pointY +
                '}';
    }
}
