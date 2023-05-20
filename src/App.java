import java.io.IOException;
import java.math.BigInteger;

public class App {
    private static String outputFilePath;
    static FileOpr fileOpr = new FileOpr();
    public static void main(String[] args) throws Exception {
        String mode = args[0];
        String encryptOrDecrypt = args[1];
        String keymode = args[2];
        Key key = keymode.equals("-gen") ? new Key() : new Key(args[3]); // get key ready for process
        String textOrFilePath = null;
        outputFilePath = args[args.length - 1];

        // FileOpr fileOpr = new FileOpr();
        
        if(fileOpr.fileExists(outputFilePath)) {
            fileOpr.writeEmptyFile(outputFilePath);
        }
        // text mode
        if (mode.equals("-t")) {
            // read rest of string input by args index
            textOrFilePath = readAllString(keymode, args);
            System.out.println("Working on: " + textOrFilePath);
            // string encryption
            if (encryptOrDecrypt.equals("-e")) {
                key.generateKey(textOrFilePath);
                encryptText(textOrFilePath, key);
                // string decryption
            } else if (encryptOrDecrypt.equals("-d")) {
                // check for key mode
                if (keymode.equals("-gen")) {
                    System.out.println("Need a key to decrypt");
                    System.exit(0);
                }
                // decrypth text
                //read key
                //read a, b
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
                if (fileOpr.fileExists(textOrFilePath)) {
                    encryptFile(textOrFilePath, key);
                } else {
                    System.out.println("Invalid file path from file encryption");
                }
            // file decryption
            } else if (encryptOrDecrypt.equals("-d")) {
                System.out.println("reading path: " + textOrFilePath);
                if (fileOpr.fileExists(textOrFilePath)) {
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
    }

    // <<----------------- END OF MAIN ----------------->>
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

    public static BigInteger readingMassage(String item) throws IOException{
        FileOpr fileOpr = new FileOpr();
        // Scanner in = new Scanner(System.in);

        BigInteger massage = fileOpr.fileExists(item) ? new BigInteger(fileOpr.readFiletoBigInteger(item)) : new BigInteger(item.getBytes());
        System.out.println("massage : "+massage);
        System.out.println("Massage Byte length : "+massage.toString().length());
        return massage;
    }

    // <<----------------- END OF STRING OPERATION ----------------->>
    // ----------------------------------------------------------------
    // <<----------------- ENCRYPT AND DECRYPT ----------------->>
    public static void encryptElgamal(BigInteger massage) throws IOException{
        generateKey genKey = new generateKey();
        FileOpr rw = new FileOpr();

        System.out.println("Reading public key...");
        BigInteger p = rw.readKeytoBigInteger("./test/key/keyP.txt");
        System.out.println("p : "+p);
        BigInteger g = rw.readKeytoBigInteger("./test/key/keyG.txt");
        System.out.println("g : "+g);
        BigInteger y = rw.readKeytoBigInteger("./test/key/keyY.txt");
        System.out.println("y : "+y);

        System.out.println("Generate k...");
        BigInteger k = genKey.random_K(p);
        rw.writeKeytoFile(k, "keyK.txt");
        System.out.println("k : "+k);

        System.out.println("Generate a...");
        BigInteger a = g.modPow(k, p);
        rw.writeKeytoFile(a, "keyA.txt");
        System.out.println("a : "+a);

        System.out.println("Encryption to b(cipher text)...");
        BigInteger b = y.modPow(k, p);
        b = b.multiply(massage).mod(p);
        rw.writeKeytoFile(b, "cipherText.txt");
        System.out.println("b : "+b);
        System.out.println("Encryption Complete!!");
    }

    public static void decryptElgamal() throws IOException{
        FileOpr rw = new FileOpr();

        System.out.println("Reading Key and Cipher Text...");
        BigInteger p = rw.readKeytoBigInteger("keyP.txt");
        BigInteger u = rw.readKeytoBigInteger("keyU(privateKey).txt");
        BigInteger a = rw.readKeytoBigInteger("keyA.txt");
        BigInteger b = rw.readKeytoBigInteger("cipherText.txt");
        System.out.println("p : "+p);
        System.out.println("u : "+u);
        System.out.println("a : "+a);
        System.out.println("b : "+b);


        System.out.println("Decryption...");
        b = b.multiply( a.modPow( p.subtract( BigInteger.valueOf(1) ).subtract(u) , p) ).mod(p);
        System.out.println("massage : "+b);
        rw.writeBytetoFile(b, "plainText.txt");
        System.out.println("Decryption Complete!!");
    }
    
    //case user input text from command line
    public static void encryptText(String text, Key key) throws IOException {
        BigInteger message = readingMassage(text);
        // encryption process
        System.out.println("Encrypting message: " + text);
        encryptElgamal(message);
        // fileOpr.writeByteToFile(text.getBytes()/* byte array after encrypt */, outputFilePath);
    };

    //case user input text from command line
    public static void decryptText(String text, Key key) throws IOException {
        // decryption process
        System.out.println("Decrypting message...");
        /*
         * DECRYPTION IMLPEMENTATION
         */
        decryptElgamal();
        fileOpr.writeByteToFile(text.getBytes() /* byte array after decrypt */, outputFilePath);
    };

    public static void encryptFile(String filePath, Key key) throws IOException {
        BigInteger message = readingMassage(filePath);
        // encryption process
        System.out.println("Encrypting file: " + filePath);
        encryptElgamal(message);
        // fileOpr.writeByteToFile(fileBytes /* byte array after encrypt */, outputFilePath);
    };

    public static void decryptFile(String filePath, Key key) throws IOException {
        byte[] fileBytes = null;
        try {
            fileBytes = fileOpr.readFileToByte(filePath);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        /*
         * DECRYPTION IMLPEMENTATION
         */
        System.out.println("Decrypting file: " + filePath);
        decryptElgamal();
        // writeTextToFile("file", filePath);
        fileOpr.writeByteToFile(fileBytes /* byte array after decrypt */, outputFilePath);
        // writeTextToFile(key.getKey().toString(), "key");
        // fileOpr.writeByteToFile(key.getKey(), outputFilePath);
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