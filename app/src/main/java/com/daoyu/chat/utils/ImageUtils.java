package com.daoyu.chat.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ImageUtils {

    public static void setRoundCornerImageView(Context context, Object resource, int errorResId, ImageView imageView) {
        MultiTransformation multi = new MultiTransformation<>(new CenterCrop(),
                new RoundedCornersTransformation(10, 0));
        GlideApp.with(context).load(resource).apply(bitmapTransform(multi))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(errorResId)
                .error(errorResId)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void setRoundCornerImageView(Context context, Object resource, ImageView imageView) {
        MultiTransformation multi = new MultiTransformation<>(new CenterCrop(),
                new RoundedCornersTransformation(10, 0));
        GlideApp.with(context).load(resource).apply(bitmapTransform(multi))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void setRoundCornerImageView(Context context, Object resource, ImageView imageView, int radius) {
        MultiTransformation multi = new MultiTransformation<>(new CenterCrop(),
                new RoundedCornersTransformation(radius, 0));
        GlideApp.with(context).load(resource).apply(bitmapTransform(multi))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void setRoundCornerImageView(Context context, Object resource, int placeholderId, ImageView imageView, int radius) {
        MultiTransformation multi = new MultiTransformation<>(new CenterCrop(),
                new RoundedCornersTransformation(radius, 0));
        GlideApp.with(context)
                .load(resource)
                .apply(bitmapTransform(multi))
                .placeholder(placeholderId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .into(imageView);
    }

    public static void setRoundImageView(Context context, Object resource, int errorResId, ImageView imageView) {
        setRoundImageView(context, resource, errorResId, false, imageView);
    }

    public static void setRoundImageView(Context context, Object resource, int errorResId, boolean cache, ImageView imageView) {
        MultiTransformation multi = new MultiTransformation<>(new CenterCrop(), new CropCircleTransformation());
        if (cache) {
            GlideApp.with(context).load(resource)
                    .apply(bitmapTransform(multi))
                    .placeholder(errorResId)
                    .error(errorResId)
                    .into(imageView);
        } else {
            GlideApp.with(context).load(resource)
                    .transition(withCrossFade())
                    .apply(bitmapTransform(multi))
                    .placeholder(errorResId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .error(errorResId)
                    .into(imageView);
        }
    }

    public static void setNormalImage(Context context, Object url, int placeholderResId, int errorResId, ImageView imageView) {
        MultiTransformation multi = new MultiTransformation<>(new CenterCrop());
        GlideApp.with(context).load(url)
               // .transition(withCrossFade())
              //  .apply(bitmapTransform(multi))
               // .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.skipMemoryCache(false)
                .placeholder(placeholderResId)
                .error(errorResId)
                .into(imageView);
    }

    public static void setNormalImage(Context context, Object url, ImageView imageView) {
        MultiTransformation multi = new MultiTransformation<>(new CenterCrop());
        GlideApp.with(context).load(url)
                .transition(withCrossFade())
                .apply(bitmapTransform(multi))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(imageView);
    }

    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
