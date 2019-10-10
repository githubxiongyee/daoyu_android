package com.daoyu.chat.module.chat.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NearbyPeopleB {

    /**
     * msg : 请求成功
     * code : 1
     * data : [{"id":2,"nickName":"孙尚香","headImg":"http://image.futruedao.com/1565592837238.jpg","sex":"M","remarks":"在线的人都可以在这里的方式去皮的价格来说也不是一般男人们可以用自己余的生命开玩笑吗、这些是你想什么","addressX":"113.957077","addressY":"22.537322","updateTime":1565425873000},{"id":4,"nickName":"曹操新","headImg":null,"sex":"M","remarks":"暂时没有个性签名！","addressX":"113.957065","addressY":"22.537324","updateTime":1565426105000},{"id":5,"nickName":"涉彩台半校","headImg":"http://image.futruedao.com/1565426920069.jpg","sex":null,"remarks":null,"addressX":"113.957078","addressY":"22.537268","updateTime":1565426178000},{"id":6,"nickName":"招聘扣扣给我考虑考虑要不要继续加","headImg":"http://image.futruedao.com/Fu8l7Ybf9BjUYQ-ZEK4MmEQqCBgp","sex":"F","remarks":"来咯弄六角恐龙","addressX":"113.9522572262963","addressY":"22.54049412834746","updateTime":1565426417000},{"id":8,"nickName":"孙策","headImg":"http://image.futruedao.com/FnrAcxGa_u6BZPbt84QGf0DS7bNP","sex":"F","remarks":null,"addressX":"113.9522594204436","addressY":"22.54044705264799","updateTime":1565427476000},{"id":9,"nickName":"暂无昵称哦了扣扣给我发个位置我看看有没有","headImg":"http://image.futruedao.com/Fj-W47uIX03sxBEScyMOBvuTnz-s","sex":"M","remarks":"家具设计和风格设计理念学校?一一于一体雪zssss","addressX":"113.9523363609158","addressY":"22.54043482292791","updateTime":1565429336000},{"id":10,"nickName":"唫先森","headImg":"http://image.futruedao.com/FrRu8SIg4zRbIyvTrFyK78zU0Vbj","sex":"M","remarks":"家具设计和风格设计理念学校?一一于一体雪zssss","addressX":"113.9518629649986","addressY":"22.53951823263687","updateTime":1565428755000},{"id":11,"nickName":"多的关怀和","headImg":"http://image.futruedao.com/FvCSokn0g-gbnvwl2n6Qe3LTc5jh","sex":"F","remarks":"哇可以","addressX":"113.9522957931065","addressY":"22.5404638635074","updateTime":1565431429000},{"id":12,"nickName":"东皇太一","headImg":"http://image.futruedao.com/Fs_r6Cb_dnCct9WD5I_L1KyyQq6_","sex":"M","remarks":null,"addressX":"113.9522624742253","addressY":"22.54042451807919","updateTime":1565428841000},{"id":14,"nickName":"密栽粘琼弃","headImg":null,"sex":null,"remarks":null,"addressX":"113.95134123","addressY":"22.53830745999999","updateTime":1565429798000},{"id":15,"nickName":"旧曾抱暖欢","headImg":null,"sex":null,"remarks":null,"addressX":"113.9513412299999","addressY":"22.53830745999936","updateTime":1565429804000},{"id":16,"nickName":"牛魔王","headImg":"http://image.futruedao.com/1565438134383.jpg","sex":"F","remarks":"还不小呢","addressX":"113.957067","addressY":"22.537313","updateTime":1565431597000},{"id":17,"nickName":"液尚凰倔篷","headImg":null,"sex":null,"remarks":null,"addressX":"113.957068","addressY":"22.537325","updateTime":1565435024000},{"id":18,"nickName":"狄仁杰","headImg":"http://image.futruedao.com/1565438012729.jpg","sex":"M","remarks":null,"addressX":"113.957061","addressY":"22.5373","updateTime":1565436123000},{"id":19,"nickName":"叶师傅_","headImg":"http://image.futruedao.com/1565439117868.jpg","sex":"F","remarks":"Locking","addressX":"113.957066","addressY":"22.53728","updateTime":1565436421000},{"id":22,"nickName":"途般从晨著","headImg":null,"sex":null,"remarks":null,"addressX":"113.9523324435482","addressY":"22.54050153776225","updateTime":1565440903000},{"id":32,"nickName":"清酒了无痕","headImg":"http://image.futruedao.com/1565508684555.jpg","sex":"F","remarks":"有个性！不签名！姐不是传说","addressX":"113.944594","addressY":"22.5272","updateTime":1565505993000},{"id":34,"nickName":"凯","headImg":"http://image.futruedao.com/1565507722469.jpg","sex":null,"remarks":null,"addressX":"113.944609","addressY":"22.527206","updateTime":1565506723000},{"id":38,"nickName":"砰匆竹术翠","headImg":null,"sex":null,"remarks":null,"addressX":"113.944598","addressY":"22.5272","updateTime":1565506969000},{"id":41,"nickName":"报捧遁欺紊","headImg":null,"sex":null,"remarks":null,"addressX":"113.94446560329861","addressY":"22.52747775607639","updateTime":1565508045000},{"id":45,"nickName":"并近咸嚣涪","headImg":null,"sex":null,"remarks":null,"addressX":"113.944592","addressY":"22.527204","updateTime":1565508714000},{"id":47,"nickName":"霖","headImg":"http://image.futruedao.com/1565509075578.jpg","sex":null,"remarks":null,"addressX":"113.94445963541666","addressY":"22.52737060546875","updateTime":1565508770000},{"id":48,"nickName":"寥乙剔锗遥","headImg":null,"sex":null,"remarks":null,"addressX":"113.943","addressY":"22.527461","updateTime":1565508824000},{"id":50,"nickName":"蔡伤将累匈","headImg":null,"sex":null,"remarks":null,"addressX":"113.944593","addressY":"22.527218","updateTime":1565508899000},{"id":52,"nickName":"杆诗帘贪癌","headImg":null,"sex":null,"remarks":null,"addressX":"113.943338","addressY":"22.52863","updateTime":1565509271000},{"id":53,"nickName":"云中君","headImg":"http://image.futruedao.com/1565509656241.jpg","sex":null,"remarks":null,"addressX":"113.944574","addressY":"22.527203","updateTime":1565509370000},{"id":58,"nickName":"坟秤辆凋例","headImg":null,"sex":null,"remarks":null,"addressX":"113.943338","addressY":"22.52863","updateTime":1565511796000},{"id":62,"nickName":"缝包恢侥仟","headImg":null,"sex":null,"remarks":null,"addressX":"113.9523448991615","addressY":"22.54032231565478","updateTime":1565575781000},{"id":66,"nickName":"达摩","headImg":null,"sex":null,"remarks":null,"addressX":"113.9523325947161","addressY":"22.54045274382632","updateTime":1565593581000},{"id":67,"nickName":"诱耿谎劳写","headImg":null,"sex":null,"remarks":null,"addressX":"113.9523399405771","addressY":"22.54038690331183","updateTime":1565677376000},{"id":68,"nickName":"电信号码","headImg":"http://image.futruedao.com/1565694719317.jpg","sex":null,"remarks":null,"addressX":"113.95723","addressY":"22.537208","updateTime":1565694593000}]
     * success : true
     */

    private String msg;
    private int code;
    private boolean success;
    private List<DataBean> data;

    public static NearbyPeopleB objectFromData(String str) {

        return new Gson().fromJson(str, NearbyPeopleB.class);
    }

    public static NearbyPeopleB objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), NearbyPeopleB.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<NearbyPeopleB> arrayNearbyPeopleBFromData(String str) {

        Type listType = new TypeToken<ArrayList<NearbyPeopleB>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<NearbyPeopleB> arrayNearbyPeopleBFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<NearbyPeopleB>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 2
         * nickName : 孙尚香
         * headImg : http://image.futruedao.com/1565592837238.jpg
         * sex : M
         * remarks : 在线的人都可以在这里的方式去皮的价格来说也不是一般男人们可以用自己余的生命开玩笑吗、这些是你想什么
         * addressX : 113.957077
         * addressY : 22.537322
         * updateTime : 1565425873000
         */

        private int id;
        private String nickName;
        private String headImg;
        private Object sex;
        private String remarks;
        private String addressX;
        private String addressY;
        private long updateTime;

        public static DataBean objectFromData(String str) {

            return new Gson().fromJson(str, DataBean.class);
        }

        public static DataBean objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DataBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<DataBean> arrayDataBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<DataBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<DataBean> arrayDataBeanFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DataBean>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public Object getSex() {
            return sex;
        }

        public void setSex(Object sex) {
            this.sex = sex;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getAddressX() {
            return addressX;
        }

        public void setAddressX(String addressX) {
            this.addressX = addressX;
        }

        public String getAddressY() {
            return addressY;
        }

        public void setAddressY(String addressY) {
            this.addressY = addressY;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
    }
}
