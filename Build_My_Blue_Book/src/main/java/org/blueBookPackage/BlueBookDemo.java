package org.blueBookPackage;

public class BlueBookDemo {
    public static void main(String[] args) {
        ConnectDatabase c=new ConnectDatabase();
        ShowPersonalInformation s=new ShowPersonalInformation();
        LogInTest.test();
        s.show();
    }
}
