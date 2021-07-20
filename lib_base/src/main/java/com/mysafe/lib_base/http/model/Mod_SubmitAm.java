package com.mysafe.lib_base.http.model;

import java.util.List;

public class Mod_SubmitAm {

    /**
     *餐次Id
     */
    public int canCiId;
    /**
     * 餐次名称
     */
    public String canCiName;
    /**
     * 人员学号
     */
    public String studentNo;
    /**
     * 日期 格式为 yyyy-mm-dd
     */
    public String forDate;
    /**
     * 选择套餐集合
     */
    public List<Integer> mealIds;

    public int getCanCiId() {
        return canCiId;
    }

    public void setCanCiId(int canCiId) {
        this.canCiId = canCiId;
    }

    public String getCanCiName() {
        return canCiName;
    }

    public void setCanCiName(String canCiName) {
        this.canCiName = canCiName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getForDate() {
        return forDate;
    }

    public void setForDate(String forDate) {
        this.forDate = forDate;
    }

    public List<Integer> getMealIds() {
        return mealIds;
    }

    public void setMealIds(List<Integer> mealIds) {
        this.mealIds = mealIds;
    }
}

