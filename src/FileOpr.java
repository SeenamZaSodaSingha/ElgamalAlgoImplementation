import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class FileOpr {

    public boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public byte[] readFileToByte(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    public void writeEmptyFile(String outputFilePath) throws IOException {
        FileWriter writer = new FileWriter(outputFilePath);
        writer.close();
    }

    //case text mode
    public void writeByteToFile(byte[] data, String outputFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFilePath, true)) {
            fos.write(data);
            System.out.println("Data has been written to the file.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public long getMetaDataLength(String filePath) throws IOException {
        Path file = Paths.get(filePath);
        BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
        return attributes.size();
    } 

    public boolean isFilePath(String input) {
        File file = new File(input);
        return file.exists();
    }
    
    public byte[] readFiletoBigInteger(String fileName) throws IOException
    {
        // fileName = "./"+fileName;
        FileInputStream fis = new FileInputStream(fileName);
        byte[] arr = new byte[(int)fileName.length()];
        //System.out.println("length byte of file : "+fileName.length());
        //System.out.println("length arr byte of file : "+arr.length);
        fis.read(arr);
        fis.close();
        //BigInteger bigInt = new BigInteger(arr);
        return arr;
    }

    public void writeBytetoFile(BigInteger massage, String fileName) throws IOException
    {
        byte[] arr = massage.toByteArray();
        fileName = "./"+fileName;
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(arr);
            fos.close();
        }
    }

    public BigInteger readKeytoBigInteger(String fileName) throws IOException
    {
        fileName = "./"+fileName;
        Path path = Path.of(fileName);
        String str = Files.readString(path);
        BigInteger bigInt = new BigInteger(str);
        return bigInt;
    }

    public void writeKeytoFile(BigInteger massage, String fileName) throws IOException
    {
        fileName = "./"+fileName;
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(massage.toString().getBytes(Charset.forName("UTF-8")));
            fos.close();
        }
    }
}
