import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */

/**
 * Class PSO - class that generates the sequence of chords and pair notes
 * Class consists of 3 subclasses
 * 1) ChordSequence PSO
 * 2) ChordOptimize PSO
 * 3) PairNoteOptimization PSO
 * Each class contains own constant values for PSO, class Particle and Fitness-Function
 * More details will be inside each of them
 *
 * Constant values:
 * C_MAJOR - sequence of notes that are appliable for this gamma, it is possible to get other notes using OCTAVE_DIFFERENCE
 * OCTAVE_DIFFERENCE - difference between two octaves in midi.
 * POSSIBLE_CHORD_BASEMENTS - array of possible values to start a chord with.
 */
public class PSO {
    private Random random;
    private double values[];
    private final int ChordNotesAmount = 3;
    private final int ChordsAmount = 16;

    private final int C_MAJOR[] = new int[]{60, 62, 64, 65, 67, 69, 71, 72};
    private final int POSSIBLE_CHORD_BASEMENTS[] = new int[]{60, 64, 65};
    private final int OCTAVE_DIFFERENCE = 12;

    /**
     * Constructor of class PSO
     * Accepts an array of values in range [-1, 1]
     * Creates random variable that will be used in every subclass
     * @param values - array of values
     */
    PSO(double values[]) {
        this.values = values;
        random = new Random();
    }

    /**
     * Method for chord generation
     * Uses 2 PSOs
     * 1) ChordSequencce PSO - generates basement of chords :: [1st note of triad]
     * 2) ChordOptimization PSO - finishes construction of each chord :: [2nd and 3rd notes of triad]
     *
     * @return sequence of chords in form of array
     */
    public Chord[] generateChords() {
        ChordSequence chSeq = new ChordSequence(values);
        ChordSequence.Particle bestChordBases = chSeq.generateBaseOfChords();
        ChordOptimization chOpt = new ChordOptimization(bestChordBases.notes);
        Chord chords[] = chOpt.generateChords();

        return chords;
    }

    /**
     * Method for generating pair notes
     * Generates pair notes based on chords
     * @param chords - array of chords
     * @return array of pair notes
     */
    public PairNote[] generatePairNotes(Chord chords[]) {
        PairNoteOptimization pso = new PairNoteOptimization(chords);
        PairNote notes[] = pso.generatePairNotes();

        return notes;
    }

    /**
     * PSO for generation the sequence of chords
     *      CONST VALUES
     *      c1 = 1.035
     *      c2 = 1.035
     *      m = 0.65
     *      Population = 25
     *      Iterations = 250
     *      MAGIC_BLOCK = 0.92
     *
     * This PSO accepts array of double values and generates sequence of 16 values close to them
     * Approach used in this PSO is to get close to the `ideal` function, in this way we construct unique likely function that may sound great.
     * The fitness function is least square method
     *
     */
    class ChordSequence {
        final double values[];
        final double c1 = 1.035;
        final double c2 = 1.035;
        final double m = 0.65;
        final int Population = 25;
        final int Iterations = 250;
        final double MAGIC_BLOCK = 0.92;

        double globalBest[] = new double[ChordsAmount];
        double globalFitness = Double.MAX_VALUE;
        int possibleValues[] = POSSIBLE_CHORD_BASEMENTS;

        /**
         * Constructs object and accepts array of double values in range [-1, 1]
         * @param values - array of values
         */
        ChordSequence(double values[]) {
            this.values = values;
        }

        /**
         * Particle class!
         * Each particle has arrays of notesAmount(16) values, it means that each particle is 16 dots on a plane
         * Using knowledge of Linear Algebra we apply Least Square Method on this Particle and try to make him closer to the `ideal` functuin
         * Values of particle are generated in range [-1, 1]
         * Default velocity = 0
         * Default fitness = Infinity
         */
        class Particle {
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

        /**
         * Generates random sample of particles
         * @return array of particles
         */
        Particle[] generateParticles() {
            Particle particles[] = new Particle[Population];
            for (int i = 0; i < Population; i++)
                particles[i] = new Particle(ChordsAmount);
            return particles;
        }

