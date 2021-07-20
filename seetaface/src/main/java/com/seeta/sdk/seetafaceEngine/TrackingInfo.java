package com.seeta.sdk.seetafaceEngine;

import com.seeta.sdk.SeetaRect;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.HashMap;
import java.util.Map;

public class TrackingInfo {
        public int personId;
        public Mat matBgr;
        public Mat matGray;
        public SeetaRect faceInfo = new SeetaRect();
        public Rect faceRect = new Rect();
        public float score;
        public long birthTime;
        public long lastProcessTime;
        public static Map<String, float[]> name2feats = new HashMap<>();
    }