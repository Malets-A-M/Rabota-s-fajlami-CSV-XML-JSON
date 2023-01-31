import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Basket {
    private String[] products;
    private int[] prices;
    private int[] amount;
    private int productNum;
    private int productCount;
    private String input;
    private String[] parts;
    private Scanner scanner = new Scanner(System.in);
    private File basket = new File("basket.txt");
    private File basketJSON = new File("basket.json");
    private File csvFile = new File("log.csv");
    private JSONObject jsonObject = new JSONObject();
    private JSONParser jsonParser = new JSONParser();
    private ClientLog clientLog = new ClientLog();


    public Basket(int[] prices, String[] products) {
        this.prices = prices;
        this.products = products;
        this.amount = new int[products.length];
    }
    public void addToCart(){
        if (basketJSON.exists()){
            System.out.println("Корзина уже существует");
           loadJSON(basketJSON);
            printCart();
        } else {
            System.out.println("Новая корзина");
        }
        while (true) {
            System.out.println("Выберите товар и количество или введите `end`");
            setInput(scanner.nextLine());

            if (getInput().equals("end")) {
                saveJSON(basketJSON);
                break;
            }
        setParts(input.split(" "));
        setProductNum(Integer.parseInt(parts[0]));
        setProductCount(Integer.parseInt(parts[1]));
        clientLog.log(getProductNum(), getProductCount());
        clientLog.exportAsCSV(csvFile);
        amount[productNum - 1] += productCount;
        System.out.println("Вы добавили в корзину: " + products[productNum - 1] + " " + productCount + " шт.\n");
        }
    }

    public int sumProducts(){
        int sum = 0;
        int s = 0;
        while (s < getAmount().length){
            sum += getAmount()[s] * getPrices()[s];
            s++;
        }
        return sum;
    }

    public void printCart() {
        System.out.println("\nВаша корзина:");
        System.out.println(listBasket());
        System.out.println("Итого: " + sumProducts() + " руб");
    }

    private StringBuilder listBasket() {
        StringBuilder listBasket = new StringBuilder();
        for (int i = 0; i < amount.length; i++) {
            if (amount[i] != 0) {
                listBasket.append(products[i] + " " + amount[i] + " шт " + prices[i] + " руб/шт " + (amount[i] * prices[i]) + " руб в сумме\n");
            }
        }
        return listBasket;
    }
    public void saveJSON(File file){
        JSONArray amount = new JSONArray();
        for (int count : getAmount()){
            amount.add(count);
        }
        jsonObject.put("amount", amount);
        try (FileWriter fileWriter = new FileWriter(file)){
            fileWriter.write(String.valueOf(jsonObject));
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadJSON(File file){
        try {
            Object obj = jsonParser.parse(new FileReader(file));
            JSONObject amountParsedJson = (JSONObject) obj;
            JSONArray amountJSON = (JSONArray)amountParsedJson.get("amount");
            List<Integer> list = new ArrayList<>();
            for (Object o : amountJSON){
                list.add(((Number)o).intValue());
            }
            setAmount(list.stream().mapToInt(i -> i).toArray());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int[] getPrices() {
        return prices;
    }

    public int[] getAmount() {
        return amount;
    }

    public String[] getProducts() {
        return products;
    }

    public String getInput() {
        return input;
    }

    public void setAmount(int[] amount) {
        this.amount = amount;
    }

    public void setProductNum(int productNum) {
        this.productNum = productNum;
    }
    public int getProductNum() {
        return productNum;
    }
    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
    public int getProductCount() {
        return productCount;
    }
    public void setInput(String input) {
        this.input = input;
    }

    public void setParts(String[] parts) {
        this.parts = parts;
    }
}
