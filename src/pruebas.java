import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.*;

class pruebas {
    public static class EjemploColas1a extends Thread {
        int miId;
        String hola;
        SynchronousQueue<String> cola = new SynchronousQueue<>();

        EjemploColas1a(int miId) {
            this.miId = miId;
        }

        public void run() {
            if (miId == 0) {
                metodo0();
                try {
                    cola.put("hola");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (miId == 1) {
                try {
                    hola = cola.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                metodo1();
            }
        }
        // Resto de código .
    }

    public static void metodo0() {
        for (int i = 0; i < 15; i++)
            System.out.println(i);
    }

    public static void metodo1() {
        for (int i = 15; i < 30; i++)
            System.out.println(i);
    }

    // ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===
    class MiHebra3b extends Thread {
        // ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===
        int miId, numHebras;
        CyclicBarrier barrera;

        // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
        MiHebra3b(int miId, int numHebras, CyclicBarrier barrera) {
            this.miId = miId;
            this.numHebras = numHebras;
            this.barrera = barrera;
        }

        // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
        public void run() {
            // ...
            System.out.println(" Hebra : " + miId + " Antes ");
            try {
                barrera.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(" Hebra : " + miId + " Despues ");
            // ...
        }
    }

    // ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===
    static class Coordinacion3b {
        // ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===
        int numHebras, numHebrasQueHanLlegado;

        // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
        Coordinacion3b(int numHebras) {
            this.numHebras = numHebras;
            numHebrasQueHanLlegado = 0
        }

        // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
        public void esperaTurno() {
            if (numHebras != numHebrasQueHanLlegado) {
                numHebrasQueHanLlegado += 1;
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else notifyAll();
        }
    }

    static class Tarea {
        boolean estaEnvenenada;
        String linea;

        public Tarea(boolean estaEnvenenada, String linea) {
            this.estaEnvenenada = estaEnvenenada;
            this.linea = linea;
        }
    }


    class hebraContadora extends Thread {
        ArrayBlockingQueue<Tarea> cola;
        boolean estaEnvenenada;

        public hebraContadora(ArrayBlockingQueue<Tarea> cola) {
            this.cola = cola;
            estaEnvenenada = false;
        }

        public void run() {
            Tarea tarea = null;
            while (!estaEnvenenada) {
                try {
                    tarea = cola.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (tarea != null) {
                    if (tarea.estaEnvenenada) {
                        //procesaLinea(tarea.linea);
                    } else this.estaEnvenenada = tarea.estaEnvenenada;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        EjemploColas1a hebra1 = new EjemploColas1a(1);
        EjemploColas1a hebra2 = new EjemploColas1a(0);
        hebra1.start();
        hebra2.start();
        hebra1.join();
        hebra2.join();
        ArrayBlockingQueue<Long> cola = new ArrayBlockingQueue<>(5);

    }


}