/**
 * Created by evger on 03-Nov-17.
 */
public class PairNote extends SoundUnit {
    public PairNote(int notes[]) {
        for (int i = 0; i < 2; i++)
            this.notes[i] = notes[i];
    }
}
