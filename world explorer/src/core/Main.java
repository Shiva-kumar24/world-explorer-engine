package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

public class Main {
    private static final int WIDTH = 80, HEIGHT = 40;
    private static final TETile GOLD_AVATAR = new TETile(Tileset.AVATAR, '$'); // avatar tile

    public static void main(String[] args) {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.enableDoubleBuffering();
        while (true) {            // main loop
            char choice = menu();
            if (choice == 'q') System.exit(0);
            if (choice == 'l') load();
            if (choice == 'n') play(getSeed(), -1, -1);
        }
    }

    private static char menu() {                       // title screen
        while (true) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(WIDTH / 2.0, HEIGHT * .75, "CS 61B: The Game");
            StdDraw.text(WIDTH / 2.0, HEIGHT * .55, "New Game (N)");
            StdDraw.text(WIDTH / 2.0, HEIGHT * .50, "Load Game (L)");
            StdDraw.text(WIDTH / 2.0, HEIGHT * .45, "Quit     (Q)");
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char k = Character.toLowerCase(StdDraw.nextKeyTyped());
                if (k == 'n' || k == 'l' || k == 'q') return k;
            }
        }
    }

    private static long getSeed() {  // seed entry
        StringBuilder typed = new StringBuilder();
        while (true) {
            StdDraw.clear(StdDraw.BLACK);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.text(WIDTH / 2.0, HEIGHT * .65, "Enter seed then press S");
            StdDraw.text(WIDTH / 2.0, HEIGHT * .55, typed.toString());
            StdDraw.show();
            if (!StdDraw.hasNextKeyTyped()) continue;
            char k = StdDraw.nextKeyTyped();
            if (k == 's' || k == 'S') break;
            if (Character.isDigit(k)) typed.append(k);
        }
        return typed.length() == 0 ? 0L : Long.parseLong(typed.toString());
    }

    private static void play(long seed, int startX, int startY) { // gameplay
        TETile[][] world = worldGen.generate(seed, WIDTH, HEIGHT);

        boolean desertTheme = (seed & 1L) == 1;       // theme choice
        TETile floor = desertTheme ? Tileset.SAND : Tileset.FLOOR;
        TETile wall  = desertTheme ? Tileset.MOUNTAIN : Tileset.WALL;
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++) {
                if (world[x][y] == Tileset.FLOOR) world[x][y] = floor;
                if (world[x][y] == Tileset.WALL)  world[x][y] = wall;
            }

        int px, py;
        if (startX >= 0) { px = startX; py = startY; }
        else {
            px = py = 0;
            outer: for (int y = 0; y < HEIGHT; y++)
                for (int x = 0; x < WIDTH; x++)
                    if (world[x][y] == floor) { px = x; py = y; break outer; }
        }
        world[px][py] = GOLD_AVATAR;

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        boolean awaitingColon = false;
        boolean visionLimited = true;

        while (true) {  // game loop
            TETile[][] frame = visionLimited ? losView(world, px, py, 5) : world;
            ter.renderFrame(frame);
            HUD.render(world, WIDTH, HEIGHT);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.textRight(WIDTH - 1, HEIGHT - 1, "seed " + seed);
            StdDraw.show();

            if (!StdDraw.hasNextKeyTyped()) continue;
            char k = StdDraw.nextKeyTyped();

            if (awaitingColon) {
                awaitingColon = false;
                if (k == 'Q' || k == 'q') {
                    SaveLoad.save(seed, px, py);
                    System.exit(0);
                }
                continue;
            }
            if (k == ':' || k == ';') { awaitingColon = true; continue; }
            if (k == 'T' || k == 't') { visionLimited = !visionLimited; continue; }

            k = Character.toLowerCase(k);
            int nx = px, ny = py;
            if (k == 'w') ny++;
            if (k == 's') ny--;
            if (k == 'a') nx--;
            if (k == 'd') nx++;
            if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT && world[nx][ny] == floor) {
                world[px][py] = floor;
                px = nx; py = ny;
                world[px][py] = GOLD_AVATAR;
            }
        }
    }

    private static void load() {   // load save
        long[] data = SaveLoad.load();
        if (data != null) play(data[0], (int) data[1], (int) data[2]);
    }

    private static TETile[][] losView(TETile[][] src, int cx, int cy, int radius) {
        TETile[][] v = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                v[x][y] = Tileset.NOTHING;
        int r2 = radius * radius;
        for (int x = Math.max(0, cx - radius); x <= Math.min(WIDTH - 1, cx + radius); x++)
            for (int y = Math.max(0, cy - radius); y <= Math.min(HEIGHT - 1, cy + radius); y++)
                if ((x - cx) * (x - cx) + (y - cy) * (y - cy) <= r2)
                    v[x][y] = src[x][y];
        return v;
    }
}
