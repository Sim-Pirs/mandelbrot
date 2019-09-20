import java.awt.Color;

public class MandelThread extends Thread{

    final static int taille = 500 ;   // nombre de pixels par ligne et par colonne
    final static Picture image = new Picture(taille, taille) ;
    // Il y a donc taille*taille pixels blancs ou gris à déterminer
    final static int max = 100_000 ;
    // C'est le nombre maximum d'itérations pour déterminer la couleur d'un pixel

    private int numThread;
    private double debutT;
    private double finT;

    public MandelThread(int numThread) {
        this.numThread = numThread;
    }

    public void run(){
        debutT  = System.nanoTime();
        for (int i = numThread * taille/4; i < (numThread+1) * taille/4; i++) {
            for (int j = 0; j < taille; j++) {
                colorierPixel(j,i) ;
            }
            //image.show();         // Pour visualiser l'évolution de l'image
        }
        finT  = System.nanoTime();
        System.out.println("Je suis le thread n° " + numThread + ": " + (finT - debutT)/1_000_000_000 + " s");
    }

    public static void main(String[] args) throws Exception {
        final long début = System.nanoTime() ;

        MandelThread[] threads = new MandelThread[4];
        for(int i=0; i < 4; i++) {
            threads[i] = new MandelThread(i);
            threads[i].start();
        }

        for(int i =0; i<4; i++){
            threads[i].join();
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
