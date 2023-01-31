import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
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
    private File fileSaveBasket;
    private File fileLoadBasket;
    private File logFileCSV;
    private File config = new File("shop.xml");
    private JSONObject jsonObject = new JSONObject();
    private JSONParser jsonParser = new JSONParser();
    private ClientLog clientLog = new ClientLog();
    private ReadShopXML readShopXML = new ReadShopXML();


    public Basket(int[] prices, String[] products) {
        this.prices = prices;
        this.products = products;
        this.amount = new int[products.length];
    }
    public void addToCart() throws ParserConfigurationException, IOException, TransformerException, SAXException {
        readShopXML.loadConfigShopXML(config);
        fileSaveBasket = new File(readShopXML.getSaveFileName());
        fileLoadBasket = new File(readShopXML.getLoadFileName());
        logFileCSV = new File(readShopXML.getLogFileName());
        if (fileLoadBasket.exists()){
            System.out.println("Корзина уже существует");
            if (readShopXML.isLoadEnabled()){
                switch (readShopXML.getLoadFormat()) {
                    case "json" -> loadJSON(fileLoadBasket);
                    case "bin" -> loadBIN(fileLoadBasket);
                    case "txt" -> loadTXT(fileLoadBasket);
                }
            }
            printCart();
        } else System.out.println("Новая корзина");
        while (true) {
            System.out.println("Выберите товар и количество или введите `end`");
            setInput(scanner.nextLine());

            if (getInput().equals("end")) break;

            setParts(input.split(" "));
            setProductNum(Integer.parseInt(parts[0]));
            setProductCount(Integer.parseInt(parts[1]));
            amount[productNum - 1] += productCount;
            System.out.println("Вы добавили в корзину: " + products[productNum - 1] + " " + productCount + " шт.\n");

            if (readShopXML.isLogEnabled()) {
                clientLog.log(getProductNum(), getProductCount());
                clientLog.exportAsCSV(logFileCSV);
            }
        }
        printCart();
        if (readShopXML.isSaveEnabled()){
            switch (readShopXML.getSaveFormat()) {
                case "json" -> saveJSON(fileSaveBasket);
                case "bin" -> saveBIN(fileSaveBasket);
                case "txt" -> saveTXT(fileSaveBasket);
            }
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
    public void saveTXT(File textFile){
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(textFile, false))){
            outputStream.write(listBasket().toString().getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void saveBIN (File file){
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file, false))) {
            Basket saveBasket = new Basket(getPrices(), getProducts());
            saveBasket.setAmount(getAmount());
            objectOutputStream.writeObject(saveBasket);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
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
    public void loadTXT(File textFile){
        try (InputStreamReader inputStream = new InputStreamReader(new FileInputStream(textFile))) {
            int byteCod = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while ((byteCod = inputStream.read()) != -1){
                stringBuilder.append(Character.toString(byteCod));
            }

            ProductsAndPrices productsPrices = new ProductsAndPrices();
            String[] products = productsPrices.getProducts();
            int[] prices = productsPrices.getPrices();
            int[] amount = new int[products.length];

            String [] parts;
            String [] partsBasket;
            parts = stringBuilder.toString().split("\n");
            int i = 0;
            int i1 = 0;
            while (i < parts.length){
                partsBasket = parts[i].split(" ");
                i1 = 0;
                while (i1 < products.length){
                    if (products[i1].equals(partsBasket[0])){
                        amount[i1] += Integer.parseInt(partsBasket[1]);
                    }
                    i1++;
                }
                i++;
            }
            setAmount(amount);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadBIN(File file){
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            Basket b;
            b = ((Basket) objectInputStream.readObject());
            setAmount(b.getAmount());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
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
