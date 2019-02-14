package com.example.jason.finalproj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {
    Utils(){};


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int pxTodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Bitmap转化为byte[]
     * @param bitmap
     * @return byte[]
     */
    public static byte[] BitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    /**
     * byte[]转化为Bitmap
     * @param bytes
     * @return Bitmap
     */
    public static Bitmap BytesToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Bitmap转化为Drawable
     * @param bitmap
     * @return Drawable
     */
    public static Drawable BitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * Drawable转化为Bitmap
     * @param drawable
     * @return Bitmap
     */
    public static Bitmap DrawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * id转化为Drawable
     * @param context
     * @param id
     * @return Drawable
     */
    public static Drawable IdToDrawable(Context context, int id) {
        return context.getResources().getDrawable(id);
    }

    /**
     * id转化为Bitmap
     * @param context
     * @param id
     * @return Bitmap
     */
    public static Bitmap IdToBitmap(Context context, int id) {
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    /**
     * 设置Drawable的大小
     * @param drawable
     * @param width 像素点
     * @param height 像素点
     * @return Drawable
     */
    public static Drawable setDrawableSize(Drawable drawable, int width, int height) {
        Bitmap originalBitmap = DrawableToBitmap(drawable);
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        float scaleWidth = ((float) width) / originalWidth;
        float scaleHeight = ((float) height) / originalHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap changedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalWidth, originalHeight, matrix, true);
        return BitmapToDrawable(changedBitmap);
    }


    /**
     * Date类型转化成String
     * @param date
     * @return
     */
    public static String getStringByDate(Date date) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return sdf.format(date);
    }
}
