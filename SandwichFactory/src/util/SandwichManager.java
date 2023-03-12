package util;

import java.util.concurrent.locks.*;
import model.food.*;
import model.machines.*;
import model.pool.*;

public class SandwichManager {
    static volatile BreadProducer[] BreadArr;
    static volatile EggProducer[] EggArr;
    static volatile SandwichConsumer[] SandwichArr;
    static volatile int breadsCount = 0;
    static volatile int eggsCount = 0;
    static volatile int sandwichesCount = 0;
    static volatile BreadPool breadPool;
    static volatile EggPool eggPool;
    static Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        // Assign input arguments
        int n_sandwiches = Integer.parseInt(args[0]);
        int bread_capacity = Integer.parseInt(args[1]);
        int egg_capacity = Integer.parseInt(args[2]);
        int n_bread_makers = Integer.parseInt(args[3]);
        int n_egg_makers = Integer.parseInt(args[4]);
        int n_sandwich_packers = Integer.parseInt(args[5]);
        int bread_rate = Integer.parseInt(args[6]);
        int egg_rate = Integer.parseInt(args[7]);
        int packing_rate = Integer.parseInt(args[8]);

        // Initialise bread and egg pools
        breadPool = new BreadPool(bread_capacity);
        eggPool = new EggPool(egg_capacity);

        // Initialise Producers and Consumers
        BreadArr = new BreadProducer[n_bread_makers];
        EggArr = new EggProducer[n_egg_makers];
        SandwichArr = new SandwichConsumer[n_sandwich_packers];

        // Log input arguments
        Logger.write("sandwiches:" + n_sandwiches);
        Logger.write("bread capacity:" + bread_capacity);
        Logger.write("egg capacity:" + egg_capacity);
        Logger.write("bread makers:" + n_bread_makers);
        Logger.write("egg makers:" + n_egg_makers);
        Logger.write("sandwich packers:" + n_sandwich_packers);
        Logger.write("bread rate:" + bread_rate);
        Logger.write("egg rate:" + egg_rate);
        Logger.write("packing rate:" + packing_rate);
        Logger.write("");

        for (int i = 0; i < n_bread_makers; i++) {
            BreadArr[i] = new BreadProducer(bread_rate, "B" + i);
        }
        for (int i = 0; i < n_egg_makers; i++) {
            EggArr[i] = new EggProducer(egg_rate, "E" + i);
        }
        for (int i = 0; i < n_sandwich_packers; i++) {
            SandwichArr[i] = new SandwichConsumer(packing_rate, "S" + i);
        }

        // Make sandwiches based on input arguments
        makeSandwiches(n_sandwiches, n_bread_makers, n_egg_makers, n_sandwich_packers);

