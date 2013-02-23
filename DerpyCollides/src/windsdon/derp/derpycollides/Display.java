package windsdon.derp.derpycollides;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 *
 * @author Windsdon
 */
public class Display extends Canvas {

    private final int width;
    private final int height;
    private final Dimension size;
    private JFrame frame;
    private String title;
    private Image icon;
    private final BufferedImage screen;

    public BufferedImage getScreen() {
        return screen;
    }

    public Display(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        size = new Dimension(width, height);
        screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        setPreferredSize(size);
        setMaximumSize(size);
        setMinimumSize(size);
    }

    public Display(int width, int height, String title, Image icon) {
        this(width, height, title);
        this.icon = icon;
    }

    public boolean display() {
        frame = new JFrame(title);
        if (icon != null) {
            frame.setIconImage(icon);
        }
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return true;
    }

    public Graphics2D getScreenGraphics() {
        return screen.createGraphics();
    }

    public Dimension getScreenSize() {
        return new Dimension(screen.getWidth(), screen.getHeight());
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.drawImage(screen, 0, 0, screen.getWidth(), screen.getHeight(), null);
        g.dispose();

        bs.show();
    }
}
