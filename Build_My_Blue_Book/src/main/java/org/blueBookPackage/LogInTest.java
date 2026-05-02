package org.blueBookPackage;

import java.util.InputMismatchException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LogInTest {

    Scanner scannerOfLogIn=new Scanner(System.in);

    private String name;
    private String webName;
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

    public static final Logger logger=LoggerFactory.getLogger(LogInTest.class);

    ConnectDatabase connectDatabase=new ConnectDatabase();

    public void show(Object obj){
        logger.info(obj.toString());
    }

    //静态变量保存添加的姓名信息，用于其他类调用
    public static String addedName;

    //实名认证，三次失败就强行禁止实名，传输真实名称                            <-name
    public void logIn(){
        logger.info("======================================");
        logger.info("        【小蓝书 1.0】用户登录");
        logger.info("    本APP模仿小红书而制作！");
        logger.info("======================================");
        logger.info("使用前必须实名认证！");
        try{
            int count=0;
            //三次输入失败禁止登陆
            while(count<3){
                show("请输入姓名：");
                setName(scannerOfLogIn.next().trim());

                addedName=getName();

                show("请输入身份证号：");
                setSelfId(scannerOfLogIn.next().trim());
                boolean justify=connectDatabase.findAndJustify(getName(),getSelfId());
                if (justify) {
                    show("输入正确！");
                    break;
                } else {
                    // 输入错误
                    count++;
                    show("输入错误！");
                    if (count == 3) {
                        System.out.println("失败次数过多！请稍后尝试！");
                        break;
                    }
                }
            }
        }catch(InputMismatchException ein){
            logger.error("输入类型不匹配！位于方法->logIn(){}", ein.getMessage());
        }
    }
    SendCheckingCode sendCheckingCode=new SendCheckingCode();
    //验证码
    public void checkCode(){
        try{
            long phoneNumber;
            show("请输入手机号：");
            phoneNumber = scannerOfLogIn.nextLong();
            while(true) {
                show("请在60秒内完成填写正确的验证码：");
                boolean check=sendCheckingCode.justify(phoneNumber);
                if(!check) show("请重新输入！");
                else {
                    logger.info("验证码正确！");
                    break;
                }
            }
        }catch(InputMismatchException ec){
            logger.error("输入类型不匹配！位于方法->checkCode(){}", ec.getMessage());
        }

    }

    public static void test(){
        logger.info("欢迎光临我们的小蓝书！");
        LogInTest logIn=new LogInTest();

        logIn.logIn();
        //logIn.checkCode();
    }
    static ShowPersonalInformation showPersonalInformation=new ShowPersonalInformation();
    public static void showPersonalInformationOnLogInTest(){
        LogInTest logInShow=new LogInTest();
        showPersonalInformation.show();
    }

}

