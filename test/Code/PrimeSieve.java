import java.util.ArrayList;
import java.util.List;

public class PrimeSieve {

    private static final int DESIRED_BYTE_LENGTH = 20; // Desired byte length
    private static final int NUM_THREADS = 16;

    public static void main(String[] args) {
        int maxNumber = (int) Math.pow(2, DESIRED_BYTE_LENGTH * 8) - 1;
        List<Integer> primes = findPrimes(maxNumber);

        System.out.println("Prime Numbers:");
        for (int prime : primes) {
            System.out.println(prime);
        }
    }

    private static List<Integer> findPrimes(int maxNumber) {
        boolean[] sieve = new boolean[maxNumber + 1];
        List<Integer> primes = new ArrayList<>();

        for (int i = 2; i <= maxNumber; i++) {
            sieve[i] = true;
        }

        for (int i = 2; i * i <= maxNumber; i++) {
            if (sieve[i]) {
                for (int j = i * i; j <= maxNumber; j += i) {
                    sieve[j] = false;
                }
            }
        }

        for (int i = 2; i <= maxNumber; i++) {
            if (sieve[i]) {
                primes.add(i);
            }
        }
        return primes;
    }
}
