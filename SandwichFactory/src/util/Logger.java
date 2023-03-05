package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Logger {
    public static void write(String line) {
        try {
            PrintStream writer;
            writer = new PrintStream(new FileOutputStream("classes\\logs\\log.txt", true));
            writer.println(line);
            writer.close();
        } catch (IOException e) {
            System.out.println("File not found!");
        }
    }

    public static void clean() {
        try {
            Files.createDirectories(Paths.get("classes\\logs"));

            PrintStream writer;
            writer = new PrintStream(new FileOutputStream("classes\\logs\\log.txt"));
            writer.print("");
            writer.close();
        } catch (IOException e) {
            System.out.println("File not found!");
        }
    }
}
