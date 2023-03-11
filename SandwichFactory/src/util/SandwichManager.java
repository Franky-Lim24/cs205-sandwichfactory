package util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import model.food.Bread;
import model.food.Egg;
import model.food.Sandwich;
import model.machines.EggScrambler;
import model.machines.Packer;
import model.machines.Toaster;
import model.pool.BreadPool;
import model.pool.EggPool;

public class SandwichManager {
    static int sandwichNeeded;
    static int breadCapacity;
    static int eggCapacity;
    static int breadMakers;
    static int eggMakers;
    static int sandwichPackers;
    static int breadRate;
    static int eggRate;
    static int packingRate;

    static volatile BreadPool breadPool;
    static volatile EggPool eggPool;

    static volatile Toaster[] toasters;
    static volatile EggScrambler[] eggScramblers;
    static volatile Packer[] packers;

    static volatile int sandwichesMade = 0;
    static volatile int breadsMade = 0;
    static volatile int eggsMade = 0;

    static Lock lock = new ReentrantLock();
    static Condition cond_bread_done = lock.newCondition();
    static Condition cond_egg_done = lock.newCondition();
    static Condition cond_bread_full = lock.newCondition();
    static Condition cond_egg_full = lock.newCondition();

    public static void main(String[] args) {
        // Initialise logger and arguments
        initialiseLogger(args);

        // Initialise the pools
        initialisePools();

        // Initialise the machines
        initialiseMachines();

        // Start the simulation
        scheduleFood();

        // Wait for the simulation to finish and summarize the results
        summarize();
    }

    public static void initialiseLogger(String[] args) {
        // Remove all previous logs
        Logger.clean();

        // Read in the arguments
        sandwichNeeded = Integer.parseInt(args[0]);
        breadCapacity = Integer.parseInt(args[1]);
        eggCapacity = Integer.parseInt(args[2]);
        breadMakers = Integer.parseInt(args[3]);
        eggMakers = Integer.parseInt(args[4]);
        sandwichPackers = Integer.parseInt(args[5]);
        breadRate = Integer.parseInt(args[6]);
        eggRate = Integer.parseInt(args[7]);
        packingRate = Integer.parseInt(args[8]);

        // Log the arguments
        Logger.write("sandwiches:" + sandwichNeeded);
        Logger.write("bread capacity:" + breadCapacity);
        Logger.write("egg capacity:" + eggCapacity);
        Logger.write("bread makers:" + breadMakers);
        Logger.write("egg makers:" + eggMakers);
        Logger.write("sandwich packers:" + sandwichPackers);
        Logger.write("bread rate:" + breadRate);
        Logger.write("egg rate:" + eggRate);
        Logger.write("packing rate:" + packingRate);
        Logger.write("");
    }

    public static void initialisePools() {
        // Initialise the pools
        breadPool = new BreadPool(breadCapacity);
        eggPool = new EggPool(eggCapacity);
    }

    public static void initialiseMachines() {
        // Initialise the machines
        toasters = new Toaster[breadMakers];
        eggScramblers = new EggScrambler[eggMakers];
        packers = new Packer[sandwichPackers];

        for (int i = 0; i < breadMakers; i++) {
            toasters[i] = new Toaster(breadRate, "B" + i);
        }

        for (int i = 0; i < eggMakers; i++) {
            eggScramblers[i] = new EggScrambler(eggRate, "E" + i);
        }

        for (int i = 0; i < sandwichPackers; i++) {
            packers[i] = new Packer(packingRate, "S" + i);
        }
    }

