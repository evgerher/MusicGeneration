/**
 * Created by evger on 03-Nov-17.
 */

/**
 * Class PairNote - container for 2 notes
 * Constructor applies array of 2 items and just stores it.
 */
public class PairNote {
    private final int notesAmount = 2;
    public int notes[];

    public PairNote(int notes[]) {
        this.notes = new int[notesAmount];

        for (int i = 0; i < notesAmount; i++)
            this.notes[i] = notes[i];
    }
}