        /**
         * Method for generating base of chords (1st note)
         * First of all it generates the best particle that is the closest to the function given higher
         * Method `optimize` transforms particles into something better
         * Later it scales its values into applicable range for MIDI
         *
         * Values 59 and 4 chosen not randomly
         * Zone of influence for value 60 is [59, 62]
         * Zone of influence for value 64 is [62, 64.5]
         * Zone of influence for value 65 is [64.5, 67]
         *
         * Zones are about to be the same, so, the probability of falling into one of the ranges is the same.
         * It is about [1/3 1/3 1/3]
         * @return
         */
        Particle generateBaseOfChords() {
            /* Generate new sample */
            Particle particles[] = generateParticles();

            /* Optimize sample according to given function */
            optimize(particles);
            /* Choose the best one */
            int index = 0;
            for (int i = 1; i < Population; i++)
                if (particles[i].fitness < particles[index].fitness)
                    index = i;

            /* Scale its values */
            Particle p = particles[index];
            for (int i = 0; i < ChordsAmount; i++) {
                p.notes[i] = Math.round(63 + p.notes[i] * 4);
                p.notes[i] = closestValue(p.notes[i], possibleValues, 0);
            }

            return p;
        }

        /**
         * Method optimize like everywhere uses GLOBAL BEST approach
         * Exist additional `stop` value in addition to iteration amount
         * When sample is already good enough, it stops
         * @param particles - particles to optimize
         */
        void optimize(Particle[] particles) {
            int iteration;

            /* Make until conditions satisfied */
            for (iteration = 0; iteration < Iterations && globalFitness > MAGIC_BLOCK; iteration++) {
                for (Particle p : particles) {
                    double fitness = fitnessFunction(p);
                    if (fitness < p.fitness) {
                        p.fitness = fitness;
                        for (int i = 0; i < ChordsAmount; i++)
                            p.myBest[i] = p.notes[i];
                    }

                    /* Finding best value */
                    if (fitness < globalFitness) {
                        globalFitness = fitness;
                        for (int i = 0; i < ChordsAmount; i++)
                            globalBest[i] = p.notes[i];
                    }
                }

                /* Update each particle */
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

        /**
         * Fitness function that represents Least Square Method
         * Least square method is easy to perform and it clearly determines closeness to the function
         * Especially that is why values in range [-1, 1] are used
         *
         * @param particle - particle to calculate Error
         * @return Error value :: less it is, better it is
         */
        double fitnessFunction(Particle particle) {
            double value = 0;
            for (int i = 0; i < ChordsAmount; i++) {
                double error = particle.notes[i] - values[i];
                value += Math.pow(error, 2);
            }

            return value;
        }

    }

    /**
     * PSO for optimizing given chord :: finishing its construction
     *      CONST VALUES :: described in doc
     *      c1 = 1.13
     *      c2 = 1.18
     *      m = 0.75
     *      Population = 20
     *      Iterations = 300
     *      MAGIC_BLOCK = 0.75
     *
     * This PSO accepts array of basements (double values) and finishes construction of chords
     * Approach used in this PSO is that chord [x x+4 x+7] is ideal.
     * The fitness function is vector difference
     *
     */
    class ChordOptimization {
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


        /**
         * Particle class!
         * Each particle has arrays of chordNotesAmount(3) values, it means that each particle is a Chord
         * We try to apply knowledge of Music Theory on this particles and make a vector of values to be as close as possible
         * to the vector [x x+4 x+7]
         *
         * Values of particle are generated in range [0, 12]
         * Default velocity = 0
         * Default fitness = Infinity
         * Default blocked = {0}
         */
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

        /**
         * Constructor, applies array of basements for finishing construction
         * @param basements - array of values
         */
        ChordOptimization(double basements[]) {
            this.basements = basements;
        }

        /**
         * Fitness function for finishing construction of chords
         * Blocks values of notes if they are alredy close enough
         *
         * @param p - particle to calculate value of
         * @return value of this particle
         */
        private double fitnessFunction(Particle p) {
            double diff1 = Math.abs(p.notes[1] - secondNoteVector);
            double diff2 = Math.abs(p.notes[2] - thirdNoteVector);
            if (diff1 < 1 && diff1 > -1)
                p.blocked[1] = true;
            if (diff2 < 1 && diff2 > -1)
                p.blocked[2] = true;

            return diff1 + diff2;
        }

        /**
         * Method that combines ChordSequence and ChordOptimization classes
         * Using values from ChordSequence finishes construction of chord by methods of ChordOptimization
         *
         * Method optimizes each chord
         * Creates array of particles
         * Sets up fitness function according to given Chord
         * Performs optimization and finds best
         * Stores chord
         *
         * @return array of Chords
         */
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

                chords[index] = new Chord(notes);
            }
            return chords;
        }

