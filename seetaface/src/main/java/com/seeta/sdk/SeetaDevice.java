package com.seeta.sdk;

/**
 * @describe 模型运行的计算设备。
 */
public enum SeetaDevice {
    /**
     * 自动检测，会优先使用 GPU
     */
    SEETA_DEVICE_AUTO(0),
    /**
     * 使用CPU计算
     */
    SEETA_DEVICE_CPU(1),
    /**
     * 使用GPU计算
     */
    SEETA_DEVICE_GPU(2);

    private int value;

    private SeetaDevice(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

