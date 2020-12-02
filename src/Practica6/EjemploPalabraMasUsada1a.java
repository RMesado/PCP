package Practica6;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static Practica6.EjemploPalabraMasUsada1a.*;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

// ============================================================================
class EjemploPalabraMasUsada1a {
// ============================================================================

    // -------------------------------------------------------------------------
    public static void main(String args[]) {
        long t1, t2;
        double tsec, tp;
        int numHebras;
        String nombreFichero, palabraActual;
        ArrayList<String> arrayLineas;
        HashMap<String, Integer> hmCuentaPalabras, maCuentaPalabras;
        Hashtable<String, Integer> htCuentaPalabras;

        // Comprobacion y extraccion de los argumentos de entrada.
        if (args.length != 2) {
            System.err.println("Uso: java programa <numHebras> <fichero>");
            System.exit(-1);
        }
        try {
            numHebras = Integer.parseInt(args[0]);
            nombreFichero = args[1];
        } catch (NumberFormatException ex) {
            numHebras = -1;
            nombreFichero = "";
            System.out.println("ERROR: Argumentos numericos incorrectos.");
            System.exit(-1);
        }

        // Lectura y carga de lineas en "arrayLineas".
        arrayLineas = readFile(nombreFichero);
        System.out.println("Numero de lineas leidas: " + arrayLineas.size());
        System.out.println();

        //
        // Implementacion secuencial sin temporizar.
        //
        hmCuentaPalabras = new HashMap<String, Integer>(1000, 0.75F);
        for (int i = 0; i < arrayLineas.size(); i++) {
            // Procesa la linea "i".
            String[] palabras = arrayLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blancos.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra(hmCuentaPalabras, palabraActual);
                }
            }
        }

        //
        // Implementacion secuencial.
        //
        t1 = System.nanoTime();
        hmCuentaPalabras = new HashMap<String, Integer>(1000, 0.75F);
        for (int i = 0; i < arrayLineas.size(); i++) {
            // Procesa la linea "i".
            String[] palabras = arrayLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blancos.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabra(hmCuentaPalabras, palabraActual);
                }
            }
        }
        t2 = System.nanoTime();
        tsec = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implemen. secuencial: ");
        imprimePalabraMasUsadaYVeces(hmCuentaPalabras);
        System.out.println(" Tiempo(s): " + tsec);
        System.out.println("Num. elems. tabla hash: " + hmCuentaPalabras.size());
        System.out.println();


        //
        // Implementacion paralela 1: Uso de synchronizedMap.

        t1 = System.nanoTime();
        maCuentaPalabras = new HashMap<String, Integer>(1000, 0.75F);
        MiHebra_1[] vectorMiHebra_1 = new MiHebra_1[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vectorMiHebra_1[i] = new MiHebra_1(i, numHebras, arrayLineas, maCuentaPalabras);
            vectorMiHebra_1[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vectorMiHebra_1[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        tp = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implemen. paralela 1: ");
        imprimePalabraMasUsadaYVeces(maCuentaPalabras);
        System.out.println(" Tiempo(s): " + tp + " , Incremento " + tsec / tp);
        System.out.println("Num. elems. tabla hash: " + maCuentaPalabras.size());
        System.out.println();

        //
        // Implementacion paralela 2: Uso de Hashtable.

        t1 = System.nanoTime();
        htCuentaPalabras = new Hashtable<>(1000, 0.75F);
        MiHebra_2[] vectorMiHebra_2 = new MiHebra_2[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vectorMiHebra_2[i] = new MiHebra_2(i, numHebras, arrayLineas, htCuentaPalabras);
            vectorMiHebra_2[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vectorMiHebra_2[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        tp = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implemen. paralela 2: ");
        imprimePalabraMasUsadaYVeces(htCuentaPalabras);
        System.out.println(" Tiempo(s): " + tp + " , Incremento " + tsec / tp);
        System.out.println("Num. elems. tabla hash: " + htCuentaPalabras.size());
        System.out.println();

        // Implementacion paralela 3: Uso de ConcurrentHashMap

        t1 = System.nanoTime();
        ConcurrentHashMap<String, Integer> chmCuentaPalabras = new ConcurrentHashMap<String, Integer>(1000, 0.75F);
        MiHebra_3[] vectorMiHebra_3 = new MiHebra_3[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vectorMiHebra_3[i] = new MiHebra_3(i, numHebras, arrayLineas, chmCuentaPalabras);
            vectorMiHebra_3[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vectorMiHebra_3[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        tp = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implemen. paralela 3: ");
        imprimePalabraMasUsadaYVeces(chmCuentaPalabras);
        System.out.println(" Tiempo(s): " + tp + " , Incremento " + tsec / tp);
        System.out.println("Num. elems. tabla hash: " + chmCuentaPalabras.size());
        System.out.println();

        // Implementacion paralela 4: Uso de ConcurrentHashMap

        t1 = System.nanoTime();
        ConcurrentHashMap<String, Integer> chm2CuentaPalabras = new ConcurrentHashMap<String, Integer>(1000, 0.75F);
        MiHebra_4[] vectorMiHebra_4 = new MiHebra_4[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vectorMiHebra_4[i] = new MiHebra_4(i, numHebras, arrayLineas, chm2CuentaPalabras);
            vectorMiHebra_4[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vectorMiHebra_4[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        tp = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implemen. paralela 4: ");
        imprimePalabraMasUsadaYVeces(chm2CuentaPalabras);
        System.out.println(" Tiempo(s): " + tp + " , Incremento " + tsec / tp);
        System.out.println("Num. elems. tabla hash: " + chm2CuentaPalabras.size());
        System.out.println();

        // Implementacion paralela 5: Uso de ConcurrentHashMap
        t1 = System.nanoTime();
        ConcurrentHashMap<String, AtomicInteger> chmAICuentaPalabras = new ConcurrentHashMap<String, AtomicInteger>(1000, 0.75F);
        MiHebra_5[] vectorMiHebra_5 = new MiHebra_5[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vectorMiHebra_5[i] = new MiHebra_5(i, numHebras, arrayLineas, chmAICuentaPalabras);
            vectorMiHebra_5[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vectorMiHebra_5[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        tp = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implemen. paralela 5: ");
        imprimePalabraMasUsadaYVecesAT(chmAICuentaPalabras);
        System.out.println(" Tiempo(s): " + tp + " , Incremento " + tsec / tp);
        System.out.println("Num. elems. tabla hash: " + chmAICuentaPalabras.size());
        System.out.println();
        // Implementacion paralela 6: Uso de ConcurrentHashMap

        t1 = System.nanoTime();
        ConcurrentHashMap<String, AtomicInteger> chmAI2CuentaPalabras = new ConcurrentHashMap<String, AtomicInteger>(1000, 0.75F, 256);
        MiHebra_6[] vectorMiHebra_6 = new MiHebra_6[numHebras];
        for (int i = 0; i < numHebras; i++) {
            vectorMiHebra_6[i] = new MiHebra_6(i, numHebras, arrayLineas, chmAI2CuentaPalabras);
            vectorMiHebra_6[i].start();
        }
        for (int i = 0; i < numHebras; i++) {
            try {
                vectorMiHebra_6[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t2 = System.nanoTime();
        tp = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implemen. paralela 6: ");
        imprimePalabraMasUsadaYVecesAT(chmAI2CuentaPalabras);
        System.out.println(" Tiempo(s): " + tp + " , Incremento " + tsec / tp);
        System.out.println("Num. elems. tabla hash: " + chmAI2CuentaPalabras.size());
        System.out.println();

        // Implementacion paralela 7: Uso de Streams

        t1 = System.nanoTime();
        Map<String, Long> stCuentaPalabras = arrayLineas.parallelStream()
                .filter(s -> s != null)
                .map(s -> s.split("\\W+"))
                .flatMap(Arrays::stream).map(String::trim)
                .filter(s -> (s.length() > 0))
                .collect(groupingBy(s -> s, counting()));

        t2 = System.nanoTime();
        tp = ((double) (t2 - t1)) / 1.0e9;
        System.out.print("Implemen. paralela 7: ");
        imprimePalabraMasUsadaYVecesStream(stCuentaPalabras);
        System.out.println(" Tiempo(s): " + tp + " , Incremento " + tsec / tp);
        System.out.println("Num. elems. tabla hash: " + stCuentaPalabras.size());
        System.out.println();

        System.out.println("Fin de programa.");
    }

    // -------------------------------------------------------------------------
    public static ArrayList<String> readFile(String fileName) {
        BufferedReader br;
        String linea;
        ArrayList<String> data = new ArrayList<String>();

        try {
            br = new BufferedReader(new FileReader(fileName));
            while ((linea = br.readLine()) != null) {
                //// System.out.println( "Leida linea: " + linea );
                data.add(linea);
            }
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    // -------------------------------------------------------------------------
    public static void contabilizaPalabra(
            HashMap<String, Integer> cuentaPalabras,
            String palabra) {
        Integer numVeces = cuentaPalabras.get(palabra);
        if (numVeces != null) {
            cuentaPalabras.put(palabra, numVeces + 1);
        } else {
            cuentaPalabras.put(palabra, 1);
        }
    }

    // -------------------------------------------------------------------------
    synchronized public static void contabilizaPalabraSynchronized(
            HashMap<String, Integer> cuentaPalabras,
            String palabra) {
        Integer numVeces = cuentaPalabras.get(palabra);
        if (numVeces != null) {
            cuentaPalabras.put(palabra, numVeces + 1);
        } else {
            cuentaPalabras.put(palabra, 1);
        }
        //cuentaPalabras.compute(palabra, (k,v) ->(v==null) ? 1 : v+1);
        //tambien se puede merge
    }

    // -------------------------------------------------------------------------
    public static void contabilizaPalabraHashTable(
            Hashtable<String, Integer> cuentaPalabras,
            String palabra) {
        cuentaPalabras.merge(palabra, 1, Integer::sum);
    }

    // -------------------------------------------------------------------------
    synchronized public static void contabilizaPalabraConcurrentHashMap(
            ConcurrentHashMap<String, Integer> cuentaPalabras,
            String palabra) {
        Integer numVeces = cuentaPalabras.get(palabra);
        if (numVeces != null) {
            cuentaPalabras.put(palabra, numVeces + 1);
        } else {
            cuentaPalabras.put(palabra, 1);
        }
    }

    // -------------------------------------------------------------------------
    public static void contabilizaPalabraConcurrentHashMap2(
            ConcurrentHashMap<String, Integer> cuentaPalabras,
            String palabra) {
        boolean modif;

        Integer numVeces = cuentaPalabras.putIfAbsent(palabra, 1);
        if (numVeces != null) {
            do {
                numVeces = cuentaPalabras.get(palabra);
                modif = cuentaPalabras.replace(palabra, numVeces, numVeces + 1);

            } while (!modif);
        }
    }

    // -------------------------------------------------------------------------
    public static void contabilizaPalabraConcurrentHashMapAtomic(
            ConcurrentHashMap<String, AtomicInteger> cuentaPalabras,
            String palabra) {

        AtomicInteger numVeces = cuentaPalabras.putIfAbsent(palabra, new AtomicInteger(1));
        if (numVeces != null) {
            numVeces.getAndIncrement();
        }
    }


    // --------------------------------------------------------------------------
    static void imprimePalabraMasUsadaYVeces(
            Map<String, Integer> cuentaPalabras) {
        ArrayList<Map.Entry> lista =
                new ArrayList<Map.Entry>(cuentaPalabras.entrySet());

        String palabraMasUsada = "";
        int numVecesPalabraMasUsada = 0;
        // Calcula la palabra mas usada.
        for (int i = 0; i < lista.size(); i++) {
            String palabra = (String) lista.get(i).getKey();
            int numVeces = (Integer) lista.get(i).getValue();
            if (i == 0) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            } else if (numVecesPalabraMasUsada < numVeces) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            }
        }
        // Imprime resultado.
        System.out.print("( Palabra: '" + palabraMasUsada + "' " +
                "veces: " + numVecesPalabraMasUsada + " )");
    }

    // --------------------------------------------------------------------------
    static void imprimePalabraMasUsadaYVecesAT(
            Map<String, AtomicInteger> cuentaPalabras) {
        ArrayList<Map.Entry> lista =
                new ArrayList<Map.Entry>(cuentaPalabras.entrySet());

        String palabraMasUsada = "";
        int numVecesPalabraMasUsada = 0;
        // Calcula la palabra mas usada.
        for (int i = 0; i < lista.size(); i++) {
            String palabra = (String) lista.get(i).getKey();
            AtomicInteger value = (AtomicInteger) lista.get(i).getValue();
            Integer numVeces = value.get();
            if (i == 0) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            } else if (numVecesPalabraMasUsada < numVeces) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            }
        }
        // Imprime resultado.
        System.out.print("( Palabra: '" + palabraMasUsada + "' " +
                "veces: " + numVecesPalabraMasUsada + " )");
    }

    // --------------------------------------------------------------------------
    static void imprimePalabraMasUsadaYVecesStream(
            Map<String, Long> cuentaPalabras) {
        ArrayList<Map.Entry> lista =
                new ArrayList<Map.Entry>(cuentaPalabras.entrySet());

        String palabraMasUsada = "";
        long numVecesPalabraMasUsada = 0;
        // Calcula la palabra mas usada.
        for (int i = 0; i < lista.size(); i++) {
            String palabra = (String) lista.get(i).getKey();
            long numVeces = (Long) lista.get(i).getValue();
            if (i == 0) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            } else if (numVecesPalabraMasUsada < numVeces) {
                palabraMasUsada = palabra;
                numVecesPalabraMasUsada = numVeces;
            }
        }
        // Imprime resultado.
        System.out.print("( Palabra: '" + palabraMasUsada + "' " +
                "veces: " + numVecesPalabraMasUsada + " )");
    }

    // --------------------------------------------------------------------------
    static void printCuentaPalabrasOrdenadas(
            HashMap<String, Integer> cuentaPalabras) {
        int i, numVeces;
        List<Map.Entry> list = new ArrayList<Map.Entry>(cuentaPalabras.entrySet());

        // Ordena por valor.
        Collections.sort(
                list,
                new Comparator<Map.Entry>() {
                    public int compare(Map.Entry e1, Map.Entry e2) {
                        Integer i1 = (Integer) e1.getValue();
                        Integer i2 = (Integer) e2.getValue();
                        return i2.compareTo(i1);
                    }
                }
        );
        // Muestra contenido.
        i = 1;
        System.out.println("Veces Palabra");
        System.out.println("-----------------");
        for (Map.Entry e : list) {
            numVeces = ((Integer) e.getValue()).intValue();
            System.out.println(i + " " + e.getKey() + " " + numVeces);
            i++;
        }
        System.out.println("-----------------");
    }
}

//-------------------------------------------------------------------------------

class MiHebra_1 extends Thread {
    int numHebras;
    int miId;
    HashMap<String, Integer> maCuentaPalabras;
    ArrayList<String> arrayLineas;

    public MiHebra_1(int miId, int numhebras, ArrayList<String> arrayLineas, HashMap<String, Integer> maCuentaPalabras) {
        this.miId = miId;
        this.numHebras = numhebras;
        this.arrayLineas = arrayLineas;
        this.maCuentaPalabras = maCuentaPalabras;
    }

    public void run() {
        String palabraActual;
        for (int i = miId; i < arrayLineas.size(); i += numHebras) {
            // Procesa la linea "i".
            String[] palabras = arrayLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blancos.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabraSynchronized(maCuentaPalabras, palabraActual);
                }
            }
        }
    }
}

class MiHebra_2 extends Thread {
    int numHebras;
    int miId;
    Hashtable<String, Integer> htCuentaPalabras;
    ArrayList<String> arrayLineas;

    public MiHebra_2(int miId, int numhebras, ArrayList<String> arrayLineas, Hashtable<String, Integer> htCuentaPalabras) {
        this.miId = miId;
        this.numHebras = numhebras;
        this.arrayLineas = arrayLineas;
        this.htCuentaPalabras = htCuentaPalabras;
    }

    public void run() {
        String palabraActual;
        for (int i = miId; i < arrayLineas.size(); i += numHebras) {
            // Procesa la linea "i".
            String[] palabras = arrayLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blancos.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabraHashTable(htCuentaPalabras, palabraActual);
                }
            }
        }
    }
}

class MiHebra_3 extends Thread {
    int numHebras;
    int miId;
    ConcurrentHashMap<String, Integer> chmCuentaPalabras;
    ArrayList<String> arrayLineas;

    public MiHebra_3(int miId, int numhebras, ArrayList<String> arrayLineas, ConcurrentHashMap<String, Integer> chmCuentaPalabras) {
        this.miId = miId;
        this.numHebras = numhebras;
        this.arrayLineas = arrayLineas;
        this.chmCuentaPalabras = chmCuentaPalabras;
    }

    public void run() {
        String palabraActual;
        for (int i = miId; i < arrayLineas.size(); i += numHebras) {
            // Procesa la linea "i".
            String[] palabras = arrayLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blancos.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabraConcurrentHashMap(chmCuentaPalabras, palabraActual);
                }
            }
        }
    }
}

class MiHebra_4 extends Thread {
    int numHebras;
    int miId;
    ConcurrentHashMap<String, Integer> chmCuentaPalabras;
    ArrayList<String> arrayLineas;

    public MiHebra_4(int miId, int numhebras, ArrayList<String> arrayLineas, ConcurrentHashMap<String, Integer> chmCuentaPalabras) {
        this.miId = miId;
        this.numHebras = numhebras;
        this.arrayLineas = arrayLineas;
        this.chmCuentaPalabras = chmCuentaPalabras;
    }

    public void run() {
        String palabraActual;
        for (int i = miId; i < arrayLineas.size(); i += numHebras) {
            // Procesa la linea "i".
            String[] palabras = arrayLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blancos.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabraConcurrentHashMap2(chmCuentaPalabras, palabraActual);
                }
            }
        }
    }
}

class MiHebra_5 extends Thread {
    int numHebras;
    int miId;
    ConcurrentHashMap<String, AtomicInteger> chmAICuentaPalabras;
    ArrayList<String> arrayLineas;

    public MiHebra_5(int miId, int numhebras, ArrayList<String> arrayLineas, ConcurrentHashMap<String, AtomicInteger> chmAICuentaPalabras) {
        this.miId = miId;
        this.numHebras = numhebras;
        this.arrayLineas = arrayLineas;
        this.chmAICuentaPalabras = chmAICuentaPalabras;
    }

    public void run() {
        String palabraActual;
        for (int i = miId; i < arrayLineas.size(); i += numHebras) {
            // Procesa la linea "i".
            String[] palabras = arrayLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blancos.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabraConcurrentHashMapAtomic(chmAICuentaPalabras, palabraActual);
                }
            }
        }
    }
}

class MiHebra_6 extends Thread {
    int numHebras;
    int miId;
    ConcurrentHashMap<String, AtomicInteger> chmAI2CuentaPalabras;
    ArrayList<String> arrayLineas;

    public MiHebra_6(int miId, int numhebras, ArrayList<String> arrayLineas, ConcurrentHashMap<String, AtomicInteger> chmAI2CuentaPalabras) {
        this.miId = miId;
        this.numHebras = numhebras;
        this.arrayLineas = arrayLineas;
        this.chmAI2CuentaPalabras = chmAI2CuentaPalabras;
    }

    public void run() {
        String palabraActual;
        for (int i = miId; i < arrayLineas.size(); i += numHebras) {
            // Procesa la linea "i".
            String[] palabras = arrayLineas.get(i).split("\\W+");
            for (int j = 0; j < palabras.length; j++) {
                // Procesa cada palabra de la linea "i", si es distinta de blancos.
                palabraActual = palabras[j].trim();
                if (palabraActual.length() > 0) {
                    contabilizaPalabraConcurrentHashMapAtomic(chmAI2CuentaPalabras, palabraActual);
                }
            }
        }
    }
}
