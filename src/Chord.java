import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */
public class Chord extends SoundUnit {
    public Chord(int notes[]) {
        for (int i = 0; i < 3; i++)
            this.notes[i] = notes[i];
    }


}