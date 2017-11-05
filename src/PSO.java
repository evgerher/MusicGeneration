import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */
public class PSO {
    private Random random;
    double values[];
    public double globalBest[] = {60, 60, 60};
    public double globalFitness = 0;

    PSO(double values[]) {
        this.values = values;
        random = new Random();
    }

    public Chord[] generateChords() {
        ChordSequence chSeq = new ChordSequence(values);
        ChordOptimization chOpt = new ChordOptimization();

        ChordSequence.Particle bestChordBases = chSeq.generateBaseOfChords();

        return new Chord[1];
    }

    public class ChordSequence {
        private double values[];
        private double c1;
        private double c2;
        private double m;
        private int bestTonics[] = {60, 64, 65};
        final int population = 30;

        ChordSequence(double values[]) {
            this.values = values;
        }

        private class Particle {
            public double notes[];
            public double myBest[];
            public double velocity[];
            public double fitness;

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

        public Particle generateBaseOfChords() {
            Particle particles[] = generateParticles();

            optimize(particles);

            int index = 0;
            for (int i = 1; i < particles.length; i++)
                if (particles[i].fitness < particles[index].fitness)
                    index = i;

            return particles[index];
        }

        private void optimize(Particle[] particles) {
            for (Particle p : particles) {
                double fitness = fitnessFunction(p);
                if (fitness < p.fitness) {
                    p.fitness = fitness;
                    for (int i = 0; i < 16; i++)
                        p.myBest[i] = p.notes[i];
                }

                if (fitness < globalFitness) {
                    globalFitness = fitness;
                    for (int i = 0; i < 16; i++)
                        globalBest[i] = p.notes[i];
                }
            }

            for (Particle p : particles) {
                int rand1 = random.nextInt(2);
                int rand2 = random.nextInt(2);

                for (int i = 0; i < p.velocity.length; i++)
                    p.velocity[i] = m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (globalBest[i] - p.notes[i]);

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

    public class ChordOptimization {

    }

    public class PairNoteOptimization {

    }
}
