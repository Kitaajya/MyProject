import java.io.IO;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
class Company{
    public String nameOfCompany;
    public Company(){
        nameOfCompany ="北京通融物管";
    }
}
class People extends Company{
    private long id;
    private String name;
    private String department;
    private String duty;
    private boolean status;
    private String position;

    public People() {
    }
    public People(long id, String name, String department, String duty, boolean status, String position) {
        super();
        this.id = id;
        this.name = name;
        this.department = department;
        this.duty = duty;
        this.status = status;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public void add(){
        IO.println("添加人数");
    }
    public void delete(){
        IO.println("删除人");
    }
    public void select(){
        IO.println("查找人");
    }
    public void edit(){
        IO.println("修改信息");
    }
}
class DatabaseOfCompany extends People{
    public DatabaseOfCompany(){
        super();
    }
    public void p(Object o){
        IO.println(o);
    }
    public static final String url="jdbc:mysql://localhost:3306/company?useSSL=" +
            "false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    public static final String username="root";
    public static final String password="123456";
    Connection connection;
    Statement statement;
    ResultSet resultSet=null;

    public void connect() {
        Scanner scanner=new Scanner(System.in);
        IO.println("==============================================================");
        try{
            connection= DriverManager.getConnection(url,username,password);
            statement=connection.createStatement();
            resultSet=statement.executeQuery("SELECT*FROM employee");

                while(resultSet.next()){
                setId(resultSet.getLong("id"));
                setName(resultSet.getString("name"));
                setDepartment(resultSet.getString("department"));
                setDuty(resultSet.getString("duty"));
                setPosition(resultSet.getString("position"));
                setStatus(resultSet.getBoolean("status"));


                IO.print("今天是否在线？->");
                setStatus(scanner.nextBoolean());
                if(isStatus()){
                    p("【工号】："+getId());
                    p("【姓名】："+getName());
                    p("【部门】："+getDepartment());
                    p("【职位】："+getPosition());
                    p("【岗位职责】："+getDuty());
                    p("【是否在线】："+isStatus());
                }else p("该员工不在线！");
            }


        }catch(SQLException e){
            IO.println("数据库连接失败！"+e.getMessage());
        }
    }
    Scanner scannerOfAdd =new Scanner(System.in);
    @Override
    public void add(){
        try{
          connection=DriverManager.getConnection(url,username,password);
          statement=connection.createStatement();

            IO.print("请输入你要添加的人数：");
            int quantity= scannerOfAdd.nextInt();
            scannerOfAdd.nextLine();
            if(quantity<=0) throw new IllegalArgumentException("请输入大于零的整数！");

            for(int i=1;i<=quantity;i++){
                IO.print("请输入姓名：");setName(scannerOfAdd.next());
                IO.println();
                IO.print("请添加部门：");setDepartment(scannerOfAdd.next());
                IO.println();
                IO.print("请添加岗位：");setPosition(scannerOfAdd.next());
                IO.println();
                IO.print("请添加职责：");setDuty(scannerOfAdd.next());
                IO.println();
                //INSERT INTO employee(name,department,duty,position)VALUES
                //        ('CONAN DOYLE','SALES DEPARTMENT','SELL PRODUCT','MANAGER');
                String newPeopleSQL = "INSERT INTO employee(name,department,duty,position) VALUES ('"
                        +getName()+"','"+getDepartment()+"','"+getDuty()+"','"+getPosition()+"')";
                statement.executeUpdate(newPeopleSQL);
               // resultSet=statement.executeQuery("SELECT*FROM employee");
            }
        }catch(SQLException e){
            IO.println("添加人数方法异常！"+e.getMessage());
        }catch(InputMismatchException e){
            IO.println("请输入正确信息！");
        }finally{
            try{
                if(resultSet!=null) resultSet.close();
                if(statement!=null) statement.close();
                if(connection!=null) connection.close();
            }catch(SQLException e){
                IO.println("数据库关闭异常");
            }
        }

    }
}

public class MyCompany{
    void main(){
        DatabaseOfCompany d=new DatabaseOfCompany();
        d.connect();
        d.add();
    }
}