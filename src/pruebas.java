// ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===
class MiHebra4b extends Thread {
    // ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===
    int miId, numHebras;
    double vector[];
    Maximo4b maxi;

    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
    MiHebra4b(int miId, int numHebras, double vector[], Maximo4b
            maxi) {
        this.miId = miId;
        this.numHebras = numHebras;

        this.vector = vector;
        this.maxi = maxi;
    }

    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
    public void run() {
        double maximo = 0;
        for (int i = miId; i < vector.length; i += numHebras) {
            if(vector[i] > maximo)
                maximo = vector[i];
        }
        maxi.actualizaMaximo(maximo);
    }
}

// ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===
class Maximo4b {
    // ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===== ===
    boolean hayDato;
    double maximoHastaElMomento;

    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
    void inicializaObjeto() {
        Maximo4b maxi = new Maximo4b();
    }

    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
    void actualizaMaximo(double numero) {
        if(numero>maximoHastaElMomento)
            maximoHastaElMomento = numero;
    }

    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- -
    double dameResultado() {
        return maximoHastaElMomento;
    }
}