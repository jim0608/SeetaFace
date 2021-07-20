package com.mysafe.lib_base.http.model;

import java.util.List;

/**
 * 套餐信息
 */
public class Mod_MealAM {

    /**
     * 订单Id
     */
    public int orderId;

    /**
     * 套餐Id
     */
    public int mealId;

    /**
     * 套餐名称
     */
    public String mealName;

    /**
     * 套餐数量（用于获取人员订餐的业务中）
     */
    public int mealCount;

    /**
     * 套餐图片
     */
    public String mealAvatar;
    /**
     * 套餐金额(单价 若存在多份  则自己计算总价 ToalMoney*MealCount)
     */
    public double toalMoney;
    /**
     * 串口名称
     */
    private List<String> provideMealWindows;//套餐对应的所有窗口号
    /**
     * 菜品名称
     */
    private List<String> recipeNames;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public int getMealCount() {
        return mealCount;
    }

    public void setMealCount(int mealCount) {
        this.mealCount = mealCount;
    }

    public String getMealAvatar() {
        return mealAvatar;
    }

    public void setMealAvatar(String mealAvatar) {
        this.mealAvatar = mealAvatar;
    }

    public double getToalMoney() {
        return toalMoney;
    }

    public void setToalMoney(double toalMoney) {
        this.toalMoney = toalMoney;
    }

    public List<String> getProvideMealWindows() {
        return provideMealWindows;
    }

    public void setProvideMealWindows(List<String> provideMealWindows) {
        this.provideMealWindows = provideMealWindows;
    }

    public List<String> getRecipeNames() {
        return recipeNames;
    }

    public void setRecipeNames(List<String> recipeNames) {
        this.recipeNames = recipeNames;
    }
}
