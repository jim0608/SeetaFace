package com.mysafe.lib_base.http.model;

import java.util.List;

/**
 * 餐次数据
 */
public class Mod_CanCiMealAM {
    /**
     * 餐次ID
     */
    public int canCiId;
    /**
     * 餐次名称
     */
    public String canCiName;
    /**
     * 人员姓名
     */
    public String studentName;
    /**
     * 人员头像
     */
    public String studentAvatar;
    /**
     * 套餐详情信息
     */
    public List<Mod_MealAM> meals;

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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentAvatar() {
        return studentAvatar;
    }

    public void setStudentAvatar(String studentAvatar) {
        this.studentAvatar = studentAvatar;
    }

    public List<Mod_MealAM> getMeals() {
        return meals;
    }

    public void setMeals(List<Mod_MealAM> meals) {
        this.meals = meals;
    }
}
