package com.example.administrator.signin.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.signin.R;
import com.example.administrator.signin.adapter.NotArriveAdapter;
import com.example.administrator.signin.adapter.RecordAdapter;
import com.example.administrator.signin.modul.Course;
import com.example.administrator.signin.modul.NotArrive;
import com.example.administrator.signin.modul.Record;
import com.example.administrator.signin.modul.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;


public class RecordActivity extends AppCompatActivity {
    private String cId,cWhich;
    public static List<Course> mList;
    private Button query;
    private LinearLayout activityrecord;
    private RecyclerView recycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        this.query = (Button) findViewById(R.id.query);
        this.activityrecord = (LinearLayout) findViewById(R.id.activity_record);
        this.recycler = (RecyclerView) findViewById(R.id.recycler);
       // Spinner spinner1 = (Spinner) findViewById(R.id.select_course);
        final Spinner spinner2 = (Spinner) findViewById(R.id.select_which);
//        ArrayList<String> strList = new ArrayList<String>();
//        for (int i = 0; i < mList.size(); i++) {
//            strList.add(mList.get(i).getCourseName());
//        }
//
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RecordActivity.this, R.layout.my_spinner,R.id.text, strList);
//        adapter1.setDropDownViewResource(R.layout.my_spinner);
//        spinner1.setAdapter(adapter1);
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                cId = mList.get(position).getObjectId();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
        final ArrayList<String> list = new ArrayList<String>();
        list.add("缺勤");
        list.add("已到");
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(RecordActivity.this, R.layout.my_spinner, R.id.text, list);
        spinner2.setAdapter(adapter2);
        spinner2.setSelection(0,true);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = BmobUser.getCurrentUser(RecordActivity.this,User.class);
                if(spinner2.getSelectedItem().toString().equals("缺勤")){
                    BmobQuery<NotArrive> query1 = new BmobQuery<NotArrive>();
                    query1.addWhereEqualTo("Id", Integer.valueOf(user.getUsername()));
                    //    query.addWhereEqualTo("cId",cId);
//返回50条数据，如果不加上这条语句，默认返回10条数据
                    query1.setLimit(50);
//执行查询方法
                    List<BmobQuery<NotArrive>> queries = new ArrayList<BmobQuery<NotArrive>>();
                    queries.add(query1);
                    BmobQuery<NotArrive> query = new BmobQuery<NotArrive>();
                    query.and(queries);
                    query.findObjects(RecordActivity.this, new FindListener<NotArrive>() {
                        @Override
                        public void onSuccess(List<NotArrive> object) {
                            // TODO Auto-generated method stub
                            if (object.size() == 0) {
                                Toast.makeText(RecordActivity.this, "无记录", Toast.LENGTH_SHORT).show();
                            } else {
                                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
                                LinearLayoutManager manager = new LinearLayoutManager(RecordActivity.this);
                                recyclerView.setLayoutManager(manager);
                                NotArriveAdapter adapter = new NotArriveAdapter(RecordActivity.this,object);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        @Override
                        public void onError(int code, String msg) {
                            // TODO Auto-generated method stub
                            Toast.makeText(RecordActivity.this,"查询失败："+msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                   BmobQuery<Record> query1 = new BmobQuery<Record>();
                    query1.addWhereEqualTo("ID", Integer.valueOf(user.getUsername()));
                    // query.addWhereEqualTo("cId",cId);

//返回50条数据，如果不加上这条语句，默认返回10条数据

                    query1.setLimit(50);
//执行查询方法
                    BmobQuery<Record> query = new BmobQuery<Record>();
                    List<BmobQuery<Record>> queries = new ArrayList<BmobQuery<Record>>();
                    queries.add(query1);
                    query.and(queries);
                    query.findObjects(RecordActivity.this, new FindListener<Record>() {
                        @Override
                        public void onSuccess(List<Record> object) {
                            // TODO Auto-generated method stub
                            if (object.size() == 0) {
                                Toast.makeText(RecordActivity.this, "无记录", Toast.LENGTH_SHORT).show();
                            } else {
                                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
                                LinearLayoutManager manager = new LinearLayoutManager(RecordActivity.this);
                                recyclerView.setLayoutManager(manager);
                                RecordAdapter adapter = new RecordAdapter(object);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        @Override
                        public void onError(int code, String msg) {
                            // TODO Auto-generated method stub
                            Toast.makeText(RecordActivity.this,"查询失败："+msg, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }
}
