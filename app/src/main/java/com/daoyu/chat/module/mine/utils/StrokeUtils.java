package com.daoyu.chat.module.mine.utils;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.daoyu.chat.module.mine.bean.Stroke;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;


/**
 * 汉字笔划工具类
 */
public class StrokeUtils {
    private static final String TAG = "StrokeUtils";
    private static final String CHARSET_NAME = "UTF-8";
    private final static StrokeUtils factory = new StrokeUtils();

    private static HashMap<String, Stroke> mapper;

    public static StrokeUtils newInstance(Context context)  {
        if (mapper == null) {
            //原始文件   stroke.json
//            String strokeJson = LocalFileUtils.getStringFormAsset(context, "stroke.json");
//            mapper = JSONUtil.toCollection(strokeJson, HashMap.class, String.class, Stroke.class);
//            // 使用 GZIP  压缩
//            String gzipStrokeJson = GzipUtil.compress(strokeJson,CHARSET_NAME);
//            writeFile(gzipStrokeJson,"gzipStrokeJson.json");

//            //使用 GZIP 解压
//            String gzipStrokeJson = LocalFileUtils.getStringFormAsset(context, "gzipStrokeJson.json");
//            String strokeJson = GzipUtil.uncompress(gzipStrokeJson,CHARSET_NAME);
//            mapper = JSONUtil.toCollection(strokeJson, HashMap.class, String.class, Stroke.class);

            /////////////////////////////////////////////////////////////////////////////////////////

//            //原始文件   stroke.json
//            String strokeJson = LocalFileUtils.getStringFormAsset(context, "stroke.json");
//            mapper = JSONUtil.toCollection(strokeJson, HashMap.class, String.class, Stroke.class);
//            // 使用 Deflater  加密
//            String deFlaterStrokeJson = DeflaterUtils.zipString(strokeJson,CHARSET_NAME);
//            writeFile(deFlaterStrokeJson,"deFlaterStrokeJson.json");

            //使用 Inflater 解密
            String deFlaterStrokeJson = LocalFileUtils.getStringFormAsset(context, "deFlaterStrokeJson.json");
            String strokeJson = DeflaterUtils.unzipString(deFlaterStrokeJson,CHARSET_NAME);
            mapper = JSONUtil.toCollection(strokeJson, HashMap.class, String.class, Stroke.class);
        }
        return factory;
    }

    private static void writeFile(String mapperJson, String fileName) {
        Writer write = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            Log.d(TAG, "file.exists():" + file.exists() + " file.getAbsolutePath():" + file.getAbsolutePath());
            // 如果父目录不存在，创建父目录
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            // 如果已存在,删除旧文件
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            write = new OutputStreamWriter(new FileOutputStream(file), CHARSET_NAME);
            write.write(mapperJson);
            write.flush();
            write.close();
        } catch (Exception e) {
            Log.e(TAG, "e = " + Log.getStackTraceString(e));
        }finally {
            if (write != null){
                try {
                    write.close();
                } catch (IOException e) {
                    Log.e(TAG, "e = " + Log.getStackTraceString(e));
                }
            }
        }
    }

    /**
     * 根据point拿到笔画封装的Stroke
     *
     * @param keyPoint code码
     * @return 笔画封装的Stroke
     */
    public Stroke getStroke(String keyPoint) {
        if (keyPoint == null) {
            return null;
        }
        if (mapper != null){
            return mapper.get(keyPoint);
        } else{
            return null;
        }
    }
}

