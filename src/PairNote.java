/**
 * Created by evger on 03-Nov-17.
 */
public class PairNote extends SoundUnit {
    private final int notesAmount = 2;

    public PairNote(int notes[]) {
        this.notes = new int[notesAmount];

        for (int i = 0; i < notesAmount; i++)
            this.notes[i] = notes[i];
    }
}
