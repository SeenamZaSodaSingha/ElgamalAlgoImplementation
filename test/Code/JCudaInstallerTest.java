import jcuda.*;

public class JCudaInstallerTest {
    public static void main(String[] args) {
        JCuda.cudaInit();
        System.out.println("JCuda installation is successful.");
    }
}
