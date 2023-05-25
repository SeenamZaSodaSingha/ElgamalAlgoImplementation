import java.math.BigInteger;
import java.util.Random;

public class SafePrimeFinder {

    private static final int DESIRED_BYTE_LENGTH = 32; // Desired byte length of the safe prime
    private static final int NUM_THREADS = 16;
    private static volatile BigInteger safePrime;

    public static void main(String[] args) {
        Random random = new Random();
        
        while (safePrime == null) {
            Thread[] threads = new Thread[NUM_THREADS];
            
            for (int i = 0; i < NUM_THREADS; i++) {
                int threadIndex = i;
                threads[i] = new SafePrimeThread(random, threadIndex);
                threads[i].start();
            }
            
            try {
                for (int i = 0; i < NUM_THREADS; i++) {
                    threads[i].join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("Safe Prime: " + safePrime);
    }

    private static boolean isProbablePrime(BigInteger number) {
        return number.isProbablePrime(50);
    }

    private static boolean isSafePrime(BigInteger number) {
        BigInteger safePrimeCandidate = number.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2));
        return safePrimeCandidate.isProbablePrime(50);
    }

    private static class SafePrimeThread extends Thread {
        private final Random random;
        private final int threadIndex;

        public SafePrimeThread(Random random, int threadIndex) {
            this.random = random;
            this.threadIndex = threadIndex;
        }

        @Override
        public void run() {
            int numBytes = DESIRED_BYTE_LENGTH / NUM_THREADS;
            int remainingBytes = DESIRED_BYTE_LENGTH % NUM_THREADS;

            int startByte = threadIndex * numBytes;
            int endByte = startByte + numBytes;

            if (threadIndex == NUM_THREADS - 1) {
                endByte += remainingBytes;
            }
            
            while (safePrime == null) {
                byte[] randomBytes = new byte[endByte - startByte];
                random.nextBytes(randomBytes);
                BigInteger number = new BigInteger(1, randomBytes);
                
                number = number.setBit(DESIRED_BYTE_LENGTH * 8 - 1).add(BigInteger.ONE); // Ensure the number is of the desired byte length
                
                if (number.bitLength() == DESIRED_BYTE_LENGTH * 8 && isProbablePrime(number) && isSafePrime(number)) {
                    safePrime = number;
                    break;
                }
            }
        }
    }
}