        /**
         * Sets up values for vector of chord
         * Was different when there were other possible chords
         * @param value - value of note (used previously)
         */
        private void setUpFitnessFunction(int value) {
            secondNoteVector = 4;
            thirdNoteVector = 7;
        }

        /**
         * Method optimize like everywhere uses GLOBAL BEST approach
         * Exist additional `stop` value in addition to iteration amount
         * When sample is already good enough, it stops
         * @param particles - particles to optimize
         */
        void optimize(Particle[] particles) {
            int iteration;

            /* Make until conditions satisfied */
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

                /* Finding best value */
                for (Particle p : particles) {
                    int rand1 = random.nextInt(2);
                    int rand2 = random.nextInt(2);

                   /*
                   * Update each particle
                   * In comparison with previous one, there exists block variable
                   * If some value is alredy good enough, it does not perform transformation
                   * It fixes some values of the particle that are alredy close enough
                   * Helps to reduce amount of iterations and to obrain more accuracy
                   * */
                    for (int i = 0; i < p.velocity.length; i++) {
                        if (!p.blocked[i]) {
                            p.velocity[i] = m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (globalBest[i] - p.notes[i]);
                            p.notes[i] = p.notes[i] + p.velocity[i];
                        }
                    }

                }
            }
        }
    }

    /**
     * PairNoteOptimization PSO!
     * Used to find best combination of chord and pair note
     * CONSTANT VALUE :: described in the doc
     * c1 = 1.05
     * c2 = 1.05
     * m = 0.67
     * Population = 15
     * Iterations = 50
     * MAGIC_BLOCK  = 0.5
     * MAX_PAIR_NOTE_DIFF = 3 :: maximal difference between values of pair note
     */
    private class PairNoteOptimization {
        private Chord chords[];
        private double globalBest[] = new double[2];
        private double globalFitness = Double.MAX_VALUE;

        private final double MAGIC_BLOCK = 0.5;
        private final double c1 = 1.05;
        private final double c2 = 1.05;
        private final double m = 0.67;
        private final int Population = 15;
        private final int Iterations = 50;
        private final double MAX_PAIR_NOTE_DIFF = 3;

        /**
         * Particle class!
         * Contains array of notes :: size = 2 (pair, com'on, it's obviously)
         * Default values of notes are 72 + [0, 12] :: OCTAVE_DIFFRENCE = 12
         */
        private class Particle {
            public int chordNotes[];
            public double notes[];
            public double myBest[];
            public double velocity[];
            public double fitness;

            /**
             * Constructor applies Chord to get synergy with
             * @param chord
             */
            Particle(Chord chord) {
                chordNotes = chord.notes;
                notes = new double[2];
                myBest = new double[2];
                velocity = new double[2];
                fitness = Double.MAX_VALUE;

                notes[0] = random.nextDouble() * OCTAVE_DIFFERENCE + OCTAVE_DIFFERENCE * 6;
                notes[1] = random.nextDouble() * OCTAVE_DIFFERENCE + OCTAVE_DIFFERENCE * 6;
            }
        }

        /**
         * Constructor, consumes array of chords
         */
        PairNoteOptimization(Chord chords[]) {
            this.chords = chords;
        }

