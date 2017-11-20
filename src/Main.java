public final class Main {
    /**
     * The main class used to generate values from some trigonometric function and pass it into MidiGenerator
     * Typically used Sin(x) function and Cos(x)
     * But it is also possible to somehow generate music based on more complicated functions like [Sin(x) * Cos(x)]
     *
     * The main requirment is to have values in range [-1, 1] :: it can be any function, just scale values into this range.
     *
     *
     * Degrees amount is chosen to be 720, provides not very big sequence of same chords.
     * @param args
     */
    private static final double DEGREES_AMOUNT = 720;
    private static final int CHORDS_AMOUNT = 16;

    public static void main(String[] args){
        String outname = "Sin";

        double values[] = new double[16];
        double step = DEGREES_AMOUNT / CHORDS_AMOUNT;
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.toRadians(i * step);
            values[i] = Math.sin(values[i]);
        }

        MidiGenerator.generateMidi(outname, values);
    }
}
