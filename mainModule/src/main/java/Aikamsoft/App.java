package Aikamsoft;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static workWithFiles.CreateJson.createOutputJson;

/**
 * Запуск программы
 */
public class App {
    public static void main(String[] args) throws IOException {
        if (new File(args[1]).exists()) {
            createOutputJson(args[0], args[1], args[2]);
        } else {
            final FileWriter writer = new FileWriter(args[2]);
            writer.write("{\n" +
                    "    \"type\": \"error\",\n" +
                    "    \"message\": \"No such input file\" \n" +
                    "}\n");
            writer.flush();
        }
    }
}
