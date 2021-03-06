package Practica2;

// ============================================================================
class EjemploFuncionSencilla1a {
    // --------------------------------------------------------------------------
    public static void main(String args[]) throws InterruptedException {
        int n, numHebras;
        long t1, t2;
        double tt, ttC, ttB, sumaX, sumaY;

        // Comprobacion y extraccion de los argumentos de entrada.
        if (args.length != 2) {
            System.err.println("Uso: java programa <numHebras> <tamanyo>");
            System.exit(-1);
        }
        try {
            numHebras = Integer.parseInt(args[0]);
            n = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            numHebras = -1;
            n = -1;
            System.out.println("ERROR: Argumentos numericos incorrectos.");
            System.exit(-1);
        }

        // Crea los vectores.
        double vectorX[] = new double[n];
        double vectorY[] = new double[n];

        //
        // Implementacion secuencial.
        //
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        t1 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            vectorY[i] = evaluaFuncion(vectorX[i]);
        }
        t2 = System.nanoTime();
        tt = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Tiempo secuencial (seg.):                    " + tt);
        //// imprimeResultado( vectorX, vectorY );
        // Comprueba el resultado.
        sumaX = sumaVector(vectorX);
        sumaY = sumaVector(vectorY);
        System.out.println("Suma del vector X:          " + sumaX);
        System.out.println("Suma del vector Y:          " + sumaY + "\n");

        // Implementacion paralelizada: cíclica.
        //
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        t1 = System.nanoTime();
        //ParalelizarBucle
        MiHebraCiclica[] vectorCiclicas = new MiHebraCiclica[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vectorCiclicas[i] = new MiHebraCiclica(i, n, numHebras, vectorX, vectorY);
        }
        for (MiHebraCiclica vectorCiclica : vectorCiclicas) {
            vectorCiclica.start();
        }
        for (MiHebraCiclica vectorCiclica : vectorCiclicas) {
            vectorCiclica.join();
        }


        t2 = System.nanoTime();
        ttC = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Tiempo con paralelización cíclica (seg.):    " + ttC);
        System.out.println("Incremento de tiempo respecto al secuencial: " + tt / ttC);
        //// imprimeResultado( vectorX, vectorY );
        // Comprueba el resultado.
        sumaX = sumaVector(vectorX);
        sumaY = sumaVector(vectorY);
        System.out.println("Suma del vector X (cíclico):          " + sumaX);
        System.out.println("Suma del vector Y (cíclico):          " + sumaY + "\n");

        // Implementacion paralelizada: Por Bloques.
        //
        inicializaVectorX(vectorX);
        inicializaVectorY(vectorY);
        t1 = System.nanoTime();
        //ParalelizarBucle
        MiHebraBloques[] vectorBloques = new MiHebraBloques[numHebras];

        for (int i = 0; i < numHebras; i++) {
            vectorBloques[i] = new MiHebraBloques(i, n, numHebras, vectorX, vectorY);
        }
        for (MiHebraBloques vectorBloque : vectorBloques) {
            vectorBloque.start();
        }
        for (MiHebraBloques vectorBloque : vectorBloques) {
            vectorBloque.join();
        }

        t2 = System.nanoTime();
        ttB = ((double) (t2 - t1)) / 1.0e9;
        System.out.println("Tiempo con paralelización Bloques (seg.):    " + ttB);
        System.out.println("Incremento de tiempo respecto al secuencial: " + tt / ttB);
        //// imprimeResultado( vectorX, vectorY );
        // Comprueba el resultado.
        sumaX = sumaVector(vectorX);
        sumaY = sumaVector(vectorY);
        System.out.println("Suma del vector X (bloques):          " + sumaX);
        System.out.println("Suma del vector Y (bloques):          " + sumaY);


        System.out.println("Fin del programa.");
    }

    // --------------------------------------------------------------------------
    static void inicializaVectorX(double vectorX[]) {
        if (vectorX.length == 1) {
            vectorX[0] = 0.0;
        } else {
            for (int i = 0; i < vectorX.length; i++) {
                vectorX[i] = 10.0 * (double) i / ((double) vectorX.length - 1);
            }
        }
    }

    // --------------------------------------------------------------------------
    static void inicializaVectorY(double vectorY[]) {
        for (int i = 0; i < vectorY.length; i++) {
            vectorY[i] = 0.0;
        }
    }

    // --------------------------------------------------------------------------
    static double sumaVector(double vector[]) {
        double suma = 0.0;
        for (int i = 0; i < vector.length; i++) {
            suma += vector[i];
        }
        return suma;
    }

    // --------------------------------------------------------------------------
    static double evaluaFuncion(double x) {
        return 3.5 * x;
    }

    // --------------------------------------------------------------------------
    static void imprimeVector(double vector[]) {
        for (int i = 0; i < vector.length; i++) {
            System.out.println(" vector[ " + i + " ] = " + vector[i]);
        }
    }

    // --------------------------------------------------------------------------
    static void imprimeResultado(double vectorX[], double vectorY[]) {
        for (int i = 0; i < Math.min(vectorX.length, vectorY.length); i++) {
            System.out.println("  i: " + i +
                    "  x: " + vectorX[i] +
                    "  y: " + vectorY[i]);
        }
    }

    // ============================================================================
    static class MiHebraCiclica extends Thread {
        int miId;
        int n;
        int numHebras;
        double[] vectorX;
        double[] vectorY;


        public MiHebraCiclica(int miId, int n, int numhebras, double[] vectorX, double[] vectorY) {
            this.miId = miId;
            this.n = n;
            this.numHebras = numhebras;
            this.vectorX = vectorX;
            this.vectorY = vectorY;
        }

        public void run() {
            int iniElem = miId;
            int finElem = n;
            for (int i = iniElem; i < finElem; i += numHebras) {
                vectorY[i] = evaluaFuncion(vectorX[i]);

            }
        }
    }


    static class MiHebraBloques extends Thread {
        int miId;
        int n;
        int numHebras;
        double[] vectorX;
        double[] vectorY;

        public MiHebraBloques(int miId, int n, int numhebras, double[] vectorX, double[] vectorY) {
            this.miId = miId;
            this.n = n;
            this.numHebras = numhebras;
            this.vectorX = vectorX;
            this.vectorY = vectorY;
        }

        public void run() {
            int trabajoHilo = (int) Math.ceil((double) n / (double) numHebras);
            int iniElem = miId * trabajoHilo;
            int finElem = Math.min(n, iniElem + trabajoHilo);
            for (int i = iniElem; i < finElem; i++) {
                vectorY[i] = evaluaFuncion(vectorX[i]);
            }

        }
    }

}