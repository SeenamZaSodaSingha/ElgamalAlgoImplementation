import java.io.IOException;
import java.math.BigInteger;

public class App {
    private static String outputFilePath;
    static FileOpr fileOpr = new FileOpr();
    static long startTime = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        
        String mode = args[0];
        String encryptOrDecrypt = args[1];
        String keymode = args[2];
        /*
         * get key ready for process, if gen mode -> generate new key, if not -> read
         * from path
         */
        Key key = keymode.equals("-gen") ? new Key() : new Key(args[3]);
        String textOrFilePath = null;
        /*
         * Out path will be cipher text, the public key will out in .key file
         */
        outputFilePath = args[args.length - 1];

        // check for output file, if non exist -> create new empty file
        if (fileOpr.fileExists(outputFilePath)) {
            fileOpr.writeEmptyFile(outputFilePath);
        }
        // after this line, key is ready for process, gen nor read
        // text mode
        if (mode.equals("-t")) {
            // read rest of string input by args index
            textOrFilePath = readAllString(keymode, args);
            System.out.println("Working on: " + textOrFilePath);
            // string encryption
            if (encryptOrDecrypt.equals("-e")) {
                // encryption need to generate key, or no key had prepare for encryption
                encryptText(textOrFilePath, key);
                // string decryption
            } else if (encryptOrDecrypt.equals("-d")) {
                // check for key mode
                if (keymode.equals("-gen")) {
                    System.out.println("Need a key to decrypt");
                    System.exit(0);
                }

                // decrypth text
                // TODO: read key from file
                // read key
                // read a, b

                // in decrypting mode, we will send b to decrypt, a will in key

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
                // System.out.println("reading path: " + textOrFilePath);
                if (fileOpr.fileExists(textOrFilePath)) {
                    byte[] file = fileOpr.readFiletoBigInteger(textOrFilePath);
                    System.out.println("File byte size from first read: " + file.length+ " bytes");
                    System.out.println("------------------------");
                    encryptFile(textOrFilePath, key);
                } else {
                    System.out.println("File byte size: " + fileOpr.getMetaDataLength(textOrFilePath) + " bytes");
                    System.out.println("Invalid file path from file encryption");
                }
                // file decryption
            } else if (encryptOrDecrypt.equals("-d")) {
                System.out.println("reading path: " + textOrFilePath);
                if (fileOpr.fileExists(textOrFilePath)) {
                    byte[] file = fileOpr.readFiletoBigInteger(textOrFilePath);
                    System.out.println("File byte size from first read: " + file.length+ " bytes");
                    System.out.println("------------------------");
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

    public static BigInteger readingMassage(String item, BigInteger message) throws IOException {
        FileOpr fileOpr = new FileOpr();
        message = fileOpr.fileExists(item) ? new BigInteger(fileOpr.readFiletoBigInteger(item))
                : new BigInteger(item.getBytes());
        return message;
    }

    // <<----------------- END OF STRING OPERATION ----------------->>
    // ----------------------------------------------------------------
    // <<----------------- ENCRYPT AND DECRYPT ----------------->>
    public static void encryptElgamal(BigInteger message, Key key) throws IOException {
        // System.out.println("Massage Byte length : " + message.toString().length());
        // System.out.println("Massage from ecnrypt elgamal : " + message.toString());
        // System.out.println("------------------------");
        FileOpr rw = new FileOpr();
        FastExponentiation fastExpo = new FastExponentiation();

        System.out.println("Generating a...");
        BigInteger g = key.getG(), p = key.getP(), k = key.getK(), y = key.getY();
        // System.out.println("g: "+g.toString() + " p: "+p.toString() + " k:
        // "+k.toString());
        // BigInteger a = g.modPow(k, p);
        BigInteger a = fastExpo.fastExponentiation(g, k, p);
        key.setA(a);
        // rw.writeKeytoFile(a, "./out/key/keyA.txt");
        // System.out.println("a: "+a);

        System.out.println("Encryption to b (cipher text)...");
        // BigInteger b = y.modPow(k, p);
        BigInteger b = fastExpo.fastExponentiation(y, k, p);
        // System.out.println("b: "+b);
        // System.out.println("message : "+message.toString());
        b = b.multiply(message).mod(p);
        rw.writeKeytoFile(b, outputFilePath);
        // System.out.println("b from encryption ELG: "+b.toString());
        System.out.println("Encryption Complete!");
    }

    public static void decryptElgamal(BigInteger a, BigInteger b, Key key) throws IOException {
        // System.out.println("b from decrypt ELG: " + b.toString());
        // System.out.println("Message Byte length : " + b.toString().length());
        FileOpr rw = new FileOpr();
        FastExponentiation fastExpo = new FastExponentiation();
        
        BigInteger p = key.getP(), u = key.getU();
        // System.out.println("p: " + p.toString());
        // System.out.println("a: " + a.toString());
        // System.out.println("u: " + u.toString());
        System.out.println("Decryption...");
        // System.out.println("message b before decrypt: " + b);
        // b = b.multiply(a.modPow(p.subtract(BigInteger.valueOf(1)).subtract(u), p));
        b = b.multiply(fastExpo.fastExponentiation(a, p.subtract(BigInteger.valueOf(1)).subtract(u), p));
        // System.out.println("message b before mod: " + b);
        b = b.mod(p);
        // System.out.println("message b after mod: " + b);
        // System.out.println("message b after decrypt: " + b);
        rw.writeBytetoFile(b, outputFilePath);
        System.out.println("Decryption Complete!");
    }

    // case user input text from command line
    public static void encryptText(String text, Key key) throws Exception {
        BigInteger message = null;
        // System.out.println("message addr: "+message);
        message = readingMassage(text, message);
        int fileLength = message.toString().length();
        // System.out.println("message addr after call: "+message);
        System.out.println("Generating p...");
        key.random_P(fileLength);
        System.out.println("Generating g...");
        key.random_G();
        System.out.println("Generating u...");
        key.random_U();
        System.out.println("Generating k...");
        key.random_K();
        System.out.println("Generating y...");
        key.generateY();
        // encryption process
        System.out.println("Encrypting message: " + text);
        encryptElgamal(message, key);
        key.writeKeytoFile("./out/key/Encrypt_key.json");
        System.out.println("Task complete in "+(System.currentTimeMillis() - startTime)/1000 + " seconds");
        // fileOpr.writeByteToFile(text.getBytes()/* byte array after encrypt */,
        // outputFilePath);
    };

    // case user input text from command line
    public static void decryptText(String text, Key key) throws IOException {
        // decryption process
        System.out.println("Decrypting message...");
        BigInteger message = null;
        // System.out.println("message addr: "+message);
        message = readingMassage(text, message);
        FileOpr rw = new FileOpr();
        // read key from json file
        key.readKeyFromFile("./out/key/Encrypt_key.json");
        // key.setP(rw.readKeytoBigInteger("./test/key/keyP.txt"));
        // key.setU(rw.readKeytoBigInteger("./test/key/keyU.txt"));
        // BigInteger a = rw.readKeytoBigInteger("./out/key/keyA.txt");
        BigInteger a = key.getA();
        BigInteger b = rw.readKeytoBigInteger(text);
        System.out.println("Decrypting text: " + text);
        decryptElgamal(a, b, key);
        System.out.println("Task complete in "+(System.currentTimeMillis() - startTime)/1000 + " seconds");
    };

    public static void encryptFile(String filePath, Key key) throws Exception {
        BigInteger message = null;
        // System.out.println("message addr: "+message);
        message = readingMassage(filePath, message);
        int fileLength = message.toString().length();
        // System.out.println("File length from encrypt file method: "+fileLength); //46
        // System.out.println("message from encrypt file method: "+message.toString());
        // System.out.println("------------------------");
        key.random_P(fileLength);
        key.random_G();
        key.random_U();
        key.random_K();
        key.generateY();
        // encryption process
        System.out.println("Encrypting file: " + filePath);
        encryptElgamal(message, key);
        key.writeKeytoFile("./out/key/Encrypt_key.json");
        // fileOpr.writeByteToFile(fileBytes /* byte array after encrypt */,
        // outputFilePath);
        System.out.println("Task complete in "+(System.currentTimeMillis() - startTime)/1000 + " seconds");
    };

    public static void decryptFile(String filePath, Key key) throws IOException {
        FileOpr rw = new FileOpr();
        // read key from file
        // key.setP(rw.readKeytoBigInteger("./test/key/keyP.txt"));
        // key.setU(rw.readKeytoBigInteger("./test/key/keyU.txt"));
        BigInteger a = key.getA();
        BigInteger b = rw.readKeytoBigInteger(filePath);
        // System.out.println("File length from decrypt file method: " + b.toString().length()); // 46
        // System.out.println("message from decrypt file method: " + b.toString());
        // System.out.println("------------------------");
        /*
         * DECRYPTION IMLPEMENTATION
         */
        System.out.println("Decrypting file: " + filePath);
        decryptElgamal(a, b, key);
        System.out.println("Task complete in "+(System.currentTimeMillis() - startTime)/1000 + " seconds");
        // writeTextToFile("file", filePath);
        // fileOpr.writeByteToFile(fileBytes /* byte array after decrypt */,
        // outputFilePath);
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