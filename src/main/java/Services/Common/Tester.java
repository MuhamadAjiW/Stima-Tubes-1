package Services.Common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
// nitip yoo buat debug lewat file aja
public class Tester {
    public static void appendFile(String line, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(line);
            bw.newLine();
            bw.close();
        } catch (IOException e) {

        }
    }
}