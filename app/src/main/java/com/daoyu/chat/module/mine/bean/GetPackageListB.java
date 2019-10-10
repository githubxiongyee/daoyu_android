package com.daoyu.chat.module.mine.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GetPackageListB {
    /**
     * msg : 请求成功
     * code : 1
     * data : {"code":1,"msg":"数据请求成功","page":0,"limit":20,"count":5,"data":[{"id":409,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":5,"createTime":1568196409000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"NearBy","balance":null},{"id":406,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":3,"createTime":1568195776000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"CheckIn","balance":null},{"id":334,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":2,"createTime":1568081712000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"CheckIn","balance":null},{"id":315,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":1,"createTime":1568028881000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"CheckIn","balance":null},{"id":240,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":1,"createTime":1567773913000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"CheckIn","balance":null}],"searchParams":null,"fields":null,"sort":{"create_time":false}}
     * success : true
     */

    private String msg;
    private int code;
    private DataBeanX data;
    private boolean success;

    public static GetPackageListB objectFromData(String str) {

        return new Gson().fromJson(str, GetPackageListB.class);
    }

    public static GetPackageListB objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), GetPackageListB.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<GetPackageListB> arrayGetPackageListBFromData(String str) {

        Type listType = new TypeToken<ArrayList<GetPackageListB>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<GetPackageListB> arrayGetPackageListBFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<GetPackageListB>>() {
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

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class DataBeanX {
        /**
         * code : 1
         * msg : 数据请求成功
         * page : 0
         * limit : 20
         * count : 5
         * data : [{"id":409,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":5,"createTime":1568196409000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"NearBy","balance":null},{"id":406,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":3,"createTime":1568195776000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"CheckIn","balance":null},{"id":334,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":2,"createTime":1568081712000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"CheckIn","balance":null},{"id":315,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":1,"createTime":1568028881000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"CheckIn","balance":null},{"id":240,"uid":null,"vipHongbao":-1,"vipStatus":1,"vipCredit":1,"createTime":1567773913000,"updateTime":null,"fid":null,"remarks":null,"namount":null,"hbcount":null,"ncount":null,"pvid":null,"source":"CheckIn","balance":null}]
         * searchParams : null
         * fields : null
         * sort : {"create_time":false}
         */

        private int code;
        private String msg;
        private int page;
        private int limit;
        private int count;
        private Object searchParams;
        private Object fields;
        private SortBean sort;
        private List<DataBean> data;

        public static DataBeanX objectFromData(String str) {

            return new Gson().fromJson(str, DataBeanX.class);
        }

        public static DataBeanX objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), DataBeanX.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<DataBeanX> arrayDataBeanXFromData(String str) {

            Type listType = new TypeToken<ArrayList<DataBeanX>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<DataBeanX> arrayDataBeanXFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<DataBeanX>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Object getSearchParams() {
            return searchParams;
        }

        public void setSearchParams(Object searchParams) {
            this.searchParams = searchParams;
        }

        public Object getFields() {
            return fields;
        }

        public void setFields(Object fields) {
            this.fields = fields;
        }

        public SortBean getSort() {
            return sort;
        }

        public void setSort(SortBean sort) {
            this.sort = sort;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class SortBean {
            /**
             * create_time : false
             */

            private boolean create_time;

            public static SortBean objectFromData(String str) {

                return new Gson().fromJson(str, SortBean.class);
            }

            public static SortBean objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), SortBean.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<SortBean> arraySortBeanFromData(String str) {

                Type listType = new TypeToken<ArrayList<SortBean>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<SortBean> arraySortBeanFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<SortBean>>() {
                    }.getType();

                    return new Gson().fromJson(jsonObject.getString(str), listType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return new ArrayList();


            }

            public boolean isCreate_time() {
                return create_time;
            }

            public void setCreate_time(boolean create_time) {
                this.create_time = create_time;
            }
        }

        public static class DataBean {
            /**
             * id : 409
             * uid : null
             * vipHongbao : -1
             * vipStatus : 1
             * vipCredit : 5
             * createTime : 1568196409000
             * updateTime : null
             * fid : null
             * remarks : null
             * namount : null
             * hbcount : null
             * ncount : null
             * pvid : null
             * source : NearBy
             * balance : null
             */

            private int id;
            private Object uid;
            private int vipHongbao;
            private int vipStatus;
            private double vipCredit;
            private long createTime;
            private Object updateTime;
            private Object fid;
            private Object remarks;
            private Object namount;
            private Object hbcount;
            private Object ncount;
            private Object pvid;
            private String source;
            private Object balance;

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

            public Object getUid() {
                return uid;
            }

            public void setUid(Object uid) {
                this.uid = uid;
            }

            public int getVipHongbao() {
                return vipHongbao;
            }

            public void setVipHongbao(int vipHongbao) {
                this.vipHongbao = vipHongbao;
            }

            public int getVipStatus() {
                return vipStatus;
            }

            public void setVipStatus(int vipStatus) {
                this.vipStatus = vipStatus;
            }

            public double getVipCredit() {
                return vipCredit;
            }

            public void setVipCredit(double vipCredit) {
                this.vipCredit = vipCredit;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public Object getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(Object updateTime) {
                this.updateTime = updateTime;
            }

            public Object getFid() {
                return fid;
            }

            public void setFid(Object fid) {
                this.fid = fid;
            }

            public Object getRemarks() {
                return remarks;
            }

            public void setRemarks(Object remarks) {
                this.remarks = remarks;
            }

            public Object getNamount() {
                return namount;
            }

            public void setNamount(Object namount) {
                this.namount = namount;
            }

            public Object getHbcount() {
                return hbcount;
            }

            public void setHbcount(Object hbcount) {
                this.hbcount = hbcount;
            }

            public Object getNcount() {
                return ncount;
            }

            public void setNcount(Object ncount) {
                this.ncount = ncount;
            }

            public Object getPvid() {
                return pvid;
            }

            public void setPvid(Object pvid) {
                this.pvid = pvid;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public Object getBalance() {
                return balance;
            }

            public void setBalance(Object balance) {
                this.balance = balance;
            }
        }
    }
}
