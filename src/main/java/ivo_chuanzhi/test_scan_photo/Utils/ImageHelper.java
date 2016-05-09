package ivo_chuanzhi.test_scan_photo.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by chenjiacheng on 2016/5/2.
 */
public class ImageHelper {

    //===== 其实图像色彩变换处理，就是研究不同的颜色矩阵对图像的处理效果

    /**
     *
     * @param bm 图像
     * @param hue 色调
     * @param saturation 饱和度
     * @param lum 亮度
     * @return
     */
    public static Bitmap handleImageEffect(Bitmap bm,float hue,float saturation,float lum){

        //因为传进来的bm不可修改，所以创建一个新的bmp
        if(bm == null){ System.out.println("bm = null"); }
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(),Bitmap.Config.ARGB_8888);
        //画布
        Canvas canvas = new Canvas(bmp);
        //画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //通过ColorMatrix调整图片三原色,即色调
        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.setRotate(0,hue); // 红
        hueMatrix.setRotate(1,hue); // 绿
        hueMatrix.setRotate(2,hue); // 蓝

        //饱和度
        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        //亮度
        ColorMatrix lumMatrix = new ColorMatrix();
        lumMatrix.setScale(lum,lum,lum,1); // RGBA模型，用颜色矩阵

        //整合在一起
        ColorMatrix imageMarix = new ColorMatrix();
        imageMarix.postConcat(hueMatrix);
        imageMarix.postConcat(saturationMatrix);
        imageMarix.postConcat(lumMatrix);

        //把bmp画出来
        paint.setColorFilter(new ColorMatrixColorFilter(imageMarix));
        canvas.drawBitmap(bm,0,0,paint);


        return bmp;
    }

    /**
     * 图片底片效果的算法
     * r = 255 - r;
     * g = 255 - g;
     * b = 255 - b;
     * @param bm
     * @return
     */
    public static Bitmap handleImageNegative(Bitmap bm){

        int width = bm.getWidth(); // 返回的是图片的所有像素点的宽
        int heigth = bm.getHeight();
        int color;
        int r,g,b,a; // 四个颜色分量
        Bitmap bmp = Bitmap.createBitmap(width,heigth, Bitmap.Config.ARGB_8888);

        //取出图像的所有像素点,保存在oldPx数组
        int[] oldPx = new int[width*heigth];
        int[] newPx = new int[width*heigth];
        bm.getPixels(oldPx,0,width,0,0,width,heigth);
        for(int i=0; i < width*heigth; i++){
            color = oldPx[i];
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            r = 255 - r;
            g = 255 - g;
            b = 255 - b;

            if(r > 255){
                r = 255;
            }else if(r < 0){
                r = 0;
            }

            if(g > 255){
                g = 255;
            }else if(g < 0){
                g = 0;
            }

            if(b > 255){
                b = 255;
            }else if(b < 0){
                b = 0;
            }

            //合成新的颜色像素数组，保存在newPx
            newPx[i] = Color.argb(a,r,g,b);
        }

        //给bmp设置新的效果
        bmp.setPixels(newPx,0,width,0,0,width,heigth);

        return bmp;
    }


    /**
     * 怀旧效果的算法
     * r1 = (int)(0.393*r+0.769*g+0.189*b);
     * g1 = (int)(0.349*r+0.686*g+0.168*b);
     * b1 = (int)(0.272*r+0.534*g+0.131*b);
     * @param bm
     * @return
     */
    public static Bitmap handleImagePixelsOldPhoto(Bitmap bm){

        int width = bm.getWidth(); // 返回的是图片的所有像素点的宽
        int heigth = bm.getHeight();
        int color;
        int r,g,b,a; // 四个颜色分量
        int r1,g1,b1;
        Bitmap bmp = Bitmap.createBitmap(width,heigth, Bitmap.Config.ARGB_8888);

        //取出图像的所有像素点,保存在oldPx数组
        int[] oldPx = new int[width*heigth];
        int[] newPx = new int[width*heigth];
        bm.getPixels(oldPx,0,width,0,0,width,heigth);
        for(int i=0; i < width*heigth; i++){
            color = oldPx[i];
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);

            r1 = (int)(0.393 * r + 0.769 * g + 0.189 * b);
            g1 = (int)(0.349 * r + 0.686 * g + 0.168 * b);
            b1 = (int)(0.272 * r + 0.534 * g + 0.131 * b);

            if(r1 > 255){
                r1 = 255;
            }else if(r1 < 0){
                r1 = 0;
            }

            if(g1 > 255){
                g1 = 255;
            }else if(g1 < 0){
                g1 = 0;
            }

            if(b1 > 255){
                b1 = 255;
            }else if(b1 < 0){
                b1 = 0;
            }

            //合成新的颜色像素数组，保存在newPx
            newPx[i] = Color.argb(a,r1,g1,b1);
        }

        //给bmp设置新的效果
        bmp.setPixels(newPx,0,width,0,0,width,heigth);

        return bmp;
    }

    /**
     * 浮雕效果的算法
     * r = (r - r1 + 127);
     * g = (g - g1 + 127);
     * b = (b - b1 + 127);
     * @param bm
     * @return
     */
    public static Bitmap handleImagePixelsRelief(Bitmap bm){

        int width = bm.getWidth(); // 返回的是图片的所有像素点的宽
        int heigth = bm.getHeight();
        int color=0,colorBefore=0;
        int r,g,b,a; // 四个颜色分量
        int r1,g1,b1;
        Bitmap bmp = Bitmap.createBitmap(width,heigth, Bitmap.Config.ARGB_8888);

        //取出图像的所有像素点,保存在oldPx数组
        int[] oldPx = new int[width*heigth];
        int[] newPx = new int[width*heigth];
        bm.getPixels(oldPx,0,width,0,0,width,heigth);
        //浮雕效果，这里i=1
        for(int i=1; i < width*heigth; i++){
            colorBefore = oldPx[i-1];
            r = Color.red(colorBefore);
            g = Color.green(colorBefore);
            b = Color.blue(colorBefore);
            a = Color.alpha(colorBefore);

            color = oldPx[i];
            r1 = Color.red(color);
            g1 = Color.green(color);
            b1 = Color.blue(color);

            r = (r - r1 + 127);
            g = (g - g1 + 127);
            b = (b - b1 + 127);

            if(r1 > 255){
                r1 = 255;
            }else if(r1 < 0){
                r1 = 0;
            }

            if(g1 > 255){
                g1 = 255;
            }else if(g1 < 0){
                g1 = 0;
            }

            if(b1 > 255){
                b1 = 255;
            }else if(b1 < 0){
                b1 = 0;
            }

            //合成新的颜色像素数组，保存在newPx
            newPx[i] = Color.argb(a,r,g,b);
        }

        //给bmp设置新的效果
        bmp.setPixels(newPx,0,width,0,0,width,heigth);

        return bmp;
    }

}
