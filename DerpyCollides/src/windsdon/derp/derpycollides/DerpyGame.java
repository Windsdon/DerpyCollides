package windsdon.derp.derpycollides;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Windsdon
 */
public class DerpyGame implements Runnable {

    private Display display;
    private ArrayList<Box> boxes = new ArrayList<>();
    private Color bgColor = new Color(0xccccff);
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final String TITLE = "Derpy Collisions";

    public DerpyGame() {
        display = new Display(WIDTH, HEIGHT, TITLE);
    }

    public void start() {
        new Thread(this, "Game").start();
    }

    @Override
    public void run() {
        setRunning(display.display());
        init();
        while (isRunning()) {
            doRunTick();
        }
    }
    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void doRunTick() {
        doPhysics();
        doRender();
    }

    private void doPhysics() {
    }

    private void doRender() {
        Graphics2D g = display.getScreenGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, display.getScreenSize().width, display.getScreenSize().height);

        for (Box box : boxes) {
            box.render(g);
        }
        
        g.dispose();
        display.render();
    }
    
    private void addBox(Box box){
        boxes.add(box);
    }

    private void init() {
        addBox(new Box(100, 100, 100, 100));
    }

    private static class Box {

        public Rectangle2D bounds;
        public Color color;
        public Physics physics;

        public Box(Rectangle2D bounds, Color color) {
            this.bounds = bounds;
            this.color = color;
            physics = null;
        }

        public Box(Rectangle2D bounds, Color color, Physics physics) {
            this.bounds = bounds;
            this.color = color;
            this.physics = physics;
        }

        public Box(Rectangle2D bounds) {
            this.bounds = bounds;
            color = getRandomColor();
            physics = null;
        }
        
        public Box(double x, double y, double w, double h){
            this(new Rectangle2D.Double(x, y, w, h));
        }

        public static Color getRandomColor() {
            return new Color(new Random().nextInt(0xffffff + 1));
        }

        private void render(Graphics2D g) {
            g.setColor(color);
            g.fill(bounds);
        }
    }

    private static class Physics {

        public double vx, vy, ax, ay, factor;

        public Physics(double vx, double vy, double ax, double ay, double factor) {
            this.vx = vx;
            this.vy = vy;
            this.ax = ax;
            this.ay = ay;
            this.factor = factor;
        }

        public Physics(double vx, double vy, double ax, double ay) {
            this.vx = vx;
            this.vy = vy;
            this.ax = ax;
            this.ay = ay;
            factor = 1;
        }

        public Physics() {
            vx = 0;
            vy = 0;
            ax = 0;
            ay = 0;
            factor = 1;
        }
    }
}
