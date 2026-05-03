package org.blueBookPackage;

import com.mysql.cj.jdbc.ConnectionImpl;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.*;
import java.util.Scanner;

public class BuildWebName {

    private static final Logger log = LoggerFactory.getLogger(BuildWebName.class);
   //共享信息->共享网名，保持不变
    public static String webName;
    public void set(){

        try{

            Scanner scanner=new Scanner(System.in);

            log.info("设置网名：");
            webName=scanner.next().trim();
            scanner.nextLine();

            ConnectDatabase connectDatabase=new ConnectDatabase();
            Connection connection;
            PreparedStatement preparedStatement;
            ResultSet resultSet;

            String sqlOfWebName="INSERT INTO logInOfInformation (name,webName,selfId) VALUES(?,?,?)"
                    +"ON DUPLICATE KEY UPDATE webName = ?";

            connection=
                    DriverManager.getConnection(connectDatabase.url,connectDatabase.username,connectDatabase.password);
            preparedStatement=connection.prepareStatement(sqlOfWebName);

            preparedStatement.setString(1,LogInTest.addedName);
            //设值网名
            preparedStatement.setString(2,webName);
            preparedStatement.setString(3,ConnectDatabase.foundSelfId);
            preparedStatement.setString(4, webName);  // 更新时用的值
            int row=preparedStatement.executeUpdate();//影响行数
            log.info("影响行数：{}行", row);

        }catch(SQLException ewn){
            log.error("查询数据库发生异常！位于方法->set(String webName){}", ewn.getMessage());
        }

    }
}
