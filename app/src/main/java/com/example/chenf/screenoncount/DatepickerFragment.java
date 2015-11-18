package com.example.chenf.screenoncount;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by chenf on 2015/11/18.
 */
public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        String date = TimeTransform.getDate(c.getTime().getTime());
        Log.d("OnDateSet", "select " + date);
        String thisday = "duration" + date;
        try {
            Cursor cursor = Statistics.database.query(thisday,null,null,null,null,null,null);
            if (cursor.moveToFirst()){
                Intent intent = new Intent(MyApplication.getContext(), MainActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
                Log.d("OnDateSet", "开启成功");
            }
        }catch (Exception e) {
            Toast.makeText(MyApplication.getContext(),"这一天没有数据",Toast.LENGTH_LONG).show();
        }
    }
}