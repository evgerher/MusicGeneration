import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */
public class PSO {
    private Random random;
    double values[];
    private final int ChordNotesAmount = 3;
    private final int ChordsAmount = 16;


    PSO(double values[]) {
        this.values = values;
        random = new Random();
    }

    public Chord[] generateChords() {
        ChordSequence chSeq = new ChordSequence(values);

        ChordSequence.Particle bestChordBases = chSeq.generateBaseOfChords();

        ChordOptimization chOpt = new ChordOptimization(bestChordBases.notes);

        Chord chords[] = chOpt.generateChords();

        return chords;
    }

    public class ChordSequence {
        private final double values[];
        private final double c1 = 1;
        private final double c2 = 1;
        private final double m = 1;
        private final int Population = 30;
        private final int Iterations = 10;
        private final double MAGIC_BLOCK = 0.5;

        private double globalBest[] = new double[ChordsAmount];
        private double globalFitness = 100500;

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
            Particle particles[] = new Particle[Population];
            for (int i = 0; i < Population; i++)
                particles[i] = new Particle(ChordsAmount);
            return particles;
        }

        public Particle generateBaseOfChords() {
            Particle particles[] = generateParticles();

            optimize(particles);

            int index = 0;
            for (int i = 1; i < Population; i++)
                if (particles[i].fitness < particles[index].fitness)
                    index = i;

            Particle p = particles[index];
            for (int i = 0; i < ChordsAmount; i++)
                p.notes[i] = Math.round(66 + p.notes[i] * 6);

            return p;
        }

        private void optimize(Particle[] particles) {
            for (int iteration = 0; iteration < Iterations && globalFitness > MAGIC_BLOCK; iteration++) {
                for (Particle p : particles) {
                    double fitness = fitnessFunction(p);
                    if (fitness < p.fitness) {
                        p.fitness = fitness;
                        for (int i = 0; i < ChordsAmount; i++)
                            p.myBest[i] = p.notes[i];
                    }

                    if (fitness < globalFitness) {
                        globalFitness = fitness;
                        for (int i = 0; i < ChordsAmount; i++)
                            globalBest[i] = p.notes[i];
                    }
                }

                for (Particle p : particles) {
                    int rand1 = random.nextInt(2);
                    int rand2 = random.nextInt(2);

                    for (int i = 0; i < ChordsAmount; i++)
                        p.velocity[i] = m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (globalBest[i] - p.notes[i]);

                }
            }
        }

        private double fitnessFunction(Particle particle) {
            double value = 0;
            for (int i = 0; i < ChordsAmount; i++) {
                double error = particle.notes[i] - values[i];
                value += Math.pow(error, 2);
            }

            return value;
        }

    }

    private class ChordOptimization {
        private double basements[];
        private double globalBest[] = new double[ChordNotesAmount];
        private double globalFitness = 100500;

        private final double MAGIC_BLOCK = 4.0;
        private final double c1 = 1;
        private final double c2 = 1;
        private final double m = 1;
        private final int Population = 15;
        private final int Iterations = 10;

        private class Particle {
            public double notes[];
            public double myBest[];
            public double velocity[];
            public double fitness;

            Particle(double basement) {
                notes = new double[ChordNotesAmount];
                myBest = new double[ChordNotesAmount];
                velocity = new double[ChordNotesAmount];

                notes[0] = basement;
                notes[1] = random.nextInt(13) + 60;
                notes[2] = random.nextInt(13) + 60;
            }
        }

        ChordOptimization(double basements[]) {
            this.basements = basements;
        }

        private double fitnessFunction(Particle p) {
            double diff1 = Math.abs(p.notes[1] - p.notes[0]);
            double diff2 = Math.abs(p.notes[2] - p.notes[0]);

            return diff1 + diff2;
        }

        public Chord[] generateChords() {
            Chord chords[] = new Chord[ChordsAmount];
            for (int index = 0; index < basements.length; index++) {
                Particle particles[] = new Particle[Population];
                for (int i = 0; i < Population; i++)
                    particles[i] = new Particle(basements[index]);

                optimize(particles);

                int best = 0;
                for (int i = 1; i < particles.length; i++)
                    if (particles[i].fitness < particles[best].fitness)
                        best = i;

                Particle bestParticle = particles[best];
                int notes[] = new int[ChordNotesAmount];
                for (int k = 0; k < ChordNotesAmount; k++)
                    notes[k] = (int)Math.round(bestParticle.notes[k]);

                chords[index] = new Chord(notes);
            }

            return chords;
        }

        private void optimize(Particle particles[]) {
            for (int iteration = 0; iteration < Iterations && globalFitness > MAGIC_BLOCK; iteration++) {
                for (Particle p: particles) {
                    double fitness = fitnessFunction(p);
                    if (fitness < p.fitness) {
                        p.fitness = fitness;
                        for (int i = 0; i < ChordNotesAmount; i++)
                            p.myBest[i] = p.notes[i];
                    }

                    if (fitness < globalFitness) {
                        globalFitness = fitness;
                        for (int i = 0; i < ChordNotesAmount; i++)
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
