import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

class pruebas {
    public static void main(String[] args) {
        Ht2 articulos;
        Hashtable<String, Integer> m1 = new Hashtable<>();
        int a = 20;
        m1.put(" abc ", 10);
        System.out.println(m1);
        m1.merge(" abc ", a, Integer::max);
        System.out.println(m1);
        m1.merge(" abc ", 5, Integer::max);
        System.out.println(m1);
        m1.merge(" def ", 5, Integer::sum);
        System.out.println(m1);
        System.out.println("/----------------------------------");
        Ht2 m2 = new Ht2();
        a = 20;
        m2.contabilizaVenta(" abc ", 10);
        System.out.println(m2);
        m2.contabilizaVenta(" abc ", a);
        System.out.println(m2);
        m2.contabilizaVenta(" abc ", 5);
        System.out.println(m2);
        m2.contabilizaVenta(" def ", 5);
        System.out.println(m2);
        System.out.println("/----------------------------------");
        hash1 m3 = new hash1();

        m3.encontradaNuevaPalabra(" abc ");
        System.out.println(m3);
        m3.encontradaNuevaPalabra(" abc ");
        System.out.println(m3);
        m3.encontradaNuevaPalabra(" nbv ");
        System.out.println(m3);
        m3.encontradaNuevaPalabra(" abc ");
        System.out.println(m3);


    }

    static class Ht2 extends Hashtable<String, Integer> {

        synchronized void contabilizaVenta(String codArticulo, int importe) {
            Integer importeActual = this.get(codArticulo);
            if (importeActual != null) {
                if (importeActual < importe)
                    this.replace(codArticulo, importe);
            } else {
                this.put(codArticulo, importe);
            }
        }

    }

    static class Chm3 extends ConcurrentHashMap<String, Integer> {
        synchronized void contabilizaVenta(String codArticulo, int importe) {
            this.merge(codArticulo, importe, Integer::max);
        }
    }

    static class Chm2 extends ConcurrentHashMap<String, Integer> {
        synchronized void contabilizaVenta(String codArticulo, int importe) {
            Integer importeActual;
            boolean modif = false;
            importeActual = putIfAbsent(codArticulo, importe);
            if (importeActual != null) {
                do {
                    importeActual = this.get(codArticulo);
                    if (importeActual < importe) {
                        modif = this.replace(codArticulo, importeActual, importe);
                    }

                } while (!modif);
            }
        }
    }

    static class hash1 extends Hashtable<String, Boolean> {

        synchronized void encontradaNuevaPalabra(String palabra) {
            Boolean valorActual = this.get(palabra);
            if (valorActual != null) {
                this.replace(palabra, !valorActual);
            } else {
                this.put(palabra, true);
            }
        }

    }

    static class hash2 extends ConcurrentHashMap<String, Boolean> {

        synchronized void encontradaNuevaPalabra(String palabra) {
            Boolean valorActual;
            boolean modif;
            valorActual = putIfAbsent(palabra, true);
            if (valorActual != null) {
                do {
                    valorActual = this.get(palabra);
                    modif = this.replace(palabra, valorActual, !valorActual);

                } while (!modif);
            }
        }
    }


    static class hash3 extends ConcurrentHashMap<String, Boolean> {
        void encontradaNuevaPalabra(String palabra) {
            this.compute(palabra, (k,oldValue)-> oldValue == null || !oldValue);
        }
    }
}