package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

final class HUD {
    private HUD() { }

    static void render(TETile[][] world, int w, int h) {
        int mx = (int) StdDraw.mouseX();   // x coord
        int my = (int) StdDraw.mouseY();   // y coord
        String info = "";
        if (mx >= 0 && mx < w && my >= 0 && my < h) {
            info = world[mx][my].description();
        }

        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(1, h - 1, info);
    }
}
