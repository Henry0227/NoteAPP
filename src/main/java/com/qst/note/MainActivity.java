package com.qst.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qst.note.activity.NoteActivity;
import com.qst.note.bean.NoteBean;
import com.qst.note.constant.Constants;
import com.qst.note.result.Result;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    Button btn;
    RequestQueue requestQueue;
    String tel;
    ArrayList<NoteBean> datas;  //用于存放所有的备忘记录的集合
    MyAdapter myAdapter;   //自定义的ListView设配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);
        btn = findViewById(R.id.btn_add);

        /*任务14，新增代码，点击新增按钮，跳转到NoteActivity*/
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("type",1);    //1代表是新增备忘
                intent.putExtra("tel",tel);   //新增备忘时需要用户tel
                startActivity(intent);
            }
        });
        /*任务14，短按列表项进入NoteActivity查看该条备忘详情*/
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                intent.putExtra("type",2);    //2代表是查看备忘详情
                intent.putExtra("id",datas.get(position).getId());   //查看备忘时需要备忘id
                startActivity(intent);
            }
        });

        requestQueue = Volley.newRequestQueue(this);  //初始化请求队列
        tel = getIntent().getStringExtra("tel");   //获取上一个页面传递过来的tel

        /*长按列表项删除该条记录，向服务器发起删除的请求*/
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String url = Constants.DELETE_NOTE + "?id=" + datas.get(position).getId(); //拼接删除操作的请求地址
                StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {  //创建请求
                    @Override
                    public void onResponse(String s) {
                        Gson gson = new Gson();
                        Result result = gson.fromJson(s,Result.class);
                        if (result.isSuccess){   //如果删除成功
                            datas.remove(position);
                            myAdapter = new MyAdapter();
                            lv.setAdapter(myAdapter);     //刷新界面
                            Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),result.msg,Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(),"网络错误，删除失败",Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(stringRequest);  //发起请求
                return false;
            }
        });
    }


    /*服务器数据获取放在onStart中，这样页面每次处于可见状态都会重新回去刷新数据*/
    @Override
    protected void onStart() {
        super.onStart();
        String url = Constants.GET_ALL_NOTES +"?tel=" + tel;  //获取所有备忘记录请求地址需要传参tel
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                datas = gson.fromJson(s, new TypeToken<ArrayList<NoteBean>>(){}.getType());  //将服务器获取到的数据转换成集合
                myAdapter = new MyAdapter();
                lv.setAdapter(myAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    public class MyAdapter extends BaseAdapter {  //为ListView自定义适配器Adapter

        @Override
        public int getCount() {
            return datas.size();  //返回集合的数据
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //将列表项的布局文件转换成View对象
            convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item,null);
            TextView tv_tltle = convertView.findViewById(R.id.tv_title);
            TextView tv_content = convertView.findViewById(R.id.tv_content);  //获取列表项中的控件

            tv_tltle.setText(datas.get(position).getTitle());
            tv_content.setText(datas.get(position).getContent()); //设置列表项内容

            return convertView;  //返回列表项
        }
    }
}
