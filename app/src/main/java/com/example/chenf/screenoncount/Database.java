package com.example.chenf.screenoncount;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenf on 2015/10/9.
 */
public class Database {
    //初始化今日表名
    public static final String DB_NAME = "Duration.db";
    public static int VERSION = Integer.parseInt(TimeTransform.getDate(Calendar.getInstance().getTime().getTime()));
    public static String today = "duration" + TimeTransform.getDate(Calendar.getInstance().getTime().getTime());
    public static String Yesterday = "duration" + TimeTransform.getYesterday(Calendar.getInstance().getTime().getTime());
    private static SQLiteDatabase db;
    private static Database dataBase;

    /**
     * 将构造方法私有化
     * @param context
     */
    private Database(Context context){
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }
    /**
     * 获取Database的实例
     */
    public synchronized static Database getDataBase(Context context){
        if (dataBase == null){
            dataBase = new Database(context);
        }
        return dataBase;
    }

    public void refresh(Context context){
        VERSION = Integer.parseInt(TimeTransform.getDate(Calendar.getInstance().getTime().getTime()));
        today = "duration" + TimeTransform.getDate(Calendar.getInstance().getTime().getTime());
        Yesterday = "duration" + TimeTransform.getYesterday(Calendar.getInstance().getTime().getTime());
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 将数据库操作封装，确保不用在其他类中调用数据库
     */
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy){
        refresh(MyApplication.getContext());
        return db.query(table, columns, selection,
                selectionArgs, groupBy, having,
                orderBy);
    }
    public void delete(){
        refresh(MyApplication.getContext());
        Cursor cursor = Statistics.database.query(today, null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                if (!cursor.isLast()){
                    long duration = cursor.getLong(cursor.getColumnIndex("duration"));
                    if (duration<1000) {
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        db.delete(today, "id = ?", new String[]{String.valueOf(id)});
                    }
                }
            }while (cursor.moveToNext());
        }
    }

    public int getTimes(String thisDate){
        refresh(MyApplication.getContext());
        int times = 0;
        Cursor cursor =db.query("duration" + thisDate,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                times += 1;
            }while(cursor.moveToNext());
        }
        return times;
    }
    public int getTimes(){
        refresh(MyApplication.getContext());
        int times = 0;
        Cursor cursor =db.query(today,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                times += 1;
            }while(cursor.moveToNext());
        }
        return times;
    }


    public void save_start(long start_time){
        refresh(MyApplication.getContext());
        ContentValues values = new ContentValues();
        values.put("start",start_time);
        db.insert(today,null,values);
    }
    public void save_end(long start_time,long end_time,Long duration){
        refresh(MyApplication.getContext());
        ContentValues values = new ContentValues();
        values.put("end",end_time);
        values.put("duration",duration);
        db.update(today, values, "start=?", new String[]{String.valueOf(start_time)});
    }
    public void save_end1(long start_time,long end_time,Long duration){
        refresh(MyApplication.getContext());
        ContentValues values = new ContentValues();
        values.put("end",end_time);
        values.put("duration",duration);
        db.update(Yesterday, values, "start=?", new String[]{String.valueOf(start_time)});
    }
    public String count_time(){
        refresh(MyApplication.getContext());
        long count = 0;
        Cursor cursor =db.query(today,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                count += cursor.getLong(cursor.getColumnIndex("duration"));
            }while (cursor.moveToNext());
        }
        /*Date date = new Date(count);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(date);
        return time;*/
        long between=count/1000;//除以1000是为了转换成秒

        long day=between/(24*3600);

        long hour=between%(24*3600)/3600;

        long minute=between%3600/60;

        long second=between%60;
        String duration;
        if (day>0){
            duration = day+"天"+hour+"小时"+minute+"分钟"+second+"秒";
        }else if (hour>0){
            duration =hour+"小时"+minute+"分钟"+second+"秒";
        }else if (minute>0){
            duration =minute+"分钟"+second+"秒";
        }else {
            duration =second+"秒";
        }
        return duration;
    }
    public String count_time(String thisDate){
        refresh(MyApplication.getContext());
        long count = 0;
        Cursor cursor =db.query("duration" + thisDate,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                count += cursor.getLong(cursor.getColumnIndex("duration"));
            }while (cursor.moveToNext());
        }
        /*Date date = new Date(count);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = dateFormat.format(date);
        return time;*/
        long between=count/1000;//除以1000是为了转换成秒

        long day=between/(24*3600);

        long hour=between%(24*3600)/3600;

        long minute=between%3600/60;

        long second=between%60;
        String duration;
        if (day>0){
            duration = day+"天"+hour+"小时"+minute+"分钟"+second+"秒";
        }else if (hour>0){
            duration =hour+"小时"+minute+"分钟"+second+"秒";
        }else if (minute>0){
            duration =minute+"分钟"+second+"秒";
        }else {
            duration =second+"秒";
        }
        return duration;
    }
}
