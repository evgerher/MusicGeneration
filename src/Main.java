import jm.JMC;
import jm.music.data.*;
import jm.midi.*;
import jm.music.tools.*;
import jm.util.*;

import java.util.Random;

public final class Main implements JMC {
    private static Score s = new Score("CPhrase class example");
    private static Part p = new Part("Piano", 0, 0);

    public static void main(String[] args){
//        //Let us know things have started
//        System.out.println("Creating chord progression . . .");
//
//        //choose rootPitch notes around the cycle of fifths
//        int rootPitch = 60; //set start note to middle C
//        for (int i = 0; i < 6; i++) {
//            secondInversion(rootPitch);
//            rootPitch -= 7;
//            rootPosition(rootPitch);
//            rootPitch += 5;
//        }
//
//        //add a final chord
//        ending(rootPitch);
//
//        //pack the part into a score
//        s.addPart(p);
//
//        //display the music
//        View.show(s);
//
//        // write the score to a MIDIfile
//        Write.midi(s, "Chords.mid");
        double values[] = new double[16];

        double step = 360.0 / 16;
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.toRadians(i * step);
            values[i] = Math.sin(values[i]);
        }

        for (int trie = 0; trie < 5; trie++) {
            PSO pso = new PSO(values);
            Chord chords[] = pso.generateChords();

            for (int i = 0; i < 16; i++) {
                CPhrase chord = new CPhrase();
                chord.addChord(chords[i].notes, C);
                p.addCPhrase(chord);
            }

            s.addPart(p);
            String name = "Try" + Integer.toString(trie) + ".mid";
            Write.midi(s, name);
            p = new Part("Piano", 0, 0);
        }
    }

    private static void rootPosition(int rootPitch) {
        // build the chord from the rootPitch
        int[] pitchArray = new int[3];
        pitchArray[0] = rootPitch;
        pitchArray[1] = rootPitch + 4;
        pitchArray[2] = rootPitch + 7;
        //add chord to the part
        CPhrase chord = new CPhrase();
        chord.addChord(pitchArray, C);
        p.addCPhrase(chord);
    }

    private static void secondInversion(int rootPitch) {
        // build the chord from the rootPitch
        int[] pitchArray = new int[3];
        pitchArray[0] = rootPitch;
        pitchArray[1] = rootPitch + 4;
        pitchArray[2] = rootPitch - 2;
        //add chord to the part
        CPhrase chord = new CPhrase();
        chord.addChord(pitchArray, C);
        p.addCPhrase(chord);
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
