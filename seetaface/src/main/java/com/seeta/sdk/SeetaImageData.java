package com.seeta.sdk;

/**
 * @describe 存储彩色（三通道）或灰度（单通道）图像，像素连续存储，行优先，采用 BGR888 格式存放彩色图像，单字节灰度值存放灰度图像。
 */
public class SeetaImageData {
    /**
     * 图像数据 颜色通道是BGR格式
     * 存储的是连续存放的使用8位无符号整数表示的像素值，存储为[height, width, channels]顺序。彩色图像时三通道以BGR通道排列。
     * 如下图所示，就是展示了高4宽3的彩色图像内存格式。
     * 颜色通道是BGR格式，而很多图像库常见的是RGB或者RGBA(A为alpha不透明度)。
     */
    public byte[] data;
    /**
     * 图像的宽度
     */
    public int width;
    /**
     * 图像的高度
     */
    public int height;
    /**
     * 图像的通道数
     */
    public int channels;

    public SeetaImageData(int width, int height) {
        this(width, height, 1);
    }

    public SeetaImageData(int width, int height, int channels) {
        this.data = new byte[height * width * channels];
        this.width = width;
        this.height = height;
        this.channels = channels;
    }


}