import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileWriter;

public class App {
    private static String outputFilePath;
    public static void main(String[] args) throws Exception {
        String mode = args[0];
        String encryptOrDecrypt = args[1];
        String keymode = args[2];
        Key key = keymode.equals("-gen") ? generateKey() : new Key(args[3]); // get key ready for process
        String textOrFilePath = null;
        outputFilePath = args[args.length - 1];
        // text mode
        if (mode.equals("-t")) {
            // read rest of string input by args index
            textOrFilePath = readAllString(keymode, args);
            System.out.println(textOrFilePath);
            // string encryption
            if (encryptOrDecrypt.equals("-e")) {
                encryptText(textOrFilePath, key);
                // string decryption
            } else if (encryptOrDecrypt.equals("-d")) {
                // check for key mode
                if (keymode.equals("-gen")) {
                    System.out.println("Need a key to decrypt");
                    System.exit(0);
                }
                // decrypth text
                decryptText(textOrFilePath, key);
            } else {
                System.out.println("Invalid input from text mode");
            }
        // file mode
        } else if (mode.equals("-f")) {
            // read file path by args index
            textOrFilePath = keymode.equals("-gen") ? args[3] : args[4];
            // file encryption
            if (encryptOrDecrypt.equals("-e")) {
                System.out.println("reading path: " + textOrFilePath);
                if (fileExists(textOrFilePath)) {
                    encryptFile(textOrFilePath, key);
                } else {
                    System.out.println("Invalid file path from file encryption");
                }
            // file decryption
            } else if (encryptOrDecrypt.equals("-d")) {
                System.out.println("reading path: " + textOrFilePath);
                if (fileExists(textOrFilePath)) {
                    decryptFile(textOrFilePath, key);
                } else {
                    System.out.println("Invalid file path from file decryption");
                }
            } else {
                System.out.println("Invalid input from file mode");
            }
        } else {
            System.out.println("Invalid input from command line");
        }
        // System.out.println("Output file: " + outputFilePath);
    }

    // <<----------------- END OF MAIN ----------------->>
    // ----------------------------------------------------------------
    // <<----------------- FILE OPERATION ----------------->>
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static byte[] readFileToByteArray(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public static void writeToFile(byte[] data) throws IOException {
        FileWriter writer = new FileWriter(outputFilePath);
        writer.write(data.toString());
        writer.close();
    }

    // <<----------------- END OF FILE OPERATION ----------------->>
    // ----------------------------------------------------------------
    // <<----------------- STRING OPERATION ----------------->>
    public static String readAllString(String keyMode, String[] args) {
        String str = "";
        if (keyMode.equals("-gen")) {
            str += args[3];
            for (int i = 4; i < args.length - 1; i++) {
                str += " " + args[i];
            }
        } else {
            str += args[4];
            for (int i = 5; i < args.length - 1; i++) {
                str += " " + args[i];
            }
        }
        return str;
    }

    // <<----------------- END OF STRING OPERATION ----------------->>
    // ----------------------------------------------------------------
    // <<----------------- KEY OPERATION ----------------->>
    public static Key generateKey() {
        Key key = new Key();
        key.generateKey();
        return key;
    }

    // <<----------------- END OF KEY OPERATION ----------------->>
    // ----------------------------------------------------------------
    // <<----------------- ENCRYPT AND DECRYPT ----------------->>
    public static void encryptText(String text, Key key) throws IOException {
        // encryption process
        System.out.println("Encrypting text: " + text + " with key: " + key.getKey());
        writeToFile(text.getBytes());
    };

    public static void decryptText(String text, Key key) throws IOException {
        // decryption process
        System.out.println("Decrypting text: " + text + " with key: " + key.getKey());
        writeToFile(text.getBytes());
    };

    public static void encryptFile(String filePath, Key key) throws IOException {
        byte[] fileBytes = null;
        try {
            fileBytes = readFileToByteArray(filePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        // encryption process
        System.out.println("Encrypting file: " + fileBytes + " with key: " + key.getKey());
        writeToFile(fileBytes);
    };

    public static void decryptFile(String filePath, Key key) throws IOException {
        byte[] fileBytes = null;
        try {
            fileBytes = readFileToByteArray(filePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        // decryption process
        System.out.println("Decrypting file: " + fileBytes + " with key: " + key.getKey());
        writeToFile(fileBytes);
    };
    // <<----------------- END OF ENCRYPT AND DECRYPT ----------------->>
}
/*                
                   ***HOLY SECTION***
                   PRAYING NOT TO BUG
                           _
                        _ooOoo_
                       o8888888o
                       88" . "88
                       (| -_- |)
                       O\  =  /O
                    ____/`---'\____
                  .'  \\|     |//  `.
                 /  \\|||  :  |||//  \
                /  _||||| -:- |||||_  \
                |   | \\\  -  /'| |   |
                | \_|  `\`---'//  |_/ |
                \  .-\__ `-. -'__/-.  /
              ___`. .'  /--.--\  `. .'___
           ."" '<  `.___\_<|>_/___.' _> \"".
          | | :  `- \`. ;`. _/; .'/ /  .' ; |
          \  \ `-.   \_\_`. _.'_/_/  -' _.' /
===========`-.`___`-.__\ \___  /__.-'_.'_.-'================
 */