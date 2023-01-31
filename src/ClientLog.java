import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ClientLog {
    private int productNum;
    private int amount;
    File csvFile = new File("log.csv");

    public void log(int productNum, int amount){
        this.productNum = productNum;
        this.amount = amount;
    }

    public void exportAsCSV(File file){
        if (!csvFile.exists()){
            try (FileWriter fileWriter = new FileWriter(file, true)){
                fileWriter
                        .append("productNum, amount\n")
                        .append(productNum+" "+amount + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (FileWriter fileWriter = new FileWriter(file, true)){
                fileWriter.append(productNum+" "+amount + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
