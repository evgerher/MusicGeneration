import jm.JMC;
import jm.music.data.*;
import jm.midi.*;
import jm.music.tools.*;
import jm.util.*;

import java.util.Random;

public final class Main implements JMC {
    private static Score s;
    private static Part p;
    private static Part p1;
    static int type = 1;


    public static void main(String[] args){
        s = new Score("MusicGeneration");
        s.setTempo(120);
        p = new Part("Chords", 0, 0);
        p1 = new Part("PairNote", 0, 1);

        int temp[] = new int[1];
        double values[] = new double[16];


        double step = 720.0 / 16;
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.toRadians(i * step);
            values[i] = Math.sin(values[i]);
        }


       for (int trie = 0; trie < 3; trie++) {
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
            String name = "Manual" + Integer.toString(type) + Integer.toString(trie) + ".mid";
            Write.midi(s, name);

            p.removeAllPhrases();
            p1.removeAllPhrases();
            s.removeAllParts();
        }
    }
}
