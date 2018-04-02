package com.example.administrator.signin.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.signin.Cal;
import com.example.administrator.signin.R;
import com.example.administrator.signin.calendar.CalendarMainActivity;
import com.example.administrator.signin.modul.GeoPoint;
import com.example.administrator.signin.modul.Record;
import com.example.administrator.signin.modul.StudentCourse;
import com.example.administrator.signin.modul.User;
import com.example.administrator.signin.schedule.ScheduleMainActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView arrive,calendar;
    private TextView exit;
    private android.widget.Button record;
    public LocationClient mLocationClient = null;
    private TextView positionText;

    private MapView mapView;

    private BaiduMap baiduMap;

    private boolean isFirstLocate = true;
    public BDLocationListener myListener = new MyLocationListener();
    private Button not;
    private int id;
    private double Latitude;
    private double Longitude;
    private double Latitude2;
    private double Longitude2;
    private Long time;
    private Button setCourse;
    private String cId;
    public static User user;
    private Button setuser;
    private android.widget.LinearLayout activitymain;
    public static GeoPoint point;
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16f));
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
//        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
               DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
//        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        positionText = (TextView) findViewById(R.id.position_text_view);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }


        initLocation();
        mLocationClient.start();
        user = BmobUser.getCurrentUser(MainActivity.this,User.class);
        arrive = (TextView) findViewById(R.id.arrive);
//        user = BmobUser.getCurrentUser(ScheduleMainActivity.this,User.class);
        exit = (TextView) findViewById(R.id.exit);
        calendar=(TextView) findViewById(R.id.calendar);
        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                View conView = layoutInflater.inflate(R.layout.start_sign_in_layout, null);
                final EditText editText = (EditText)conView.findViewById(R.id.start_sign_in_id);
                builder.setView(conView);
                builder.setTitle("请输入4位签到码");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = editText.getText().toString();
                        if (str.length() != 4) {
                            Toast.makeText(MainActivity.this, "签到码没有4位", Toast.LENGTH_SHORT).show();
                        } else {
                            id = Integer.valueOf(str);
                            BmobQuery<GeoPoint> query = new BmobQuery<GeoPoint>();
                            query.addWhereEqualTo("ID", id);
                            query.findObjects(MainActivity.this, new FindListener<GeoPoint>() {
                                @Override
                                public void onSuccess(List<GeoPoint> list) {
                                    if (list.size() != 1){
                                        Toast.makeText(MainActivity.this, "签到码错误", Toast.LENGTH_SHORT).show();
                                    }else{
                                        final GeoPoint geoPoint = list.get(0);
                                        Latitude2 = geoPoint.getLatitude();
                                        Longitude2 = geoPoint.getLongitude();
                                        time = geoPoint.getTime();
                                        cId = geoPoint.getcId();
                                        BmobQuery<StudentCourse> query1 = new BmobQuery<StudentCourse>();
                                        query1.addWhereEqualTo("sId",Integer.valueOf(user.getUsername()));
                                        query1.addWhereEqualTo("cId",cId);
                                        query1.findObjects(MainActivity.this, new FindListener<StudentCourse>() {
                                            @Override
                                            public void onSuccess(List<StudentCourse> list) {
                                                if (!list.isEmpty()){
                                                    double dis = Cal.GetShortDistance(Longitude, Latitude, Longitude2, Latitude2);
                                                    // Toast.makeText(MainActivity.this, ""+dis, Toast.LENGTH_SHORT).show();
                                                    if (dis <= 2000) {
                                                        signOn();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "请退出程序并离老师更近些签到", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else {
                                                    Toast.makeText(MainActivity.this, "你没有选这节课", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });
                        }
                    }
                });
                builder.show();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Record record = new Record();
                record.setOut_time(System.currentTimeMillis());
                String str = getSharedPreferences("data",MODE_PRIVATE).getString("Object",user.getObjectId());
                record.update(MainActivity.this, str, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "签退成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(MainActivity.this, "签退失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,CalendarMainActivity.class));
            }
        });
    }


    //map
    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            Toast.makeText(this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sign_in) {
            Intent intent=new Intent(MainActivity.this, ScheduleMainActivity.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.curriculum) {
            Intent intent=new Intent(MainActivity.this,SetCourse.class);
            startActivity(intent);
        } else if (id == R.id.check) {
            Intent intent=new Intent(MainActivity.this,RecordActivity.class);
            startActivity(intent);
        } else if (id == R.id.change_key) {
            Intent intent=new Intent(MainActivity.this,newPassword.class);
            startActivity(intent);
        } else if (id == R.id.news) {
            Intent intent=new Intent(MainActivity.this,NewsMainActivity.class);
            startActivity(intent);
        }else if(id == R.id.exit){
            BmobUser.logOut(this);
            startActivity(new Intent(this,LoginActivity.class));
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    //
    private void signOn(){
        BmobQuery<Record> query = new BmobQuery<Record>();
        query.addWhereGreaterThanOrEqualTo("in_time", time);
        query.addWhereEqualTo("name", user.getName());
        query.findObjects(MainActivity.this, new FindListener<Record>() {
            @Override
            public void onSuccess(List<Record> list) {
                if (list.isEmpty()) {
                    final Record record = new Record();
                    record.setName(user.getName());
                    record.setID(Integer.valueOf(user.getUsername()));
                    record.setIn_time(System.currentTimeMillis());
                    record.setOut_time(0L);
                    record.setcId(cId);
                    record.save(MainActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(MainActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                            getSharedPreferences("data", MODE_PRIVATE).edit().putString("Object", record.getObjectId()).apply();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(MainActivity.this, "签到失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "已签到!请不要重复签到", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Longitude = location.getLongitude();
            Latitude = location.getLatitude();
            //Receive Location
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
            //Receive Location
            if (point != null) {
                point.setLatitude(location.getLatitude());
                point.setLongitude(location.getLongitude());
                point.update(MainActivity.this, point.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
    }
    }

}


