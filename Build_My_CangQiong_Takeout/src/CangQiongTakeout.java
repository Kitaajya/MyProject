import java.sql.*;
import java.util.*;

class IO{
    public static void println(Object obj){
        System.out.println(obj);
    }
    public static void println(){
        System.out.println();
    }
    public static void print(Object obj){
        System.out.print(obj);
    }

}
//产品类
class Product{
    private String[] production ;       //产品名称
    private double[] price      ;       //商品原价
    private double[] killedPrice;       //砍价后的商品
    public Product(String[] production, double[] price, double[] killedPrice){
        this.production=production;
        this.price=price;
        this.killedPrice=killedPrice;
    }
    public String[]getProduction(){
        return production;
    }
    public double[]getPrice(){
        return price;
    }
    public double[]getKilledPrice(){
        return killedPrice;
    }
    public void setProduction(String[]production){
        this.production=production;
    }
    public void setPrice(double[]price){
        this.price=price;
    }
    public void setKilledPrice(double[]killedPrice){
        this.killedPrice=killedPrice;
    }
}
class Purchase extends Product{
    private String user               ;    //用户姓名
    private long orderId              ;    //订单号
    private int quantity              ;    //购买数量
    private String[]name              ;    //购买产品名称
    private String[] purchaseIntention;    //购买意向，推荐商品

    public Purchase(String[] phone, double[] price, double[] killedPrice){
        super(phone,price,killedPrice);
        user              = "user";
        orderId           = 20260001;
        quantity          = 1;
        name              = new String[]{"name"};
        purchaseIntention = new String[]{"苹果","芒果"};
    }
    // user
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // orderId
    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    // quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // name
    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    // purchaseIntention
    public String[] getPurchaseIntention() {
        return purchaseIntention;
    }

    public void setPurchaseIntention(String[] purchaseIntention) {
        this.purchaseIntention = purchaseIntention;
    }
    final String url="jdbc:mysql://localhost:3306/Shop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    protected  final String username="root";
    protected  final String password="123456";
    String sql="select * from product";
    String[]originalProduct=new String[]{"苹果","香蕉","橙子","橘子","菠萝"};

    private void off(){
        try{
            if(resultSet!=null)resultSet.close();
            if(statement!=null)statement.close();
            if(connection!=null)connection.close();
        }catch(SQLException ec){
            IO.println("数据库关闭异常！"+ec.getMessage());
        }
    }

    private List<String>showOriginalProduct(){
        List<String>originalProductList=new ArrayList<>();
        for(int i=0;i<originalProduct.length;i++){
            originalProductList.add(originalProduct[i]);
        }
        return originalProductList;
    }
    Connection connection;
    Statement statement;
    ResultSet resultSet;
    private void showConnect(){
        try{
            final String url="jdbc:mysql://localhost:3306/Shop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            final String username="root";
            final String password="123456";
            connection= DriverManager.getConnection(url,username,password);
            statement=connection.createStatement();
            resultSet=statement.executeQuery(sql);
        } catch(SQLException e) {
            IO.println("数据库连接异常！--位于方法showConnect()"+e.getMessage());
        }
    }


    public void connect(){
        try{
            showConnect();
            while(resultSet.next()){
                int things=resultSet.getInt("things");
                String thingsName=resultSet.getString("thingsName");
                long thingsOrderID=resultSet.getLong("thingsOrderID");
                String purchaseIntention=resultSet.getString("purchaseIntention");
                IO.println("╔══════════════════════════════════════╗");
                IO.println("║             苍穹外卖详细信息            ║");
                IO.println("╠══════════════════════════════════════╝");
                IO.println("║ A:苹果  B:香蕉  C:橙子  D:橘子  E:菠萝                              ");
                IO.println("║ 商品名称："+thingsName);
                IO.println("║ 商品个数："+things);
                IO.println("║ 商品订单："+thingsOrderID);
                IO.println("║ 购买意向："+purchaseIntention);
                IO.println("╚═══════════════════════════════════════\n");
                List<String>recordPurchasedProduct=new ArrayList<>();
                recordPurchasedProduct.add(thingsName);
                List<String>recordOriginalProduct=new ArrayList<>();
                for(int i=0;i<originalProduct.length;i++){
                    recordOriginalProduct.add(originalProduct[i]);
                }
                recommend(recordPurchasedProduct,recordOriginalProduct);
                //参数：购买的商品，原始的商品。类型：List<String>,List<String>
            }
        } catch(SQLException e) {
            IO.println("数据库连接异常！"+e.getMessage());
        }finally {
            off();
        }
    }

    Scanner scanner=new Scanner(System.in);
    private List<String> purchase(){
        List<String>purchaseProduct=new ArrayList<>();
        String A="苹果",B="香蕉",C="橙子",D="橘子",E="菠萝";

        HashMap<String,Runnable>map=new HashMap<>();
        IO.println("请输入选择购买/或输入exit以退出购买：");

        while(true){
            String choice=scanner.next().trim();
            if(choice.equals("exit"))break;
            Runnable task=()-> showWhatYouWillPurchase(choice);
            task.run();
            map.put(choice,task);
            purchaseProduct.add(choice);
        }
        return purchaseProduct;
    }

    private void showWhatYouWillPurchase(String choice){
        IO.println("选择了"+choice+" ");
        if(!Objects.equals(choice, "A") && !Objects.equals(choice, "B") && !Objects.equals(choice, "C") && !Objects.equals(choice, "D") && !Objects.equals(choice, "E"))
            throw new IllegalArgumentException("不存在此商品！");
    }

    public List<String> recommend(List<String>purchaseProduct,List<String>originalProduct) {
        List<String>recommendProduct=new ArrayList<>();
        for(String goods:originalProduct){
            if(!purchaseProduct.contains(goods))recommendProduct.add(goods);
        }
        IO.println("推荐商品："+recommendProduct);
        return recommendProduct;
    }
    public void testRecommend(){
        recommend(purchase(),showOriginalProduct());
    }
}
public class CangQiongTakeout{
   public static void main(String[]args) {
        Purchase p=new Purchase(new String[]{"苹果","芒果"},new double[]{32.2,242.4},new double[]{31.0,241.2});
        p.connect();
    }
}