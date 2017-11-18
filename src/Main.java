
public final class Main {

    public static void main(String[] args){
        String outname = "Sin";

        double values[] = new double[16];
        double step = 720.0 / 16;
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.toRadians(i * step);
            values[i] = Math.sin(values[i]);
        }

        MidiGenerator.generateMidi(outname, values);
    }
}
