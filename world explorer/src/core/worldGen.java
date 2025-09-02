package core;

import tileengine.TETile;
import tileengine.Tileset;
import java.util.Random;

public class worldGen {

    public static TETile[][] generate(long seed, int W, int H) {
        TETile[][] w = new TETile[W][H];
        fillWithNothing(w);
        carveSimpleRooms(seed, w);
        addWalls(w);
        return w;
    }


    private static void fillWithNothing(TETile[][] w) {
        for (int x = 0; x < w.length; x++)
            for (int y = 0; y < w[0].length; y++)
                w[x][y] = Tileset.NOTHING;
    }

    private static void carveSimpleRooms(long seed, TETile[][] w) {
        Random rng = new Random(seed);
        int rooms = 8 + rng.nextInt(5);
        for (int k = 0; k < rooms; k++) {
            int rw = 4 + rng.nextInt(6);
            int rh = 4 + rng.nextInt(6);
            int x0 = rng.nextInt(w.length - rw - 2) + 1; // leave border
            int y0 = rng.nextInt(w[0].length - rh - 2) + 1;
            for (int x = x0; x < x0 + rw; x++)
                for (int y = y0; y < y0 + rh; y++)
                    w[x][y] = Tileset.FLOOR;

            if (k > 0) connectCentresSimple(w, x0 + rw/2, y0 + rh/2,
                    w[0].length / 2, w[0].length / 2);
        }
    }

    private static void connectCentresSimple(TETile[][] w,
                                             int x1, int y1, int x2, int y2) {
        // horizontal
        for (int x = Math.min(x1,x2); x <= Math.max(x1,x2); x++)
            if (w[x][y1] == Tileset.NOTHING) w[x][y1] = Tileset.FLOOR;

        // vertical
        for (int y = Math.min(y1,y2); y <= Math.max(y1,y2); y++)
            if (w[x2][y] == Tileset.NOTHING) w[x2][y] = Tileset.FLOOR;
    }

    private static void addWalls(TETile[][] w) {
        int W = w.length, H = w[0].length;
        for (int x = 0; x < W; x++)
            for (int y = 0; y < H; y++)
                if (w[x][y] == Tileset.FLOOR)
                    for (int dx = -1; dx <= 1; dx++)
                        for (int dy = -1; dy <= 1; dy++)
                            if (Math.abs(dx)+Math.abs(dy)==1 &&
                                    w[x+dx][y+dy] == Tileset.NOTHING)
                                w[x+dx][y+dy] = Tileset.WALL;
    }
}
