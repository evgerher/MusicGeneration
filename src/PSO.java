import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */
public class PSO {
    private static Random random;
    {
        random = new Random();
    }

    public static class ChordOptimization {
        private static double values[];
        private static double c1;
        private static double c2;
        private static double m;
        private static int bestTonics[] = {60, 64, 65};
        final int population = 30;

        ChordOptimization(double values[]) {
            this.values = values;
        }

        private class Particle {
            public double notes[];
            public double myBest[];
            public double velocity[];
            public double fitness;

            public final double globalBest[] = {60, 60, 60};
            public static double globalFitness = 0;

            Particle(int notesAmount) {
                notes = new double[notesAmount];
                myBest = new double[notesAmount];
                velocity = new double[notesAmount];
                for (int i = 0; i < notesAmount; i++) {
                    notes[i] = random.nextDouble() * 2 - 1;
                    myBest[i] = notes[i];
                    velocity[i] = 0;
                }
            }
        }

        private Particle[] generateParticles() {

            Particle particles[] = new Particle[population];
            for (int i = 0; i < population; i++)
                particles[i] = new Particle(16);
            return particles;
        }

        public Particle generateChords() {
            Particle particles[] = generateParticles();

            optimize(particles);


            Particle particle = new Particle(3);
            return particle;
        }

        private void optimize(Particle[] particles) {
            for (Particle p : particles) {
                double fitness = fitnessFunction(p);
                if (fitness < p.fitness) {
                    p.fitness = fitness;
                    for (int i = 0; i < 3; i++)
                        p.myBest[i] = p.notes[i];
                }

                if (fitness < p.globalFitness) {
                    p.globalFitness = fitness;
                    for (int i = 0; i < 3; i++)
                        p.globalBest[i] = p.notes[i];
                }
            }

            for (Particle p : particles) {
                int rand1 = random.nextInt(1);
                int rand2 = random.nextInt(1);

                for (int i = 0; i < p.velocity.length; i++)
                    p.velocity[i] = (int)(m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (p.globalBest[i] - p.notes[i]));

            }
        }

        private double fitnessFunction(Particle particle) {
            double value = 0;
            for (int i = 0; i < population; i++) {
                double error = particle.notes[i] - values[i];
                value += Math.pow(error, 2);
            }

            return value;
        }

    }

    public static class BarOptimization {

    }

    public static class PairNoteOptimization {

    }
}
