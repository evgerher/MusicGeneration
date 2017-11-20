/**
 * Created by evger on 03-Nov-17.
 */

/**
 * Class Chord - container for 3 notes
 * Constructor applies array of 3 items and just stores it.
 */
public class Chord {
    private final int notesAmount = 3;
    public int notes[];

    public Chord(int notes[]) {
        this.notes = new int[notesAmount];

        for (int i = 0; i < notesAmount; i++)
            this.notes[i] = notes[i];
    }


}