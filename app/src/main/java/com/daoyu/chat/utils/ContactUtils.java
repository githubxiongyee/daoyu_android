package com.daoyu.chat.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.daoyu.chat.base.BaseApplication;
import com.daoyu.chat.module.login.bean.UserBean;

import java.util.ArrayList;

public class ContactUtils {
    public static ArrayList<ContactMobileBean.ContactMobileData> getAllContacts(Context context) {
        ArrayList<ContactMobileBean.ContactMobileData> contacts = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            //新建一个联系人实例
            ContactMobileBean.ContactMobileData temp = new ContactMobileBean.ContactMobileData();
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //获取联系人姓名
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            temp.name = name;

            //获取联系人电话号码
            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while (phoneCursor.moveToNext()) {
                String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone = phone.replace("-", "");
                phone = phone.replace(" ", "");
                phone = phone.replaceAll("[^\\d]", "");
                temp.mobile = phone;
            }
            String mobile = temp.mobile;
            UserBean.UserData userInfoData = BaseApplication.getInstance().getUserInfoData();
            if (!TextUtils.isEmpty(mobile) && !mobile.equals(userInfoData.userPhone)) {
                contacts.add(temp);
            }
            phoneCursor.close();

        }
        cursor.close();
        return contacts;
    }
}
