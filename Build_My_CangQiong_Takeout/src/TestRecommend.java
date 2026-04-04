import java.util.ArrayList;
import java.util.List;

public class TestRecommend {

    static List<String> recommend(List<String> purchaseProduct, List<String> originalProduct) {
        List<String> recommendProduct = new ArrayList<>();

        for (String things : originalProduct) {
            if (!purchaseProduct.contains(things)) {
                recommendProduct.add(things);
            }
        }
        return recommendProduct;
    }

    public static void main(String[] a) {
        List<String> purchaseProduct = new ArrayList<>();
        purchaseProduct.add("苹果");
        purchaseProduct.add("橘子");

        List<String> originalProduct = new ArrayList<>();
        originalProduct.add("苹果");
        originalProduct.add("香蕉");
        originalProduct.add("橙子");
        originalProduct.add("橘子");

        List<String> result = recommend(purchaseProduct, originalProduct);
        System.out.println("原始商品："+originalProduct);
        System.out.println("购买"+purchaseProduct);
        System.out.println(result);  // 输出：[香蕉, 橙子]
    }
}