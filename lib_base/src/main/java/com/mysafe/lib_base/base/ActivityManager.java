package com.mysafe.lib_base.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ActivityManager {

    //activity 栈管理
    private Stack<Activity> activityStack ;

    public int getActivitys() {
        return activityStack.size();
    }
    //添加Activity
    public void addActivity(Activity activity){
            activityStack.add(activity);
    }

    /**
     * 移除activity
     */
    public void removeActivity( Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    //获取栈顶 Activity
    public Activity getTopActivity(){
        return activityStack.lastElement();
    }

    //获取栈底 Activity
    public Activity getBottomActivity(){
        return activityStack.firstElement();
    }

    public void finishAllActivity() {
        while(!activityStack.empty()){
            activityStack.pop().finish();
        }
    }
    public void finishTopActivity() {
        finishActivity(activityStack.lastElement());
    }
    public Activity findActivity(Class<?> cls){
        for(Activity activity : activityStack){
            if(activity.getClass() == cls){
                return activity;
            }
        }

        return null;
    }

    public void finishActivity(Activity activity){
        activity.finish();
        removeActivity(activity);
    }

    public void finishActivity(Class<?> cls){
        Activity activity = findActivity(cls);
        removeActivity(activity);
        activity.finish();
    }

    public void finishOtherActivity(Class<?> cls){
        List<Activity> activityList = new ArrayList<>();
        for(Activity activity:activityStack){
            if(activity.getClass() != cls){
                activityList.add(activity);
            }
        }

        for(Activity activity:activityList){
            finishActivity(activity);
        }
    }

    private ActivityManager(){
        activityStack = new Stack<>();
    }

    public static ActivityManager Instance(){
        return ActivityManagerHolder.activityManager;
    }

    public static class ActivityManagerHolder{
        private static final ActivityManager activityManager = new ActivityManager();
    }


}