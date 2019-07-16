package com.fz.network.demo;

/**
 * 属性对象接口
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2018/12/19 16:56
 */
public interface IAttribute {
    /**
     * 控件上内边距
     *
     * @return
     */
    int getPaddingTop();

    /**
     * 控件左内边距
     *
     * @return
     */
    int getPaddingStart();

    /**
     * 控件右内边距
     *
     * @return
     */
    int getPaddingEnd();

    /**
     * 控件下内边距
     *
     * @return
     */
    int getPaddingBottom();

    /**
     * 文本颜色值，十六进制字符串
     *
     * @return
     */
    String getTextColor();

    /**
     * 控件背景颜色值,十六进制字符串
     *
     * @return
     */
    String getBgColor();

    /**
     * 文本字符串
     *
     * @return
     */
    String getText();

    /**
     * 文本字体大小
     *
     * @return
     */
    int getTextSize();

    /**
     * 文本对齐方式
     *
     * @return
     */
    int getTextAlign();

    /**
     * 倒计时对齐方式
     *
     * @return
     */
    int getCountdownAlign();

    /**
     * 倒计时左内边距
     *
     * @return
     */
    int getCountdownPaddingStart();

    /**
     * 倒计时上内边距
     *
     * @return
     */
    int getCountdownPaddingTop();

    /**
     * 倒计时右内边距
     *
     * @return
     */
    int getCountdownPaddingEnd();

    /**
     * 倒计时下内边距
     *
     * @return
     */
    int getCountdownPaddingBottom();
}
