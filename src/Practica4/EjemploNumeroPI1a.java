package Practica4;

import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

// ===========================================================================
class Acumula {
    // ===========================================================================
    double suma;

    // -------------------------------------------------------------------------
    Acumula() {
        this.suma = 0;
    }

    // -------------------------------------------------------------------------
    synchronized void acumulaDato(double dato) {
        this.suma += dato;
    }

    // -------------------------------------------------------------------------
    double dameDato() {
        return this.suma;
    }
}

// ===========================================================================
class MiHebraMultAcumulaciones1a extends Thread {
    // ===========================================================================
    int miId, numHebras;
    long numRectangulos;
    Acumula a;

    // -------------------------------------------------------------------------
    MiHebraMultAcumulaciones1a(int miId, int numHebras, long numRectangulos,
                               Acumula a) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.a = a;
    }

    // -------------------------------------------------------------------------
    public void run() {
        double x, baseRectangulo;
        baseRectangulo = 1.0 / ((double) numRectangulos);
        for (long i = miId; i < numRectangulos; i += numHebras) {
            x = baseRectangulo * (((double) i) + 0.5);
            a.acumulaDato(EjemploNumeroPI1a.f(x));
        }
    }
}

// ===========================================================================
class MiHebraUnaAcumulacion1b extends Thread {
    // ===========================================================================
    int miId, numHebras;
    long numRectangulos;
    Acumula a;

    // -------------------------------------------------------------------------
    MiHebraUnaAcumulacion1b(int miId, int numHebras, long numRectangulos,
                            Acumula a) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.a = a;
    }

    // -------------------------------------------------------------------------
    public void run() {
        double x, baseRectangulo;
        double sumaL = 0.0;
        baseRectangulo = 1.0 / ((double) numRectangulos);
        for (long i = miId; i < numRectangulos; i += numHebras) {
            x = baseRectangulo * (((double) i) + 0.5);
            sumaL += EjemploNumeroPI1a.f(x);
        }
        a.acumulaDato(sumaL);
    }
}

// ===========================================================================
class MiHebraMultAcumulacionesAtomic1 extends Thread {
    // ===========================================================================
// ===========================================================================
    int miId, numHebras;
    long numRectangulos;
    DoubleAdder a;

    // -------------------------------------------------------------------------
    MiHebraMultAcumulacionesAtomic1(int miId, int numHebras, long numRectangulos,
                                    DoubleAdder a) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.a = a;
    }

    // -------------------------------------------------------------------------
    public void run() {
        double x, baseRectangulo;
        baseRectangulo = 1.0 / ((double) numRectangulos);
        for (long i = miId; i < numRectangulos; i += numHebras) {
            x = baseRectangulo * (((double) i) + 0.5);
            a.add(EjemploNumeroPI1a.f(x));
        }
    }
}

// ===========================================================================
class MiHebraUnaAcumulacionAtomica1d extends Thread {
// ===========================================================================

    int miId, numHebras;
    long numRectangulos;
    DoubleAdder a;

    // -------------------------------------------------------------------------
    MiHebraUnaAcumulacionAtomica1d(int miId, int numHebras, long numRectangulos,
                                   DoubleAdder a) {
        this.miId = miId;
        this.numHebras = numHebras;
        this.numRectangulos = numRectangulos;
        this.a = a;
    }

    // -------------------------------------------------------------------------
    public void run() {
        double x, baseRectangulo;
        DoubleAdder sumaL = new DoubleAdder();
        baseRectangulo = 1.0 / ((double) numRectangulos);
        for (long i = miId; i < numRectangulos; i += numHebras) {
            x = baseRectangulo * (((double) i) + 0.5);
            sumaL.add(EjemploNumeroPI1a.f(x));
        }
        a.add(sumaL.doubleValue());
    }
}


// ===========================================================================
class EjemploNumeroPI1a {
// ===========================================================================

