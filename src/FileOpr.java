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
import org.json.JSONObject;

public class FileOpr {

    public JSONObject readJsonFromFile (String filePath) throws IOException{
        String jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject;
    }

    public void writeJsonToFile(JSONObject jsonObject, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            // Write the JSON string to the file
            fileWriter.write(jsonObject.toString());
            System.out.println();
            System.out.println("Data written to JSON file successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    
    public byte[] readFiletoBigInteger(String filePath) throws IOException
    {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] arr = new byte[(int)file.length()];
        //System.out.println("length byte of file : "+fileName.length());
        //System.out.println("length arr byte of file : "+arr.length);
        fis.read(arr);
        fis.close();
        //BigInteger bigInt = new BigInteger(arr);
        return arr;
    }

    public void writeBytetoFile(BigInteger massage, String filePath) throws IOException
    {
        byte[] arr = massage.toByteArray();
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(arr);
            fos.close();
        }
    }

    public BigInteger readKeytoBigInteger(String filePath) throws IOException
    {
        Path path = Paths.get(filePath);
        BigInteger bigInt = new BigInteger(Files.readString(path));
        return bigInt;
    }

    // write b to file
    public void writeKeytoFile(BigInteger massage, String fileName) throws IOException
    {
        fileName = "./"+fileName;
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(massage.toString().getBytes(Charset.forName("UTF-8")));
            fos.close();
        }
    }
}
