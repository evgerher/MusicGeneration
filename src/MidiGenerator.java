import jm.music.data.CPhrase;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Write;
import jm.JMC;

import static jm.constants.Durations.C;
import static jm.constants.ProgramChanges.XYLOPHONE;

/**
 * Created by evger on 03-Nov-17.
 */

/***
 * Class MidiGenerator - transforms double values in range [-1, 1] into a sequence of chords & notes in midi file.
 */
public class MidiGenerator implements JMC{
    private static Score s;
    private static Part p;
    private static Part p1;
    static int type = -1;

    private static final int TEMPO = 120;
    private static final int CHORDS_AMOUNT = 16;
    private static final double PAIR_NOTE_DURATION = 0.5;
    private static final double CHORD_DURATION = 1;

    /**
     * generateMidi - tranformation of values into midi file.
     * @param outname - how to name a file
     * @param values - array of values from external function [typically used sin(x)]
     * Creates a sequence of chords & pair notes and stores them into a midi file.
     *
     *
     * !) First of all it creates a PSO object, that consumes double values
     * 2) Generation of chords
     * 3) Generation of PairNotes depends on chords from previous step
     * 4) Writing to file
     *
     *               Sound instrument is chosen to be XYLOPHONE or VIOLIN
     *               Parts in the code named as Chords and PairNote - separate channels for chords and pair notes in final midi.
     *               Chord duration is predefined to be 1 second :: CHORD_DURATION
     *               Pair note duration is predefined to be 0.5 seconds :: PAIR_NOTE_DURATION
     *               Song tempo is predefined to be 120 :: TEMPO
     *
     */
    public static void generateMidi(String outname, double values[]) {
        s = new Score("Music generation");
        s.setTempo(TEMPO);

        p = new Part("Chords", VIOLIN);
        p1 = new Part("PairNote", VIOLIN);

        int temp[] = new int[1];

        for (int trie = 0; trie < 1; trie++) {
            /*
                Music generation starts
             */
            PSO pso = new PSO(values);
            Chord chords[] = pso.generateChords();
            PairNote pairNotes[] = pso.generatePairNotes(chords);
            /*
                Music generation ends
             */

            /*
                Writing midi starts
             */
            for (int i = 0; i < CHORDS_AMOUNT; i++) {
                CPhrase chord = new CPhrase();
                chord.addChord(chords[i].notes, CHORD_DURATION);
                p.addCPhrase(chord);
            }

            for (int i = 0; i < CHORDS_AMOUNT; i++) {
                CPhrase pairNote = new CPhrase();

                for (int j = 0; j < 2; j++) {
                    temp[0] = pairNotes[i].notes[j];
                    pairNote.addChord(temp, PAIR_NOTE_DURATION);
                }
                p1.addCPhrase(pairNote);
            }


            /* Uncomment this lines of code to see the generated chords & pair notes */
            /*
            for (int i = 0; i < 16; i++) {
                System.out.printf("[");
                for (int j = 0; j < 3; j++)
                    System.out.printf(" %d ", chords[i].notes[j]);
                System.out.printf("] [");
                for (int j = 0; j < 2; j++)
                    System.out.printf(" %d ", pairNotes[i].notes[j]);
                System.out.printf("]\n");
            }
            */

            s.addPart(p);
            s.addPart(p1);
            String name = outname + Integer.toString(type) + Integer.toString(trie) + ".mid";
            Write.midi(s, name);
            /*
                Writing midi ends
             */

            /*
                Cleaning Parts & Score
                Used to generate more than 1 midi file
             */

            p.removeAllPhrases();
            p1.removeAllPhrases();
            s.removeAllParts();
        }
    }
}
