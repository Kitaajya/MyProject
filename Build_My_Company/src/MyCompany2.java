import java.io.IO;
import java.sql.*;
import java.util.HashMap;
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
    public void delete(){IO.println("删除人");}
    public void select(){
        IO.println("查找人");
    }
    public void onLine(){IO.println("是否在线？");}
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
    /***************************************************************************************/
    Scanner scanner=new Scanner(System.in);
    public void connect() {
        IO.println("==============================================================");
        try{
            open();
            resultSet=statement.executeQuery("SELECT*FROM employee");
            while(resultSet.next()){
                setId(resultSet.getLong("id"));
                setName(resultSet.getString("name"));
                setDepartment(resultSet.getString("department"));
                setDuty(resultSet.getString("duty"));
                setPosition(resultSet.getString("position"));
                setStatus(resultSet.getBoolean("status"));
                online();
            }
        }catch(SQLException e){
            IO.println("数据库连接失败！"+e.getMessage());
        }
    }
    /**********************************************************************************/

    public void online(){
        try{
            open();
            IO.print("请打卡【输入打卡工号】：");
            long idToClockIN=scanner.nextLong();
            if(idToClockIN==-1){
                IO.println("退出程序！");
                System.exit(0);
            }else{
                setId(idToClockIN);
                String selectIdOnSQL="SELECT*FROM employee WHERE id="+idToClockIN+";";
                statement.executeQuery(selectIdOnSQL);
                resultSet=statement.executeQuery(selectIdOnSQL);//影响行数

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
        }catch(SQLException eo){
            IO.println("打卡数据库连接异常！"+ eo.getMessage());
        }
    }
    //连接数据库
    public void open(){
        try{
            connection=DriverManager.getConnection(url,username,password);
            statement=connection.createStatement();
            //resultSet=statement.executeQuery("SELECT*FROM employee");
        }catch(SQLException ep){
            IO.println("数据库连接异常！");
        }
    }

    public void off(){
        try{
            if(resultSet!=null) resultSet.close();
            if(statement!=null) statement.close();
            if(connection!=null) connection.close();
        }catch(SQLException e){
            IO.println("数据库关闭异常");
        }
    }

    Scanner scannerOfAdd =new Scanner(System.in);
    @Override
    public void add(){
        try{
            open();
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
           off();
        }

    }
    @Override
    public void select() {
        try{
            open();
            resultSet=statement.executeQuery("SELECT*FROM employee");
            while(resultSet.next()){

                setId(resultSet.getLong("id"));
                setName(resultSet.getString("name"));
                setDepartment(resultSet.getString("department"));
                setPosition(resultSet.getString("position"));
                setDuty(resultSet.getString("duty"));

                p("【工   号】："+getId());
                p("【姓   名】："+getName());
                p("【部   门】："+getDepartment());
                p("【职   位】："+getPosition());
                p("【岗位职责】："+getDuty());
                p("【是否在线】："+isStatus());
            }
        }catch(SQLException w){
            IO.println("数据库查询异常！"+w.getMessage());
        }finally {
            off();
        }
    }
    @Override
    public void delete() {
        try {
            open();
            p("请输入删除者的工号：");
            setId(scannerOfAdd.nextLong());
            String deletePeopleSQL = "delete from employee where id=" + getId();
            connection=DriverManager.getConnection(url,username,password);
            statement=connection.createStatement();
            int r=statement.executeUpdate(deletePeopleSQL);     //影响行数
            p("影响行数"+r+"行");

        }catch(SQLException e){
            IO.println("删除异常！"+e.getMessage());
        }catch(InputMismatchException ew){
            IO.println("输入信息爆发异常！");
        }finally{
            off();
        }
    }
    @Override
    public void edit(){
        try{
            open();
            IO.println("修改信息！");
            String willName;//要修改的名称
            IO.println("请输入要修改的名字：");
            willName=scannerOfAdd.next();
            scannerOfAdd.nextLine();
            IO.println("请输入修改者的工号：");
            long willId=scannerOfAdd.nextLong();
            String editInfo="update employee set name='"+willName+"' where id="+willId;
            int i=statement.executeUpdate(editInfo);
            IO.println("影响行数"+i);
        }catch(SQLException ed){
            IO.println("修改信息发生异常！"+ed.getMessage());
        }
    }
    void show(){
        p("\n■□■□■□■□■□■□■□■□■□■□■□■□■□■□*");
        p("□    北京通融物管 人事管理系统    ■");
        p("■□■□■□■□■□■□■□■□■□■□■□■□■□■□■□■");
        p("□    A. 添加员工               ■");
        p("□    B. 查询员工               ■");
        p("□    C. 修改信息               ■");
        p("□    D. 删除员工               ■");
        p("□    E. 退出系统               ■");
        p("□    F. 连接公司               ■");
        p("■□■□■□■□■□■□■□■□■□■□■□■□■□■□■□■");
        p("请选择功能：");
    }
    public void self(){
        HashMap<Character,Runnable>map=new HashMap<>();
        Runnable taskOfAdd,taskOfSelect,taskOfDelete,taskOfEdit,taskOfConnect;
        taskOfAdd=this::add;
        taskOfSelect= this::select;
        taskOfDelete= this::delete;
        taskOfEdit=this::edit;
        taskOfConnect=this::connect;
        Scanner scannerSelf=new Scanner(System.in);
        show();
        Runnable taskOfExit=()->{
            IO.println("退出程序！");
            System.exit(0);
        };
        map.put('A', taskOfAdd);
        map.put('B',taskOfSelect);
        map.put('C',taskOfEdit);
        map.put('D',taskOfDelete);
        map.put('E',taskOfExit);
        map.put('F',taskOfConnect);
        while(true){
            char choice=scannerSelf.next().charAt(0);
            if(map.containsKey(choice)) map.get(choice).run();
            else throw new IllegalArgumentException("请输入正确文本！");
        }
    }
}
void main(){
    DatabaseOfCompany d=new DatabaseOfCompany();
    d.self();
}
