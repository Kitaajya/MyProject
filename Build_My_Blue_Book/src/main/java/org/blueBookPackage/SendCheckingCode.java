package org.blueBookPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

//发送验证码类
public class SendCheckingCode {

    public static final Logger log = LoggerFactory.getLogger(SendCheckingCode.class);
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

    //共享信息->电话号保持不变
    public static long addedPhoneNumber;

    Connection connectionOfCheck;
    PreparedStatement preparedStatementOfCheck;
    ResultSet resultSetOfCheck;

    ConnectDatabase c=new ConnectDatabase();

    public boolean justify(long phoneNumber){
        try{
            setPhoneNumber(phoneNumber);
            addedPhoneNumber=getPhoneNumber();
            c.open(c.url,c.username,c.password);
            //随机生成4个数字
            String sqlOfCheck="select checkingNumber " +
                    "from checkAndJustify \n" +
                    "where phoneNumber=?";
            connectionOfCheck=DriverManager.getConnection(c.url,c.username,c.password);
            preparedStatementOfCheck=connectionOfCheck.prepareStatement(sqlOfCheck);

            preparedStatementOfCheck.setLong(1, addedPhoneNumber);
            resultSetOfCheck=preparedStatementOfCheck.executeQuery();

            log.debug("输入验证码：");

            int yourAddedCheckingCode=scannerOfCheck.nextInt();

            if(resultSetOfCheck.next()){
                setCheckingCode(resultSetOfCheck.getInt("checkingNumber"));
                if(Objects.equals(yourAddedCheckingCode,getCheckingCode())) {
                    SendCheckingCode.log.info("输入正确！");
                    return true;
                }
                else log.error("验证码错误！");
            }
        }catch(SQLException ec){
            log.error("判断验证码方法发生异常，位于方法-> justify(long phoneNumber,int checkingCode){}", ec.getMessage());
        }finally {
            c.off();
        }
        return false;
    }
}
