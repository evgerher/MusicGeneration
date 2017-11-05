import java.util.Random;

/**
 * Created by evger on 03-Nov-17.
 */
public class Chord extends SoundUnit {
    private final int notesAmount = 3;

    public Chord(int notes[]) {
        for (int i = 0; i < notesAmount; i++)
            this.notes[i] = notes[i];
    }


}