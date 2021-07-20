package com.seeta.sdk;

/**
 * 人脸检测器需要传入的结构体参数
 */
public class SeetaModelSetting {
    /**
     * 计算设备(CPU or GPU)
     */
    public SeetaDevice device;
    /**
     *  when device is GPU, id means GPU id
     */
    public int id;
    /**
     * 检测器模型
     */
    public String[] model;

    public SeetaModelSetting(int id, String[] models, SeetaDevice dev) {
        this.id = id;
        this.device = dev;
        this.model = new String[models.length];
        for (int i = 0; i < models.length; i++) {
            this.model[i] = models[i];
        }
    }
}
