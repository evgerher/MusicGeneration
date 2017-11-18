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
public class MidiGenerator implements JMC{
    private static Score s;
    private static Part p;
    private static Part p1;
    static int type = -1;

    public static void generateMidi(String outname, double values[]) {
        s = new Score("Music generation");
        s.setTempo(120);

        p = new Part("Chords", XYLOPHONE);
        p1 = new Part("PairNote", XYLOPHONE);

        int temp[] = new int[1];

        for (int trie = 0; trie < 1; trie++) {
            PSO pso = new PSO(values);
            Chord chords[] = pso.generateChords();

            PairNote pairNotes[] = pso.generatePairNotes(chords);

            for (int i = 0; i < 16; i++) {
                CPhrase chord = new CPhrase();
                chord.addChord(chords[i].notes, C);
                p.addCPhrase(chord);
            }

            for (int i = 0; i < 16; i++) {
                CPhrase pairNote = new CPhrase();

                for (int j = 0; j < 2; j++) {
                    temp[0] = pairNotes[i].notes[j];
                    pairNote.addChord(temp, 0.5);
                }
                p1.addCPhrase(pairNote);
            }

            for (int i = 0; i < 16; i++) {
                System.out.printf("[");
                for (int j = 0; j < 3; j++)
                    System.out.printf(" %d ", chords[i].notes[j]);
                System.out.printf("] [");
                for (int j = 0; j < 2; j++)
                    System.out.printf(" %d ", pairNotes[i].notes[j]);
                System.out.printf("]\n");
            }

            s.addPart(p);
            s.addPart(p1);
            String name = outname + Integer.toString(type) + Integer.toString(trie) + ".mid";
            Write.midi(s, name);

            p.removeAllPhrases();
            p1.removeAllPhrases();
            s.removeAllParts();
        }
    }
}
