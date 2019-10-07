import java.awt.*;

public class Dynamique250_000Threads extends Thread {

    final static int taille = 500;   // nombre de pixels par ligne et par colonne
    final static Picture image = new Picture(taille, taille);
    // Il y a donc taille*taille pixels blancs ou gris à déterminer
    final static int max = 100_000;
    // C'est le nombre maximum d'itérations pour déterminer la couleur d'un pixel

    private int i;
    private int j;

    private double debutT;
    private double finT;

    static final Object verrou = new Object();

    static volatile double debutProgramme;
    static volatile double tempsCourant;
    static volatile int numeroImage = 0;

    static boolean creationImage = false;

    public Dynamique250_000Threads(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public void run() {
        debutT = System.nanoTime();
        colorierPixel(j, i);
        synchronized (verrou) {
            //image.show();
            tempsCourant = System.nanoTime();
            if ((tempsCourant - debutProgramme) / 1_000_000 >= numeroImage * 100 && numeroImage < 1000 && creationImage) {
                if (numeroImage < 10)
                    image.save("mandelbrot00" + numeroImage + ".png");
                else if (numeroImage < 100)
                    image.save("mandelbrot0" + numeroImage + ".png");
                else
                    image.save("mandelbrot" + numeroImage + ".png");
                numeroImage++;
            }
        }

        finT =System.nanoTime();
        //System.out.println("Je suis le thread n° " + numThread + ": " + (finT - debutT)/1_000_000_000 + " s");
    }

    public static void main(String[] args) throws Exception {
        final long début = System.nanoTime() ;
        int nbThread = 250_000;

        debutProgramme = System.nanoTime() ;

        Dynamique250_000Threads[] threads = new Dynamique250_000Threads[nbThread];
        for(int i=0; i < taille; i++) {
            for(int j= 0; j < taille; j++) {
                threads[i] = new Dynamique250_000Threads(i, j);
                threads[i].start();
            }
        }

        for(int i=0; i < taille; i++) {
            for(int j= 0; j < taille; j++) {
                threads[i].join();
            }
        }

        final long fin = System.nanoTime() ;
        final long durée = (fin - début) / 1_000_000 ;
        System.out.println("Durée = " + (double) durée / 1000 + " s.") ;
        image.show() ;
    }

    // La fonction colorierPixel(i,j) colorie le pixel (i,j) de l'image en gris ou blanc
    public static void colorierPixel(int i, int j) {
        final Color gris = new Color(90, 90, 90) ;
        final Color blanc = new Color(255, 255, 255) ;
        final double xc = -.5 ;
        final double yc = 0 ; // Le point (xc,yc) est le centre de l'image
        final double region = 2 ;
        /*
          La région du plan considérée est un carré de côté égal à 2.
          Elle s'étend donc du point (xc - 1, yc - 1) au point (xc + 1, yc + 1)
          c'est-à-dire du point (-1.5, -1) en bas à gauche au point (0.5, 1) en haut
          à droite
        */
        double a = xc - region/2 + region*i/taille ;
        double b = yc - region/2 + region*j/taille ;
        // Le pixel (i,j) correspond au point (a,b)
        if (mandelbrot(a, b, max)) image.set(i, j, gris) ;
        else image.set(i, j, blanc) ;
    }

    // La fonction mandelbrot(a, b, max) détermine si le point (a,b) est gris
    public static boolean mandelbrot(double a, double b, int max) {
        double x = 0 ;
        double y = 0 ;
        for (int t = 0; t < max; t++) {
            if (x*x + y*y > 4.0) return false ; // Le point (a,b) est blanc
            double nx = x*x - y*y + a ;
            double ny = 2*x*y + b ;
            x = nx ;
            y = ny ;
        }
        return true ; // Le point (a,b) est gris
    }

}
