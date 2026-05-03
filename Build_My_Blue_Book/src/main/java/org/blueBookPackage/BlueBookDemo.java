package org.blueBookPackage;

public class BlueBookDemo {

    public void method(){
        ShowPersonalInformation find=new ShowPersonalInformation();
        //登录，主方法，包含验证码
        LogInTest.test();
        //设置网名
        BuildWebName b=new BuildWebName();
        b.set();
        //检查实名认证
        find.show();
    }


    public static void main(String[] args) {
        BlueBookDemo e=new BlueBookDemo();
        e.method();

    }
}
