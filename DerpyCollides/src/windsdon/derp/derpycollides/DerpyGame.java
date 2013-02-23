package windsdon.derp.derpycollides;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 *
 * @author Windsdon
 */
public class DerpyGame implements Runnable, MouseListener {

    private Display display;
    private ArrayList<Box> boxes = new ArrayList<>();
    private Color bgColor = new Color(0xccccff);
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final String TITLE = "Derpy Collisions";
    private Image icon;
    private long lastRenderTickTime;
    private boolean stopPhysics = true;

    public DerpyGame() {
        try {
            icon = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("res/icon.png"));
            display = new Display(WIDTH, HEIGHT, TITLE, icon);
        } catch (IOException ex) {
            icon = null;
            display = new Display(WIDTH, HEIGHT, TITLE);
        }

    }

    public void start() {
        new Thread(this, "Game").start();
    }

    @Override
    public void run() {
        setRunning(display.display(this));
        init();
        while (isRunning()) {
            doRunTick();
        }
    }

    private void init() {
        addBox(new Box(100, 100, 100, 100));
        addBox(new Box(new Rectangle2D.Double(100, 600, 100, 100), Color.red, new Physics(500, -1000, 0, 1000, 1000, false)));
        lastRenderTickTime = System.currentTimeMillis();
    }
    private boolean running = false;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void doRunTick() {
        long now = System.currentTimeMillis();
        if (!stopPhysics) {
            doPhysics(now - lastRenderTickTime);
        }
        lastRenderTickTime = now;
        doRender();
    }

    private void doPhysics(long deltaMS) {
        for (Box box : boxes) {
            //no need to check for NullPointerExcpetion
            if (box.physics == null || box.physics.fixed) {
                //fixed or doesn't have physics. Skip.
                continue;
            }
            double cycles = deltaMS / box.physics.factor;
            while (cycles > 0) {
                double delta = Math.min(cycles, Physics.PRECISON);

                box.physics.vx += box.physics.ax * delta;
                box.physics.vy += box.physics.ay * delta;

                double dx = box.physics.vx * delta;
                double dy = box.physics.vy * delta;

                box.bounds.setRect(box.bounds.getX() + dx, box.bounds.getY() + dy, box.bounds.getWidth(), box.bounds.getHeight());

                cycles -= Physics.PRECISON;
            }
        }
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

    private void addBox(Box box) {
        boxes.add(box);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        stopPhysics = false;
    }

    //<editor-fold defaultstate="collapsed" desc="not used MouseListener stuff">
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    //</editor-fold>

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

        public Box(double x, double y, double w, double h) {
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

        public static double PRECISON = 1;
        public double vx, vy, ax, ay, factor;
        public boolean fixed;
        public static final double DEFAULT_FACTOR = 1000;

        public Physics(double vx, double vy, double ax, double ay, double factor, boolean fixed) {
            this.vx = vx;
            this.vy = vy;
            this.ax = ax;
            this.ay = ay;
            this.factor = factor;
            this.fixed = fixed;
        }

        public Physics(double vx, double vy, double ax, double ay, boolean fixed) {
            this.vx = vx;
            this.vy = vy;
            this.ax = ax;
            this.ay = ay;
            this.fixed = fixed;
            factor = DEFAULT_FACTOR;
        }

        public Physics() {
            vx = 0;
            vy = 0;
            ax = 0;
            ay = 0;
            factor = DEFAULT_FACTOR;
            fixed = true;
        }
    }
}
