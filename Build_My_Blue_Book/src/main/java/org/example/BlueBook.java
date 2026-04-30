package org.example;

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

    public String getWebName() {  // 已修正
        return webName;
    }

    public void setWebName(String webName) {  // 已修正
        this.webName = webName;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    ConnectDatabase connectDatabase=new ConnectDatabase();

    public void testDatabase(){

        connectDatabase.connect(connectDatabase.url, connectDatabase.username,connectDatabase.password);
    }
    public void show(Object obj){
        System.out.println(obj);
    }
    public void logIn(){

        System.out.println("使用本APP之前必须先登录！");
        try{
            while(true){
                show("请输入姓名：");
                setName(scannerOfLogIn.next().trim());
                show("请输入身份证号：");
                setSelfId(scannerOfLogIn.next().trim());
                boolean justify=connectDatabase.findAndJustify(getName(),getSelfId());
                if(!justify) show("输入错误！");
                else {
                    show("输入正确！");
                }
            }

        }catch(InputMismatchException ein){
            System.out.println("输入类型不匹配！位于方法->logIn()"+ein.getMessage());
        }
    }
}


public class BlueBook {
    public static void main(String[] args) {
        System.out.println("欢迎光临我们的小蓝书！本APP专门为独立男性制作！");
        LogIn logIn=new LogIn();
        logIn.logIn();
    }
}