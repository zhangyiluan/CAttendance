package com.example.administrator.signin_Teacher.view;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.example.administrator.signin_Teacher.R;
import com.example.administrator.signin_Teacher.adapter.UnSignOnAdapter;
import com.example.administrator.signin_Teacher.module.Course;
import com.example.administrator.signin_Teacher.module.GeoPoint;
import com.example.administrator.signin_Teacher.module.Record;
import com.example.administrator.signin_Teacher.module.StudentCourse;
import com.example.administrator.signin_Teacher.module.User;
import com.example.administrator.signin_Teacher.tool.MyReceiver;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/3/23.
 */

public class DetailsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener{
    private static final int UPDATE_TEXT1=1;
    private static final int UPDATE_TEXT2=2;
    private DevicePolicyManager dpm;
    private ComponentName component;
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private android.widget.Button record;
    private FloatingActionButton startsignin;
    private FloatingActionButton shownum;
    public LocationClient mLocationClient = null;
    private int id;
    private String cId;
    public static GeoPoint point;
    public static List<Course> mList;
    public static User user;
    private List<Record> jList = new ArrayList<>();
    private List<StudentCourse> mStudentList = new ArrayList<>();
    private TextView signOnCount;
    private RecyclerView mRecycler;
    public static List<StudentCourse> mUnSignOn = new ArrayList<>();
    private UnSignOnAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.detail);
        user = BmobUser.getCurrentUser(DetailsActivity.this,User.class);
        initWidget();
        initSwipeRefreshLayout();
        initData();
        LinearLayoutManager manager = new LinearLayoutManager(DetailsActivity.this);
        mRecycler.setLayoutManager(manager);
        mUnSignOn = new ArrayList<StudentCourse>();
        adapter = new UnSignOnAdapter(DetailsActivity.this,R.id.sign_on_person,mUnSignOn);
        mRecycler.setAdapter(adapter);
    }
    /**
     * 获取控件
     */
    private void initWidget(){
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        component = new ComponentName(this, MyReceiver.class);
        mRecycler = (RecyclerView)findViewById(R.id.sign_on_person);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

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
            Toast.makeText(DetailsActivity.this, "还未发起签到", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);

        }else {
            mStudentList.clear();
            mUnSignOn.clear();
            initStudentData();
            initData1();
        }
    }
    /**
     * 刷新控件设置
     */
    private void initSwipeRefreshLayout(){
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
    }

    /**
     * 获取总人数和未到人数
     */
    public void initData1() {
        BmobQuery<Record> query = new BmobQuery<>();
        query.addWhereEqualTo("cId", point.getcId());
        query.addWhereGreaterThanOrEqualTo("in_time", point.getTime());
        query.findObjects(DetailsActivity.this, new FindListener<Record>() {
            @Override
            public void onSuccess(List<Record> list) {
                jList = list;
                for (int i = 0; i < mUnSignOn.size(); i++) {
                    mUnSignOn.remove(i);
                }
                // mUnSignOn = new ArrayList<StudentCourse>();
                //  swipeRefreshLayout.setRefreshing(false);
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

    private void initData(){
        BmobQuery<GeoPoint> query1 = new BmobQuery();
        query1.addWhereEqualTo("tId",Integer.valueOf(user.getUsername()));
        query1.findObjects(DetailsActivity.this, new FindListener<GeoPoint>() {
            @Override
            public void onSuccess(List<GeoPoint> list) {
                if (list.isEmpty()){

                }else {
                    point = list.get(0);
                    initData1();
                    initStudentData();

                    Toast.makeText(DetailsActivity.this, "有签到未结束", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
        mList = new ArrayList<>();
        BmobQuery<Course> query = new BmobQuery<>();
        query.addWhereEqualTo("Id",Integer.valueOf(user.getUsername()));
        query.findObjects(DetailsActivity.this, new FindListener<Course>() {
            @Override
            public void onSuccess(List<Course> list) {
                for (int i = 0; i < list.size(); i++) {
                    mList.add(list.get(i));
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(DetailsActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initStudentData(){
        BmobQuery<StudentCourse> query = new BmobQuery<>();
        query.addWhereEqualTo("cId",point.getcId());
        query.findObjects(DetailsActivity.this, new FindListener<StudentCourse>() {
            @Override
            public void onSuccess(List<StudentCourse> list) {
                mStudentList = list;
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
