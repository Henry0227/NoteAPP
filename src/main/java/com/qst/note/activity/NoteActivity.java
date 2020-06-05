package com.qst.note.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.qst.note.MainActivity;
import com.qst.note.R;
import com.qst.note.bean.NoteBean;
import com.qst.note.constant.Constants;
import com.qst.note.result.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*新增备忘、查看备忘详情都使用该activity，启动该界面时需要传一个int类型的type参数
* 1:代表使用该界面新增备忘，2:代表使用该界面查看备忘详情*/
public class NoteActivity extends AppCompatActivity {
    public int type;   //1:代表使用该界面新增备忘，2:代表使用该界面查看备忘详情
    RequestQueue requestQueue;
    String time;      //闹钟提醒时间
    String date;      //闹钟提醒日期
    AlarmManager alarmManager;

    EditText et_title,et_content;
    Button btn_date,btn_time,btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        type = getIntent().getIntExtra("type",1);  //获取上一个界面传过来的type
        requestQueue = Volley.newRequestQueue(this);  //初始化请求队列

        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        btn_date = findViewById(R.id.btn_date);
        btn_time = findViewById(R.id.btn_time);
        btn = findViewById(R.id.btn);

        /*点击日期按钮，弹出日期选择器对话框*/
        btn_date.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(NoteActivity.this);  //创建一个日期选择器对话框
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {  //在该方法获取用户选择的日期
                        date = year + "年" + month + "月" + dayOfMonth + "日";
                        btn_date.setText(date);  //将用户选择的日期显示在按钮上
                    }
                });
                datePickerDialog.show();  //展示该日期选择对话框
            }
        });
        /*点击时间按钮，弹出时间选择器对话框*/
        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(NoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time = hourOfDay + ":" + minute;
                        btn_time.setText(time);  //将用户选择的时间显示在按钮上
                    }
                },12,0,true);
                timePickerDialog.show();
            }
        });


        if (type ==1){  //如果是新增备忘
            btn.setText("保存");
            btn.setOnClickListener(new View.OnClickListener() {  //如果是新增备忘点击按钮就是发送新增请求
                @Override
                public void onClick(View v) {
                    add();
                }
            });
        }else if (type == 2){   //如果是查看备忘详情
            btn.setText("修改");
            getNoteByID();
            btn.setOnClickListener(new View.OnClickListener() {  //如果是新增备忘点击按钮就是发送新增请求
                @Override
                public void onClick(View v) {
                    modify();
                }
            });
        }
    }

    //如果是新增备忘，底部按钮就是保存按钮，add
    public void add(){
        String tel = getIntent().getStringExtra("tel");  // 如果是新增备忘，需要获取上一个界面传过来的用户电话tel
        String title = et_title.getText().toString();
        String content = et_content.getText().toString();
        final String noteTime;   //闹钟提醒时间为日期+时间

        if (content.equals("")&&title.equals("")){
            Toast.makeText(getApplicationContext(),"标题和内容不能同时为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (date == null || time == null){  //如果闹钟提醒的日期或时间为空，上传到服务器的提醒时间就是空字符串
            noteTime = "";
        }else{
            noteTime = date + " " + time;
        }

        String url = Constants.ADD_NOTE_URL + "?tel=" + tel + "&title=" + title + "&content=" + content + "&noteTime=" + noteTime;   //新增备忘请求地址
        StringRequest addRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {   //请求成功的回调
                Gson gson = new Gson();
                Result result = gson.fromJson(s,Result.class);  //将服务器返回的结果转换成Result对象
                if (result.isSuccess){  //成功上传到服务器，关闭该界面
                    alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);


                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                        Date date = sdf.parse(noteTime);
                        long timeInLong = date.getTime();  //以上三行，将字符串类型的闹钟提醒时间转换long类型
                        alarmManager.set(AlarmManager.RTC_WAKEUP,timeInLong,null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    finish();
                }else {
                    Toast.makeText(getApplicationContext(),result.msg,Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {  //请求失败的回调
                Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(addRequest);
    }

    //如果该界面用于查看note详情，获取note详情并展示
    public void getNoteByID(){
        int id = getIntent().getIntExtra("id",0);  //note的id是由主界面传过来
        String url = Constants.GET_NOTE_BY_ID +"?id=" + id;
        Log.d("msg",url);
        /*创建一个volley请求*/
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {   //请求成功的回调
                Log.d("msg",s);
                Gson gson = new Gson();
                NoteBean note = gson.fromJson(s,NoteBean.class);  //将从服务器获取的数据转换成NoteBean对象
                et_content.setText(note.getContent());
                et_title.setText(note.getTitle());
                Log.d("msg",note.getNoteTime());
                if(note.getNoteTime()!=null && !note.getNoteTime().equals("")){  //如果备忘的闹钟时间不为空，分别将日期和时间展示到两个按钮中
                    date = note.getNoteTime().split(" ")[0];
                    time = note.getNoteTime().split(" ")[1];
                    btn_date.setText(date);
                    btn_time.setText(time);
                }
            }
        }, new Response.ErrorListener() {   //请求失败的回调
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    //如果是查看note详情，底部按钮就是修改按钮，点击修改执行modify
    public void modify(){
        int id = getIntent().getIntExtra("id",0);  //note的id是由主界面传过来
        String title = et_title.getText().toString();
        String content = et_content.getText().toString();
        final String noteTime;   //闹钟提醒时间为日期+时间

        if (content.equals("")&&title.equals("")){
            Toast.makeText(getApplicationContext(),"标题和内容不能同时为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (date == null || time == null){  //如果闹钟提醒的日期或时间为空，上传到服务器的提醒时间就是空字符串
            noteTime = "";
        }else{
            noteTime = date + " " + time;
        }

        String url = Constants.MODIFY_NOTE_URL + "?id=" + id + "&title=" + title + "&content=" + content + "&noteTime=" + noteTime;   //新增备忘请求地址
        StringRequest addRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {   //请求成功的回调
                Gson gson = new Gson();
                Result result = gson.fromJson(s,Result.class);  //将服务器返回的结果转换成Result对象
                if (result.isSuccess){  //成功上传到服务器，关闭该界面
                    alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                        Date date = sdf.parse(noteTime);
                        long timeInLong = date.getTime();  //以上三行，将字符串类型的闹钟提醒时间转换long类型SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                        alarmManager.set(AlarmManager.RTC_WAKEUP,timeInLong,null);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    finish();  //修改成功关闭当前界面，回到主界面
                }else {
                    Toast.makeText(getApplicationContext(),result.msg,Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {  //请求失败的回调
                Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(addRequest);
    }
}
