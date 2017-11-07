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


    public static void main(String[] args){
//        //pack the part into a score
//        s.addPart(p);
//
//        //display the music
//        View.show(s);
//
//        // write the score to a MIDIfile
//        Write.midi(s, "Chords.mid");

        s = new Score("MusicGeneration");
        s.setTempo(120);
        p = new Part("Chords", 0, 0);
        p1 = new Part("PairNote", 0, 1);

        int temp[] = new int[1];
        double values[] = new double[16];

        double step = 360.0 / 16;
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.toRadians(i * step);
            values[i] = Math.sin(values[i]);
        }

        for (int trie = 0; trie < 5; trie++) {


            PSO pso = new PSO(values);
            Chord chords[] = pso.generateChords();
//            for (int i = 0; i < 16; i++) {
//                chords[i].notes[1] = chords[i].notes[0] + 4;
//                chords[i].notes[2] = chords[i].notes[0] + 7;
//            }
//            PairNote pairNotes[] = pso.generatePairNotes(chords);

            for (int i = 0; i < 16; i++) {
                CPhrase chord = new CPhrase();
                chord.addChord(chords[i].notes, C);
                p.addCPhrase(chord);
            }

//            for (int i = 0; i < 16; i++) {
//                CPhrase pairNote = new CPhrase();
//
//                for (int j = 0; j < 2; j++) {
//                    temp[0] = pairNotes[i].notes[j];
//                    pairNote.addChord(temp, 0.5);
//                }
//                p1.addCPhrase(pairNote);
//            }

            s.addPart(p);
            s.addPart(p1);
            String name = "Try" + Integer.toString(trie) + ".mid";
            Write.midi(s, name);

            p.removeAllPhrases();
            p1.removeAllPhrases();
            s.removeAllParts();
        }
    }

    private static void ending(int rootPitch) {
        // build the chord from the rootPitch
        int[] pitchArray = new int[3];
        pitchArray[0] = rootPitch;
        pitchArray[1] = rootPitch + 4;
        pitchArray[2] = rootPitch + 7;
        //add chord to the part
        CPhrase chord = new CPhrase();
        chord.addChord(pitchArray, SB);
        p.addCPhrase(chord);
    }
}
