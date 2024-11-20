import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;


public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        task1();

        task2();

        task3();

        task4();

        task5();

        task6();

        task7();

        task8();

        task9();

        task10();

        task11();

        task12();
    }

    // Задание 1
    private static void task1() throws InterruptedException {
        System.out.println("Задание 1: Общий счетчик");
        final int THREADS = 5;
        final int INCREMENTS = 1000;
        AtomicInteger counter = new AtomicInteger(0);
        ReentrantLock lock = new ReentrantLock();

        Runnable task = () -> {
            for (int i = 0; i < INCREMENTS; i++) {
                lock.lock();
                try {
                    counter.incrementAndGet();
                } finally {
                    lock.unlock();
                }
            }
        };

        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Итоговый счетчик: " + counter.get());
    }

    // Задание 2
    private static void task2() throws InterruptedException {
        System.out.println("Задание 2: Генерация последовательности чисел");
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        Runnable task = () -> {
            for (int i = 1; i <= 100; i++) {
                list.add(i);
            }
        };

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Размер списка: " + list.size());
        System.out.println("Числа в списке: " + list);
    }

    // Задание 3
    private static void task3() {
        System.out.println("Задание 3: Распределение задач с использованием пула потоков");
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 1; i <= 20; i++) {
            final int taskNumber = i;
            executor.execute(() -> System.out.println("Задача " + taskNumber + " выполняется потоком " + Thread.currentThread().getName()));
        }
        executor.shutdown();
    }

    // Задание 4
    private static void task4() throws InterruptedException {
        System.out.println("Задание 4: Симуляция работы банка");
        class Account {
            private final ReentrantLock lock = new ReentrantLock();
            private int balance;

            public Account(int initialBalance) {
                this.balance = initialBalance;
            }

            public void transfer(Account target, int amount) {
                lock.lock();
                try {
                    target.lock.lock();
                    try {
                        if (balance >= amount) {
                            balance -= amount;
                            target.balance += amount;
                            System.out.println("Переведено " + amount + ". Остаток на текущем счёте: " + balance);
                        }
                    } finally {
                        target.lock.unlock();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        Account account1 = new Account(1000);
        Account account2 = new Account(1000);

        Runnable transferTask = () -> account1.transfer(account2, 100);
        Thread t1 = new Thread(transferTask);
        Thread t2 = new Thread(transferTask);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    // Задание 5
    private static void task5() throws InterruptedException {
        System.out.println("Задание 5: Барьер синхронизации");
        CyclicBarrier barrier = new CyclicBarrier(5, () -> System.out.println("Все потоки завершили текущую фазу!"));
        Runnable task = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " выполняет задачу");
                Thread.sleep(1000);
                barrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Восстановить флаг прерывания
                System.out.println(Thread.currentThread().getName() + " прерван");
            } catch (BrokenBarrierException e) {
                System.out.println("Барьер был сломан: " + e.getMessage());
            }
        };

        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }


    // Задание 6
    private static void task6() throws InterruptedException {
        System.out.println("Задание 6: Ограниченный доступ к ресурсу");
        Semaphore semaphore = new Semaphore(2);

        Runnable task = () -> {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + " получил доступ к ресурсу");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + " освобождает ресурс");
                semaphore.release();
            }
        };

        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    // Задание 7
    private static void task7() throws InterruptedException, ExecutionException {
        System.out.println("Задание 7: Обработка результатов задач");
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Long>> results = new ArrayList<>();

        Callable<Long> task = () -> {
            long result = 1;
            for (int i = 1; i <= 10; i++) {
                result *= i;
            }
            return result;
        };

        for (int i = 0; i < 10; i++) {
            results.add(executor.submit(task));
        }

        for (Future<Long> future : results) {
            System.out.println("Результат: " + future.get());
        }

        executor.shutdown();
    }

    // Задание 8
    private static void task8() throws InterruptedException {
        System.out.println("Задание 8: Симуляция производственной линии");
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        Runnable producer = () -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    queue.put(i);
                    System.out.println("Произведено: " + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable consumer = () -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    int item = queue.take();
                    System.out.println("Обработано: " + item);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();

        producerThread.join();
        consumerThread.join();
    }

    // Задание 9
    private static void task9() throws InterruptedException, ExecutionException {
        System.out.println("Задание 9: Многопоточная сортировка");
        int[] array = {5, 3, 8, 6, 2, 7, 4, 1};
        int parts = 2;
        ExecutorService executor = Executors.newFixedThreadPool(parts);
        List<Future<int[]>> results = new ArrayList<>();

        int partSize = array.length / parts;
        for (int i = 0; i < parts; i++) {
            final int start = i * partSize;
            final int end = (i == parts - 1) ? array.length : start + partSize;
            Callable<int[]> task = () -> {
                int[] part = Arrays.copyOfRange(array, start, end);
                Arrays.sort(part);
                return part;
            };
            results.add(executor.submit(task));
        }

        int[] sortedArray = new int[array.length];
        int index = 0;
        for (Future<int[]> future : results) {
            int[] part = future.get();
            System.arraycopy(part, 0, sortedArray, index, part.length);
            index += part.length;
        }
        Arrays.sort(sortedArray); // Final merge sort for simplicity

        System.out.println("Отсортированный массив: " + Arrays.toString(sortedArray));
        executor.shutdown();
    }

    // Задание 10
    private static void task10() throws InterruptedException {
        System.out.println("Задание 10: Обед философов");
        class Fork {
        }

        class Philosopher implements Runnable {
            private final Fork leftFork;
            private final Fork rightFork;

            public Philosopher(Fork leftFork, Fork rightFork) {
                this.leftFork = leftFork;
                this.rightFork = rightFork;
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        synchronized (leftFork) {
                            synchronized (rightFork) {
                                System.out.println(Thread.currentThread().getName() + " ест");
                                Thread.sleep(1000);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Fork[] forks = new Fork[5];
        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Fork();
        }

        Thread[] philosophers = new Thread[5];
        for (int i = 0; i < philosophers.length; i++) {
            Fork leftFork = forks[i];
            Fork rightFork = forks[(i + 1) % forks.length];
            philosophers[i] = new Thread(new Philosopher(leftFork, rightFork), "Философ " + (i + 1));
            philosophers[i].start();
        }

        for (Thread philosopher : philosophers) {
            philosopher.join();
        }
    }

    // Задание 11
    private static void task11() throws InterruptedException {
        System.out.println("Задание 11: Расчёт матрицы в параллельных потоках");
        int[][] matrixA = {{1, 2}, {3, 4}};
        int[][] matrixB = {{5, 6}, {7, 8}};
        int[][] result = new int[matrixA.length][matrixB[0].length];
        ExecutorService executor = Executors.newFixedThreadPool(matrixA.length);

        for (int i = 0; i < matrixA.length; i++) {
            final int row = i;
            executor.execute(() -> {
                for (int j = 0; j < matrixB[0].length; j++) {
                    for (int k = 0; k < matrixB.length; k++) {
                        result[row][j] += matrixA[row][k] * matrixB[k][j];
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Результат умножения матриц:");
        for (int[] row : result) {
            System.out.println(Arrays.toString(row));
        }
    }

    // Задание 12
    private static void task12() throws InterruptedException {
        System.out.println("Задание 12: Таймер с многопоточностью");
        AtomicBoolean running = new AtomicBoolean(true);

        Thread timer = new Thread(() -> {
            int seconds = 0;
            while (running.get()) {
                try {
                    System.out.println("Таймер: " + seconds++ + " секунд");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(10000);
                running.set(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        timer.start();
        stopper.start();

        timer.join();
        stopper.join();
        System.out.println("Таймер остановлен");
    }
}