    public static void scheduleFood() {
        // Create Toaster threads
        Runnable runToaster = new Runnable() {
            @Override
            public void run() {
                // Toast breads
                while (breadsMade < 2 * sandwichNeeded) {
                    // Get the next available toaster
                    Toaster toaster = getToaster();
                    Bread bread = createBread(toaster);
                    toaster.startToasting(bread);
                    if (bread.isToasted()) {
                        try {
                            lock.lock();
                            while (breadPool.isFull()) {
                                cond_bread_full.await();
                            }
                            breadPool.enqueue(bread);
                            Logger.write(toaster.getId() + " puts bread "
                                    + (toaster.getBreadsToasted() - 1));
                            cond_bread_done.signal();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }

            private synchronized Bread createBread(Toaster toaster) {
                breadsMade++;
                return new Bread(toaster.getBreadsToasted(), toaster.getId());
            }
        };

        // Create Egg Scrambler threads
        Runnable runScrambler = new Runnable() {
            @Override
            public void run() {

                // Scramble eggs
                while (eggsMade < sandwichNeeded) {
                    // Get the next available egg scrambler
                    EggScrambler eggScrambler = getEggScrambler();
                    Egg egg = createEgg(eggScrambler);
                    eggScrambler.startScrambling(egg);
                    try {
                        lock.lock();
                        if (egg.isScrambled()) {
                            while (eggPool.isFull()) {
                                cond_egg_full.await();
                            }
                            eggPool.enqueue(egg);
                            Logger.write(eggScrambler.getId() + " puts egg "
                                    + (eggScrambler.getEggsScrambled() - 1));
                            cond_egg_done.signal();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }

            private synchronized Egg createEgg(EggScrambler eggScrambler) {
                eggsMade++;
                return new Egg(eggScrambler.getEggsScrambled(), eggScrambler.getId());
            }
        };

        // Create Packer threads
        Runnable runPacker = new Runnable() {
            @Override
            public void run() {
                // Pack sandwiches
                while (sandwichesMade < sandwichNeeded) {
                    // Get the next available packer
                    Packer packer = getPacker();
                    // Get the next available ingredients
                    waitBread();
                    Bread top = (Bread) breadPool.dequeue();
                    waitEgg();
                    Egg egg = (Egg) eggPool.dequeue();
                    waitBread();
                    Bread bottom = (Bread) breadPool.dequeue();

                    try {
                        lock.lock();
                        // Signal that the bread and egg pool is not full
                        cond_bread_full.signal();
                        cond_egg_full.signal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }

                    // Pack the sandwich
                    Sandwich sandwich = createSandwich(top, bottom, egg);
                    packer.startPacking(sandwich);
                    if (sandwich.isPacked()) {
                        Logger.write(packer.getId() + " packs sandwich "
                                + (packer.getSandwichPacked() - 1) + " with bread " + top.getId()
                                + " from " + top.getToaster() + " and egg " + egg.getId() + " from "
                                + egg.getScrambler() + " and bread " + bottom.getId() + " from "
                                + bottom.getToaster());
                    }
                }
            }

            private synchronized Sandwich createSandwich(Bread top, Bread bottom, Egg egg) {
                return new Sandwich(sandwichesMade++, top, bottom, egg);
            }

            private void waitEgg() {
                lock.lock();
                try {
                    while (eggPool.isEmpty()) {
                        cond_egg_done.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }

            private void waitBread() {
                lock.lock();
                try {
                    while (breadPool.isEmpty()) {
                        cond_bread_done.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        };

        // Create the breads
        Thread[] breads = new Thread[breadMakers];
        for (int i = 0; i < breadMakers; i++) {
            Thread bread = new Thread(runToaster);
            bread.start();
            breads[i] = bread;
        }

        // Create the eggs
        Thread[] eggs = new Thread[eggMakers];
        for (int i = 0; i < eggMakers; i++) {
            Thread egg = new Thread(runScrambler);
            egg.start();
            eggs[i] = egg;
        }

        // Create the sandwiches
        Thread[] sandwiches = new Thread[sandwichPackers];
        for (int i = 0; i < sandwichPackers; i++) {
            Thread sandwich = new Thread(runPacker);
            sandwich.start();
            sandwiches[i] = sandwich;
        }

        while (sandwichesMade < sandwichNeeded) {
            // Wait for the sandwiches to be made
        }

        // Wait for all threads to finish
        try {
            for (Thread t : breads)
                t.join();
            for (Thread t : eggs)
                t.join();
            for (Thread t : sandwiches)
                t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Get the next available packer
    private static synchronized Packer getPacker() {
        for (int i = 0; i < sandwichPackers; i++) {
            if (!packers[i].isPacking()) {
                // Indicate that machine is being used
                packers[i].updateStatus();
                return packers[i];
            }
        }
        return null;
    }

    // Get the next available egg scrambler
    private static synchronized EggScrambler getEggScrambler() {
        for (int i = 0; i < eggMakers; i++) {
            if (!eggScramblers[i].isScrambling()) {
                // Indicate that machine is being used
                eggScramblers[i].updateStatus();
                return eggScramblers[i];
            }
        }
        return null;
    }

    // Get the next available toaster
    private static synchronized Toaster getToaster() {
        for (int i = 0; i < breadMakers; i++) {
            if (!toasters[i].isToasting()) {
                // Indicate that machine is being used
                toasters[i].updateStatus();
                return toasters[i];
            }
        }
        return null;
    }

    public static void summarize() {
        Logger.write("");
        Logger.write("Summary:");
        for (int i = 0; i < breadMakers; i++) {
            Logger.write("Toaster " + toasters[i].getId() + " made "
                    + toasters[i].getBreadsToasted() + " breads");
        }

        for (int i = 0; i < eggMakers; i++) {
            Logger.write("Egg Scrambler " + eggScramblers[i].getId() + " made "
                    + eggScramblers[i].getEggsScrambled() + " eggs");
        }

        for (int i = 0; i < sandwichPackers; i++) {
            Logger.write("Packer " + packers[i].getId() + " made " + packers[i].getSandwichPacked()
                    + " sandwiches");
        }
    }
}
