import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */
public class PSO {
    private Random random;
    private double values[];
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

    public PairNote[] generatePairNotes(Chord chords[]) {
        PairNoteOptimization pso = new PairNoteOptimization(chords);
        PairNote notes[] = pso.generatePairNotes();

        return notes;
    }

    public class ChordSequence {
        private final double values[];
        private final double c1 = 1.035;
        private final double c2 = 1.035;
        private final double m = 0.65;
        private final int Population = 25;
        private final int Iterations = 250;
        private final double MAGIC_BLOCK = 0.92;

        private double globalBest[] = new double[ChordsAmount];
        private double globalFitness = Double.MAX_VALUE;

//        private double possibleValues[] = new double[]{60, 62, 64, 65, 67, 69, 71, 72};
        private double possibleValues[] = new double[]{60, 64, 65};

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
                fitness = Double.MAX_VALUE;
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
            for (int i = 0; i < ChordsAmount; i++) {
                p.notes[i] = Math.round(63 + p.notes[i] * 4);
                p.notes[i] = closestValue(p.notes[i]);
            }
//            System.out.printf("\n\nBasement of chord [");
//            for (int i = 0; i < 15; i++)
//                System.out.printf("%d, ", (int)Math.round(p.notes[i]));
//            System.out.printf("%d]\n\n", (int)Math.round(p.notes[15]));

            return p;
        }

        private double closestValue(double value) {
            int bestIndex = 0;
            double bestDifference = Double.MAX_VALUE;
            for (int i = 0; i < possibleValues.length; i++) {
                if (Math.abs(possibleValues[i] - value) < bestDifference) {
                    bestDifference = Math.abs(possibleValues[i] - value);
                    bestIndex = i;
                }
            }

            return possibleValues[bestIndex];
        }

        private void optimize(Particle[] particles) {
            int iteration;
            for (iteration = 0; iteration < Iterations && globalFitness > MAGIC_BLOCK; iteration++) {
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

                    for (int i = 0; i < ChordsAmount; i++) {
                        p.velocity[i] = m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (globalBest[i] - p.notes[i]);
                        p.notes[i] = p.notes[i] + p.velocity[i];
                    }
                }
            }
//            System.out.println("Iteration = " + Integer.toString(iteration));
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
        private double globalFitness = Double.MAX_VALUE;

        private int secondNoteVector;
        private int thirdNoteVector;

        private final double MAGIC_BLOCK = 0.3;
        private final double c1 = 1.13;
        private final double c2 = 1.18;
        private final double m = 0.75;
        private final int Population = 20;
        private final int Iterations = 300;

        private class Particle {
            public double notes[];
            public double myBest[];
            public double velocity[];
            public boolean blocked[];
            public double fitness;

            Particle() {
                notes = new double[ChordNotesAmount];
                myBest = new double[ChordNotesAmount];
                velocity = new double[ChordNotesAmount];
                blocked = new boolean[ChordNotesAmount];

                notes[0] = 0;
                notes[1] = random.nextDouble() * 12;
                notes[2] = random.nextDouble() * 12;
                fitness = Double.MAX_VALUE;
            }
        }

        ChordOptimization(double basements[]) {
            this.basements = basements;
        }

        private double fitnessFunction(Particle p) {
            double diff1 = Math.abs(p.notes[1] - secondNoteVector);
            double diff2 = Math.abs(p.notes[2] - thirdNoteVector);
            if (diff1 < 1 && diff1 > -1)
                p.blocked[1] = true;
            if (diff2 < 1 && diff2 > -1)
                p.blocked[2] = true;

            return diff1 + diff2;
        }

        public Chord[] generateChords() {
            Chord chords[] = new Chord[ChordsAmount];
            for (int index = 0; index < basements.length; index++) {
                Particle particles[] = new Particle[Population];
                for (int i = 0; i < Population; i++)
                    particles[i] = new Particle();

                setUpFitnessFunction((int)basements[index]);
                optimize(particles);

                int best = 0;
                for (int i = 1; i < particles.length; i++)
                    if (particles[i].fitness < particles[best].fitness)
                        best = i;

                Particle bestParticle = particles[best];
                int notes[] = new int[ChordNotesAmount];
                notes[0] = (int)Math.round(basements[index]);
                for (int k = 1; k < ChordNotesAmount; k++)
                    notes[k] = (int)Math.round(bestParticle.notes[k] + notes[0]);

//                System.out.printf("Best chord :: [%d %d %d]\n", notes[0], notes[1], notes[2]);
                chords[index] = new Chord(notes);
            }

//            for (int i = 0; i < chords.length; i++) {
//                System.out.printf("[ ");
//                for(int j = 0; j < chords[0].notes.length; j++) {
//                    System.out.printf("%d ", chords[i].notes[j]);
//                }
//                System.out.printf("]\n");
//            }
            return chords;
        }

