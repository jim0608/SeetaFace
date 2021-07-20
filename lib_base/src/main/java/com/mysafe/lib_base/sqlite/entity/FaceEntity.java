package com.mysafe.lib_base.sqlite.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * 人脸库中的单条人脸记录
 * @author Admin
 */
@Entity(tableName = "face_table")
public class FaceEntity implements Parcelable {
    /**
     * 数据库内ID,人脸id
     */
    @PrimaryKey(autoGenerate = true)
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
    @ColumnInfo(name = "user_name")
    private String userName;
    /**
     * 用户手机号/学号
     */
    @ColumnInfo(name = "user_num")
    private String userNum;
    /**
     * 图片路径
     */
    @ColumnInfo(name = "image_path")
    private String imagePath;
    /**
     * 人脸特征数据
     */
    @ColumnInfo(name = "feature_data")
    private byte[] featureData;
    /**
     * 注册时间
     */
    @ColumnInfo(name = "register_time")
    private long registerTime;

    public FaceEntity(int id, String userName, String userNum, String imagePath, byte[] featureData, long registerTime) {
        this.id = id;
        this.userName = userName;
        this.userNum = userNum;
        this.imagePath = imagePath;
        this.featureData = featureData;
        this.registerTime = registerTime;
    }

    public FaceEntity(String faceName, byte[] featureData, long syncDatetime) {
        userName = faceName;
        this.featureData = featureData;
        registerTime = syncDatetime;
    }

    protected FaceEntity(Parcel in) {
        id = in.readInt();
        userName = in.readString();
        userNum = in.readString();
        imagePath = in.readString();
        featureData = in.createByteArray();
        registerTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(userName);
        dest.writeString(userNum);
        dest.writeString(imagePath);
        dest.writeByteArray(featureData);
        dest.writeLong(registerTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FaceEntity> CREATOR = new Creator<FaceEntity>() {
        @Override
        public FaceEntity createFromParcel(Parcel in) {
            return new FaceEntity(in);
        }

        @Override
        public FaceEntity[] newArray(int size) {
            return new FaceEntity[size];
        }
    };

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

    public byte[] getFeatureData() {
        return featureData;
    }

    public void setFeatureData(byte[] featureData) {
        this.featureData = featureData;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }
}
