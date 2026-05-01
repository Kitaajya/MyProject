package org.example;

import javax.swing.*;
import java.util.InputMismatchException;
import java.util.Scanner;

class LogIn{

    Scanner scannerOfLogIn=new Scanner(System.in);

    private String name;
    private String webName; // 已修正
    private String selfId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    ConnectDatabase connectDatabase=new ConnectDatabase();

    public void show(Object obj){
        System.out.println(obj);
    }
    //实名认证
    public void logIn(){
        System.out.println("======================================");
        System.out.println("        【小蓝书 1.0】用户登录");
        System.out.println("    本APP模仿小红书而制作！");
        System.out.println("======================================");
        System.out.println("使用前必须实名认证！");
        try{
            while(true){
                show("请输入姓名：");
                setName(scannerOfLogIn.next().trim());
                show("请输入身份证号：");
                setSelfId(scannerOfLogIn.next().trim());
                boolean justify=connectDatabase.findAndJustify(getName(),getSelfId());
                if(!justify) show("输入错误！");
                else show("输入正确！");
            }
        }catch(InputMismatchException ein){
            System.out.println("输入类型不匹配！位于方法->logIn()"+ein.getMessage());
        }
    }
    SendCheckingCode sendCheckingCode=new SendCheckingCode();
    //验证码
    public void checkCode(){
        long phoneNumber;
        System.out.println("请输入手机号！");
        phoneNumber=scannerOfLogIn.nextLong();
        sendCheckingCode.justify(phoneNumber);
    }

    public static void test(){
        System.out.println("欢迎光临我们的小蓝书！");
        LogIn logIn=new LogIn();
        logIn.logIn();
        logIn.checkCode();
    }

}


public class BlueBook {
    public static void main(String[] args) {
        LogIn.test();
    }
}