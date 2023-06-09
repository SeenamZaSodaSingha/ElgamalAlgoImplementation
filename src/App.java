import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class App {
    private static String outputFilePath;
    static FileOpr fileOpr = new FileOpr();
    static long startTime = System.currentTimeMillis();
    static private int bitLength = 0;
    static private SignAlgorithm sign;
    static private Verify verify;

    public static void main(String[] args) throws Exception {
        String mode = args[0];
        if (mode.equals("-s")) {
            keyGenerator(Integer.parseInt(args[1]), new Key());
        } else {
            String encryptOrDecrypt = args[1];
            String keymode = args[2];
            /*
             * get key ready for process, if gen mode -> generate new key, if not -> read
             * from path
             */
            Key key;
            if (keymode.equals("-k")) {
                key = new Key(args[3]);
                // bitLength = Integer.parseInt(args[3]);
            } else {
                //check at first run from server
                key = new Key(keymode, "server");
            }
            String textOrFilePath = null;
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
                } else if (encryptOrDecrypt.equals("-d")) {
                    // check for key mode
                    if (keymode.equals("-gen")) {
                        System.out.println("Need a key to decrypt");
                        System.exit(0);
                    }
                    decryptText(textOrFilePath, key);
                } else {
                    System.out.println("Invalid input from text mode");
                }
                // file mode
            } else if (mode.equals("-f")) {
                // read file path by args index
                textOrFilePath = args[4];
                // file encryption
                if (encryptOrDecrypt.equals("-e")) {
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
    }

    // <<----------------- END OF MAIN ----------------->>
    // ----------------------------------------------------------------
    // <<----------------- STRING OPERATION ----------------->>
    public static String readAllString(String keyMode, String[] args) {
        String str = "";
        str += args[4];
        for (int i = 5; i < args.length - 1; i++) {
            str += " " + args[i];
        }
        return str;
    }

    public static String paddingMsg(String msg, int blockSize) {
        String pad = "";
        // for increse block size to same as receiver block size
        blockSize++;
        int lenRemining = (blockSize - msg.length()) % 10;
        pad = "" + lenRemining;
        System.out.println("pad : " + pad);
        while (msg.length() < blockSize) {
            msg = msg + pad;
        }
        return msg;
    }

    public static String unpaddingMsg(String msg, int blockSize) {
        int lastNum = Integer.valueOf(msg.substring((blockSize - 1)));
        int count = 1;
        // counting same num as last num
        for (int i = blockSize - 2; i > 0; i--) {
            if (Integer.valueOf(msg.substring(i, i + 1)) == lastNum) {
                count++;
            } else {
                break;
            }
        }
        if (lastNum == (count % 10)) {
            msg = msg.substring(0, blockSize - count);
        } else if (count > 1 && count > lastNum) {
            if (count % 10 > lastNum) {
                // like pad 3 3byte but read last number same as 3 is 4 byte
                // (count % 10) - lastNum should be real plaintext not padding
                // (4%10)-1 = first 1 number should be real plaintext
                // another 3 should be padding
                count -= ((count % 10) - lastNum);
                msg = msg.substring(0, blockSize - count);
            } else if (count % 10 < lastNum) {
                // like pad 9 9byte but read last number same as 9 is 12 byte
                // (count%10) + (10-lastnum) should be real plaintext not padding
                // (12%10)+(10-9) = first 3 number should be real plaintext
                // another 9 should be padding
                System.out.println("before count : " + count);
                System.out.println((count % 10) + (10 - lastNum));
                count -= ((count % 10) + (10 - lastNum));
                System.out.println("after count : " + count);
                System.out.println("lastNum : " + lastNum);
                msg = msg.substring(0, blockSize - count);
            }
        }
        return msg;
    }

    public static String zeroPadding(String msg, int blockSize) {
        blockSize--;
        while (msg.length() < blockSize) {
            msg = "0" + msg;
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
    // <<----------------- HASH OPERAITON    ----------------->>
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        System.out.println("input from getSha: " + input);
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        byte[] positiveBytes = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, positiveBytes, 1, bytes.length);
        BigInteger positiveBigInt = new BigInteger(positiveBytes);
    
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    // <<----------------- END OF HASH OPERATION ----------------->>
    // ----------------------------------------------------------------
    // <<----------------- ENCRYPT AND DECRYPT ----------------->>
    public static void encryptElgamal(BigInteger message, Key key) throws IOException {
        FileOpr rw = new FileOpr();
        FastExponentiation fastExpo = new FastExponentiation();
        padding pad = new padding();

        System.out.println("Generating a...");
        BigInteger g = key.getG(), p = key.getP(), y = key.getY();
        key.random_K();
        BigInteger k = key.getK();
        int blockSize = key.getBlockSize();

        System.out.println("Blocksize from ENC_ELGAMOL: " + blockSize);
        System.out.println("p using in elgamol: " + p.toString());
        BigInteger a = fastExpo.fastExponentiation(g, k, p);
        key.setA(a);

        String strMassage = message.toString();
        System.out.println("strMassage : " + strMassage);

        System.out.println("Encryption to b(cipher text)...");
        String strB = "", strTemp = "";
        BigInteger b;
        // encrypt massage by split massage to block
        while (strMassage != null && strMassage.length() >= blockSize) {
            strTemp = strMassage.substring(0, blockSize);
            strMassage = strMassage.substring(blockSize);

            b = fastExpo.fastExponentiation(y, k, p);
            b = b.multiply(new BigInteger(strTemp)).mod(p);

            strB = strB + pad.paddingMsg(b.toString(), blockSize);
            strB = strB + pad.isPad();
            pad.resetStatus();
        }
        // encrypt last block(remining massage on block less than block size)
        if (strMassage != null && !strMassage.equals("")) {
            strMassage = pad.paddingMsg(strMassage, blockSize - 1);

            b = fastExpo.fastExponentiation(y, k, p);
            b = b.multiply(new BigInteger(strMassage)).mod(p);

            pad.resetStatus();
            strB = strB + pad.paddingMsg(b.toString(), blockSize);
            strB = strB + pad.isPad();
        }
        b = new BigInteger(strB);
        System.out.println("final b length before exchange: " + b.toString().length());
        rw.writeKeytoFile(b, outputFilePath);
        System.out.println("b: " + b);
        System.out.println("Encryption Complete!");
    }

    public static BigInteger decryptElgamal(BigInteger a, BigInteger b, Key key) throws IOException {
        FileOpr rw = new FileOpr();
        FastExponentiation fastExpo = new FastExponentiation();
        padding pad = new padding();

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
        while (strCipher != null && strCipher.length() > blockSize + 1) {
            strTemp = strCipher.substring(0, blockSize);
            strCipher = strCipher.substring(blockSize);

            if (strCipher.charAt(0) == '0') {
                b = new BigInteger(strTemp);
            } else {
                b = new BigInteger(pad.unpaddingMsg(strTemp, blockSize));
            }
            strCipher = strCipher.substring(1);
            b = b.multiply(fastExpo.fastExponentiation(a, p.subtract(BigInteger.valueOf(1)).subtract(u), p));
            b = b.mod(p);

            massage = massage + pad.zeroPadding(b.toString(), blockSize);
        }
        // decrypt last block(remining massage on block less than block size)
        System.out.println("message length remining: " + strCipher.length());
        if (strCipher != null && !strCipher.equals("")) {

            if (strCipher.charAt(blockSize) == '0') {
                strCipher = strCipher.substring(0, blockSize);
                b = new BigInteger(strCipher);
            } else {
                strCipher = strCipher.substring(0, blockSize);
                b = new BigInteger(pad.unpaddingMsg(strCipher, blockSize));
            }

            b = b.multiply(fastExpo.fastExponentiation(a, p.subtract(BigInteger.valueOf(1)).subtract(u), p));
            b = b.mod(p);

            System.out.println("message decrypt length: " + b.toString().length());
            System.out.println("This is line 389");
            System.out.println("message decrypt: " + b.toString());

            strCipher = pad.zeroPadding(b.toString(), blockSize);
            strCipher = pad.unpaddingMsg(strCipher, blockSize - 1);

            System.out.println("real message decrypt length: " + strCipher.toString().length());
            System.out.println("real message decrypt: " + strCipher.toString());

            massage = massage + strCipher;
        }

        b = new BigInteger(massage);
        System.out.println("message: " + b);
        System.out.println("Massage Byte length: " + b.toString().length());
        
        rw.writeBytetoFile(b, outputFilePath);
        System.out.println("Decryption Complete!");
        return b;
    }

    // case user input text from command line
    public static void encryptText(String text, Key key) throws Exception {
        BigInteger message = null;

        message = readingMassage(text, message);

        key.random_P(bitLength);
        key.random_G();
        key.random_U();
        key.random_K();
        key.generateY();

        System.out.println("Encrypting message: " + text);
        encryptElgamal(message, key);
        key.writeKeytoFile("./out/key/Encrypt_key.json");
        System.out.println("Task complete in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
    };

    // case user input text from command line
    public static void decryptText(String text, Key key) throws IOException {
        // decryption process
        System.out.println("Decrypting message...");
        BigInteger message = null;

        message = readingMassage(text, message);
        FileOpr rw = new FileOpr();
        // read key from json file
        key.readKeyFromFile("./out/key/Encrypt_key.json");

        BigInteger a = key.getA();
        BigInteger b = rw.readKeytoBigInteger(text);

        System.out.println("Decrypting text: " + text);
        decryptElgamal(a, b, key);
        System.out.println("Task complete in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
    };

    public static void keyGenerator(int bitLength, Key key){
        key.random_P(bitLength); // input bit length
        key.random_G();
        key.random_U();
        // key.random_K();
        key.generateY();
        key.writeKeytoFile("./out/key/Encrypt_key.json");
    }

    public static void encryptFile(String filePath, Key key) throws Exception {
        BigInteger message = null;
        padding pad = new padding();

        message = readingMassage(filePath, message); // read as byte

        BigInteger msgHashBigInt = new BigInteger(bytesToHex(getSHA(message.toString())), 16);
        System.out.println("message hash: " + msgHashBigInt);
        System.out.println("message hash length: " + msgHashBigInt.toString().length() + " bytes");

        System.out.println("File length from encrypt file method: " + bitLength); // 46

        // encryption process
        
        System.out.println("Encrypting file: " + filePath);

        encryptElgamal(message, key);
        
        /*
         * Generate signature
         * Sign before encrypt
         * Put at the EOF
        */

        Key signKey = new Key();
        System.out.println("Bit length sent to signkeyP: "+ bitLength);
        signKey.random_P((key.getBlockSize()+1)*4);
        System.out.println("------------------");
        System.out.println("sign key p: " + signKey.getP());
        System.out.println("------------------");
        signKey.random_G();
        signKey.random_K();
        signKey.random_U();
        signKey.generateY();
        signKey.writeKeytoFile("./out/key/sign_key.json");
        sign = new SignAlgorithm(signKey.getP(), signKey.getG(), msgHashBigInt, signKey.getK(), signKey.getU(), signKey.getY()); //regen p g k u

        BigInteger s = sign.createS();
        BigInteger r = sign.createR(signKey.getG(), signKey.getK());
        System.out.println("s before pad: " + s);
        System.out.println("r before pad: " + r);
        String tmp;
        tmp = pad.paddingMsg(s.toString(), key.getBlockSize());
        tmp = tmp + pad.isPad();
        pad.resetStatus();

        s = new BigInteger(tmp);
        System.out.println("s after pad: " + s);

        tmp = pad.paddingMsg(r.toString(), key.getBlockSize());
        tmp = tmp + pad.isPad();
        pad.resetStatus();
        r = new BigInteger(tmp);

        System.out.println("s length: " + s.toString().length());
        System.out.println("r length: " + r.toString().length());

        fileOpr.writeSignatureToFile(r, outputFilePath);
        fileOpr.writeSignatureToFile(s, outputFilePath);
        key.writeKeytoFile("./out/key/Encrypt_key.json");
        

        System.out.println("Task complete in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
    };

    public static void decryptFile(String filePath, Key key) throws IOException, NoSuchAlgorithmException {
        FileOpr rw = new FileOpr();
        padding pad = new padding();
        BigInteger s = BigInteger.ZERO, r = BigInteger.ZERO;

        BigInteger a = key.getA();

        BigInteger b = rw.readKeytoBigInteger(filePath);
        System.out.println("Blocksize: " + key.getBlockSize());
        System.out.println("b: " + b);
        int messageLength = b.toString().length();
        String signatureR = b.toString().substring(messageLength-((key.getBlockSize()+2)*2), messageLength-((key.getBlockSize()+2)));
        String signatureS = b.toString().substring(messageLength-(key.getBlockSize()+2), messageLength);
        BigInteger message = new BigInteger(b.toString().substring(0, messageLength-((key.getBlockSize()+2)*2)));
        System.out.println("message aft sub signature: " + message);
        /*
         * Read signature
         * Validate signature
         * Decrypt
         */
        if (signatureR.charAt(key.getBlockSize()+1) == '0') {
            System.out.println("non-unpadding");
            signatureR = signatureR.substring(0, key.getBlockSize()+1);
            r = new BigInteger(signatureR);
        }
        else {
            signatureR = signatureR.substring(0, key.getBlockSize()+1);
            r = new BigInteger(pad.unpaddingMsg(signatureR, key.getBlockSize()+1 ));
        }

        if (signatureS.charAt(key.getBlockSize()+1) == '0') {
            System.out.println("non-unpadding");
            signatureS = signatureS.substring(0, key.getBlockSize()+1);
            s = new BigInteger(signatureS);
        }
        else {
            signatureS = signatureS.substring(0, key.getBlockSize()+1);
            s = new BigInteger(pad.unpaddingMsg(signatureS, key.getBlockSize()+1 ));
        }

        System.out.println("s: " + s);
        System.out.println("s length after unp: " + s.toString().length());
        System.out.println("r: " + r);
        System.out.println("r length after uno: " + r.toString().length());

        System.out.println("Message: "  + message.toString().length());

        System.out.println("Decrypting file: " + filePath);

        System.out.println("b: " + b);
        BigInteger deCipherText = decryptElgamal(a, message, key);
        System.out.println("B after decrypt: " + deCipherText.toString());

        Key signKey = new Key();
        signKey.readSignatureKeyFromFile("./out/key/sign_key.json");
        System.out.println("===\nsign p decrypt: " + signKey.getP());

        BigInteger msgHashBigInt = new BigInteger(bytesToHex(getSHA(deCipherText.toString())), 16);
        System.out.println("msgHashBigInt: " + msgHashBigInt);
        verify = new Verify(signKey.getP(), signKey.getG(), signKey.getY(), msgHashBigInt, r, s);
        System.out.println("Verify: " + verify.verifySignature());

        System.out.println("Task complete in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
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