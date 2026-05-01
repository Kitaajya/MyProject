package org.example;

import java.sql.*;
import java.util.Objects;
import java.util.Scanner;

//发送验证码类
public class SendCheckingCode {

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


            System.out.println("输入验证码：");

            int yourAddedCheckingCode=scannerOfCheck.nextInt();

            if(resultSetOfCheck.next()){
                setCheckingCode(resultSetOfCheck.getInt("checkingNumber"));
                if(Objects.equals(yourAddedCheckingCode,getCheckingCode())) {
                    System.out.println("输入正确！");
                    return true;
                }
                else System.out.println("验证码错误！");
            }
        }catch(SQLException ec){
            System.out.println("判断验证码方法发生异常，位于方法-> justify(long phoneNumber,int checkingCode)"+ec.getMessage());
        }finally{
            c.off();
        }
        return false;
    }
}
