package com.example.administrator.signin_Teacher.view;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.administrator.signin_Teacher.R;
import com.example.administrator.signin_Teacher.adapter.UnSignOnAdapter;
import com.example.administrator.signin_Teacher.calendar.CalendarMainActivity;
import com.example.administrator.signin_Teacher.module.Course;
import com.example.administrator.signin_Teacher.module.GeoPoint;
import com.example.administrator.signin_Teacher.module.NotArrive;
import com.example.administrator.signin_Teacher.module.Record;
import com.example.administrator.signin_Teacher.module.StudentCourse;
import com.example.administrator.signin_Teacher.module.User;
import com.example.administrator.signin_Teacher.schedule.ScheduleMainActivity;
import com.example.administrator.signin_Teacher.tool.MyReceiver;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SwipeRefreshLayout.OnRefreshListener,
FloatingActionButton.OnClickListener{
    private static final int UPDATE_TEXT1=1;
    private static final int UPDATE_TEXT2=2;
    private DevicePolicyManager dpm;
    private ComponentName component;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private android.widget.Button record;
    private TextView startsignin,map,calendar,fine;
    private TextView shownum;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private int id;
    private String cId;
    public static GeoPoint point;
    public static List<Course> mList;
    public static User user;
    private List<Record> jList = new ArrayList<>();
    private List<StudentCourse> mStudentList = new ArrayList<>();
    private TextView signOnCount,details;
    private RecyclerView mRecycler;
    public static List<StudentCourse> mUnSignOn = new ArrayList<>();
    private UnSignOnAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        user = BmobUser.getCurrentUser(MainActivity.this,User.class);
        getPermission();
        initWidget();
        setNavAndToolBar();
        initSwipeRefreshLayout();
        initData();
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        mRecycler.setLayoutManager(manager);
        mUnSignOn = new ArrayList<StudentCourse>();
        adapter = new UnSignOnAdapter(MainActivity.this,R.id.sign_on_person,mUnSignOn);
        mRecycler.setAdapter(adapter);
        if (point == null) {
            signOnCount.setText("0/0");
        }
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DetailsActivity.class));
            }
        });
    }

    private void setNavAndToolBar(){
        //侧滑菜单
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    /**
     * 获取权限
     */
    private void getPermission(){
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
    }
    /**
     * 获取控件
     */
    private void initWidget(){
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        component = new ComponentName(this, MyReceiver.class);
        this.map = (TextView) findViewById(R.id.map);
        this.calendar=(TextView) findViewById(R.id.calendar);
        this.fine=(TextView) findViewById(R.id.fine);
        this.shownum = (TextView) findViewById(R.id.show_num);
//        user = BmobUser.getCurrentUser(ScheduleMainActivity.this,User.class);
        this.startsignin = (TextView) findViewById(R.id.start_sign_in);
        signOnCount = (TextView)findViewById(R.id.sign_on_count);
        details = (TextView)findViewById(R.id.details);
        mRecycler = (RecyclerView)findViewById(R.id.sign_on_person);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        startsignin.setOnClickListener(this);
        shownum.setOnClickListener(this);
        map.setOnClickListener(this);
        calendar.setOnClickListener(this);
       fine.setOnClickListener(this);
    }

    /**
     * 刷新控件设置
     */
    private void initSwipeRefreshLayout(){
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
    }

    private void initData(){
        BmobQuery<GeoPoint> query1 = new BmobQuery();
        query1.addWhereEqualTo("tId",Integer.valueOf(user.getUsername()));
        query1.findObjects(MainActivity.this, new FindListener<GeoPoint>() {
            @Override
            public void onSuccess(List<GeoPoint> list) {
                if (list.isEmpty()){

                }else {
                    point = list.get(0);
                    initData1();
                    initStudentData();
                    Toast.makeText(MainActivity.this, "有签到未结束", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        mList = new ArrayList<>();
        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereEqualTo("Id",Integer.valueOf(user.getUsername()));
        query.findObjects(MainActivity.this, new FindListener<Course>() {
            @Override
            public void onSuccess(List<Course> list) {
                for (int i = 0; i < list.size(); i++) {
                    mList.add(list.get(i));
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取总人数和未到人数
     */
    public void initData1() {
        BmobQuery<Record> query = new BmobQuery<>();
        query.addWhereEqualTo("cId", point.getcId());
        query.addWhereGreaterThanOrEqualTo("in_time", point.getTime());
        query.findObjects(MainActivity.this, new FindListener<Record>() {
            @Override
            public void onSuccess(List<Record> list) {
                jList = list;
                for (int i = 0; i < mUnSignOn.size(); i++) {
                    mUnSignOn.remove(i);
                }
                mUnSignOn = new ArrayList<StudentCourse>();
                swipeRefreshLayout.setRefreshing(false);
                signOnCount.setText(list.size() + "/" + mStudentList.size());
                for (int i = 0; i < mStudentList.size(); ++i) {
                    boolean flag = false;
                    for (Record record : list) {
                        if (mStudentList.get(i).getsId() == record.getID()) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                       mUnSignOn.add(mStudentList.get(i));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
    private void initStudentData(){
        BmobQuery<StudentCourse> query = new BmobQuery<>();
        query.addWhereEqualTo("cId",point.getcId());
        query.findObjects(MainActivity.this, new FindListener<StudentCourse>() {
            @Override
            public void onSuccess(List<StudentCourse> list) {
                mStudentList = list;
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 百度定位
     */
    private void initLocation() {
        //百度地图
        mapView = (MapView) findViewById(R.id.bmapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        positionText = (TextView) findViewById(R.id.position_text_view);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(16f));
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 5000;
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
    /**
     * 百度地图
     */
    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            Toast.makeText(this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
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

    /**
     * 获取权限的回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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



    //侧滑菜单
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    /**
     * 刷新控件的监听事件
     */
    @Override
    public void onRefresh() {
        if(record==null){
            swipeRefreshLayout.setRefreshing(false);
            mRecycler.removeAllViews();
        }
        if (point == null) {
            Toast.makeText(MainActivity.this, "还未发起签到", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);

        }else {
            mStudentList.clear();
            mUnSignOn.clear();
            initStudentData();
            initData1();

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_sign_in:
                BmobQuery<GeoPoint> geoQuery = new BmobQuery<GeoPoint>();
                geoQuery.addWhereEqualTo("tId",Integer.valueOf(user.getUsername()));
                geoQuery.findObjects(MainActivity.this, new FindListener<GeoPoint>() {
                    @Override
                    public void onSuccess(List<GeoPoint> list) {
                        if (list.isEmpty()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                            View conView = layoutInflater.inflate(R.layout.start_sign_in_layout, null);
                            final EditText editText = (EditText) conView.findViewById(R.id.start_sign_in_id);
                            final Spinner spinner = (Spinner) conView.findViewById(R.id.select_course);
                            ArrayList<String> strList = new ArrayList<String>();
                            for (int i = 0; i < mList.size(); i++) {
                                strList.add(mList.get(i).getCourseName());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.my_spinner,R.id.text, strList);
                            adapter.setDropDownViewResource(R.layout.my_spinner);
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    cId = mList.get(position).getObjectId();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
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
                                        if (cId == null) {
                                            Toast.makeText(MainActivity.this, "未选择课程", Toast.LENGTH_SHORT).show();
                                        } else {
                                            id = Integer.valueOf(str);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    BmobQuery<GeoPoint> query = new BmobQuery<GeoPoint>();
                                                    query.addWhereEqualTo("ID", id);
                                                    query.findObjects(MainActivity.this, new FindListener<GeoPoint>() {
                                                        @Override
                                                        public void onSuccess(List<GeoPoint> list) {
                                                            if (list.isEmpty()) {
                                                                point = new GeoPoint();
                                                                point.setID(id);
                                                                point.setcId(cId);
                                                                point.settId(Integer.valueOf(user.getUsername()));
                                                                point.setTime(System.currentTimeMillis());
                                                                point.save(MainActivity.this, new SaveListener() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        onRefresh();
                                                                        Toast.makeText(MainActivity.this, "发起签到成功", Toast.LENGTH_SHORT).show();
                                                                        getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean("signFlag", true).apply();
                                                                        getSharedPreferences("data", MODE_PRIVATE).edit().putInt("signId", id).apply();
                                                                        getSharedPreferences("data", MODE_PRIVATE).edit().putString("signObjectId", point.getObjectId()).apply();
//                                                            if (dpm.isAdminActive(component)) {
//                                                                dpm.lockNow();
//                                                            } else {
//                                                                openAdmin();
//                                                            }
//                                                            finish();
                                                                    }

                                                                    public void openAdmin() {
                                                                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                                                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, component);
                                                                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Only after opening administrator can be used");
                                                                        startActivity(intent);
                                                                    }

                                                                    @Override
                                                                    public void onFailure(int i, String s) {

                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(MainActivity.this, "该签到码已经被使用！", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(int i, String s) {
                                                        }
                                                    });
                                                }
                                            }).start();
                                        }
                                    }
                                }
                            });
                            builder.show();
                        }else {
                            Toast.makeText(MainActivity.this, "尚有签到未结束，请先结束!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

                break;
            case R.id.show_num:
                BmobQuery<GeoPoint> geoQuery2 = new BmobQuery<GeoPoint>();
                geoQuery2.addWhereEqualTo("tId",Integer.valueOf(user.getUsername()));
                geoQuery2.findObjects(MainActivity.this, new FindListener<GeoPoint>() {
                    @Override
                    public void onSuccess(List<GeoPoint> list) {
                        if (list.isEmpty()){
                            Toast.makeText(MainActivity.this, "未发起签到", Toast.LENGTH_SHORT).show();
                        }else {
                            if (mUnSignOn == null){
                                Toast.makeText(MainActivity.this, "请先查看签到情况", Toast.LENGTH_SHORT).show();
                            }else{
                                for (StudentCourse stu:mUnSignOn){
                                    NotArrive not = new NotArrive();
                                    not.setName(stu.getName());
                                    not.setId(stu.getsId());
                                    not.setTime(System.currentTimeMillis());
                                    not.setcId(stu.getcId());
                                    not.save(MainActivity.this, new SaveListener() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFailure(int i, String s) {

                                        }
                                    });
                                }
                            }
                            point = list.get(0);
                            point.delete(MainActivity.this, new DeleteListener() {
                                @Override
                                public void onSuccess() {
                                    mStudentList.clear();
                                    mUnSignOn.clear();
                                    adapter.notifyDataSetChanged();
                                    signOnCount.setText("0/0");
                                    Toast.makeText(MainActivity.this, "已结束签到！", Toast.LENGTH_SHORT).show();
                                    point=null;
                                    record=null;

                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

                break;
            case R.id.map:
                startActivity(new Intent(MainActivity.this,MapActivity.class));
                break;
            case R.id.calendar:
                startActivity(new Intent(MainActivity.this,CalendarMainActivity.class));
                break;
        }
    }

    /**
     * 获取百度定位数据
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
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
