package com.bozlun.healthday.android.bi8i.b18ibean;

/**
 * 描述：点
 * </br>
 */
public class PointValue {

    private float x; //占x轴总长度的权重
    private float y;
    private float diffX;
    private float diffY;
    private float originX; // 点坐标
    private float originY;
    private String label;
    private int n;

    private boolean isShowLabel; //是否显示label

    public PointValue() {

    }

    public PointValue(int n) {
        this.n = n;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public PointValue(String label) {
        this.label = label;
    }

    public float getOriginX() {
        return originX;
    }

    public PointValue setOriginX(float originX) {
        this.originX = originX;
        return this;
    }

    public float getOriginY() {
        return originY;
    }

    public PointValue setOriginY(float originY) {
        this.originY = originY;
        return this;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDiffX() {
        return diffX;
    }

    public void setDiffX(float diffX) {
        this.diffX = diffX;
    }

    public float getDiffY() {
        return diffY;
    }

    public void setDiffY(float diffY) {
        this.diffY = diffY;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isShowLabel() {
        return isShowLabel;
    }

    public void setShowLabel(boolean showLabel) {
        isShowLabel = showLabel;
    }
}
