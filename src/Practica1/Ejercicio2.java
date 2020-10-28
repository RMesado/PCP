package Practica1;

class MiHebra2 extends Thread {
    int miId;
    int ini;
    int fin;

    public MiHebra2(int miId, int ini, int fin) {
        this.miId = miId;
        this.ini = ini;
        this.fin = fin;
    }

    public void run() {
        int suma = 0;
        for (int i = ini + 1; i < fin; i++)
            suma += i;
        System.out.println("Soy la hebra " + miId + " y la suma de los números comprendidos entre " + ini + " y " + fin + " es " + suma);

    }
}

class EjemploCreacion5a {
    public static void main(String args[]) {
        System.out.println("Vamos a arrancar las 2 hebras");
        new Thread(new MiHebra2(0, 7, 15)).start();
        new Thread(new MiHebra2(1, 4, 10)).start();
        System.out.println("Trabajo terminado");

    }
}

class EjemploCreacion5b {

    public static void main(String args[]) throws InterruptedException {
        System.out.println("Vamos a arrancar las 2 hebras");
        MiHebra2 t = new MiHebra2(0, 1, 1000000);
        MiHebra2 t2 = new MiHebra2(1, 1, 1000000);
        new Thread(t).setDaemon(true);
        new Thread(t2).setDaemon(true);
        t.start();
        t2.start();
        t.join();
        t2.join();
        System.out.println("Trabajo terminado");
    }
}

