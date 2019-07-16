package com.fz.network.demo;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

/**
 * @author dingpeihua
 * @version 1.0
 * @date 2018/12/12 16:56
 */
public class MenuListData implements Parcelable {
    /**
     * col_id : f71be98a-3098
     * ad_id : 57cf8235-8b8a
     * point_name : FLASH SALE
     * name : FLASH SALE
     * actionType : embedPage
     * node_type :
     * url : https://m.rosegal.com/promotion/app/free112.html?is_app=1
     * image : https://uidesign.rglcdn.com/RG/images/APP/1109/WIN1242x620.jpg
     * shop_price : 0.00
     * market_price:0.00
     */

    public String col_id;
    public String ad_id;
    public String point_name;
    public String name;
    public int actionType;
    public String node_type;
    public String url;
    public String image;
    public String shop_price;
    public String market_price;
    public int subType;
    public long countdown_time;
    public Attribute attributes;
    public int alertNum;
    private long elapsedRealTime;

    public MenuListData() {
        /**
         * 记录接口返回数据时当前系统时间
         */
        elapsedRealTime = SystemClock.elapsedRealtime();
    }

    @Override
    public String toString() {
        return "MenuListData{" +
                "col_id='" + col_id + '\'' +
                ", ad_id='" + ad_id + '\'' +
                ", point_name='" + point_name + '\'' +
                ", name='" + name + '\'' +
                ", actionType='" + actionType + '\'' +
                ", node_type='" + node_type + '\'' +
                ", url='" + url + '\'' +
                ", image='" + image + '\'' +
                ", shop_price='" + shop_price + '\'' +
                ", subType=" + subType +
                ", countdown_time=" + countdown_time +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.col_id);
        dest.writeString(this.ad_id);
        dest.writeString(this.point_name);
        dest.writeString(this.name);
        dest.writeInt(this.actionType);
        dest.writeString(this.node_type);
        dest.writeString(this.url);
        dest.writeString(this.image);
        dest.writeString(this.shop_price);
        dest.writeInt(this.subType);
        dest.writeLong(this.countdown_time);
        dest.writeParcelable(this.attributes, flags);
    }

    protected MenuListData(Parcel in) {
        this.col_id = in.readString();
        this.ad_id = in.readString();
        this.point_name = in.readString();
        this.name = in.readString();
        this.actionType = in.readInt();
        this.node_type = in.readString();
        this.url = in.readString();
        this.image = in.readString();
        this.shop_price = in.readString();
        this.subType = in.readInt();
        this.countdown_time = in.readLong();
        this.attributes = in.readParcelable(Attribute.class.getClassLoader());
    }

    public static final Creator<MenuListData> CREATOR = new Creator<MenuListData>() {
        @Override
        public MenuListData createFromParcel(Parcel source) {
            return new MenuListData(source);
        }

        @Override
        public MenuListData[] newArray(int size) {
            return new MenuListData[size];
        }
    };

}
