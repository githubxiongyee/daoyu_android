package com.daoyu.chat.module.mine.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 国家或地区
 * <p/>
 * created by Eugene
 */
public class CountryOrRegion implements Parcelable {

    private Integer id;

    /**
     * 国家或地区 代号码，比如 CN、TW、HK、US
     */
    private String countryCode;

    /**
     * 国家或地区 区号
     */
    private Integer areaCode; //区号

    // 拼音排序后需要的两个字段
    /**
     * 排序首字母
     */
    private String sortLetters;
    /**
     * 转换后的拼音名称
     */
    private String pinyinName;

    // 笔画排序后需要的两个字段

    /**
     * 国家或地区的名称，根据countryCode从资源文件获取到的不同的名称，可能是英文、简体中文、繁体中文、印尼文等
     */
    private String name;

    /**
     * 国家或地区的名称的笔画数量
     */
    private int strokeCount;

    private boolean isChecked = false;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Integer areaCode) {
        this.areaCode = areaCode;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean check) {
        this.isChecked = check;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getPinyinName() {
        return pinyinName;
    }

    public void setPinyinName(String pinyinName) {
        this.pinyinName = pinyinName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrokeCount() {
        return strokeCount;
    }

    public void setStrokeCount(int strokeCount) {
        this.strokeCount = strokeCount;
    }

    @Override
    public String toString() {
        return "CountryOrRegion{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", areaCode=" + areaCode +
                ", sortLetters='" + sortLetters + '\'' +
                ", pinyinName='" + pinyinName + '\'' +
                ", name='" + name + '\'' +
                ", strokeCount='" + strokeCount + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.countryCode);
        dest.writeValue(this.areaCode);
        dest.writeString(this.sortLetters);
        dest.writeString(this.pinyinName);
        dest.writeString(this.name);
        dest.writeInt(this.strokeCount);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    public CountryOrRegion() {
    }

    protected CountryOrRegion(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.countryCode = in.readString();
        this.areaCode = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sortLetters = in.readString();
        this.pinyinName = in.readString();
        this.name = in.readString();
        this.strokeCount = in.readInt();
        this.isChecked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<CountryOrRegion> CREATOR = new Parcelable.Creator<CountryOrRegion>() {
        @Override
        public CountryOrRegion createFromParcel(Parcel source) {
            return new CountryOrRegion(source);
        }

        @Override
        public CountryOrRegion[] newArray(int size) {
            return new CountryOrRegion[size];
        }
    };
}
