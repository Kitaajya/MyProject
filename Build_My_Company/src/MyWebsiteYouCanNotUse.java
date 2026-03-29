import java.sql.*;

class IO{
    public static void println(Object a){
        System.out.println(a);
        if(a==null) System.out.println();
    }
    public static void print(Object a){
        System.out.print(a);
    }
}
abstract class Demo {
    private String name;
    private long id;
    private String work;        //职业
    abstract void add() throws SQLException;
    abstract void delete();
    abstract void edit();
    abstract void select();

    public Demo() {
    }

    public Demo(String name, long id, String work) {
        this.name = name;
        this.id = id;
        this.work = work;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getWork() {
        return work;
    }
    public void setWork(String work) {
        this.work = work;
    }
}
class DemoChild extends Demo {

    public String sqlToSelect="select * from Infor";

    private static final String url="jdbc:mysql://localhost:3306/Web?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String username="root";
    private static final String password ="123456";

    Connection connection=null;
    PreparedStatement preparedStatement=null;
    ResultSet resultSet;
    public DemoChild(String name, long id, String work){
        super(name,id,work);
    }
    public DemoChild(){}

    public void connect() throws SQLException {
        connection= DriverManager.getConnection(url,username, password);
    }

    @Override
    void add() throws SQLException {
        connect();
        setName("柯南道尔");
        setWork("侦探");
        setName("福尔摩斯");
        setWork("伊沙恩");
        String sqlToAdd=" insert into Infor(name,work) values (?,?)";  //第一个问号是name，第二个问号是work
        preparedStatement =connection.prepareStatement(sqlToAdd);
        //添加姓名
        preparedStatement.setString(1,"卡车");
        //添加工作
        preparedStatement.setString(2,"afcv1");

        int i= preparedStatement.executeUpdate();//影响行数
        //preparedStatement = connection.prepareStatement(sqlToSelect);

        PreparedStatement q=connection.prepareStatement(sqlToAdd);
        resultSet=q.executeQuery(sqlToAdd);

        while(resultSet.next()){
            String name_q=resultSet.getString("name");
            String work_q=resultSet.getString("work");
            IOTest.println(name_q);
            IOTest.println(work_q);
            IOTest.println("影响行数"+i);
        }

        resultSet= preparedStatement.executeQuery();

        String w1=resultSet.getString("name");
        String w2=resultSet.getString("work");
        IOTest.println(w1+w2);
        resultSet=preparedStatement.executeQuery();
        preparedStatement.close();
    }
    @Override
    void delete(){

    }
    @Override
    void edit(){

    }
    @Override
    void select(){

    }

}


public class MyWebsiteYouCanNotUse{
    public static void main(String[]a) throws SQLException {

        DemoChild w=new DemoChild();
        w.add();
    }
}
