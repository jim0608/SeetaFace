package com.mysafe.lib_base.http;

import com.mysafe.lib_base.http.model.Mod_CanCiMealAM;
import com.mysafe.lib_base.http.model.Mod_FacialFeaturesAM;
import com.mysafe.lib_base.http.model.Mod_Page;
import com.mysafe.lib_base.http.model.Mod_Result;
import com.mysafe.lib_base.http.model.Mod_SubmitAm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager_HttpRequest extends HttpRequest_Base {

    //region 单例声明
    private volatile static Manager_HttpRequest singleton;

    private Manager_HttpRequest() {

    }

    public static Manager_HttpRequest GetSingleton() {
        if (singleton == null) {
            synchronized (Manager_HttpRequest.class) {
                if (singleton == null) {
                    singleton = new Manager_HttpRequest();
                }
            }
        }
        return singleton;
    }
//endregion

    /**
     * 初始化请求地址和Head内容
     *

     * @param devicesNo   设备的唯一序列号,用于验证设备身份
     * @param devicesType 设备的类型,Customer = 1 Staff = 2,Public = 3
     *                    deviceNo : 设备编号
     *                    deviceType : 设备类型
     *                    {@link com.mysafe.lib_base.StandardConstants#TsugaiDeviceType_Customer} 扫脸人员端
     *                    {@link com.mysafe.lib_base.StandardConstants#TsugaiDeviceType_Staff}配餐职工端
     *                    {@link com.mysafe.lib_base.StandardConstants#TsugaiDeviceType_Public}公众查询点餐端
     */
    public void InitHostAndPortAndHeader(String baseUrl, String devicesNo, String devicesType) {
        _BaseRequestUrl = baseUrl;
        HttpRequest_Base.devicesNo = devicesNo;
        HttpRequest_Base.devicesType = devicesType;
        saveAddressUrl();
    }


    //region 共用接口

    /**
     * 测试服务器地址是否正确
     *
     * @param callBack 如果请求成功并返回"success"字符串则代表能够正确访问,否则访问服务器失败;
     */
    public void HttpRequest_Ping(RequestCallBack<Mod_Result> callBack) {
        HttpRequest_AsyncGet_Json("ordermeal/ping", callBack);
    }

    //region 人脸相关接口

    /**
     * 获取服务器上指定日期之后的人脸信息
     *
     * @param date     上次同步的时间 格式:yyyy-mm-dd
     * @param callBack
     */
    public void HttpRequest_Arc_DownloadFacedData(String date, RequestCallBack<Mod_Result<List<Mod_FacialFeaturesAM>>> callBack) {
        Map<String, Object> data = GetNewParams();
        data.put("date", date);
        HttpRequest_AsyncPost_Json("ordermeal/om_downloadfaceddata", data, callBack);
    }

    /**
     * 分页获取服务器上指定日期之后的人脸信息
     *
     * @param page     页码
     * @param rows     一页的数量
     * @param date     上次同步时间 格式:yyyy-mm-dd
     * @param callBack
     */
    public void HttpRequest_Arc_DownloadFacedDatePage(int page, int rows, String date, RequestCallBack<Mod_Page<Mod_FacialFeaturesAM>> callBack) {
        Map<String, Object> data = GetNewParams();
        data.put("page", page);
        data.put("rows", rows);
        data.put("date", date);
        HttpRequest_AsyncPost_Json("ordermeal/om_downloadfaceddatapage", data, callBack);
    }
    //region Staff端调用接口


//endregion

    //region Public端调用接口

    /**
     * 根据人员证号查询该人员是否存在
     *
     * @param studentNo 人员学号
     */
    public void HttpRequest_Public_CheckStudent(String studentNo, RequestCallBack<Boolean> callBack) {
        Map<String, Object> data = GetNewParams();
        data.put("studentNo", studentNo);
        HttpRequest_AsyncPost_Json("ordermeal/om_checkstudent", data, callBack);
    }

    /**
     * 上传图片
     *
     * @param pic      拍摄的人脸照片，单张上传
     * @param callBack 上传后的图片地址存放在对象中的Message属性中
     */
    public void HttpRequest_Public_UploadPicture(byte[] pic, RequestCallBack<Mod_Result> callBack) {
        Map<String, Object> data = GetNewParams();
        data.put("pic", pic);
        HttpRequest_AsyncPost_Json("ordermeal/om_uploadpicture", data, callBack);
    }

    /**
     * 上传人员人脸图片[正,左,右]
     *
     * @param studentNo 人员学号
     * @param fronturl  人员正面照片
     * @param lefturl   人员左侧面照片
     * @param righturl  人员右侧面照片
     */
    public void HttpRequest_Public_UploadStudentFacePic(String studentNo, String fronturl, String lefturl, String righturl, RequestCallBack<Mod_Result> callBack) {
        Map<String, Object> data = GetNewParams();
        data.put("studentNo", studentNo);
        data.put("fronturl", fronturl);
        data.put("lefturl", lefturl);
        data.put("righturl", righturl);
        HttpRequest_AsyncPost_Json("ordermeal/om_uploadstudentfacepic", data, callBack);
    }


    /**
     * 获取人员当前所定套餐
     *
     * @param studentNo 人员学号
     * @param date      日期 格式是:yyyy-mm-dd
     * @param callBack
     */
    public void HttpRequest_Public_OrderMealByStudentNo(String studentNo, String date, RequestCallBack<List<Mod_CanCiMealAM>> callBack) {
        Map<String, Object> data = GetNewParams();
        data.put("studentNo", studentNo);
        data.put("date", date);
        HttpRequest_AsyncPost_Json("ordermeal/om_ordermealbystudentno", data, callBack);
    }

    /**
     * 提交人员的订餐信息
     *
     * @param mod_submitAm 订餐信息模型
     * @param callBack
     */
    public void HttpRequest_Public_OrderSubmit(Mod_SubmitAm mod_submitAm, RequestCallBack<Mod_Result> callBack) {
        Map<String, Object> data = GetNewParams();
        data.put("mod_submitAm", mod_submitAm);
        HttpRequest_AsyncPost_Json("ordermeal/om_ordersubmit", data, callBack);
    }

    Map<String, Object> GetNewParams() {
        return new HashMap<>();
    }
//endregion

}