        private void setUpFitnessFunction(int value) {
//            if (value % 12 <= 3) {
//                secondNoteVector = 3;
//                thirdNoteVector = 7;
//            } else if (value % 12 >= 7) {
//                secondNoteVector = -7;
//                thirdNoteVector = -3;
//            } else if (value % 12 >= 4) {
//                secondNoteVector = -3;
//                thirdNoteVector = 4;
//            }
            secondNoteVector = 4;
            thirdNoteVector = 7;
        }

        private void optimize(Particle particles[]) {
            int iteration;
            globalFitness = Double.MAX_VALUE;
            for (iteration = 0; iteration < Iterations && globalFitness > MAGIC_BLOCK; iteration++) {
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

                    for (int i = 0; i < p.velocity.length; i++) {
                        if (!p.blocked[i]) {
                            p.velocity[i] = m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (globalBest[i] - p.notes[i]);
                            p.notes[i] = p.notes[i] + p.velocity[i];
                        }
                    }

                }
            }
//            System.out.println("BestFitness :: " + Double.toString(globalFitness));
//            System.out.println("ChordOptEndIteration :: " + Integer.toString(iteration));
        }
    }

    public class PairNoteOptimization {
        private Chord chords[];
        private double globalBest[] = new double[2];
        private double globalFitness = Double.MAX_VALUE;

        private final double MAGIC_BLOCK = 0.5;
        private final double c1 = 1.05;
        private final double c2 = 1.05;
        private final double m = 0.67;
        private final int Population = 15;
        private final int Iterations = 50;

        private class Particle {
            public int chordNotes[];
            public double notes[];
            public double myBest[];
            public double velocity[];
            public double fitness;

            Particle(Chord chord) {
                chordNotes = chord.notes;
                notes = new double[2];
                myBest = new double[2];
                velocity = new double[2];
                fitness = Double.MAX_VALUE;

                notes[0] = random.nextDouble() * 12 + 72;
                notes[1] = random.nextDouble() * 12 + 72;
            }
        }

        PairNoteOptimization(Chord chords[]) {
            this.chords = chords;
        }

        private double fitnessFunction(Particle p) {
            double diff1 = Math.abs((p.chordNotes[0] + 12) - p.notes[0]);
            double diff2 = Math.abs((p.chordNotes[1] + 12) - p.notes[0]);
            double diff3 = Math.abs((p.chordNotes[2] + 12) - p.notes[0]);
            double diff_min = Math.min(diff1, diff2);

            double secondNoteDiff = Math.abs(p.notes[0] - p.notes[1]);
            diff_min = Math.min(diff_min, diff3);
            if (secondNoteDiff >= 4)
                diff_min += secondNoteDiff;

            return diff_min;
        }

        public PairNote[] generatePairNotes() {
            PairNote pairNotes[] = new PairNote[ChordsAmount];
            for (int index = 0; index < chords.length; index++) {
                Particle particles[] = new Particle[Population];
                for (int i = 0; i < Population; i++)
                    particles[i] = new Particle(chords[index]);

                optimize(particles);

//                int best = 0;
//                for (int i = 1; i < particles.length; i++)
//                    if (particles[i].fitness < particles[best].fitness)
//                        best = i;

//                Particle bestParticle = particles[best];
                int notes[] = new int[2];
                notes[0] = (int)globalBest[0];
                notes[1] = (int)globalBest[1];
//                for (int k = 0; k < 2; k++)
//                    notes[k] = (int)Math.round(bestParticle.notes[k]);

                pairNotes[index] = new PairNote(notes);
            }

            return pairNotes;
        }

        private void optimize(Particle particles[]) {
            int iteration;
            globalFitness = Double.MAX_VALUE;
            for (iteration = 0; iteration < Iterations && globalFitness > MAGIC_BLOCK; iteration++) {
                for (Particle p: particles) {
                    double fitness = fitnessFunction(p);
                    if (fitness < p.fitness) {
                        p.fitness = fitness;
                        for (int i = 0; i < 2; i++)
                            p.myBest[i] = p.notes[i];
                    }

                    if (fitness < globalFitness) {
                        globalFitness = fitness;
                        for (int i = 0; i < 2; i++)
                            globalBest[i] = p.notes[i];
                    }
                }

                for (Particle p : particles) {
                    int rand1 = random.nextInt(2);
                    int rand2 = random.nextInt(2);

                    for (int i = 0; i < p.velocity.length; i++) {
                        p.velocity[i] = m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (globalBest[i] - p.notes[i]);
                        p.notes[i] = p.notes[i] + p.velocity[i];
                    }
                }
            }

            System.out.printf("Amount of iterations :: PairNote :: %d\n", iteration);
            System.out.printf("Global fitness :: PairNote :: %.2f\n", globalFitness);
        }
    }
}
