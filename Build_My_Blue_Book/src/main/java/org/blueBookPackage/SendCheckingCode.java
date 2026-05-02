package org.blueBookPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

//发送验证码类
public class SendCheckingCode {

    private static final Logger log = LoggerFactory.getLogger(SendCheckingCode.class);
    Scanner scannerOfCheck=new Scanner(System.in);

    private long phoneNumber;
    private int checkingCode;

    public long getPhoneNumber(){
        return phoneNumber;
    }
    public int getCheckingCode(){
        return checkingCode;
    }
    public void setPhoneNumber(long phoneNumber){
        this.phoneNumber=phoneNumber;
    }
    public void setCheckingCode(int checkingCode){
        this.checkingCode=checkingCode;
    }


    Connection connectionOfCheck;
    PreparedStatement preparedStatementOfCheck;
    ResultSet resultSetOfCheck;

    ConnectDatabase c=new ConnectDatabase();

    public boolean justify(long phoneNumber){
        try{
            c.open(c.url,c.username,c.password);
            //随机生成4个数字
            String sqlOfCheck="select checkingNumber " +
                    "from checkAndJustify \n" +
                    "where phoneNumber=?";
            connectionOfCheck=DriverManager.getConnection(c.url,c.username,c.password);
            preparedStatementOfCheck=connectionOfCheck.prepareStatement(sqlOfCheck);

            preparedStatementOfCheck.setLong(1, phoneNumber);
            resultSetOfCheck=preparedStatementOfCheck.executeQuery();

            log.debug("输入验证码：");

            int yourAddedCheckingCode=scannerOfCheck.nextInt();

            if(resultSetOfCheck.next()){
                setCheckingCode(resultSetOfCheck.getInt("checkingNumber"));
                if(Objects.equals(yourAddedCheckingCode,getCheckingCode())) {
                    SendCheckingCode.log.info("输入正确！");
                    return true;
                }
                else System.out.println("验证码错误！");
            }
        }catch(SQLException ec){
            log.error("判断验证码方法发生异常，位于方法-> justify(long phoneNumber,int checkingCode){}", ec.getMessage());
        }finally {
            c.off();
        }
        return false;
    }
    public static void main(String[]asd){
        Scanner scanner=new Scanner(System.in);
        log.debug("输入手机号");
        long p=scanner.nextLong();
        SendCheckingCode s=new SendCheckingCode();
        System.out.println(s.justify(p));
    }
}
