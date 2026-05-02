package org.blueBookPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Objects;

public class ConnectDatabase {
    private static final Logger log = LoggerFactory.getLogger(ConnectDatabase.class);
    static Connection connection;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;

    public final String url="jdbc:mysql://localhost:3306/LogInBlueBook?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    public final String username="root";
    public final String password ="123456";

    public static String name;
    public static String webName;
    public static String selfId;
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
    public boolean findAndJustify(String name, String yourInsertedSelfId){
        try{
            open(url,username,password);

            String selectRightInforId="select * from logInOfInformation where name= ? ";

            preparedStatement=connection.prepareStatement(selectRightInforId);

            preparedStatement.setString(1,name);
            resultSet= preparedStatement.executeQuery();
            while(resultSet.next()){
                selfId=resultSet.getString("selfId");
                ConnectDatabase.log.info("\r正在匹配中->{}", yourInsertedSelfId);
                if(Objects.equals(yourInsertedSelfId,selfId)) break;
                else return false;
            }
            return true;
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
}
