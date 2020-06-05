package com.qst.note.constant;

public class Constants {
    //public final static String ROOT_RUL = "http://10.90.96.13:8080/note_server/";   //服务器跟地址
    public final static String ROOT_RUL = "http://192.168.159.1:8080/note_server/";

    public final static String LOGIN_URL = ROOT_RUL+"login";    //登录请求地址
    public final static String REGISTER_URL = ROOT_RUL + "regist";   //注册请求地址
    public final static String GET_ALL_NOTES = ROOT_RUL + "GetAllNoteServlet";  //获取所有备忘记录请求地址
    public final static String DELETE_NOTE = ROOT_RUL + "DeleteNoteServlet";   //删除备忘记录请求地址
    public final static String ADD_NOTE_URL = ROOT_RUL + "InsertNoteServlet";   //添加备忘记录请求地址
    public final static String GET_NOTE_BY_ID = ROOT_RUL + "GetNoteServlet";    //根据id获取一条备忘记录请求地址
    public final static String MODIFY_NOTE_URL = ROOT_RUL + "UpdateNoteServlet";  //修改备忘记录请求地址
}
