package com.daoyu.chat.module.mine.utils;


import android.util.Log;

import com.daoyu.chat.base.BaseApplication;

/**
 * 除了中国之外的国家列表按英文单词排序.
 */
public class OtherLanguageCharacterParser {

    private static OtherLanguageCharacterParser hkCharacterParser = new OtherLanguageCharacterParser();

    public static OtherLanguageCharacterParser getInstance() {
        return hkCharacterParser;
    }

    public String getEnglishName(String strCode) {
        String strResult = "";
        // 将国家代码转换一下， 比如 CN  转为 SORT_CN
        strCode = "SORT_" + strCode;
        //获取到要显示的国家或地区的名称
        strResult = AreaCodeUtils.getCountryNameByCountryCode(BaseApplication.getContext(), strCode);
        Log.d("OtherCharacterParser", "strCode = " + strCode + " ,strResult:" + strResult);
        return strResult;
    }

}
