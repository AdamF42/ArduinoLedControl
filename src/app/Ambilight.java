package app;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

public class Ambilight {
    //Arduino communication config
    public static final int DATA_RATE = 9600;
    public static final int TIMEOUT = 2000;
    //delay between next color calculations in [ms]
    private static final long DELAY = 100;
    //leds number on strip
    public static final int LEDS_NUM = 60;
    //number of leds per section
    public static final int LEDS_PER_SECTION = 5;
    //we split strip to sections because of performance reasons
    public static final int SECTIONS = LEDS_NUM / LEDS_PER_SECTION;
    //screen resolution
    public static final int X_RES = 3440;
    public static final int Y_RES = 1440;
    //sections width and height
    public static final int SECT_WIDTH = X_RES / SECTIONS;
    public static final int SECT_HEIGHT = Y_RES;
    //for better performance we do not calculate every pixel,
//but skip some of them
    public static final int SECT_SKIP = 10;
    // robot to read the data from the screen
    private Robot robot;
    // arduino communication
    private SerialPort serial;
    private OutputStream output;
    /**
     * init arduino communication
     */
    private void initSerial() {
// find the port where teh arduino is connected
        CommPortIdentifier serialPortId = null;
        Enumeration enumComm = CommPortIdentifier.getPortIdentifiers();
        while (enumComm.hasMoreElements() && serialPortId == null) {
            serialPortId = (CommPortIdentifier) enumComm.nextElement();
        }
        try {
            serial = (SerialPort) serialPortId.open(this.getClass().getName(),
                    TIMEOUT);
            serial.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        } catch (PortInUseException | UnsupportedCommOperationException e) {
            e.printStackTrace();
        }
    }
    /**
     * init the robot
     */
    private void initRobot() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    /**
     * init arduino output
     */
    private void initOutputStream() {
        try {
            output = serial.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * read colors from the screen
     *
     * @return array with colors that will be send to arduino
     */
    private Color[] getColors() {
        BufferedImage screen = robot.createScreenCapture(new Rectangle(
                new Dimension(X_RES, Y_RES)));
        Color[] leds = new Color[SECTIONS];
        for (int led = 0; led < SECTIONS; led++) {
            BufferedImage section = screen.getSubimage(led * SECT_WIDTH, 0, SECT_WIDTH, SECT_HEIGHT);
            Color sectionAvgColor = getAvgColor(section);
            leds[led] = sectionAvgColor;
        }
        return leds;
    }
    /**
     * calculate average color for section
     */
    private Color getAvgColor(BufferedImage imgSection) {
        int width = imgSection.getWidth();
        int height = imgSection.getHeight();
        int r = 0, g = 0, b = 0;
        int loops = 0;
        for (int x = 0; x < width; x += SECT_SKIP) {
            for (int y = 0; y < height; y += SECT_SKIP) {
                int rgb = imgSection.getRGB(x, y);
                Color color = new Color(rgb);
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
                loops++;
            }
        }
        r = r / loops;
        g = g / loops;
        b = b / loops;
        return new Color(r, g, b);
    }
    /**
     * Send the data to Arduino
     */
    private void sendColors(Color[] leds) {
        try {
            output.write(0xff);
            for (int i = 0; i < SECTIONS; i++) {
                output.write(leds[i].getRed());
                output.write(leds[i].getGreen());
                output.write(leds[i].getBlue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Main Loop
    private void loop() {
        while (true) {
            Color[] leds = getColors();
            sendColors(leds);
            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        Ambilight ambi = new Ambilight();
        ambi.initRobot();
        ambi.initSerial();
        ambi.initOutputStream();
        ambi.loop();
    }
}