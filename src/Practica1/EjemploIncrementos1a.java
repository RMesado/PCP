package Practica1;

// ============================================================================
class CuentaIncrementos1a {
    // ============================================================================
    long contador = 0;

    // --------------------------------------------------------------------------
    void incrementaContador() {
        contador++;
    }

    // --------------------------------------------------------------------------
    long dameContador() {
        return (contador);
    }
}

class MiHebra3 extends Thread {
    int miId;
    CuentaIncrementos1a obj;

    public MiHebra3(int miId, CuentaIncrementos1a obj) {
        this.miId = miId;
        this.obj = obj;

    }

    public void run() {
        for (int i = 1; i < 1000000; i++) {
            System.out.println("Soy la hebra " + miId + " y voy a realizar un incremento");
            obj.incrementaContador();
            System.out.println("Incremento realizado");
        }

    }
}


// ============================================================================
class EjemploIncrementos1a {
// ============================================================================

    // --------------------------------------------------------------------------
    public static void main(String args[]) {
        int numHebras;

        // Comprobacion y extraccion de los argumentos de entrada.
        if (args.length != 1) {
            System.err.println("Uso: java programa <numHebras>");
            System.exit(-1);
        }
        try {
            numHebras = Integer.parseInt(args[0]);
            CuentaIncrementos1a contador = new CuentaIncrementos1a();
            System.out.println("El valor inicial del contador es: " + contador.dameContador());
            MiHebra3[] vectorHebras = new MiHebra3[numHebras];
            for (int i = 0; i < numHebras; i++) {
                vectorHebras[i] = new MiHebra3(i, contador);
                vectorHebras[i].start();
            }
            vectorHebras[0].join();
            vectorHebras[1].join();
            vectorHebras[2].join();
            vectorHebras[3].join();

            System.out.println("El valor final del contador es: " + contador.dameContador());



        } catch (NumberFormatException | InterruptedException ex) {
            numHebras = -1;
            System.out.println("ERROR: Argumentos numericos incorrectos.");
            System.exit(-1);
        }

        System.out.println("numHebras: " + numHebras);
    }
}

