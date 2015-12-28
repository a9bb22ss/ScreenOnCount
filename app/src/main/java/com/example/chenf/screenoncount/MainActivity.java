package com.example.chenf.screenoncount;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity implements RecyclerAdapter.OnRecyclerViewListener{

    private List<Duration> durations = new ArrayList<>();
    private String thisDate = null;
    private RecyclerAdapter recyclerAdapter;

    /**
     *界面组件初始化
     */
    private RecyclerView recyclerView;
    private TextView date;
    private TextView times;
    private TextView all_duration;
    private refreshReceiver refresh;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        Intent start_service = new Intent(this, Service.class);
        startService(start_service);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        refresh = new refreshReceiver();
        registerReceiver(refresh, intentFilter);
        durations = initList();//初始化时长数据
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerAdapter = new RecyclerAdapter(durations);
        recyclerAdapter.setOnRecyclerViewListener(this);
        recyclerView.setAdapter(recyclerAdapter);


        int tmp = Statistics.database.getTimes();
        Date date_cotent = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String time_str = sdf.format(date_cotent);
        date.setText(time_str);
        times.setText(String.valueOf(tmp));
        all_duration.setText(Statistics.database.count_time());

        swipeRefresh.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        times = (TextView) findViewById(R.id.times);
        date = (TextView) findViewById(R.id.date);
        all_duration = (TextView) findViewById(R.id.all_duration);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
    }
    private List<Duration> initList(){
        Statistics.database.delete();
        List<Duration> list = new ArrayList<>();
        //new Thread(new Runnable() {
           // @Override
            //public void run() {
        String thisday = "duration" + TimeTransform.getDate(Calendar.getInstance().getTime().getTime());
        if(thisDate != null){
            thisday = "duration" + thisDate;
        }
        Cursor cursor = null;
                try {
                    cursor = Statistics.database.query(thisday,null,null,null,null,null,null);
                    if (cursor.moveToFirst()){
                        int i =1;
                        do {
                            Duration duration = new Duration();
                            duration.setSequenceNum(i);
                            long count = cursor.getLong(cursor.getColumnIndex("duration"));
                            long start = cursor.getLong(cursor.getColumnIndex("start"));
                            long end = cursor.getLong(cursor.getColumnIndex("end"));
                            long between=count/1000;//除以1000是为了转换成秒

                            long day=between/(24*3600);

                            long hour=between%(24*3600)/3600;

                            long minute=between%3600/60;

                            long second=between%60;
                            String time;
                            if (day>0){
                                time = day+"天"+hour+"小时"+minute+"分钟"+second+"秒";
                            }else if (hour>0){
                                time =hour+"小时"+minute+"分钟"+second+"秒";
                            }else if (minute>0){
                                time =minute+"分钟"+second+"秒";
                            }else {
                                time =second+"秒";
                            }
                            duration.setDuration(time);
                            duration.setStart_time(TimeTransform.LongToString(start));
                            if(end != 0) {
                                duration.setEnd_time(TimeTransform.LongToString(end));
                            }
                            list.add(duration);
                            i += 1;
                        }while (cursor.moveToNext());
                    }
                }catch (Exception e){
                    Toast.makeText(this,"这一天没有数据",Toast.LENGTH_LONG).show();
                }

           // }
        //}).start();

        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent start_service = new Intent(this, Service.class);
        startService(start_service);
        unregisterReceiver(refresh);
    }

    @Override
    public void onItemClick(int position) {
        Duration this_duration = durations.get(position);
        Bundle bundle = new Bundle();
        Intent intent = new Intent(MainActivity.this,ParticularsActivity.class);
        bundle.putSerializable("duration",this_duration);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(int position) {
        return false;
    }

    class refreshReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    }

    //刷新数据和界面
    private void refresh(){
        new  Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                durations = initList();//初始化时长数据
                int tmp = Statistics.database.getTimes();
                Date date_cotent = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

                String time_str = sdf.format(date_cotent);
                recyclerAdapter.notifyDataSetChanged();
                date.setText(time_str);
                times.setText(String.valueOf(tmp));
                all_duration.setText(Statistics.database.count_time());
            }
        },100);
    }
@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if (id == R.id.history){
            DatePickerFragment datePicker = new DatePickerFragment();
            datePicker.show(getFragmentManager(),"datePicker");
        }

        return super.onOptionsItemSelected(item);
    }
}
