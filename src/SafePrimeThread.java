import java.math.BigInteger;
import java.util.Random;

class SafePrimeThread extends Thread {
    private final Random random;
    private final int threadIndex;
    private int DESIRED_BYTE_LENGTH;
    private int NUM_THREADS = 1;
    private volatile BigInteger safePrime;

    public SafePrimeThread(Random random, int threadIndex, int n, int thread) {
        this.random = random;
        this.threadIndex = threadIndex;
        this.DESIRED_BYTE_LENGTH = n;
        this.NUM_THREADS = thread;
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

    private boolean isProbablePrime(BigInteger number) {
        return number.isProbablePrime(50);
    }

    private boolean isSafePrime(BigInteger number) {
        BigInteger safePrimeCandidate = number.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2));
        return safePrimeCandidate.isProbablePrime(50);
    }
}