package com.fz.network.demo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * cms组件数据,包括启动页banner、弹窗、首页悬浮、分类顶部、个人中心、首页banner及社区banner等
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/2/15 10:23
 */
public class MenuData implements IAttribute, Parcelable {

    /**
     * list : [{"col_id":"f71be98a-3098","ad_id":"57cf8235-8b8a","point_name":"FLASH SALE","name":"FLASH SALE","actionType":"embedPage","node_type":"","url":"https://m.rosegal.com/promotion/app/free112.html?is_app=1","image":"https://uidesign.rglcdn.com/RG/images/APP/1109/WIN1242x620.jpg","shop_price":"0.00"}]
     * type : 103 // 组件类型  101: 弹窗    102: 滑动   103: 轮播   105: 固定   106: 平铺  107:下拉  108:浮窗  109:推荐商品    110:文本
     * display_count : 1  //子项目显示列数
     * is_sku : 0 //是否是商品
     * subType:1 // 子项目类型  1：商品    2：banner图片 3：历史记录
     * prop_w : 621 //列表子项目宽度，图片宽度和高度正确比例即可，忽略单位
     * prop_h : 310 //列表子项目高度，图片宽度和高度正确比例即可，忽略单位
     * item_left : 0 //列表子项目左边距，单位：安卓以sp为单位，ios以pt为单位
     * item_top : 0 //列表子项目上边距，单位：安卓以sp为单位，ios以pt为单位
     * padding_top : 0 //上内边距，单位：安卓以sp为单位，ios以pt为单位
     * padding_left : 0 //左内边距，单位：安卓以sp为单位，ios以pt为单位
     * padding_right : 0 //右内边距，单位：安卓以sp为单位，ios以pt为单位
     * padding_bottom :  //下内边距，单位：安卓以sp为单位，ios以pt为单位
     * text_color://文本颜色，必须是十六进字符串，以"#"开头
     * text:,//文本内容
     * text_size: //文本字体大小，单位：安卓以sp为单位，ios以pt为单位
     * text_align:,//文本对齐方式，0：上左，1：上中，2：上右，3：居左，4：居中，5：居右，6：下左，7：下中，8：下右。
     * bg_img: , //背景图片
     * bg_color: ,  //背景颜色，必须是十六进字符串，以"#"开头
     * countdown_time://倒计时时间，如果有倒计时，则倒计时时间应大于0，反之没有倒计时
     * countdown_align:,//倒计时对齐方式，0：上左，1：上中，2：上右，3：居左，4：居中，5：居右，6：下左，7：下中，8：下右。
     * component_id : md-tvvc1542248228913
     */

    public int type;
    public float display_count;
    public int is_sku;
    public int subType;
    public int prop_w;
    public int prop_h;
    public int padding_top;
    public int padding_left;
    public int padding_right;
    public int padding_bottom;
    public String bg_img;
    public String bg_color;
    public String component_id;
    public Attribute attributes;
    public List<MenuListData> list;
    /**
     * 推荐商品接口类型，0为请求大数据推荐商品，其他则请求zaful商品列表
     */
    public String recommendType;
    /**
     * 如果{@link #recommendType}不为0，则当前字段为分类id或虚拟分类名等
     */
    public String recommendContent;
    /**
     * 记录当前所处列表中的索引
     */
    public int curPosition = -1;

    /**
     * 如果宽或高未设置，则取1:1
     *
     * @return
     */
    public float getSale() {
        float w = prop_w;
        float h = prop_h;
        if (w == 0 || h == 0) {
            w = 1;
            h = 1;
        }
        return h / w;
    }

    @Override
    public String toString() {
        return "MenuData{" +
                "type=" + type +
                ", display_count=" + display_count +
                ", is_sku=" + is_sku +
                ", subType=" + subType +
                ", prop_w=" + prop_w +
                ", prop_h=" + prop_h +
                ", padding_top=" + padding_top +
                ", padding_left=" + padding_left +
                ", padding_right=" + padding_right +
                ", padding_bottom=" + padding_bottom +
                ", bg_img='" + bg_img + '\'' +
                ", bg_color='" + bg_color + '\'' +
                ", component_id='" + component_id + '\'' +
                ", attributes=" + attributes +
                ", list=" + list +
                ", curPosition=" + curPosition +
                '}';
    }


    @Override
    public int getPaddingTop() {
        return attributes != null ? attributes.padding_top : 0;
    }

    @Override
    public int getPaddingStart() {
        return attributes != null ? attributes.padding_left : 0;
    }

    @Override
    public int getPaddingEnd() {
        return attributes != null ? attributes.padding_right : 0;
    }

    @Override
    public int getPaddingBottom() {
        return attributes != null ? attributes.padding_bottom : 0;
    }

    @Override
    public String getTextColor() {
        return attributes != null ? attributes.text_color : "";
    }

    @Override
    public String getBgColor() {
        return attributes != null ? attributes.bg_color : "";
    }

    @Override
    public String getText() {
        return attributes != null ? attributes.text : "";
    }

    @Override
    public int getTextSize() {
        return attributes != null ? attributes.text_size : 12;
    }

    @Override
    public int getTextAlign() {
        return attributes != null ? attributes.getTextAlign() : 5;
    }

    @Override
    public int getCountdownAlign() {
        return attributes != null ? attributes.countdown_align : 5;
    }

    @Override
    public int getCountdownPaddingStart() {
        return attributes != null ? attributes.countdown_padding_left : 0;
    }

    @Override
    public int getCountdownPaddingTop() {
        return attributes != null ? attributes.countdown_padding_top : 0;
    }

    @Override
    public int getCountdownPaddingEnd() {
        return attributes != null ? attributes.countdown_padding_right : 0;
    }

    @Override
    public int getCountdownPaddingBottom() {
        return attributes != null ? attributes.countdown_padding_bottom : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeFloat(this.display_count);
        dest.writeInt(this.is_sku);
        dest.writeInt(this.subType);
        dest.writeInt(this.prop_w);
        dest.writeInt(this.prop_h);
        dest.writeInt(this.padding_top);
        dest.writeInt(this.padding_left);
        dest.writeInt(this.padding_right);
        dest.writeInt(this.padding_bottom);
        dest.writeString(this.bg_img);
        dest.writeString(this.bg_color);
        dest.writeString(this.component_id);
        dest.writeParcelable(this.attributes, flags);
        dest.writeTypedList(this.list);
        dest.writeString(this.recommendType);
        dest.writeString(this.recommendContent);
        dest.writeInt(this.curPosition);
    }

    public MenuData() {
    }

    protected MenuData(Parcel in) {
        this.type = in.readInt();
        this.display_count = in.readFloat();
        this.is_sku = in.readInt();
        this.subType = in.readInt();
        this.prop_w = in.readInt();
        this.prop_h = in.readInt();
        this.padding_top = in.readInt();
        this.padding_left = in.readInt();
        this.padding_right = in.readInt();
        this.padding_bottom = in.readInt();
        this.bg_img = in.readString();
        this.bg_color = in.readString();
        this.component_id = in.readString();
        this.attributes = in.readParcelable(Attribute.class.getClassLoader());
        this.list = in.createTypedArrayList(MenuListData.CREATOR);
        this.recommendType = in.readString();
        this.recommendContent = in.readString();
        this.curPosition = in.readInt();
    }

    public static final Creator<MenuData> CREATOR = new Creator<MenuData>() {
        @Override
        public MenuData createFromParcel(Parcel source) {
            return new MenuData(source);
        }

        @Override
        public MenuData[] newArray(int size) {
            return new MenuData[size];
        }
    };
}
