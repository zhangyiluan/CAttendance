package com.example.administrator.signin.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.signin.R;
import com.example.administrator.signin.calendar.Tool.FileTool;
import com.example.administrator.signin.calendar.activity.ADDDouteActivity;
import com.example.administrator.signin.calendar.routeinfo.AllInfo;
import com.example.administrator.signin.calendar.routeinfo.DayInfo;
import com.example.administrator.signin.calendar.routeinfo.Info;
import com.example.administrator.signin.calendar.view.ScheduleView;

public class CalendarMainActivity extends AppCompatActivity {

    private static String yearKey  = "year";
    private static String monthKey = "month";
    private static String daykey   = "day";
    private static String hourkey  = "hour";
    private static String minuteKey = "minute";
    private static String dataKey  = "data";

    private ScheduleView scheduleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity_main);
        scheduleView = (ScheduleView)findViewById(R.id.schedule_view);
        scheduleView.setAddRouteListener(new ScheduleView.AddRouteListener() {
            @Override
            public void addRoute(View view){
                if(view == null){
                    Toast.makeText(getApplicationContext(),"请选择日期", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), ADDDouteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(yearKey,scheduleView.getCurrentYear());
                bundle.putInt(monthKey,scheduleView.getCurrentMonth());
                bundle.putInt(daykey,scheduleView.getSelectDay());
                intent.putExtras(bundle);
                startActivityForResult(intent,100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("--->"+requestCode+" "+resultCode);
        if(requestCode == 100 && resultCode == 0){
            System.out.println("--->+"+requestCode+" "+resultCode+data);
            Bundle bundle = data.getExtras();
            int year = bundle.getInt(yearKey);
            int month = bundle.getInt(monthKey);
            int day = bundle.getInt(daykey);
            int hour = bundle.getInt(hourkey);
            int minute = bundle.getInt(minuteKey);
            String mydata = bundle.getString(dataKey);
            System.out.println("--->my"+year+" "+month+" "+day+" "+hour+" "+minute+" "+mydata);
            try {
                saveRouteData(year,month,day,hour,minute,mydata);
                scheduleView.mySelect(year,month,day);
                System.out.println("--->++"+requestCode+" "+resultCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //保存文件
    private void saveRouteData(int year,int month,int day
            ,int hour,int minute,String data) throws Exception {

        String infoKey = FileTool.getInfoKey(hour,minute);
        String dayinfoKey = FileTool.getDayInfoKey(year,month,day);

        AllInfo infos = FileTool.getAllInfo(getApplicationContext());
        if(infos == null){infos = new AllInfo();}

        DayInfo dayInfo = infos.getDayRouteList(dayinfoKey);
        if(dayInfo == null){
            dayInfo = new DayInfo();
        }
        Info info = dayInfo.getInfo(infoKey);
        if(info == null){
            info = new Info();   //创建一个Info存储行程
        }
        info.setYear(year); info.setMonth(month); info.setDay(day);
        info.setHour(hour);info.setMinute(minute);
        info.setData(data);

        System.out.println("--->info.setData(data);"+info.getData());

        dayInfo.addInfo(infoKey,info);
        infos.addDayRouteList(dayinfoKey,dayInfo);

        FileTool.writeAllInfo(getApplicationContext(),infos);

    }
}
