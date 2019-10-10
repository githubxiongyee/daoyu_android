package com.daoyu.chat.module.mine.bean;

/**
 * *****************************************************
 *
 * @Description： 用于记录二维码在背景图片的位置信息
 * *****************************************************
 */
public class DrawCodeBean {
    private int size; //二维码大小
    private int x;     //二维码左上角X点坐标
    private int y;      //二维码左上角Y点坐标
    private int angle;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

}
