import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */
public class PSO {
    private static Random random;
    {
        random = new Random();
    }

    private static class Particle {
        public int notes[];
        public int myBest[];
        public int velocity[];
        public static int globalBest[] = {60, 60, 60};
        public static int globalFitness = 0;
        public int fitness;

        Particle(int notesAmount) {
            notes = new int[notesAmount];
            myBest = new int[notesAmount];
            velocity = new int[notesAmount];
            for (int i = 0; i < notesAmount; i++) {
                notes[i] = random.nextInt(12) + 60;
                myBest[i] = notes[i];
                velocity[i] = 0;
            }
        }
    }

    public static class ChordOptimization {

        private static double c1;
        private static double c2;
        private static double m;
        private static int bestTonics[] = {60, 64, 65};


        private static Particle[] generateParticles() {
            Particle particles[] = new Particle[50];
            for (int i = 0; i < 50; i++)
                particles[i] = new Particle(3);
            return particles;
        }

        public static Chord[] generateChords() {
            Particle particles[] = generateParticles();

            optimize(particles);


            Chord chords[] = new Chord[1];
            return chords;
        }

        private static void optimize(Particle[] particles) {
            for (Particle p : particles) {
                int fitness = fitnessFunction(p);
                if (fitness > p.fitness) {
                    p.fitness = fitness;
                    for (int i = 0; i < 3; i++)
                        p.myBest[i] = p.notes[i];
                }
            }

            chooseBestParticle();

            for (Particle p : particles) {
                int rand1 = random.nextInt(1);
                int rand2 = random.nextInt(1);

                for (int i = 0; i < p.velocity.length; i++)
                    p.velocity[i] = (int)(m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (p.globalBest[i] - p.notes[i]));

            }
        }

        private static int fitnessFunction(Particle particle) {
            int value = 50;
            int notes[] = particle.notes;
            int tonicDiff = minTonicDiff(notes[0]);
            int dominantDiff = Math.abs(notes[1] - (notes[0] + 4));
            int subdominantDiff = Math.abs(notes[2] - (notes[0] + 7));

            return value - tonicDiff * 2 - dominantDiff - subdominantDiff;
        }

        private static int minTonicDiff(int tonic) {
            int min = 100;
            for (int i = 0; i < bestTonics.length; i++)
                if (Math.abs(bestTonics[i] - tonic) < min)
                    min = Math.abs(bestTonics[i] - tonic);

            return min;
        }

    }

    public static class BarOptimization {

    }

    public static class PairNoteOptimization {

    }
}
