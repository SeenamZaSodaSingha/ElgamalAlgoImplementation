import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.json.JSONObject;

public class Key {
    private final int NUM_THREADS = 9;
    private volatile BigInteger safePrime;

    private JSONObject jsonObject = new JSONObject();
    private FileOpr fileOpr = new FileOpr();
    private lehmenn_test lehm;
    private gcdExtended gcdE;
    private FastExponentiation fastExpo;
    private BigInteger _zero, _one, _two;
    private BigInteger p = new BigInteger("10"), g, u, y, k, a;

    public Key() {
        lehm = new lehmenn_test();
        gcdE = new gcdExtended();
        fastExpo = new FastExponentiation();
        _zero = BigInteger.valueOf(0);
        _one = BigInteger.valueOf(1);
        _two = BigInteger.valueOf(2);
    }

    public Key(String path) throws IOException {
        // will fix to read accept key from file
        lehm = new lehmenn_test();
        gcdE = new gcdExtended();
        _zero = BigInteger.valueOf(0);
        _one = BigInteger.valueOf(1);
        _two = BigInteger.valueOf(2);
        readKeyFromFile(path);
    }

    public void setA(BigInteger a) {
        this.a = a;
        jsonObject.put("a", a);
    }

    public void setP(BigInteger p) {
        this.p = p;
        jsonObject.put("p", p);
    }

    public void setG(BigInteger g) {
        this.g = g;
        jsonObject.put("g", g);
    }

    public void setU(BigInteger u) {
        this.u = u;
        jsonObject.put("u", u);
    }

    public void setY(BigInteger y) {
        this.y = y;
        jsonObject.put("y", y);
    }

    public void setK(BigInteger k) {
        this.k = k;
        jsonObject.put("k", k);

    }

    public BigInteger getP() {
        return p;
    }

    public BigInteger getG() {
        return g;
    }

    public BigInteger getU() {
        return u;
    }

    public BigInteger getY() {
        return y;
    }

    public BigInteger getK() {
        return k;
    }

    public BigInteger getA() {
        return a;
    }

    public void writeKeytoFile(String filePath) {
        // System.out.println("FROM WRITE KEY TO FILE");
        // System.out.println("p: " + jsonObject.getBigInteger("p"));
        // System.out.println("g: " + jsonObject.getBigInteger("g"));
        // System.out.println("a: " + jsonObject.getBigInteger("a"));
        // System.out.println("u: " + jsonObject.getBigInteger("u"));
        fileOpr.writeJsonToFile(jsonObject, filePath);
    }

    public void readKeyFromFile(String filePath) throws IOException {
        // read in json form
        jsonObject = fileOpr.readJsonFromFile(filePath);
        this.p = jsonObject.getBigInteger("p");
        this.g = jsonObject.getBigInteger("g");
        this.a = jsonObject.getBigInteger("a");
        this.u = jsonObject.getBigInteger("u");
    }

    // Beat's code
    // public void random_P(int n) {
    //     int roundTest = 100;
    //     Random rand = new Random();
    //     boolean loopI = true, loopJ = true;

    //     String upperboundString = "9";
    //     String lowerboundString = "1";
    //     for (int i = 0; i < n; i++) {
    //         upperboundString = upperboundString + "9";
    //         lowerboundString = lowerboundString + "0";
    //     }
    //     BigInteger minLimit = new BigInteger(lowerboundString);
    //     BigInteger maxLimit = new BigInteger(upperboundString);
    //     maxLimit = maxLimit.subtract(minLimit);
    //     int len = maxLimit.bitLength();
    //     BigInteger p = _zero;

    //     for (int i = 0; i < 300 && loopI; i++) {
    //         // System.out.println("i = "+i);
    //         p = new BigInteger(len, rand);
    //         if (p.compareTo(minLimit) < 0)
    //             p = p.add(minLimit);
    //         if (p.compareTo(maxLimit) >= 0)
    //             p = p.mod(maxLimit).add(minLimit);
    //         // System.out.println("p = "+p);

    //         //for cut down even number
    //         if (p.mod(_two).equals(_zero))
    //             p = p.add(_one);

    //         //check prime
    //         if(!lehm.testPrime(p, roundTest)){
    //             continue;
    //         }

    //         // check safe prime
    //         if (!lehm.testPrime(p.subtract(_one).divide(_two), roundTest)) {
    //             // System.out.println(p+" is not safe prime");
    //             p = p.multiply(_two).add(_one);

    //             //for break loopI
    //             loopI = !lehm.testPrime(p, roundTest);
    //             // System.out.println("is new p is prime? : "+(!loopI));
    //         } else{
    //             //break in safe prime case
    //             loopI = false;
    //             break;
    //         }
    //     }
    //     if (loopI)
    //         System.out.println("Loop I is: " + loopI);
    //     this.p = p;
    //     jsonObject.put("p", p);
    // }