        // Summarise after making sandwiches
        summarise(n_bread_makers, n_egg_makers, n_sandwich_packers);
    }

    public static void makeSandwiches(int n_sandwiches, int n_bread_makers, int n_egg_makers, int n_sandwich_packers) {
        // Make runBread threads
        Runnable runBread = () -> {
            while (breadsCount < 2 * n_sandwiches) {
                // Get the next available bread producer
                BreadProducer breadProducer = getBreadProducer(n_bread_makers);
                Bread bread = new Bread(breadProducer.getBreadCount(), breadProducer.getBreadId());
                breadsCount += 1;
                breadProducer.createBread(bread);
                // critical section: enqueue a new bread into breadPool
                if (bread.isBreadMade()) {
                    try {
                        lock.lock();
                        breadPool.enqueue(bread);
                        // Log produced bread
                        Logger.write(breadProducer.getBreadId() + " puts bread " +
                                (breadProducer.getBreadCount() -1));
                    } finally {
                        lock.unlock();
                    }
                }
            }
        };

        // Make runEgg threads
        Runnable runEgg = () -> {
            while (eggsCount < n_sandwiches) {
                // Get the next available egg producer
                EggProducer eggProducer = getEggProducer(n_egg_makers);
                Egg egg = new Egg(eggProducer.getEggCount(), eggProducer.getEggId());
                eggsCount += 1;
                eggProducer.createEgg(egg);
                // critical section: enqueue a new egg into eggPool
                if (egg.isEggMade()) {
                    try {
                        lock.lock();
                        eggPool.enqueue(egg);
                        // Log produced egg
                        Logger.write(eggProducer.getEggId() + " puts egg "
                                + (eggProducer.getEggCount() - 1));
                    } finally {
                        lock.unlock();
                    }
                }
            }
        };

        // Make runPacker threads
        Runnable runPacker = () -> {
            // Make sandwiches
            while (sandwichesCount < n_sandwiches) {
                // Get the next sandwich consumer
                SandwichConsumer sandwichConsumer = getPacker(n_sandwich_packers);
                Bread topBread = (Bread) breadPool.dequeue();
                Bread bottomBread = (Bread) breadPool.dequeue();
                Egg egg = (Egg) eggPool.dequeue();

                // Pack the sandwich
                Sandwich sandwich = new Sandwich(sandwichesCount++);
                sandwichConsumer.consumeSandwich(sandwich);
                // Log consumed sandwich
                if (sandwich.isSandwichConsumed()) {
                    Logger.write(sandwichConsumer.getSandwichId() + " packs sandwich "
                            + (sandwichConsumer.getSandwichCount() - 1) + " with bread " + topBread.getBreadId()
                            + " from " + topBread.getToaster() + " and egg " + egg.getEggId() + " from "
                            + egg.getScrambler() + " and bread " + bottomBread.getBreadId() + " from "
                            + bottomBread.getToaster());
                }
            }
        };

        // Create and start bread threads
        Thread[] bread_threads = new Thread[n_bread_makers];
        for (int i = 0; i < n_bread_makers; i++) {
            Thread bread = new Thread(runBread);
            bread.start();
            bread_threads[i] = bread;
        }

        // Create and start egg threads
        Thread[] eggs_threads = new Thread[n_egg_makers];
        for (int i = 0; i < n_egg_makers; i++) {
            Thread egg = new Thread(runEgg);
            egg.start();
            eggs_threads[i] = egg;
        }

        // Create and start sandwiches threads
        Thread[] sandwiches_threads = new Thread[n_sandwich_packers];
        for (int i = 0; i < n_sandwich_packers; i++) {
            Thread sandwich = new Thread(runPacker);
            sandwich.start();
            sandwiches_threads[i] = sandwich;
        }

        // Join threads
        for (int i = 0; i < n_bread_makers; i++)
            try {
                bread_threads[i].join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        for (int i = 0; i < n_egg_makers; i++)
            try {
                eggs_threads[i].join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        for (int i = 0; i < n_sandwich_packers; i++)
            try {
                sandwiches_threads[i].join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
    }

    // Retrieving next available BreadProducer
    private static synchronized BreadProducer getBreadProducer(int n_bread_makers) {
        for (int i = 0; i < n_bread_makers; i++) {
            if (!BreadArr[i].getIsBreadMaking()) {
                // Indicate that BreadProducer is being used
                BreadArr[i].setIsBreadMaking();
                return BreadArr[i];
            }
        }
        return null;
    }

    // Retrieving next available EggProducer
    private static synchronized EggProducer getEggProducer(int n_egg_makers) {
        for (int i = 0; i < n_egg_makers; i++) {
            if (!EggArr[i].getIsEggMaking()) {
                // Indicate that EggProducer is being used
                EggArr[i].setIsEggMaking();
                return EggArr[i];
            }
        }
        return null;
    }

    // Retrieving next available SandwichConsumer
    private static synchronized SandwichConsumer getPacker(int n_sandwich_packers) {
        for (int i = 0; i < n_sandwich_packers; i++) {
            if (!SandwichArr[i].getIsSandwichConsuming()) {
                SandwichArr[i].setIsSandwichConsuming();
                return SandwichArr[i];
            }
        }
        return null;
    }

    // Summary log function
    public static void summarise(int n_bread_makers, int n_egg_makers, int n_sandwich_packers) {
        Logger.write("");
        Logger.write("Summary:");
        for (int i = 0; i < n_bread_makers; i++) {
            Logger.write(BreadArr[i].getBreadId() + " makes " + BreadArr[i].getBreadCount());
        }

        for (int i = 0; i < n_egg_makers; i++) {
            Logger.write(EggArr[i].getEggId() + " makes " + EggArr[i].getEggCount());
        }

        for (int i = 0; i < n_sandwich_packers; i++) {
            Logger.write(SandwichArr[i].getSandwichId() + " makes " + SandwichArr[i].getSandwichCount());
        }
    }
}