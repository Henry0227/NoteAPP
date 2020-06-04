package com.qst.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.qst.note.MainActivity;
import com.qst.note.R;
import com.qst.note.constant.Constants;
import com.qst.note.result.Result;

public class RegisterActivity extends AppCompatActivity {
    EditText et_name,et_pass,et_pass_conf,et_tel;
    Button btn;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_name = findViewById(R.id.et_name);
        et_pass = findViewById(R.id.et_pass);
        et_pass_conf = findViewById(R.id.et_pass_conf);
        et_tel = findViewById(R.id.et_tel);
        btn = findViewById(R.id.btn_register);

        requestQueue = Volley.newRequestQueue(this);  //初始化请求队列

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(et_name.getText().toString(),et_pass.getText().toString(),et_pass_conf.getText().toString(),et_tel.getText().toString());
            }
        });
    }

    public void register(String name, String pass, String passConf, final String tel){
        if (tel == null||tel.equals("")){
            Toast.makeText(this,"电话不允许为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass == null||pass.equals("")){
            Toast.makeText(this,"密码不允许为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (name == null||name.equals("")){
            Toast.makeText(this,"用户名不允许为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(passConf)){
            Toast.makeText(this,"两次输入的密码不一致，请重新输入",Toast.LENGTH_SHORT).show();
            return;
        }
        String url = Constants.REGISTER_URL + "?name=" + name + "&pass=" + pass + "&tel=" + tel;  //拼接注册请求请求地址
        //创建volley请求
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                Result result = gson.fromJson(s,Result.class);  //将服务器返回的结果s解析成Result对象
                if(result.isSuccess){  //注册成功跳转到主界面，并且将tel传给主界面
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("tel",tel);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),result.msg,Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(request);


    }
}
