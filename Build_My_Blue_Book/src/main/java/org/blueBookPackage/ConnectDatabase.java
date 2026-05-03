package org.blueBookPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

public class ConnectDatabase {

    public static final Logger log = LoggerFactory.getLogger(ConnectDatabase.class);
    static Connection connection;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;

    public final String url="jdbc:mysql://localhost:3306/LogInBlueBook?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    public final String username="root";
    public final String password ="123456";

    private static String name;
    public static String webName;
    public static String selfId;

    public String getName(){
        return name;
    }

    //打开数据库
    public void open(String url,String username,String password){
        try{
            connection=DriverManager.getConnection(url,username,password);
            preparedStatement=connection.prepareStatement("select * from logInOfInformation ");
            resultSet= preparedStatement.executeQuery();
        }catch(SQLException eo){
            ConnectDatabase.log.error("打开数据库方法异常！" +
                    "位于方法->open(String url,String username,String password){}",
                    eo.getMessage());
        }
    }
    public void off(){
        try{
            if(resultSet!=null) resultSet.close();
            if(preparedStatement!=null) preparedStatement.close();
            if(connection!=null) connection.close();
        }catch(SQLException eo){
            ConnectDatabase.log.error("关闭数据库方法异常！位于方法->off(){}", eo.getMessage());
        }

    }
    //展示所有用户的信息
    public void showInformationOfUsers(String url, String username, String password){
        try{
            open(url, username, password);
            while(resultSet.next()){

                name   =resultSet.getString("name");
                webName=resultSet.getString("webName");
                selfId =resultSet.getString("selfId");

                ConnectDatabase.log.info("姓名：    {}", name);
                ConnectDatabase.log.info("网名：    {}", webName);
                ConnectDatabase.log.info("身份证号： {}", selfId);
            }
        }catch(SQLException ec){
            ConnectDatabase.log.error
                    ("数据库连接错误！" +
                                    "位于方法->showInformationOfUsers(String url,String username,String password)->{}",
                            ec.getMessage());
        }finally {
            off();
        }
    }

    //查询实名信息
    public boolean findAndJustify(String name, String yourInsertedSelfId){
        try{
            open(url,username,password);

            String selectRightInforId="select * from logInOfInformation where name= ? ";

            preparedStatement=connection.prepareStatement(selectRightInforId);
            /**把实名的值放到这里**/
            preparedStatement.setString(1,name);
            resultSet= preparedStatement.executeQuery();
            while(resultSet.next()){
                selfId=resultSet.getString("selfId");
                ConnectDatabase.log.info("\r正在匹配中->{}", yourInsertedSelfId);
                if(Objects.equals(yourInsertedSelfId,selfId)) return true;
            }
            return false;
        }catch(SQLException efj){
            ConnectDatabase.log.info
                    ("查询与比较方法异常！" +
                            "位于方法->findAndJustify(String name,String selfId)->{}",
                            efj.getMessage());
        }finally {
            off();
        }
        return false;
    }
    Scanner scanner=new Scanner(System.in);
    //共享信息->保持身份证信息不变
    public static String foundSelfId;


    //检测姓名与登录的姓名是否一直，(如果一致，展示身份证信息)
    public void showPersonalInformation() {
        try {
            log.info("请输入你的姓名：");
            String yourAddedName = scanner.next().trim();
            if(!Objects.equals(yourAddedName, LogInTest.addedName)) {
                log.error("填写的姓名不正确！");
                return;
            }
            log.info("姓名信息正确！");
            String sql = "select * from logInOfInformation where name = ?";
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, yourAddedName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        //共享的身份证信息，方便以后调用
                        foundSelfId= rs.getString("selfId");
                        log.info("你的真实网名：{}", BuildWebName.webName);
                        log.info("展示的身份证号：{}", foundSelfId);
                        log.info("注册的手机号：{}",SendCheckingCode.addedPhoneNumber);
                    } else log.error("数据库中未找到该用户的信息！");
                }
            }
        } catch(SQLException esi) {
            log.error("数据库查询个人信息异常", esi);
        } catch(InputMismatchException e) {
            log.error("输入类型异常", e);
        }
    }
}
