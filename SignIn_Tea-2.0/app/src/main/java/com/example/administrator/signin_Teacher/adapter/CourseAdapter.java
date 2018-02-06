package com.example.administrator.signin_Teacher.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.administrator.signin_Teacher.R;
import com.example.administrator.signin_Teacher.module.Course;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-02-26 .
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private List<Course> mList;
    private ArrayList<Boolean> flag;

    public CourseAdapter(List<Course> a){
        mList = a;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item,parent,false));
    }

    @Override
    public int getItemCount() {
        flag = new ArrayList<Boolean>();
        for (int i = 0; i < mList.size(); i++) {
            flag.add(false);
        }
        return mList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Course course = mList.get(position);
        holder.name.setText(course.getCourseName());
        holder.id.setText(course.getTeacherName());
        holder.name.setOnCheckedChangeListener(null);
        holder.name.setChecked(flag.get(position));
        holder.name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flag.set(position , isChecked);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox name;
        TextView id;
        public ViewHolder(View view){
            super(view);
            name = (CheckBox) view.findViewById(R.id.setCourse);
            id = (TextView) view.findViewById(R.id.set_teacher_id);
        }
    }
    public ArrayList<Boolean> getFlag(){return flag;}

    public void setFlag(ArrayList<Boolean> flag) {
        this.flag = flag;
    }
}
