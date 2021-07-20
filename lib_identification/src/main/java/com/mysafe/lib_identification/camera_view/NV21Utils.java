package com.mysafe.lib_identification.camera_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NV21Utils {
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;
    private int mWidth, mHeight;

    public NV21Utils(Context context) {
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }

    /**
     * nv21转bitmap
     *
     * @param nv21
     * @param width
     * @param height
     * @return
     */
    public Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        if (width <= 0 || height <= 0 || nv21 == null || nv21.length != width * height * 3 / 2) {
            return null;
        }
        if (yuvType == null || mWidth != width || mHeight != height) {
            mWidth = width;
            mHeight = height;
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }

        in.copyFrom(nv21);

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);

        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);
        return bmpout;
    }

    /**
     * 按比例缩放并且旋转图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleAndRotateBitmap(Bitmap origin, float ratio, float degree) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        matrix.preRotate(degree);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    /**
     * NV21裁剪  算法效率 3ms
     *
     * @param src    源数据
     * @param width  源宽
     * @param height 源高
     * @param left   顶点坐标
     * @param top    顶点坐标
     * @param clip_w 裁剪后的宽
     * @param clip_h 裁剪后的高
     * @return 裁剪后的数据
     */
    public static byte[] clipNV21(byte[] src, int width, int height, int left, int top, int clip_w, int clip_h) {
        if (left > width || top > height || left + clip_w > width || top + clip_h > height) {
            return null;
        }
        //取偶
        int x = left / 4 * 4, y = top / 4 * 4;
        int w = clip_w / 4 * 4, h = clip_h / 4 * 4;
        int y_unit = w * h;
        int uv = y_unit / 2;
        byte[] nData = new byte[y_unit + uv];
        int uv_index_dst = w * h - y / 2 * w;
        int uv_index_src = width * height + x;
        for (int i = y; i < y + h; i++) {
            System.arraycopy(src, i * width + x, nData, (i - y) * w, w);//y内存块复制
            if (i % 2 == 0) {
                System.arraycopy(src, uv_index_src + (i >> 1) * width, nData, uv_index_dst + (i >> 1) * w, w);//uv内存块复制
            }
        }
        return nData;
    }

    /**
     * 剪切数据并且镜像 算法效率14ms
     *
     * @param src
     * @param width
     * @param height
     * @param left
     * @param top
     * @param clip_w
     * @param clip_h
     * @return
     */
    public static byte[] clipMirrorNV21(byte[] src, int width, int height, int left, int top, int clip_w, int clip_h) {
        if (left > width || top > height) {
            return null;
        }
        //取偶
        int x = left, y = top;
        int w = clip_w, h = clip_h;
        int y_unit = w * h;
        int src_unit = width * height;
        int uv = y_unit / 2;
        byte[] nData = new byte[y_unit + uv];
        int nPos = (y - 1) * width;
        int mPos;
        for (int i = y, len_i = y + h; i < len_i; i++) {
            nPos += width;
            mPos = src_unit + i / 2 * width;
            for (int j = x, len_j = x + w; j < len_j; j++) {
                nData[(i - y + 1) * w - j + x - 1] = src[nPos + j];
                if (i % 2 == 0) {
                    int m = y_unit + ((i - y) / 2 + 1) * w - j + x - 1;
                    if (m % 2 == 0) {
                        m++;
                        nData[m] = src[mPos + j];
                        continue;
                    }
                    m--;
                    nData[m] = src[mPos + j];
                }
            }
        }
        return nData;
    }

    /**
     * 任意裁剪YUV420SP格式，这种存储格式决定了 left, top, clipW, clipH 必须是偶数。
     * 如果传进来的不是偶数，函数内部会处理成偶数，left、top会向右下或者左上偏移一个像素
     * 显示的图像颜色不对，不是绿就是蓝。
     * 裁剪的原理：
     * 1. NV21(YUV420SP)的存储格式：先逐行存储Y，再交叉存储VU，Y的大小是width*height，VU的大小是 width*height/2；
     * 2. 裁剪时，可以想象原始数据byte[] src是一个二维数据(顺序存放了而已)，长度是width，高度是height*3/2；
     * 3. 需要裁剪的部分是(left,top), (left+clipW, top+clipH)，大小为(clipW*clipH*3/2)按照步长为width；
     * 4. 新建一个byte[] data,一个for循环把Y复制到data，再一个for循环，把VU复制到data，裁剪完成。
     * 效率：设备RK3399，从2160*3840的原始图像中，裁剪出1920*1920大概需要16447669-32777211ns(16-32ms)，均值应该在25ms左右
     *
     * @param src    原始数据
     * @param width  原始图像的width
     * @param height 原始图像height
     * @param left   裁剪区域左上角的x
     * @param top    裁剪区域左上角的y
     * @param clipW  裁剪的宽度
     * @param clipH  裁剪的高度
     * @return 裁剪后的图像数据
     */
    public static byte[] clipNV212(byte[] src, int width, int height, int left, int top, int clipW, int clipH) {
        // 目标区域取偶(YUV420SP要求图像高度是偶数)
        long begin = System.nanoTime();
        if (left % 2 == 1) {
            left--;
        }
        if (top % 2 == 1) {
            top--;
        }
        int bottom = top + clipH;
        // 裁剪后的占用的大小
        int size = clipW * clipH * 3 / 2;
        final byte[] data = new byte[size];
        // 按照YUV420SP格式，复制Y
        for (int i = top; i < bottom; i++) {
            System.arraycopy(src, left + i * width, data, (i - top) * clipW, clipW);
        }
        // 按照YUV420SP格式，复制UV
        int startH = height + top / 2;
        int endH = height + bottom / 2;
        for (int i = startH; i < endH; i++) {
            System.arraycopy(src,
                    left + i * width,
                    data,
                    (i - startH + clipH) * clipW,  // i - startH 容易理解，+ clipH 是在Y数据后面添加数据
                    clipW);
        }
        long end = System.nanoTime();
        Log.d("NV21Utils", "clip use: " + (end - begin) + "ns");
        //final int cw = clipW;
        //final int ch = clipH;
        //ThreadPoolManager.getInstance().execute(() -> save(data, cw, ch));
        return data;
    }

    private static int count = 0;

    private static void save(byte[] bytes, int width, int height) {
        YuvImage image = new YuvImage(bytes, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream outputSteam = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 70, outputSteam);
        byte[] jpegData = outputSteam.toByteArray();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bmp = BitmapFactory.decodeStream(new ByteArrayInputStream(jpegData), null, options);
        try {
            saveBitmapToFile(bmp, Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "clip" + File.separator
                    + "test" + (count++ % 50) + ".jpg");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static File saveBitmapToFile(Bitmap bmp, String name) throws IOException {
        BufferedOutputStream os = null;
        File file = null;
        try {
            file = new File(name);
            if (!new File(file.getParent()).exists()) {
                new File(file.getParent()).mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ex) {
                    Log.e("Cache", ex.getMessage());
                }
            }
        }
        return file;
    }

//    private fun yuv420ToNv21(image: ImageProxy): ByteArray? {
//        val planes = image.planes
//        val yBuffer = planes[0].buffer
//        val uBuffer = planes[1].buffer
//        val vBuffer = planes[2].buffer
//        val ySize: Int = yBuffer.remaining()
//        val uSize: Int = uBuffer.remaining()
//        val vSize: Int = vBuffer.remaining()
//        if (nv21Rotate == null) {
//            val size = image.height * image.width
//            nv21Rotate = ByteArray(size * 3 / 2)
//        }
//
//        yBuffer.get(nv21Rotate, 0, ySize)
//        vBuffer.get(nv21Rotate, ySize, vSize)
//        val u = ByteArray(uSize)
//        uBuffer.get(u)
//
//        //每隔开一位替换V，达到VU交替
//        var pos = ySize + 1
//        if (nv21Rotate != null) {
//            for (i in 0 until uSize) {
//                if (i % 2 == 0) {
//                    nv21Rotate!![pos] = u[i]
//                    pos += 2
//                }
//            }
//        }
//
//        return nv21Rotate
//    }
}









