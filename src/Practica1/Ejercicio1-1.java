package Practica1;

class MiHebra extends Thread{
    int miId;
    public MiHebra(int miId){
        this.miId = miId;
    }
    public void run(){
        for(int i = 0; i<1000; i++)
            System.out.println(miId);
    }
}

class EjemploCreacion4a{
    public static void main( String args[]){
        new MiHebra(0).start();
        new MiHebra(1).start();
    }
}
