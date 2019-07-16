package com.fz.network.demo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 组件属性
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2018/12/12 16:35
 */
public class Attribute implements IAttribute, Parcelable {
    /**
     * width : 0      子项目宽度，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * height : 0      子项目高度，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * padding_top : 0      子项目上内边距，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * padding_left : 0     子项目左内边距，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * padding_right : 0    子项目右内边距，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * padding_bottom : 0   子项目下内边距，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * text_color :         文本颜色，必须是十六进字符串，以"#"开头
     * bg_color :           背景颜色，必须是十六进字符串，以"#"开头
     * text :               文本内容
     * text_size :          文本字体大小，单位：安卓以sp为单位，ios以pt为单位
     * text_align :         文本对齐方式，1：上左，2：上中，3：上右，4：居左，5：居中，6：居右，7：下左，8：下中，9：下右。
     * countdown_align :                倒计时对齐方式，1：上左，2：上中，3：上右，4：居左，5：居中，6：居右，7：下左，8：下中，9：下右。
     * countdown_padding_left : 0       倒计时左边距，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * countdown_padding_top : 0        倒计时上边距，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * countdown_padding_right : 0      倒计时右边距，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     * countdown_padding_bottom : 0     倒计时下边距，默认值为0，单位：安卓以dp为单位，ios以pt为单位
     */
    public int width;
    public int height;
    public int padding_top;
    public int padding_left;
    public int padding_right;
    public int padding_bottom;
    public String text_color;
    public String bg_color;
    public String text;
    public int text_size;
    public int text_align;
    public int countdown_align;
    public int countdown_padding_left;
    public int countdown_padding_top;
    public int countdown_padding_right;
    public int countdown_padding_bottom;

    public Attribute() {
    }

    public Attribute(Attribute other) {
        this.width = other.width;
        this.height = other.height;
        this.padding_top = other.padding_top;
        this.padding_left = other.padding_left;
        this.padding_right = other.padding_right;
        this.padding_bottom = other.padding_bottom;
        this.text_color = other.text_color;
        this.bg_color = other.bg_color;
        this.text = other.text;
        this.text_size = other.text_size;
        this.text_align = other.text_align;
        this.countdown_align = other.countdown_align;
        this.countdown_padding_left = other.countdown_padding_left;
        this.countdown_padding_top = other.countdown_padding_top;
        this.countdown_padding_right = other.countdown_padding_right;
        this.countdown_padding_bottom = other.countdown_padding_bottom;
    }

    @Override
    public String toString() {
        return "Attribute{" +
                "padding_top=" + padding_top +
                ", padding_left=" + padding_left +
                ", padding_right=" + padding_right +
                ", padding_bottom=" + padding_bottom +
                ", text_color='" + text_color + '\'' +
                ", bg_color='" + bg_color + '\'' +
                ", text='" + text + '\'' +
                ", text_size='" + text_size + '\'' +
                ", text_align='" + text_align + '\'' +
                ", countdown_align='" + countdown_align + '\'' +
                ", countdown_padding_left=" + countdown_padding_left +
                ", countdown_padding_top=" + countdown_padding_top +
                ", countdown_padding_right=" + countdown_padding_right +
                ", countdown_padding_bottom=" + countdown_padding_bottom +
                '}';
    }

    /**
     * 如果宽或高未设置，则取1:1
     *
     * @return
     */
    public float getSale() {
        float w = width;
        float h = height;
        if (w == 0 || h == 0) {
            w = 1;
            h = 1;
        }
        return h / w;
    }

    @Override
    public int getPaddingTop() {
        return padding_top;
    }

    @Override
    public int getPaddingStart() {
        return padding_left;
    }

    @Override
    public int getPaddingEnd() {
        return padding_right;
    }

    @Override
    public int getPaddingBottom() {
        return padding_bottom;
    }

    @Override
    public String getTextColor() {
        return text_color;
    }

    @Override
    public String getBgColor() {
        return bg_color;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getTextSize() {
        return text_size;
    }

    @Override
    public int getTextAlign() {
        return text_align > 0 ? text_align : 5;
    }

    @Override
    public int getCountdownAlign() {
        return countdown_align;
    }

    @Override
    public int getCountdownPaddingStart() {
        return countdown_padding_left;
    }

    @Override
    public int getCountdownPaddingTop() {
        return countdown_padding_top;
    }

    @Override
    public int getCountdownPaddingEnd() {
        return countdown_padding_right;
    }

    @Override
    public int getCountdownPaddingBottom() {
        return countdown_padding_bottom;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.padding_top);
        dest.writeInt(this.padding_left);
        dest.writeInt(this.padding_right);
        dest.writeInt(this.padding_bottom);
        dest.writeString(this.text_color);
        dest.writeString(this.bg_color);
        dest.writeString(this.text);
        dest.writeInt(this.text_size);
        dest.writeInt(this.text_align);
        dest.writeInt(this.countdown_align);
        dest.writeInt(this.countdown_padding_left);
        dest.writeInt(this.countdown_padding_top);
        dest.writeInt(this.countdown_padding_right);
        dest.writeInt(this.countdown_padding_bottom);
    }

    protected Attribute(Parcel in) {
        this.width = in.readInt();
        this.height = in.readInt();
        this.padding_top = in.readInt();
        this.padding_left = in.readInt();
        this.padding_right = in.readInt();
        this.padding_bottom = in.readInt();
        this.text_color = in.readString();
        this.bg_color = in.readString();
        this.text = in.readString();
        this.text_size = in.readInt();
        this.text_align = in.readInt();
        this.countdown_align = in.readInt();
        this.countdown_padding_left = in.readInt();
        this.countdown_padding_top = in.readInt();
        this.countdown_padding_right = in.readInt();
        this.countdown_padding_bottom = in.readInt();
    }

    public static final Creator<Attribute> CREATOR = new Creator<Attribute>() {
        @Override
        public Attribute createFromParcel(Parcel source) {
            return new Attribute(source);
        }

        @Override
        public Attribute[] newArray(int size) {
            return new Attribute[size];
        }
    };
}
