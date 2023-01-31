import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException {
        ProductsAndPrices productsPrices = new ProductsAndPrices();
        String[] products = productsPrices.getProducts();
        int[] prices = productsPrices.getPrices();
        Basket basket = new Basket(prices,products);

        System.out.println("\nСписок возможных товаров для покупки:\n" +
                           "=====================================");
        for (int i = 0; i < products.length; i++) {
            System.out.println(i+1 + ". " + products[i] + " " + prices[i] + " руб/шт;");
        }
        System.out.println("=====================================");
        basket.addToCart();
    }
}
