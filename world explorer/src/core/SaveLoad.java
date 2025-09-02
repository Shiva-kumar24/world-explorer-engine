package core;

import java.io.*;

final class SaveLoad {
    private static final String F = "save.txt";

    static void save(long s, int x, int y) {   // write save
        try (PrintWriter w = new PrintWriter(new FileWriter(F))) {
            w.println(s + " " + x + " " + y);
        } catch (IOException e) { }
    }

    static long[] load() {        // read the save
        try (BufferedReader r = new BufferedReader(new FileReader(F))) {
            String[] p = r.readLine().split(" ");
            return new long[] {
                    Long.parseLong(p[0]),
                    Long.parseLong(p[1]),
                    Long.parseLong(p[2])
            };
        } catch (Exception e) { return null; }
    }

    private SaveLoad() { }
}
