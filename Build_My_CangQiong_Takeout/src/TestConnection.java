import java.sql.*;

public class TestConnection {

    static void connection() throws SQLException {
        final String url="jdbc:mysql://localhost:3306/Shop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        final String username="root";
        final String password="123456";
        String sql="select * from product";

        Connection connection;
        Statement statement;
        ResultSet resultSet=null;
        connection= DriverManager.getConnection(url,username,password);
        statement=connection.createStatement();
        resultSet=statement.executeQuery(sql);
        while(resultSet.next()){
            int things=resultSet.getInt("things");
            String thingsName=resultSet.getString("thingsName");
            long thingsOrderID=resultSet.getLong("thingsOrderID");
            String purchaseIntention=resultSet.getString("purchaseIntention");
            System.out.println("商品个数："+things);
            System.out.println("商品名称："+thingsName);
            System.out.println("商品订单："+thingsOrderID);
            System.out.println("购买意向："+purchaseIntention);
        }
    }
    public static void main(String[] args) throws SQLException {
        connection();
    }
}
