import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ThreadLocalRandom;
/*
  A random number generator isolated to the current thread. Like the
  global Random generator used by the Math class, a ThreadLocalRandom
  is initialized with an internally generated seed that may not
  otherwise be modified. When applicable, use of ThreadLocalRandom
  rather than shared Random objects in concurrent programs will
  typically encounter much less overhead and contention. Use of
  ThreadLocalRandom is particularly appropriate when multiple tasks
  (for example, each a ForkJoinTask) use random numbers in parallel
  in thread pools.
*/

public class ThreadPool {
    static int nbThreads = Runtime.getRuntime().availableProcessors();
    final static int taille = 500;   // nombre de pixels par ligne et par colonne
    final static Picture image = new Picture(taille, taille);
    final static int max = 100_000;
    // C'est le nombre maximum d'itérations pour déterminer la couleur d'un pixel

    public static class TraceLigne implements Runnable {
        int numeroLigne;

        public TraceLigne(int numeroLigne) {
            this.numeroLigne = numeroLigne;
        }

        @Override
        public void run() {
            for (int i = 0; i < taille; i++) {
                colorierPixel(i, numeroLigne);
            }
        }
    }

    public static void main(String[] args) {

        // Création du réservoir formé de nbThreads esclaves
        ExecutorService executeur = Executors.newFixedThreadPool(nbThreads);

        // Remplissage de la liste des tâches
        // Tirages[] mesTaches = new Tirages[nbThreads] ;
        for (int j = 0; j < taille; j++) {
            TraceLigne traceLigne = new TraceLigne(j);
            executeur.execute(traceLigne);
        }
        executeur.shutdown(); // Il n'y a plus aucune tâche à soumettre
        // Il faut maintenant attendre la fin des calculs
        try {
            while (!executeur.awaitTermination(1, TimeUnit.SECONDS)) {
                //image.show();
                System.out.print("#");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        image.show();

    }

    // La fonction colorierPixel(i,j) colorie le pixel (i,j) de l'image en gris ou blanc
    public static void colorierPixel(int i, int j) {
        final Color gris = new Color(90, 90, 90);
        final Color blanc = new Color(255, 255, 255);
        final double xc = -.5;
        final double yc = 0; // Le point (xc,yc) est le centre de l'image
        final double region = 2;
        /*
          La région du plan considérée est un carré de côté égal à 2.
          Elle s'étend donc du point (xc - 1, yc - 1) au point (xc + 1, yc + 1)
          c'est-à-dire du point (-1.5, -1) en bas à gauche au point (0.5, 1) en haut
          à droite
        */
        double a = xc - region / 2 + region * i / taille;
        double b = yc - region / 2 + region * j / taille;
        // Le pixel (i,j) correspond au point (a,b)
        if (mandelbrot(a, b, max)) image.set(i, j, gris);
        else image.set(i, j, blanc);
    }

    // La fonction mandelbrot(a, b, max) détermine si le point (a,b) est gris
    public static boolean mandelbrot(double a, double b, int max) {
        double x = 0;
        double y = 0;
        for (int t = 0; t < max; t++) {
            if (x * x + y * y > 4.0) return false; // Le point (a,b) est blanc
            double nx = x * x - y * y + a;
            double ny = 2 * x * y + b;
            x = nx;
            y = ny;
        }
        return true; // Le point (a,b) est gris
    }
}
