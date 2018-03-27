package com.example.administrator.signin_Teacher.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.administrator.signin_Teacher.R;
import com.example.administrator.signin_Teacher.adapter.NewsAdapter;
import com.example.administrator.signin_Teacher.module.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class NewsMainActivity extends AppCompatActivity {

    private List<News> newsList;
    private NewsAdapter adapter;
    private Handler handler;
    private ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity_main);
        newsList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.news_lv);
        getNews();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    adapter = new NewsAdapter(NewsMainActivity.this,newsList);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            News news = newsList.get(position);
                            Intent intent = new Intent(NewsMainActivity.this,NewsDisplayActvivity.class);
                            intent.putExtra("news_url",news.getNewsUrl());
                            startActivity(intent);
                        }
                    });
                }
            }
        };

    }



    private void getNews(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //获取虎扑新闻20页的数据，网址格式为：https://voice.hupu.com/nba/第几页
                    for(int i = 1;i<=20;i++) {

                        Document doc = Jsoup.connect("http://www.hbu.edu.cn/hdyw/index.jhtml").get();
                        Elements titleLinks = doc.select("div.c1-body").select("div.c1-bline").select("div.f-left");    //解析来获取每条新闻的标题与链接地址
                     // Elements descLinks = doc.select("div.list-content");//解析来获取每条新闻的简介
                        Elements timeLinks = doc.select("div.c1-body").select("div.c1-bline");   //解析来获取每条新闻的时间与来源
                        Elements uriLinks = doc.select("div.c1-body").select("div.c1-bline").select("div.f-left").select(".red").next();
                        Log.e("title", Integer.toString(titleLinks.size()));
                        for(int j = 0;j < titleLinks.size();j++){
                            String title = titleLinks.get(j).select("span").text();
                            String uri = uriLinks.get(j).select("a").attr("href");
                       //   String desc = descLinks.get(j).select("span").text();
                          String time = timeLinks.get(j).select("div.gray f-right").text();
                            News news = new News(title,uri,null,time);
                            newsList.add(news);
                        }
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
            }

    }



