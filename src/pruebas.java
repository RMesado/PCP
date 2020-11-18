import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class pruebas {
    static int cuentaNumMaxMayusculasEnUnaLinea_ThreadPool(String[] vectorTiras, int numHebras) {
        AtomicInteger numMaxEnUnaLinea = new AtomicInteger();
        ExecutorService exec = Executors.newFixedThreadPool(numHebras);

        class Tarea implements Runnable {
            final private AtomicInteger numMaxEnUnaLinea;
            final private String tiraAProcesar;
            private final AtomicInteger numEnLineaActual = new AtomicInteger();

            Tarea(AtomicInteger numMaxEnUnaLinea, String tiraAProcesar) {
                this.numMaxEnUnaLinea = numMaxEnUnaLinea;
                this.tiraAProcesar = tiraAProcesar;
            }

            public void run() {
                numEnLineaActual.set(cuentaMayusculas(tiraAProcesar));
                if (numEnLineaActual.get() > numMaxEnUnaLinea.get())
                    numMaxEnUnaLinea.set(numEnLineaActual.get());
            }
        }
        numMaxEnUnaLinea.set(cuentaMayusculas(vectorTiras[0]));
        for (int i = 1; i < vectorTiras.length; i++)
            exec.execute((new Tarea(numMaxEnUnaLinea, vectorTiras[i])));

        exec.shutdown();
        try {
            while (!exec.awaitTermination(2L, TimeUnit.MILLISECONDS)) {
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return numMaxEnUnaLinea.get();
    }

    static int cuentaNumMaxMayusculasEnUnaLinea_ThreadPool_Callable(String[] vectorTiras, int numHebras) {
        int numMaxEnUnaLinea = 0;
        int numEnLineaActual = 0;
        Future<Integer> f;
        ExecutorService exec = Executors.newFixedThreadPool(numHebras);
        ArrayList<Future<Integer>> alf = new ArrayList<Future<Integer>>();

        class Tarea implements Callable {
            final private String tiraAProcesar;

            Tarea(String tiraAProcesar) {
                this.tiraAProcesar = tiraAProcesar;
            }

            public Integer call() {
                return cuentaMayusculas(tiraAProcesar);
            }
        }
        for (int i = 0; i < vectorTiras.length; i++) {
            f = exec.submit(new Tarea(vectorTiras[i]));
            alf.add(f);
        }

        exec.shutdown();
        for (int i = 0; i < alf.size(); i++) {
            try {
                f = alf.get(i);
                numEnLineaActual = f.get();
                if (numEnLineaActual > numMaxEnUnaLinea) numMaxEnUnaLinea = numEnLineaActual;
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        }
        return numMaxEnUnaLinea;
    }

    static public int cuentaMayusculas(String s) {
        int numMayusculas = 0;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                numMayusculas++;
            }
        }
        return numMayusculas;
    }

    public static void main(String[] args) {
        String[] vector = {"Hola", "Adios", "Muy Buenas", "HOSTIA", "JeSUSSSS"};
        System.out.println(cuentaNumMaxMayusculasEnUnaLinea_ThreadPool(vector, 4));

    }
}