package Practica1;

class MiRun implements Runnable {
    int miId;

    public MiRun(int miId) {
        this.miId = miId;
    }

    public void run() {
        for (int i = 0; i < 1000; i++)
            System.out.println(miId);
    }
}

class EjemploCreacion4b {
    public static void main(String args[]) {
        new Thread(new MiRun(0)).start();
        new Thread(new MiRun(1)).start();

    }
}
