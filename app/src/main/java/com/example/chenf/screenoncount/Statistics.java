package com.example.chenf.screenoncount;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by chenf on 2015/9/28.
 */
public final class Statistics {
    private static Long duration;
    public static Database database = Database.getDataBase(MyApplication.getContext());

    public static void start(){
        /**
         * 读取当前时间，即开始时间
         * 把时间以long格式存储
         */
        //database.refresh(MyApplication.getContext());
        Long start = Calendar.getInstance().getTime().getTime();
        database.save_start(start);
    }

    /**
     * 读取当前时间，即结束时间
     * 计算时长，并将结束时间和时长以Long格式存入数据库
     */
    public static void end(){
        database.delete();
        Long start;
        Long end2;
        Long end = Calendar.getInstance().getTime().getTime();
        Cursor cursor = database.query(Database.today, null, null, null, null, null, null);
        if(!cursor.moveToFirst()){
            try {
                cursor = database.query(Database.Yesterday, null, null, null, null, null, null);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (cursor.moveToLast()) {
            //结束时间是否为空，为空则储存
            if (cursor.isNull(cursor.getColumnIndex("end"))) {
                start = cursor.getLong(cursor.getColumnIndex("start"));
                //结束时间和开始时间是否为同一天，是则直接储存，否则分开储存
                if (TimeTransform.getDate(start).equals(TimeTransform.getDate(end))) {
                    duration = end - start;
                    //时长是否超过一秒，超过则储存，否则把此数据删除
                    if (duration>1000) {
                        database.save_end(start, end, duration);
                    }
                }else {
                    end2 = TimeTransform.getDate_mill(TimeTransform.getDate(end))-1;
                    duration = end2 - start;
                    if (duration>1000) {
                        database.save_end1(start, end2, duration);
                    }
                    Long start1 = TimeTransform.getDate_mill(TimeTransform.getDate(end))+1;
                    database.save_start(start1);
                    Long end1 = Calendar.getInstance().getTime().getTime();
                    Long duration1 = end1 - start1;
                    if (duration1>1000) {
                        database.save_end(start1, end1, duration1);
                    }
                }
            }
        }
    }




}
