// import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// import java.util.Arrays;

public class Key {
    private FileOpr fileOpr = new FileOpr();

    public Key() {
    }

    public Key(String path){
        readKeyFromFile(path);
    }

    public void setKey(String key) {
        // this.key = key.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] readKeyFromFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            byte[] fileBytes = Files.readAllBytes(path);
            return fileBytes;
        } catch (Exception e) {
            System.out.println("Error reading key from file");
            return null;
        }
    }

    public void generateKey(String item) throws IOException {
        System.out.println("Generating key...");
        byte[] array = fileOpr.isFilePath(item) ? fileOpr.readFileToByte(item) : item.getBytes();
        BigInteger bigInt = new BigInteger(array);

        generateKey genKey = new generateKey();
        BigInteger p = genKey.random_P(bigInt.toString().length());
        BigInteger g = genKey.random_G(p);
        BigInteger u = genKey.random_U(p);
        BigInteger y = g.modPow(u, p);
        BigInteger k = genKey.random_K(p);
        BigInteger a = g.modPow(k, p);
        BigInteger b = y.modPow(k, p);
        b = b.multiply(bigInt).mod(p);
        /*
         * not sure what need to print to file
         * after key geneeration key must save to file but idk which to write
         */
        byte[] arr = b.toByteArray();
        fileOpr.writeByteToFile(arr, "./out/key/key.txt");
    }
}
