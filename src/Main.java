import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] products = {"Хлеб", "Яйца", "Молоко", "Картофель", "Орехи"};
        int[] prices = {100, 200, 250, 185, 500};
        int[] amount = new int[products.length];
        int productNumber = 0;
        int productCount = 0;
        int sumProducts = 0;

        System.out.println("\nСписок возможных товаров для покупки:\n" +
                           "=====================================");
        for (int i = 0; i < products.length; i++) {
            System.out.println(i+1 + ". " + products[i] + " " + prices[i] + " руб/шт;");
        }
        System.out.println("=====================================");
        while (true) {
            System.out.println("Выберите товар и количество или введите `end`");
            String input = scanner.nextLine();

            if (input.equals("end")){
                break;
            }

            String parts[] = input.split(" ");
            productNumber = Integer.parseInt(parts[0]);
            productCount = Integer.parseInt(parts[1]);
            amount[productNumber - 1] += productCount;
            sumProducts += amount[productNumber - 1] * prices[productNumber - 1];
            System.out.println("Вы добавили в корзину: " + products[productNumber - 1] + " " + productCount + " шт.\n");
        }
        System.out.println("\nВаша корзина:");
        for (int i = 0; i < amount.length; i++) {
            if (amount[i] != 0) {
                System.out.println(products[i] + " " + amount[i] + " шт " + prices[i] + " руб/шт " + (amount[i] * prices[i]) + " руб в сумме");
            }
        }
        System.out.println("Итого: " + sumProducts + " руб");
    }
}
