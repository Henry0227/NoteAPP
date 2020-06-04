package com.qst.note.constant;

public class Constants {
    public final static String ROOT_RUL = "http://10.90.96.13:8080/note_server/";   //服务器跟地址

    public final static String LOGIN_URL = ROOT_RUL+"login";    //登录请求地址
    public final static String REGISTER_URL = ROOT_RUL + "regist";   //注册请求地址
    public final static String GET_ALL_NOTES = ROOT_RUL + "GetAllNoteServlet";  //获取所有备忘记录请求地址
    public final static String DELETE_NOTE = ROOT_RUL + "DeleteNoteServlet";   //删除备忘记录请求地址
}
