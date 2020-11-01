package Practica2;

// ============================================================================
class EjemploMuestraNumeros1a {
    // ============================================================================

    // --------------------------------------------------------------------------
    public static void main(String[] args) throws InterruptedException {
        int n, numHebras;

        // Comprobacion y extraccion de los argumentos de entrada.
        if (args.length != 2) {
            System.err.println("Uso: java programa <numHebras> <n>");
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
        //
        // Implementacion paralela con distribucion ciclica o por bloques.
        class MiHebraBloque extends Thread {
            int miId;
            int numHebras;
            int n;

            public MiHebraBloque(int miId, int numHebras, int n) {
                this.miId = miId;
                this.numHebras = numHebras;
                this.n = n;
            }

            public void run() {
                int trabajoHilo = (int) Math.ceil((double) n / (double) numHebras);
                int iniElem = miId * trabajoHilo;
                int finElem = Math.min(n, iniElem + trabajoHilo);
                for (int i = iniElem; i <= finElem; i++) {
                    System.out.println(" Hebra " + miId + ": Numero: " + i);
                }

            }
        }
        //
        // Crea y arranca el vector de hebras.
        MiHebraBloque[] vectorBloques = new MiHebraBloque[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vectorBloques[i] = new MiHebraBloque(i, numHebras, n);
        }
        for (MiHebraBloque vectorBloque : vectorBloques) {
            vectorBloque.start();
        }
        // Espera a que terminen las hebras.
        for (MiHebraBloque vectorParalela : vectorBloques) {
            vectorParalela.join();
        }

    }
}