    // -------------------------------------------------------------------------
    public static void main(String args[]) {
        long numRectangulos;
        double baseRectangulo, x, suma, pi;
        int numHebras;
        long t1, t2;
        double tSec, tPar;
        Acumula a;
        LongAdder aL;
        // MiHebraMultAcumulaciones1b  vt[];

        // Comprobacion de los argumentos de entrada.
        if (args.length != 2) {
            System.out.println("ERROR: numero de argumentos incorrecto.");
            System.out.println("Uso: java programa <numHebras> <numRectangulos>");
            System.exit(-1);
        }
        try {
            numHebras = Integer.parseInt(args[0]);
            numRectangulos = Long.parseLong(args[1]);
        } catch (NumberFormatException ex) {
            numHebras = -1;
            numRectangulos = -1;
            System.out.println("ERROR: Numeros de entrada incorrectos.");
            System.exit(-1);
        }

        System.out.println();
        System.out.println("Calculo del numero PI mediante integracion.");

        //
        // Calculo del numero PI de forma secuencial.
        //
        System.out.println();
        System.out.println("Comienzo del calculo secuencial.");
        t1 = System.nanoTime();
        baseRectangulo = 1.0 / ((double) numRectangulos);
        suma = 0.0;
        for (long i = 0; i < numRectangulos; i++) {
            x = baseRectangulo * (((double) i) + 0.5);
            suma += f(x);
        }
        pi = baseRectangulo * suma;
        t2 = System.nanoTime();
        tSec = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Version secuencial. Numero PI: " + pi);
        System.out.println("Tiempo secuencial (s.):        " + tSec);

        //
        // Calculo del numero PI de forma paralela:
        // Multiples acumulaciones por hebra.
        //
        System.out.println();
        System.out.print("Comienzo del calculo paralelo: ");
        System.out.println("Multiples acumulaciones por hebra.");
        t1 = System.nanoTime();

        a = new Acumula();
        MiHebraMultAcumulaciones1a[] vectorCiclicas = new MiHebraMultAcumulaciones1a[numHebras];

        for (int i = 0; i < numHebras; i++) {
            vectorCiclicas[i] = new MiHebraMultAcumulaciones1a(i, numHebras, numRectangulos, a);
            vectorCiclicas[i].start();
        }

        for (MiHebraMultAcumulaciones1a vectorCiclica : vectorCiclicas) {
            try {
                vectorCiclica.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pi = baseRectangulo * a.dameDato();
        t2 = System.nanoTime();
        tPar = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Calculo del numero PI:   " + pi);
        System.out.println("Tiempo ejecucion (s.):   " + tPar);
        System.out.println("Incremento velocidad :   " + tSec / tPar);

        //
        // Calculo del numero PI de forma paralela:
        // Una acumulacion por hebra.
        //
        System.out.println();
        System.out.print("Comienzo del calculo paralelo: ");
        System.out.println("Una acumulacion por hebra.");
        t1 = System.nanoTime();

        a = new Acumula();
        MiHebraUnaAcumulacion1b[] vectorUnaAcumulacion = new MiHebraUnaAcumulacion1b[numHebras];

        for (int i = 0; i < numHebras; i++) {
            vectorUnaAcumulacion[i] = new MiHebraUnaAcumulacion1b(i, numHebras, numRectangulos, a);
            vectorUnaAcumulacion[i].start();
        }

        for (MiHebraUnaAcumulacion1b hebra : vectorUnaAcumulacion) {
            try {
                hebra.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pi = baseRectangulo * a.dameDato();
        t2 = System.nanoTime();
        tPar = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Calculo del numero PI:   " + pi);
        System.out.println("Tiempo ejecucion (s.):   " + tPar);
        System.out.println("Incremento velocidad :   " + tSec / tPar);

        //
        // Calculo del numero PI de forma paralela:
        // Multiples acumulaciones por hebra (Atomica)
        //
        System.out.println();
        System.out.print("Comienzo del calculo paralelo: ");
        System.out.println("Multiples acumulaciones por hebra (At).");
        t1 = System.nanoTime();

        DoubleAdder b = new DoubleAdder();
        MiHebraMultAcumulacionesAtomic1[] vectorAtomicMultAcum = new MiHebraMultAcumulacionesAtomic1[numHebras];

        for (int i = 0; i < numHebras; i++) {
            vectorAtomicMultAcum[i] = new MiHebraMultAcumulacionesAtomic1(i, numHebras, numRectangulos, b);
            vectorAtomicMultAcum[i].start();
        }

        for (MiHebraMultAcumulacionesAtomic1 hebra : vectorAtomicMultAcum) {
            try {
                hebra.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pi = baseRectangulo * b.doubleValue();

        t2 = System.nanoTime();
        tPar = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Calculo del numero PI:   " + pi);
        System.out.println("Tiempo ejecucion (s.):   " + tPar);
        System.out.println("Incremento velocidad :   " + tSec / tPar);

        //
        // Calculo del numero PI de forma paralela:
        // Una acumulacion por hebra (Atomica).

        System.out.println();
        System.out.print("Comienzo del calculo paralelo: ");
        System.out.println("Una acumulacion por hebra (At).");
        t1 = System.nanoTime();

        b = new DoubleAdder();
        MiHebraUnaAcumulacionAtomica1d[] vectorAtomicUnaAcum = new MiHebraUnaAcumulacionAtomica1d[numHebras];

        for (int i = 0; i < numHebras; i++) {
            vectorAtomicUnaAcum[i] = new MiHebraUnaAcumulacionAtomica1d(i, numHebras, numRectangulos, b);
            vectorAtomicUnaAcum[i].start();
        }

        for (MiHebraUnaAcumulacionAtomica1d hebra : vectorAtomicUnaAcum) {
            try {
                hebra.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        pi = baseRectangulo * b.doubleValue();

        t2 = System.nanoTime();
        tPar = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Calculo del numero PI:   " + pi);
        System.out.println("Tiempo ejecucion (s.):   " + tPar);
        System.out.println("Incremento velocidad :   " + tSec / tPar);

        System.out.println();
        System.out.println("Fin de programa.");
    }

    // -------------------------------------------------------------------------
    static double f(double x) {
        return (4.0 / (1.0 + x * x));
    }
}

