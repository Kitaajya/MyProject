package org.blueBookPackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//检查实名信息类
public class ShowPersonalInformation {

    public static final Logger logger=LoggerFactory.getLogger(ShowPersonalInformation.class);
    ConnectDatabase connectDatabase=new ConnectDatabase();
    public void show(){
        connectDatabase.open(connectDatabase.url,connectDatabase.username, connectDatabase.password);
        connectDatabase.showPersonalInformation();
    }

}
