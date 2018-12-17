package javakarol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class WeltAnzeige
extends JPanel
{
    private Welt welt;
    private GraphicsConfiguration gfxConf;
    private BufferedImage zeichenFlaeche;
    private Point weltOrigin3D = new Point(0, 0);
    private Graphics2D gDC;
    private BufferedImage ziegelImg;
    private BufferedImage ziegelImg2;
    private BufferedImage ziegelImg3;
    private BufferedImage ziegelImg4;
    private BufferedImage ziegelImg5;
    private BufferedImage ziegelImg6;
    private BufferedImage ziegelImg7;
    private BufferedImage ziegelImg8;
    private BufferedImage markeImg;
    private BufferedImage quaderImg = null;
    private final int maxRobotImages = 9;
    private BufferedImage[][] karolImg = new BufferedImage[4][9];
    private BufferedImage weltFlaeche = null;
    private int maxImageHoehe = 10;
    private Color hintergrundFarbe = new Color(0x404040);
    private final int randLinks = 40;
    private final int randOben = 60;
    private final int randUnten = 30;
    private final int weltOben = 40;

    WeltAnzeige(Welt welt){
        this.welt = welt;

        this.gfxConf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        zeichenFlaecheVorbereiten();
        weltFlaecheVorbereiten();
        imagesLaden();

        setBackground(hintergrundFarbe);
        setPreferredSize(new Dimension(this.zeichenFlaeche.getWidth(), this.zeichenFlaeche.getHeight()));
    }

    public int getWidth(){
        return this.zeichenFlaeche.getWidth();
    }

    public void paint(Graphics g){
        g.drawImage(this.zeichenFlaeche, 0, 0, null);
    }

    public void paintToFile(File f, String fileTyp){
        try
        {
            ImageIO.write(this.zeichenFlaeche, fileTyp, f);
        }
        catch (IOException localIOException) {}
    }

    private void loescheWelt(){
        Color aktuell = this.gDC.getColor();
        this.gDC.setColor(this.hintergrundFarbe);
        this.gDC.fillRect(0, 0, this.zeichenFlaeche.getWidth(), this.zeichenFlaeche.getHeight());
        this.gDC.setColor(aktuell);
    }

    public void zeichneWelt(){
        int links = Integer.MAX_VALUE;
        int oben = Integer.MAX_VALUE;
        int rechts = Integer.MIN_VALUE;
        int unten = Integer.MIN_VALUE;
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 0);
        for (int a = 1; a <= this.welt.areaBreite; a++) {
            for (int b = 1; b <= this.welt.areaLaenge; b++) {
                for (int c = 0; c < this.welt.areaHoehe; c++) {
                    if (this.welt.areaStapelInvalid[a][b][c] != false)
                    {
                        p1 = p3(a - 1, b, c);
                        p2 = p3(a, b - 1, c);
                        if (links > p1.x) {
                            links = p1.x;
                        }
                        if (oben > p2.y - this.maxImageHoehe) {
                            oben = p2.y - this.maxImageHoehe;
                        }
                        if (rechts < p2.x) {
                            rechts = p2.x;
                        }
                        if (unten < p1.y) {
                            unten = p1.y;
                        }
                        this.welt.areaStapelInvalid[a][b][c] = false;
                    }
                }
            }
        }
        if ((links != Integer.MAX_VALUE) && (links != Integer.MIN_VALUE))
        {
            links = Math.max(0, links - 10);
            oben = Math.max(0, oben - 10);
            rechts = Math.min(this.weltFlaeche.getWidth(), rechts + 10);
            unten = Math.min(this.weltFlaeche.getHeight(), unten + 10);
            zeichenWeltRechteck(links, oben, rechts, unten);
            repaint();
        }
    }

    public void zeichneWeltGanz(){
        loescheWelt();
        zeichenWeltRechteck(0, 0, this.weltFlaeche.getWidth(), this.weltFlaeche.getHeight());
        for (int a = 1; a <= this.welt.areaBreite; a++) {
            for (int b = 1; b <= this.welt.areaLaenge; b++) {
                for (int c = 0; c < this.welt.areaHoehe; c++) {
                    this.welt.areaStapelInvalid[a][b][c] = false;
                }
            }
        }
        repaint();
    }

    private void zeichenWeltRechteck(int x1, int y1, int x2, int y2){
        Point p1 = new Point(0, 0);

        Rectangle clipRect = new Rectangle(x1, y1, x2 - x1, y2 - y1);

        this.gDC.setClip(x1 + 40, y1 + 60, x2 - x1, y2 - y1);

        this.gDC.drawImage(this.weltFlaeche, x1 + 40, y1 + 60, x2 + 40, y2 + 60, x1, y1, x2, y2, this);
        for (int a = 1; a <= this.welt.areaBreite; a++) {
            for (int b = 1; b <= this.welt.areaLaenge; b++) {
                for (int c = 0; c <= this.welt.areaStapelHoehe[a][b]; c++)
                {
                    byte part = this.welt.getPart(a, b, c);
                    this.welt.getClass();
                    if (part == 1){ //Ziegel rot
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.ziegelImg.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.ziegelImg.getWidth(), this.ziegelImg.getHeight()))) {
                            this.gDC.drawImage(this.ziegelImg, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    if (part == 3){ //Ziegel gelb
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.ziegelImg2.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.ziegelImg2.getWidth(), this.ziegelImg2.getHeight()))) {
                            this.gDC.drawImage(this.ziegelImg2, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    if (part == 4){ //Ziegel blau
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.ziegelImg3.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.ziegelImg3.getWidth(), this.ziegelImg3.getHeight()))) {
                            this.gDC.drawImage(this.ziegelImg3, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    if (part == 5){ //Ziegel gruen
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.ziegelImg4.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.ziegelImg4.getWidth(), this.ziegelImg4.getHeight()))) {
                            this.gDC.drawImage(this.ziegelImg4, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    if (part == 6){ //Ziegel durchsichtig
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.ziegelImg5.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.ziegelImg5.getWidth(), this.ziegelImg5.getHeight()))) {
                            this.gDC.drawImage(this.ziegelImg5, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    if (part == 7){ //Ziegel durchsichtig
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.ziegelImg6.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.ziegelImg6.getWidth(), this.ziegelImg6.getHeight()))) {
                            this.gDC.drawImage(this.ziegelImg6, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    if (part == 8){ //Ziegel durchsichtig
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.ziegelImg7.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.ziegelImg7.getWidth(), this.ziegelImg7.getHeight()))) {
                            this.gDC.drawImage(this.ziegelImg7, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    if (part == 9){ //Ziegel durchsichtig
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.ziegelImg8.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.ziegelImg8.getWidth(), this.ziegelImg8.getHeight()))) {
                            this.gDC.drawImage(this.ziegelImg8, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    this.welt.getClass();
                    if (part == 2){ //Quader
                        p1 = p3(a - 1, b, 0.0F);
                        p1.y -= this.quaderImg.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.quaderImg.getWidth(), this.quaderImg.getHeight()))) {
                            this.gDC.drawImage(this.quaderImg, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    if ((this.welt.isMarker(a, b)) && (this.welt.brickCount(a, b) == c)){ //Marke
                        p1 = p3(a - 1, b, c);
                        p1.y -= this.markeImg.getHeight();
                        if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.markeImg.getWidth(), this.markeImg.getHeight()))) {
                            this.gDC.drawImage(this.markeImg, p1.x + 40, p1.y + 60, this);
                        }
                    }
                    int anzRoboter = this.welt.alleRoboter.size(); //Roboter
                    for (int i = 0; i < anzRoboter; i++){
                        Roboter robo = (Roboter)this.welt.alleRoboter.get(i);
                        if ((a == robo.PositionXGeben()) && (b == robo.PositionYGeben()) && (c == this.welt.areaStapelHoehe[a][b]) && (robo.SichtbarkeitGeben()))
                        {
                            p1 = p3(a - 0.9F, b - 0.3F, c);
                            int richtung = robo.getBlickrichtungNr();
                            int knr = Math.min(Math.max(robo.KennungGeben() - 1, 0), 8);
                            p1.x -= (this.karolImg[richtung][knr].getWidth() - 30) / 2;
                            p1.y -= this.karolImg[richtung][knr].getHeight();
                            if (clipRect.intersects(new Rectangle(p1.x, p1.y, this.karolImg[richtung][knr].getWidth(), this.karolImg[richtung][knr].getHeight()))) {
                                this.gDC.drawImage(this.karolImg[richtung][knr], p1.x + 40, p1.y + 60, this);
                            }
                        }
                    }
                }
            }
        }
    }

    private void weltFlaecheVorbereiten(){
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 0);

        float[] dash_array = new float[4];
        dash_array[0] = 10.0F;
        dash_array[1] = 5.0F;
        dash_array[2] = 5.0F;
        dash_array[3] = 5.0F;
        BasicStroke gestrichelt = new BasicStroke(
                1.0F, 
                0, 
                2, 
                1.0F, 
                dash_array, 
                0.0F);

        BasicStroke durchgehend = new BasicStroke();

        this.weltFlaeche = this.gfxConf.createCompatibleImage(this.zeichenFlaeche.getWidth() - 80 + 1, this.zeichenFlaeche.getHeight() - 60 - 30 + 1);

        Graphics2D g = this.weltFlaeche.createGraphics();
        g.setColor(this.hintergrundFarbe);
        g.fillRect(0, 0, this.weltFlaeche.getWidth(), this.weltFlaeche.getHeight());

        g.setColor(new Color(128,128,128));

        g.setStroke(durchgehend);
        for (int i = 0; i <= this.welt.areaLaenge; i++)
        {
            p1 = p3(0.0F, i, 0.0F);
            p2 = p3(this.welt.areaBreite, i, 0.0F);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        for (int i = 0; i <= this.welt.areaBreite; i++)
        {
            p1 = p3(i, 0.0F, 0.0F);
            p2 = p3(i, this.welt.areaLaenge, 0.0F);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        g.setStroke(gestrichelt);
        for (int i = 0; i <= this.welt.areaBreite; i++)
        {
            p1 = p3(i, 0.0F, 0.0F);
            p2 = p3(i, 0.0F, this.welt.areaHoehe);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        p1 = p3(this.welt.areaBreite, 0.0F, this.welt.areaHoehe);
        p2 = p3(0.0F, 0.0F, this.welt.areaHoehe);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
        for (int i = 0; i <= this.welt.areaLaenge; i++)
        {
            p1 = p3(0.0F, i, 0.0F);
            p2 = p3(0.0F, i, this.welt.areaHoehe);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        p1 = p3(0.0F, this.welt.areaLaenge, this.welt.areaHoehe);
        p2 = p3(0.0F, 0.0F, this.welt.areaHoehe);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);

        g.setStroke(durchgehend);
        p1 = p3(-2.0F, 0.0F, this.welt.areaHoehe);
        p2 = p3(-2.0F, 2.0F, this.welt.areaHoehe);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
        g.drawLine(p1.x - 10, p1.y + 5, p1.x, p1.y);
        g.drawLine(p1.x - 5, p1.y + 10, p1.x, p1.y);

        g.setFont(new Font("Arial", 0, 14));
        g.drawString("N", p1.x + 4, p1.y);
    }

    private BufferedImage imageLaden(String name){
        BufferedImage bi = null;

        URL u = ClassLoader.getSystemResource("imgs/" + name + ".gif");
        if (u == null) {
            u = WeltAnzeige.class.getClassLoader().getResource("imgs/" + name + ".gif");
        }
        try
        {
            bi = ImageIO.read(u);
        }
        catch (IOException e)
        {
            System.out.println("Ein noetiges Image f?r Ziegel/Quader/Roboter kann nicht geladen werden.");
            throw new RuntimeException("Fehler beim Laden eines Images.");
        }
        if (bi.getHeight() > this.maxImageHoehe) {
            this.maxImageHoehe = bi.getHeight();
        }
        return bi;
    }

    private void imagesLaden(){
        this.markeImg = imageLaden("Marke");
        this.quaderImg = imageLaden("Quader");
        this.ziegelImg = imageLaden("Ziegel");   //Ziegel rot
        this.ziegelImg2 = imageLaden("Ziegel2"); //Ziegel gelb
        this.ziegelImg3 = imageLaden("Ziegel3"); //Ziegel blau
        this.ziegelImg4 = imageLaden("Ziegel4"); //Ziegel gruen
        this.ziegelImg5 = imageLaden("Ziegel5"); //Ziegel durchsichtig
        this.ziegelImg6 = imageLaden("Ziegel6"); //Ziegel grau
        this.ziegelImg7 = imageLaden("Ziegel7"); //Ziegel schwarz
        this.ziegelImg8 = imageLaden("Ziegel8"); //Ziegel weiss
        
        for (int i = 1; i <= 9; i++)
        {
            this.karolImg[0][(i - 1)] = imageLaden("robotS" + i);
            this.karolImg[1][(i - 1)] = imageLaden("robotW" + i);
            this.karolImg[2][(i - 1)] = imageLaden("robotN" + i);
            this.karolImg[3][(i - 1)] = imageLaden("robotO" + i);
        }
    }

    private Point p3(float x, float y, float z){
        Point ergeb = new Point();
        ergeb.x = Math.round(this.weltOrigin3D.x + 30.0F * x - 15.0F * y);
        ergeb.y = Math.round(this.weltOrigin3D.y + 15.0F * y - 15.0F * z);
        return ergeb;
    }

    private void zeichenFlaecheVorbereiten(){
        Dimension ergeb = new Dimension(0, 0);
        Point[] punkte = new Point[8];
        int links = Integer.MAX_VALUE;
        int oben = Integer.MAX_VALUE;
        int rechts = Integer.MIN_VALUE;
        int unten = Integer.MIN_VALUE;

        punkte[0] = p3(0.0F, 0.0F, 0.0F);
        punkte[1] = p3(0.0F, 0.0F, this.welt.areaHoehe);
        punkte[2] = p3(0.0F, this.welt.areaLaenge, 0.0F);
        punkte[3] = p3(0.0F, this.welt.areaLaenge, this.welt.areaHoehe);
        punkte[4] = p3(this.welt.areaBreite, 0.0F, 0.0F);
        punkte[5] = p3(this.welt.areaBreite, 0.0F, this.welt.areaHoehe);
        punkte[6] = p3(this.welt.areaBreite, this.welt.areaLaenge, 0.0F);
        punkte[7] = p3(this.welt.areaBreite, this.welt.areaLaenge, this.welt.areaHoehe);
        for (int i = 0; i < 7; i++)
        {
            if (punkte[i].x > rechts) {
                rechts = punkte[i].x;
            }
            if (punkte[i].y > unten) {
                unten = punkte[i].y;
            }
            if (punkte[i].x < links) {
                links = punkte[i].x;
            }
            if (punkte[i].y < oben) {
                oben = punkte[i].y;
            }
        }
        ergeb.width = (Math.abs(rechts - links) + 80);
        ergeb.height = (Math.abs(unten - oben) + 60 + 30 + 40);
        this.weltOrigin3D.x = Math.abs(punkte[0].x - links);
        this.weltOrigin3D.y = (Math.abs(punkte[0].y - oben) + 40);

        this.zeichenFlaeche = this.gfxConf.createCompatibleImage(ergeb.width, ergeb.height);
        this.gDC = this.zeichenFlaeche.createGraphics();
        loescheWelt();
    }
}