        /**
         * Fitness function for PairNoteOptimization
         * Finds minimal diffrence between first note in PairNotes and values of chord, ignoring OCTAVE_DIFFERENCE
         * Also takes into account large difference of second and first notes in pair note
         * @param p - pair to calculate
         * @return value of particle
         */
        private double fitnessFunction(Particle p) {
            double diff1 = Math.abs((p.chordNotes[0] + OCTAVE_DIFFERENCE) - p.notes[0]);
            double diff2 = Math.abs((p.chordNotes[1] + OCTAVE_DIFFERENCE) - p.notes[0]);
            double diff3 = Math.abs((p.chordNotes[2] + OCTAVE_DIFFERENCE) - p.notes[0]);
            double diff_min = Math.min(diff1, diff2);

            double secondNoteDiff = Math.abs(p.notes[0] - p.notes[1]);
            diff_min = Math.min(diff_min, diff3);
            if (secondNoteDiff >= MAX_PAIR_NOTE_DIFF)
                diff_min += secondNoteDiff;

            return diff_min;
        }

        /**
         * Generates pair notes according to given chords earlier
         *
         * @return array of pair notes to be used
         */
        public PairNote[] generatePairNotes() {
            PairNote pairNotes[] = new PairNote[ChordsAmount];
            /* Make optimization for each chord */
            for (int index = 0; index < chords.length; index++) {
                Particle particles[] = new Particle[Population];
                for (int i = 0; i < Population; i++)
                    particles[i] = new Particle(chords[index]);

                optimize(particles);

                int notes[] = new int[2];
                notes[0] = (int)globalBest[0];
                notes[1] = (int)globalBest[1];
                /* Finds closest value for pair_note */
                notes[1] = closestValue(globalBest[1], C_MAJOR, OCTAVE_DIFFERENCE);
                pairNotes[index] = new PairNote(notes);
            }

            return pairNotes;
        }

        /**
         * Method optimize like everywhere uses GLOBAL BEST approach
         * Exist additional `stop` value in addition to iteration amount
         * When sample is already good enough, it stops
         * @param particles - particles to optimize
         */
        void optimize(Particle[] particles) {
            int iteration;

            /* Make until conditions satisfied */
            globalFitness = Double.MAX_VALUE;
            for (iteration = 0; iteration < Iterations && globalFitness > MAGIC_BLOCK; iteration++) {
                for (Particle p: particles) {
                    double fitness = fitnessFunction(p);
                    if (fitness < p.fitness) {
                        p.fitness = fitness;
                        for (int i = 0; i < p.myBest.length; i++)
                            p.myBest[i] = p.notes[i];
                    }

               /* Finding best value */
                    if (fitness < globalFitness) {
                        globalFitness = fitness;
                        for (int i = 0; i < globalBest.length; i++)
                            globalBest[i] = p.notes[i];
                    }
                }

                /* Update each particle */
                for (Particle p : particles) {
                    int rand1 = random.nextInt(2);
                    int rand2 = random.nextInt(2);

                    for (int i = 0; i < p.velocity.length; i++) {
                        p.velocity[i] = m * p.velocity[i] + c1 * rand1 * (p.myBest[i] - p.notes[i]) + c2 * rand2 * (globalBest[i] - p.notes[i]);
                        p.notes[i] = p.notes[i] + p.velocity[i];
                    }
                }
            }

//            System.out.printf("Amount of iterations :: PairNote :: %d\n", iteration);
//            System.out.printf("Global fitness :: PairNote :: %.2f\n", globalFitness);
        }
    }

    /**
     * Finds closest value for `v` in set of `values`
     * @param v - value to find closest for
     * @param values - set of values to compare with
     * @param difference - difference in octaves (OCTAVE_DIFFERENCE) - may happen a request for pairNote and Chord (diff = 1 * OCTAVE_DIFFERENCE)
     * @return closest value
     */
    private int closestValue(double v, int[] values, int difference) {
        int index = 0;
        for (int i = 0; i < values.length; i++)
            if (Math.abs(values[i] - (v - difference)) < Math.abs((v - difference) - values[index]))
                index = i;

        return values[index] + difference;
    }
}
