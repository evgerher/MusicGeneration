import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */
public class PSO {
    private Random random;
    double values[];


    PSO(double values[]) {
        this.values = values;
        random = new Random();
    }

    public Chord[] generateChords() {
        ChordSequence chSeq = new ChordSequence(values);

        ChordSequence.Particle bestChordBases = chSeq.generateBaseOfChords();
        ChordOptimization chOpt = new ChordOptimization(bestChordBases.notes);

        return new Chord[1];
    }

    public class ChordSequence {
        private final double values[];
        private final double c1 = 1;
        private final double c2 = 1;
        private final double m = 1;
        private final int Population = 30;
        private final int Iterations = 10;
        public double globalBest[] = {60, 60, 60};
        public double globalFitness = 100500;

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

            Particle particles[] = new Particle[Iterations];
            for (int i = 0; i < Iterations; i++)
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

            Particle p = particles[index];
            for (int i = 0; i < p.notes.length; i++)
                p.notes[i] = Math.round(66 + p.notes[i] * 6);

            return p;
        }

        private void optimize(Particle[] particles) {
            for (int iteration = 0; iteration < Iterations && globalFitness > 0.5; iteration++) {
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
        }

        private double fitnessFunction(Particle particle) {
            double value = 0;
            for (int i = 0; i < Population; i++) {
                double error = particle.notes[i] - values[i];
                value += Math.pow(error, 2);
            }

            return value;
        }

    }

    private class ChordOptimization {
        private double basements[];
        public double globalBest[] = {60, 60, 60};
        public double globalFitness = 100500;

        private final double c1 = 1;
        private final double c2 = 1;
        private final double m = 1;
        private final int Population = 30;
        private final int Iterations = 10;

        private class Particle {
            public double notes[];
            public double myBest[];
            public double velocity[];
            public double fitness;

            Particle(double basement) {
                notes[0] = basement;
                notes[1] = random.nextInt(13) + 60;
                notes[2] = random.nextInt(13) + 60;
            }
        }

        ChordOptimization(double basements[]) {
            this.basements = basements;
        }

        double fitnessFunction(Particle p) {
            double diff1 = Math.abs(p.notes[1] - p.notes[0]);
            double diff2 = Math.abs(p.notes[2] - p.notes[0]);

            return diff1 + diff2;
        }

        public Chord[] generateChords() {
            Chord chords[] = new Chord[16];
            for (int index = 0; index < basements.length; index++) {
                Particle particles[] = new Particle[15];
                for (Particle p: particles)
                    p = new Particle(basements[index]);

                optimize(particles);

                int best = 0;
                for (int i = 1; i < particles.length; i++)
                    if (particles[i].fitness < particles[best].fitness)
                        best = i;

                Particle bestParticle = particles[best];
                int notes[] = new int[3];
                for (int k = 0; k < 3; k++)
                    notes[k] = (int)Math.round(bestParticle.notes[k]);

                chords[index] = new Chord(notes);
            }

            return chords;
        }

        private void optimize(Particle particles[]) {
            for (int iteration = 0; iteration < Iterations && globalFitness > 1; iteration++) {
                for (Particle p: particles) {
                    double fitness = fitnessFunction(p);
                    if (fitness < p.fitness) {
                        p.fitness = fitness;
                        for (int i = 0; i < 3; i++)
                            p.myBest[i] = p.notes[i];
                    }

                    if (fitness < globalFitness) {
                        globalFitness = fitness;
                        for (int i = 0; i < 3; i++)
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
        }
    }

    public class PairNoteOptimization {

    }
}
