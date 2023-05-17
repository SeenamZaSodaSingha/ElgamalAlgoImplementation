import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Key {
    private byte[] key = null;
    private int gcd;

    public Key() {
        generateKey();
    }

    public Key(String key) {
        this.key = isFilePath(key) ? readKeyFromFile(key) : key.getBytes(StandardCharsets.UTF_8);;
    }

    public boolean isFilePath(String input) {
        File file = new File(input);
        return file.exists();
    }

    private byte[] readKeyFromFile(String filePath) {
        try{
            Path path = Paths.get(filePath);
            byte[] fileBytes = Files.readAllBytes(path);
            return fileBytes;
        } catch (Exception e) {
            System.out.println("Error reading key from file");
            return null;
        }
    }

    public void setKey(String key) {
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }
    
    public byte[] getKey() {
        return key;
    }

    public void generateKey() {
        System.out.println("Generating key...");
        //implement key generation algorithm
        this.key = "GENERATED KEY".getBytes(StandardCharsets.UTF_8);;
    }

    public void gcd(int a, int b) {
        if (b == 0) {
            this.gcd = a;
        } else {
            gcd(b, a % b);
        }
        System.out.println("GCD: " + this.gcd);
    }
}