    // Seenam's code
    public void random_P(int n) throws Exception {
        // CountDownLatch latch = new CountDownLatch(NUM_THREADS - 1);
        System.out.println("Generating safe prime...");
        while (safePrime == null) {
            Thread[] threads = new Thread[NUM_THREADS];

            for (int i = 0; i < NUM_THREADS; i++) {
                threads[i] = new SafePrimeThread(i, n);
                // SafePrimeThread.setLatch(latch);
                threads[i].start();
                // System.out.println("Thread " + i + " create complete");
            }
            System.out.println(NUM_THREADS+" threads created.");
            System.out.println("Waiting for threads to complete...");
            // latch.await();

            try {
                for (int i = 1; i <= NUM_THREADS; i++) {
                    threads[i].join();
                    // System.out.println("Thread " + i + " join");
                }
                System.out.println("All threads shutdown.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Safe Prime: " + safePrime);

        // this.p = safePrime;
        this.p = safePrime;
        jsonObject.put("p", p);
    }

    public void random_G() {
        // g is root of p
        Random rand = new Random();
        // g = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger g = new BigInteger(len, rand);
        if (g.compareTo(minLimit) < 0)
            g = g.add(minLimit);
        if (g.compareTo(maxLimit) >= 0)
            g = g.mod(maxLimit).add(minLimit);
        // System.out.println("g = "+g);

        // g^(p-1)/2 % p must != 1
        // if g^(p-1)/2 % p = 1 then g = p-g
        // if( g.modPow(p.subtract( _one ).divide( _two ), p).equals( _one ) )
        g = fastExpo.fastExponentiation(g, p.subtract(_one).divide(_two), p);
        if (g.equals(_one))
            g = p.subtract(g);
        this.g = g;
        jsonObject.put("g", g);
    }

    public void random_U() {
        Random rand = new Random();
        // u = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger u = new BigInteger(len, rand);
        if (u.compareTo(minLimit) < 0)
            u = u.add(minLimit);
        if (u.compareTo(maxLimit) >= 0)
            u = u.mod(maxLimit).add(minLimit);
        // System.out.println("u = "+u);

        this.u = u;
        jsonObject.put("u", u);
    }

    public void random_K() {
        Random rand = new Random();
        boolean loopI = true;
        // k = random from 2 to p-1
        BigInteger minLimit = _two;
        BigInteger maxLimit = p.subtract(minLimit);
        int len = p.bitLength();
        BigInteger k = _zero;

        for (int i = 0; i < 100 && loopI; i++) {
            k = new BigInteger(len, rand);
            if (k.compareTo(minLimit) < 0)
                k = k.add(minLimit);
            if (k.compareTo(maxLimit) >= 0)
                k = k.mod(maxLimit).add(minLimit);
            // System.out.println("k = "+k);

            loopI = !gcdE.testGCD(p.subtract(_one), k);
        }
        this.k = k;
        jsonObject.put("k", k);
    }

    public void generateY() {
        // this.y = g.modPow(u, p);
        this.y = fastExpo.fastExponentiation(g, u, p);
    }

    class SafePrimeThread extends Thread {
        private final int threadIndex;
        private int DESIRED_BYTE_LENGTH;
        // private static CountDownLatch latch;
        private String upperboundString = "9";
        private String lowerboundString = "1";

        public SafePrimeThread(int threadIndex, int n) {
            this.threadIndex = threadIndex;
            this.DESIRED_BYTE_LENGTH = n;
            upperboundString = ""+threadIndex;
            lowerboundString = ""+threadIndex;
            for (int i = 0; i < DESIRED_BYTE_LENGTH; i++) {
                upperboundString = upperboundString + "9"; //19999...n
                lowerboundString = lowerboundString + "0"; //10000...n
            }
        }

        // public static void setLatch(CountDownLatch latch) {
        //     SafePrimeThread.latch = latch;
        // }

        @Override
        public void run() {
            int roundTest = 100;
            Random rand = new Random();
            boolean loopI = true;
            
            BigInteger minLimit = new BigInteger(lowerboundString);
            BigInteger maxLimit = new BigInteger(upperboundString);
            maxLimit = maxLimit.subtract(minLimit);
            int len = maxLimit.bitLength();
            BigInteger p = _zero;

            for (int i = 0; i < 300 && loopI; i++) {
                if (safePrime != null) {
                    // latch.countDown(); 
                    break;  
                }
                
                // System.out.println("i = "+i);
                p = new BigInteger(len, rand);
                if (p.compareTo(minLimit) < 0)
                    p = p.add(minLimit);
                if (p.compareTo(maxLimit) >= 0)
                    p = p.mod(maxLimit).add(minLimit);
                // System.out.println("p = "+p);
    
                //for cut down even number
                if (p.mod(_two).equals(_zero))
                    p = p.add(_one);
    
                //check prime
                if(!lehm.testPrime(p, roundTest)){
                    continue;
                }
    
                // check safe prime
                if (!lehm.testPrime(p.subtract(_one).divide(_two), roundTest)) {
                    // System.out.println(p+" is not safe prime");
                    p = p.multiply(_two).add(_one);
    
                    //for break loopI
                    loopI = !lehm.testPrime(p, roundTest);
                    // System.out.println("is new p is prime? : "+(!loopI));
                } else{
                    //break in safe prime case
                    loopI = false;
                    break;
                }
            }
            // if(loopI) System.out.println("Loop I is: " + loopI);
            if (!loopI) {
                safePrime = p;
                System.out.println("Safe prime found in thread: " + threadIndex);
                System.out.println("Shutting down other threads...");
            } else {
                System.out.println("Safe prime not found in thread: " + threadIndex);
            }

        }
    }
}