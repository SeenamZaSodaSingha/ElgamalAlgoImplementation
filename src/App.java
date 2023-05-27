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
                    System.out.println("File byte size from first read: " + file.length + " bytes");
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
                    System.out.println("File byte size from first read: " + file.length + " bytes");
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

    public static String paddingMsg(String msg, int blockSize){
        String pad = "";
        //for increse block size to same as receiver block size
        blockSize++;
        int lenRemining = (blockSize - msg.length()) % 10;
        pad = ""+lenRemining;
        System.out.println("pad : "+pad);
        while (msg.length() < blockSize) {
            msg = msg + pad;
        }
        return msg;
    }

    public static String unpaddingMsg(String msg, int blockSize){
        int lastNum = Integer.valueOf( msg.substring( (blockSize-1) ) );
        int count = 1;
        //counting same num as last num
        for (int i = blockSize-2; i > 0; i--) {
            if ( Integer.valueOf(msg.substring(i, i+1)) == lastNum ) {
                count++;
            }
            else{
                break;
            }
        }
        if (lastNum==(count % 10)) {
            msg = msg.substring( 0, blockSize - count);
        }
        else if(count > 1) {
            if (count % 10 > lastNum) {
                //like pad 3 3byte but read last number same as 3 is 4 byte
                //(count % 10) - lastNum should be real plaintext not padding
                //(4%10)-1 = first 1 number should be real plaintext
                //another 3 should be padding
                count -= ( (count % 10) - lastNum );
                msg = msg.substring( 0, blockSize - count);
            }
            // STILL BUG-------------------->
            else if(count % 10 < lastNum){
                //like pad 3 23byte but read last number same as 3 is 32 byte
                //(count%10) + (10-lastnum) should be real plaintext not padding
                //(32%10)+(10-3) = first 9 number should be real plaintext
                //another 23 should be padding
                //---- Seenam's Debug
                // System.out.println("before count : "+count);
                // int blc = (count % 10) + (10 - lastNum);
                // System.out.println("BLC = " + blc);
                // count  = Math.abs(count-blc);
                //---- Seenam's Debug
                count -= ((count % 10) - (10 + lastNum));
                System.out.println("(count % 10) = " + (count % 10) );
                System.out.println("(10 - lastNum) = " + (10 - lastNum));
                System.out.println("result inside blancket = " + (count % 10) + (10 - lastNum));
                System.out.println("after count : "+count);
                System.out.println("lastNum : "+lastNum);
            }
            // <---------------------------STILL BUG
            msg = msg.substring( 0, blockSize - count);
        }
        return msg;
    }

    public static String zeroPadding(String msg, int blockSize){
        blockSize--;
        while (msg.length() < blockSize) {
            msg = "0"+msg;
        }
        return msg;
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
        int blockSize = key.getBlockSize();

        System.out.println("Blocksize from ENC_ELGAMOL: " + blockSize);
        // System.out.println("g: "+g.toString() + " p: "+p.toString() + " k:
        // "+k.toString());
        // BigInteger a = g.modPow(k, p);
        BigInteger a = fastExpo.fastExponentiation(g, k, p);
        key.setA(a);
        // rw.writeKeytoFile(a, "./out/key/keyA.txt");
        // System.out.println("a: "+a);

        String strMassage = message.toString();
        System.out.println("strMassage : "+strMassage);

        System.out.println("Encryption to b(cipher text)...");
        String strB = "", strTemp = "";
        BigInteger b;
        //encrypt massage by split massage to block
        while (strMassage != null && strMassage.length() >= blockSize) {
            System.out.println("on operate...");
            System.out.println("msg length remining before : "+strMassage.length());
            System.out.println("msg length remining before : "+strMassage);
            strTemp = strMassage.substring( 0, blockSize );
            System.out.println("msg length remining : "+strTemp);
            strMassage = strMassage.substring(blockSize);
            System.out.println("msg length remining after : "+strMassage.length());
            System.out.println("msg length remining after : "+strMassage);

            //BigInteger b = y.modPow(k, p);
            b = fastExpo.fastExponentiation(y, k, p);
            b = b.multiply( new BigInteger( strTemp ) ).mod(p);
            System.out.println("b after : "+b);
            System.out.println(b.toString().length());
            
            strB = strB + paddingMsg( b.toString(), blockSize );
        }
        //encrypt last block(remining massage on block less than block size)
        if (strMassage != null && !strMassage.equals("")) {
            System.out.println("last operate...");
            strMassage = paddingMsg(strMassage, blockSize-1);
            System.out.println("msg after padding : "+strMassage);

            //BigInteger b = y.modPow(k, p);
            b = fastExpo.fastExponentiation(y, k, p);
            b = b.multiply( new BigInteger( strMassage ) ).mod(p);
            System.out.println("b after : "+b);
            System.out.println(b.toString().length());
            
            strB = strB + paddingMsg( b.toString(), blockSize );
        }
        b = new BigInteger( strB );
        System.out.println("final b length before exchange : "+b.toString().length());
        rw.writeKeytoFile(b, outputFilePath);
        System.out.println("b : "+b);
        System.out.println("Encryption Complete!!");
    }

    public static void decryptElgamal(BigInteger a, BigInteger b, Key key) throws IOException {
        // System.out.println("b from decrypt ELG: " + b.toString());
        // System.out.println("Message Byte length : " + b.toString().length());
        FileOpr rw = new FileOpr();
        FastExponentiation fastExpo = new FastExponentiation();

        BigInteger p = key.getP(), u = key.getU();
        System.out.println("Reading Block Size...");
        int blockSize = key.getBlockSize() + 1;
        System.out.println("blockSize: " + blockSize);
        String strCipher = b.toString();
        System.out.println("strCipher: " + strCipher);
        System.out.println("-------------------------------------------------------");

        System.out.println("Decryption...");
        String massage = "", strTemp = "";
        // decrypt massage by split massage to block
        while (strCipher != null && strCipher.length() > blockSize) {
            System.out.println("Operating...");
            System.out.println("cipher length remining: " + strCipher.length());
            strTemp = strCipher.substring(0, blockSize);
            strCipher = strCipher.substring(blockSize);

            b = new BigInteger(unpaddingMsg(strTemp, blockSize));
            // b = b.multiply( a.modPow( p.subtract( BigInteger.valueOf(1) ).subtract(u) ,
            // p) );
            b = b.multiply(fastExpo.fastExponentiation(a, p.subtract(BigInteger.valueOf(1)).subtract(u), p));
            b = b.mod(p);
            System.out.println("Plaintext length: " + b.toString().length());
            System.out.println("Decipher text: " + b.toString());

            massage = zeroPadding(b.toString(), blockSize) + massage;
            System.out.println("-------------------------------------------------------");
        }
        // decrypt last block(remining massage on block less than block size)
        if (strCipher != null && !strCipher.equals("")) {
            System.out.println("Last operation...");
            System.out.println("cipher length remining: " + strCipher.length());
            strTemp = strCipher.substring(0, blockSize);
            System.out.println("Str Temp: " + strTemp);
            strCipher = strCipher.substring(blockSize);
            System.out.println("Str Cipher: " + strCipher);
            b = new BigInteger(unpaddingMsg(strTemp, blockSize));
            // b = b.multiply( a.modPow( p.subtract( BigInteger.valueOf(1) ).subtract(u) ,
            // p) );
            b = b.multiply(fastExpo.fastExponentiation(a, p.subtract(BigInteger.valueOf(1)).subtract(u), p));
            b = b.mod(p);
            strCipher = zeroPadding(b.toString(), blockSize);
            System.out.println("Str after zero padding: " + strCipher);
            strCipher = unpaddingMsg(strCipher, blockSize - 1);
            System.out.println("Str after unpadding: " + strCipher);
            System.out.println("Str after unpadding length: " + strCipher.length());

            massage = massage + strCipher;
        }

        b = new BigInteger(massage);
        System.out.println("massage : " + b);
        System.out.println("Massage Byte length : " + b.toString().length());
        rw.writeBytetoFile(b, outputFilePath);
        System.out.println("Decryption Complete!!");
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
        /*
         * Generate signature
         * Sign before encrypt
         * Put at the head of file 
         */
        System.out.println("Encrypting message: " + text);
        encryptElgamal(message, key);
        key.writeKeytoFile("./out/key/Encrypt_key.json");
        System.out.println("Task complete in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
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
        /*
         * Read signature
         * Validate signature
         * Decrypt
         */
        System.out.println("Decrypting text: " + text);
        decryptElgamal(a, b, key);
        System.out.println("Task complete in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
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
        /*
         * Generate signature
         * Sign before encrypt
         * Put at the head of file 
         */
        System.out.println("Encrypting file: " + filePath);
        encryptElgamal(message, key);
        key.writeKeytoFile("./out/key/Encrypt_key.json");
        // fileOpr.writeByteToFile(fileBytes /* byte array after encrypt */,
        // outputFilePath);
        System.out.println("Task complete in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
    };

    public static void decryptFile(String filePath, Key key) throws IOException {
        FileOpr rw = new FileOpr();
        // read key from file
        // key.setP(rw.readKeytoBigInteger("./test/key/keyP.txt"));
        // key.setU(rw.readKeytoBigInteger("./test/key/keyU.txt"));
        BigInteger a = key.getA();
        BigInteger b = rw.readKeytoBigInteger(filePath);
        // System.out.println("File length from decrypt file method: " +
        // b.toString().length()); // 46
        // System.out.println("message from decrypt file method: " + b.toString());
        // System.out.println("------------------------");
        /*
         * DECRYPTION IMLPEMENTATION
         */
        /*
         * Read signature
         * Validate signature
         * Decrypt
         */
        System.out.println("Decrypting file: " + filePath);
        decryptElgamal(a, b, key);
        System.out.println("Task complete in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
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