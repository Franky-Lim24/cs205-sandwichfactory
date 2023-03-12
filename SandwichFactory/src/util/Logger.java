package util;

import java.io.*;
import java.nio.file.*;

public class Logger {
    // Writes a string line to a file called "log.txt" located in the "classes/logs" directory.
    // If the directory doesn't exist, the method creates it before writing to the file.
    public static void write(String line) {
        try {
            Files.createDirectories(Paths.get("classes/logs"));
            PrintStream writer;
            writer = new PrintStream(new FileOutputStream("classes/logs/output.txt", true));
            writer.println(line);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
