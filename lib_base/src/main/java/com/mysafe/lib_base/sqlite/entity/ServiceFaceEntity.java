package com.mysafe.lib_base.sqlite.entity;


/**
 * 人脸库中的单条人脸记录
 * @author Admin
 */
public class ServiceFaceEntity {
    /**
     * 数据库内ID,人脸id
     */
    private int id;
//
//    /**
//     * 人脸id，
//     */
//    @ColumnInfo(name = "face_id")
//    private int faceId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户手机号/学号
     */
    private String userNum;
    /**
     * 图片路径
     */
    private String imagePath;
    /**
     * 人脸特征数据
     */
    private String featureData;
    /**
     * 注册时间
     */
    private long registerTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFeatureData() {
        return featureData;
    }

    public void setFeatureData(String featureData) {
        this.featureData = featureData;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }
}
