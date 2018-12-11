package javakarol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class Welt
{
  private WeltFenster weltFenster;
  private final int maxHoehe = 31;
  final byte st_Nichts = 0;
  final byte st_Ziegel = 1;
  final byte st_Quader = 2;
  final byte st_Roboter = 3;
  int areaBreite;
  int areaLaenge;
  int areaHoehe;
  boolean[][] areaMarkiert;
  int[][] areaStapelHoehe;
  byte[][][] areaStapelInhalt;
  boolean[][][] areaStapelInvalid;
  private final int maxRoboter = 9;
  private int nextRoboter = 1;
  List<Object> alleRoboter;
  private List<Object> geladeneRoboter;
  private String weltDateiname;
  
  public Welt(int breite, int laenge, int hoehe)
  {
    init(breite, laenge, hoehe);
    this.weltDateiname = "";
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception localException) {}
    initView();
  }
  
  public Welt(String weltdatei)
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception localException) {}
    this.weltDateiname = "";
    loadKarolFile(validWorldFile(weltdatei), false);
    initView();
  }
  
  public Welt()
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception localException) {}
    this.weltDateiname = "";
    loadKarolFile(validWorldFile(""), false);
    initView();
  }
  
  /*public void Speichern(String dateiname)  //Kann nicht decompiliert werden
  {
    saveKarolFile(dateiname);
  }*/
  
  public void BildSpeichern(String dateiname)
  {
    saveWorldImage(dateiname);
  }
  
  public void ZurueckSetzen()
  {
    reset();
    this.weltFenster.ganzZeichnen();
    this.weltFenster.fehlerAusgabe("");
  }
  
  public void ZiegelVerstreuen(int anzahlZiegel, int maxStapelhoehe)
  {
    randomBrick(anzahlZiegel, maxStapelhoehe);
    this.weltFenster.ganzZeichnen();
  }
  
  private void init(int breite, int laenge, int hoehe)
  {
    this.areaBreite = Math.max(breite, 1);
    this.areaLaenge = Math.max(laenge, 1);
    this.areaHoehe = Math.max(Math.min(hoehe, 31), 1);
    
    this.areaStapelHoehe = new int[this.areaBreite + 1][this.areaLaenge + 1];
    for (int x = 0; x <= this.areaBreite; x++) {
      Arrays.fill(this.areaStapelHoehe[x], 0);
    }
    this.areaMarkiert = new boolean[this.areaBreite + 1][this.areaLaenge + 1];
    for (int x = 0; x <= this.areaBreite; x++) {
      Arrays.fill(this.areaMarkiert[x], false);
    }
    this.areaStapelInhalt = new byte[this.areaBreite + 1][this.areaLaenge + 1][this.areaHoehe];
    for (int x = 0; x <= this.areaBreite; x++) {
      for (int y = 0; y <= this.areaLaenge; y++) {
        Arrays.fill(this.areaStapelInhalt[x][y], (byte)0);
      }
    }
    this.areaStapelInvalid = new boolean[this.areaBreite + 1][this.areaLaenge + 1][this.areaHoehe];
    for (int x = 0; x <= this.areaBreite; x++) {
      for (int y = 0; y <= this.areaLaenge; y++) {
        Arrays.fill(this.areaStapelInvalid[x][y], true);
      }
    }
    this.alleRoboter = new ArrayList();
    this.geladeneRoboter = new ArrayList();
  }
  
  private void initView()
  {
    this.weltFenster = new WeltFenster(this);
    this.weltFenster.ganzZeichnen();
  }
  
  private void clear()
  {
    for (int x = 0; x <= this.areaBreite; x++) {
      Arrays.fill(this.areaStapelHoehe[x], 0);
    }
    for (int x = 0; x <= this.areaBreite; x++) {
      Arrays.fill(this.areaMarkiert[x], false);
    }
    for (int x = 0; x <= this.areaBreite; x++) {
      for (int y = 0; y <= this.areaLaenge; y++) {
        Arrays.fill(this.areaStapelInhalt[x][y], (byte)0);
      }
    }
    for (int x = 0; x <= this.areaBreite; x++) {
      for (int y = 0; y <= this.areaLaenge; y++) {
        Arrays.fill(this.areaStapelInvalid[x][y], true);
      }
    }
  }
  
  private String validWorldFile(String fileName)
  {
    String ergeb = "";
    if (fileName.length() > 0)
    {
      File f = new File(fileName);
      boolean check = false;
      try
      {
        if (f.isFile()) {
          if ((f.getName().toLowerCase().endsWith(".kdw")) || 
            (f.getName().toLowerCase().endsWith(".jkw"))) {
            check = true;
          }
        }
      }
      catch (SecurityException localSecurityException) {}
      if (check) {
        try
        {
          ergeb = f.getCanonicalPath();
        }
        catch (IOException localIOException) {}
      } else {
        fileName = "";
      }
    }
    if (fileName.length() == 0)
    {
      JFileChooser d = new JFileChooser();
      d.setFileFilter(new FileFilter()
      {
        public boolean accept(File f)
        {
          return (f.isDirectory()) || (f.getName().toLowerCase().endsWith(".kdw")) || (f.getName().toLowerCase().endsWith(".jkw"));
        }
        
        public String getDescription()
        {
          return "Karolwelt *.kdw; *.jkw";
        }
      });
      d.setDialogTitle("Karolwelt laden");
      int status = d.showOpenDialog(null);
      File f = d.getSelectedFile();
      if ((f == null) || (status != 0))
      {
        ergeb = "";
        throw new RuntimeException("Keine Karolwelt-Datei ausgewaehlt.");
      }
      try
      {
        ergeb = f.getCanonicalPath();
      }
      catch (IOException localIOException1) {}
    }
    return ergeb;
  }
  
  private void loadKarolFile(String absFileName, boolean reload)
  {
    File f = new File(absFileName);
    try
    {
      Scanner s = new Scanner(f);
      String version = s.next();
      if ((version.equals("KarolVersion1Deutsch")) || (version.equals("KarolVersion2Deutsch")) || (version.equals("JavaKarolVersion1")) || (version.equals("JavaKarolVersion1.1")))
      {
        int breite = s.nextInt();
        int laenge = s.nextInt();
        int hoehe = s.nextInt();
        if (reload)
        {
          if ((breite != this.areaBreite) || (laenge != this.areaLaenge) || (hoehe != this.areaHoehe)) {
            throw new RuntimeException("Die Karolwelt-Datei wurde geaendert. Zuruecksetzen nicht moeglich.");
          }
        }
        else
        {
          init(breite, laenge, hoehe);
          this.weltDateiname = absFileName;
        }
        this.geladeneRoboter.clear();
        int posx = s.nextInt() + 1;
        int posy = s.nextInt() + 1;
        char direct = "SWNO".charAt(s.nextInt());
        this.geladeneRoboter.add(new RoboDat(posx, posy, direct));
        for (int a = 1; a <= this.areaBreite; a++) {
          for (int b = 1; b <= this.areaLaenge; b++)
          {
            for (int c = 0; c < this.areaHoehe; c++)
            {
              String token = s.next();
              if (token.equals("z")) {
                push(a, b, (byte)1);
              }
              if (token.equals("q")) {
                push(a, b, (byte)2);
              }
            }
            String token = s.next();
            if (token.equals("m")) {
              setMarker(a, b);
            }
          }
        }
        if ((version.equals("JavaKarolVersion1")) || (version.equals("JavaKarolVersion1.1"))) {
          this.geladeneRoboter.clear();
        }
        while (s.hasNextInt())
        {
          posx = s.nextInt();
          posy = s.nextInt();
          direct = s.next().charAt(0);
          this.geladeneRoboter.add(new RoboDat(posx, posy, direct));
        }
        s.close();
      }
      else
      {
        throw new RuntimeException("Die Datei ist keine Karolwelt-Datei.");
      }
    }
    catch (FileNotFoundException localFileNotFoundException) {}
  }
  
  private void saveWorldImage(String fileName)
  {
    File f = null;
    
    JFileChooser d = new JFileChooser();
    d.setFileFilter(new FileFilter()
    {
      public boolean accept(File f)
      {
        return (f.isDirectory()) || (f.getName().toLowerCase().endsWith(".bmp")) || (f.getName().toLowerCase().endsWith(".jpg"));
      }
      
      public String getDescription()
      {
        return "Bilder *.bmp; *.jpg";
      }
    });
    d.setDialogTitle("Bild der Welt speichern");
    if ((!fileName.toLowerCase().endsWith(".bmp")) && (!fileName.toLowerCase().endsWith(".jpg"))) {
      fileName = fileName + ".jpg";
    }
    f = new File(fileName);
    d.setSelectedFile(f);
    int status = d.showSaveDialog(null);
    if (status == 0)
    {
      f = d.getSelectedFile();
      if (f.exists())
      {
        status = JOptionPane.showConfirmDialog(null, "Die vorhandene Datei\n" + f.getName() + "\n?berschreiben?", 
          "Bild der Welt speichern", 0);
        if (status == 0) {
          try
          {
            f.delete();
          }
          catch (SecurityException localSecurityException) {}
        }
      }
      if (!f.exists())
      {
        String fn = f.getName().toLowerCase();
        if ((!fn.endsWith(".jpg")) && (!fn.endsWith(".bmp")))
        {
          JOptionPane.showMessageDialog(null, "Es werden nur die Grafikformate \njpg und bmp unterst?tzt.", 
            "Bild der Welt speichern", 0);
        }
        else
        {
          if (fn.endsWith(".jpg")) {
            this.weltFenster.bildAusgabe(f, "jpg");
          }
          if (fn.endsWith(".bmp")) {
            this.weltFenster.bildAusgabe(f, "bmp");
          }
        }
      }
    }
  }
  
  private void reset()
  {
    clear();
    if (this.weltDateiname.length() > 0) {
      loadKarolFile(this.weltDateiname, true);
    }
    for (int i = 0; i < this.alleRoboter.size(); i++)
    {
      Roboter robo = (Roboter)this.alleRoboter.get(i);
      robo.reset();
    }
  }
  
  private void randomBrick(int anzahl, int maxBrick)
  {
    Random zufall = new Random();
    
    anzahl = Math.min(anzahl, this.areaBreite * this.areaLaenge * this.areaHoehe);
    maxBrick = Math.min(Math.max(maxBrick, 1), this.areaHoehe);
    
    int i = 0;
    for (int j = 0; (i < anzahl) && (j < 3 * anzahl); j++)
    {
      int x = zufall.nextInt(this.areaBreite) + 1;
      int y = zufall.nextInt(this.areaLaenge) + 1;
      if ((this.areaStapelHoehe[x][y] < maxBrick) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1))) {
        push(x, y, (byte)1);
      } else {
        i--;
      }
      i++;
    }
  }
  
  int roboterAnmelden(Roboter anmeldeRoboter, boolean neu)
  {

    int neueKennung;
    if (neu)
    {
      if (this.nextRoboter > 9) {
        throw new RuntimeException("Maximal 9 Roboter erlaubt.");
      }
      neueKennung = this.nextRoboter;
    }
    else
    {
      neueKennung = anmeldeRoboter.KennungGeben();
    }
    int posX = anmeldeRoboter.PositionXGeben();
    int posY = anmeldeRoboter.PositionYGeben();
    if (getRobotID(posX, posY) > 0) {
      throw new RuntimeException("An dieser Stelle steht schon ein Roboter.");
    }
    if (isStone(posX, posY)) {
      throw new RuntimeException("An dieser Stelle steht ein Quader.");
    }
    if (neu) {
      this.nextRoboter += 1;
    }
    this.alleRoboter.add(anmeldeRoboter);
    this.areaStapelInvalid[posX][posY][Math.max(this.areaStapelHoehe[posX][posY] - 1, 0)] = true;
    return neueKennung;
  }
  
  void roboterAbmelden(Roboter abmeldeRoboter)
  {
    int posX = abmeldeRoboter.PositionXGeben();
    int posY = abmeldeRoboter.PositionYGeben();
    this.areaStapelInvalid[posX][posY][Math.max(this.areaStapelHoehe[posX][posY] - 1, 0)] = true;
    
    this.alleRoboter.remove(abmeldeRoboter);
  }
  
  RoboDat roboterDatenAbholen()
  {
    RoboDat robodat = new RoboDat(1, 1, 'S');
    
    int anzRoboter = this.alleRoboter.size();
    int anzRoboDaten = this.geladeneRoboter.size();
    if (anzRoboter < anzRoboDaten) {
      robodat = (RoboDat)this.geladeneRoboter.get(anzRoboter);
    }
    return robodat;
  }
  
  void fehlerMelden(String was)
  {
    this.weltFenster.fehlerAusgabe(was);
  }
  
  void paintWorld()
  {
    this.weltFenster.zeichnen();
  }
  
  boolean isInside(int x, int y)
  {
    return (x >= 1) && (x <= this.areaBreite) && (y >= 1) && (y <= this.areaLaenge);
  }
  
  void push(int x, int y, byte what)
  {
    if ((what == 1) && (this.areaStapelHoehe[x][y] < this.areaHoehe) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1) || (this.areaStapelInhalt[x][y][0] == 3) || (this.areaStapelInhalt[x][y][0] == 4) || (this.areaStapelInhalt[x][y][0] == 5) || (this.areaStapelInhalt[x][y][0] == 6) || (this.areaStapelInhalt[x][y][0] == 7) || (this.areaStapelInhalt[x][y][0] == 8) || (this.areaStapelInhalt[x][y][0] == 9)))
    {
      this.areaStapelInhalt[x][y][this.areaStapelHoehe[x][y]] = 1;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
      this.areaStapelHoehe[x][y] += 1;
    }
    if ((what == 2) && (this.areaStapelHoehe[x][y] == 0) && (this.areaMarkiert[x][y] == false))
    {
      this.areaStapelHoehe[x][y] = 2;
      this.areaStapelInhalt[x][y][0] = 2;
      this.areaStapelInhalt[x][y][1] = 2;
      this.areaStapelInvalid[x][y][0] = true;
      this.areaStapelInvalid[x][y][1] = true;
    }
     if ((what == 3) && (this.areaStapelHoehe[x][y] < this.areaHoehe) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1) || (this.areaStapelInhalt[x][y][0] == 3) || (this.areaStapelInhalt[x][y][0] == 4) || (this.areaStapelInhalt[x][y][0] == 5) || (this.areaStapelInhalt[x][y][0] == 6) || (this.areaStapelInhalt[x][y][0] == 7) || (this.areaStapelInhalt[x][y][0] == 8) || (this.areaStapelInhalt[x][y][0] == 9)))
    {
      this.areaStapelInhalt[x][y][this.areaStapelHoehe[x][y]] = 3;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
      this.areaStapelHoehe[x][y] += 1;
    }
    if ((what == 4) && (this.areaStapelHoehe[x][y] < this.areaHoehe) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1) || (this.areaStapelInhalt[x][y][0] == 3) || (this.areaStapelInhalt[x][y][0] == 4) || (this.areaStapelInhalt[x][y][0] == 5) || (this.areaStapelInhalt[x][y][0] == 6) || (this.areaStapelInhalt[x][y][0] == 7) || (this.areaStapelInhalt[x][y][0] == 8) || (this.areaStapelInhalt[x][y][0] == 9)))
    {
      this.areaStapelInhalt[x][y][this.areaStapelHoehe[x][y]] = 4;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
      this.areaStapelHoehe[x][y] += 1;
    }
    if ((what == 5) && (this.areaStapelHoehe[x][y] < this.areaHoehe) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1) || (this.areaStapelInhalt[x][y][0] == 3) || (this.areaStapelInhalt[x][y][0] == 4) || (this.areaStapelInhalt[x][y][0] == 5) || (this.areaStapelInhalt[x][y][0] == 6) || (this.areaStapelInhalt[x][y][0] == 7) || (this.areaStapelInhalt[x][y][0] == 8) || (this.areaStapelInhalt[x][y][0] == 9)))
    {
      this.areaStapelInhalt[x][y][this.areaStapelHoehe[x][y]] = 5;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
      this.areaStapelHoehe[x][y] += 1;
    }
    if ((what == 6) && (this.areaStapelHoehe[x][y] < this.areaHoehe) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1) || (this.areaStapelInhalt[x][y][0] == 3) || (this.areaStapelInhalt[x][y][0] == 4) || (this.areaStapelInhalt[x][y][0] == 5) || (this.areaStapelInhalt[x][y][0] == 6) || (this.areaStapelInhalt[x][y][0] == 7) || (this.areaStapelInhalt[x][y][0] == 8) || (this.areaStapelInhalt[x][y][0] == 9)))
    {
      this.areaStapelInhalt[x][y][this.areaStapelHoehe[x][y]] = 6;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
      this.areaStapelHoehe[x][y] += 1;
    }
    if ((what == 7) && (this.areaStapelHoehe[x][y] < this.areaHoehe) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1) || (this.areaStapelInhalt[x][y][0] == 3) || (this.areaStapelInhalt[x][y][0] == 4) || (this.areaStapelInhalt[x][y][0] == 5) || (this.areaStapelInhalt[x][y][0] == 6) || (this.areaStapelInhalt[x][y][0] == 7) || (this.areaStapelInhalt[x][y][0] == 8) || (this.areaStapelInhalt[x][y][0] == 9)))
    {
      this.areaStapelInhalt[x][y][this.areaStapelHoehe[x][y]] = 7;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
      this.areaStapelHoehe[x][y] += 1;
    }
    if ((what == 8) && (this.areaStapelHoehe[x][y] < this.areaHoehe) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1) || (this.areaStapelInhalt[x][y][0] == 3) || (this.areaStapelInhalt[x][y][0] == 4) || (this.areaStapelInhalt[x][y][0] == 5) || (this.areaStapelInhalt[x][y][0] == 6) || (this.areaStapelInhalt[x][y][0] == 7) || (this.areaStapelInhalt[x][y][0] == 8) || (this.areaStapelInhalt[x][y][0] == 9)))
    {
      this.areaStapelInhalt[x][y][this.areaStapelHoehe[x][y]] = 8;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
      this.areaStapelHoehe[x][y] += 1;
    }
    if ((what == 9) && (this.areaStapelHoehe[x][y] < this.areaHoehe) && ((this.areaStapelHoehe[x][y] == 0) || (this.areaStapelInhalt[x][y][0] == 1) || (this.areaStapelInhalt[x][y][0] == 3) || (this.areaStapelInhalt[x][y][0] == 4) || (this.areaStapelInhalt[x][y][0] == 5) || (this.areaStapelInhalt[x][y][0] == 6) || (this.areaStapelInhalt[x][y][0] == 7) || (this.areaStapelInhalt[x][y][0] == 8) || (this.areaStapelInhalt[x][y][0] == 9)))
    {
      this.areaStapelInhalt[x][y][this.areaStapelHoehe[x][y]] = 9;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
      this.areaStapelHoehe[x][y] += 1;
    }
  }
  
  void pop(int x, int y)
  {
    if (this.areaStapelHoehe[x][y] > 0)
    {
      if (this.areaStapelInhalt[x][y][0] == 1)
      {
        this.areaStapelInhalt[x][y][(this.areaStapelHoehe[x][y] - 1)] = 0;
        this.areaStapelInvalid[x][y][(this.areaStapelHoehe[x][y] - 1)] = true;
        this.areaStapelHoehe[x][y] -= 1;
      }
      if (this.areaStapelInhalt[x][y][0] == 2)
      {
        this.areaStapelHoehe[x][y] = 0;
        this.areaStapelInhalt[x][y][0] = 0;
        this.areaStapelInhalt[x][y][1] = 0;
        this.areaStapelInvalid[x][y][0] = true;
        this.areaStapelInvalid[x][y][1] = true;
      }
      if (this.areaStapelInhalt[x][y][0] == 3)
      {
        this.areaStapelInhalt[x][y][(this.areaStapelHoehe[x][y] - 1)] = 0;
        this.areaStapelInvalid[x][y][(this.areaStapelHoehe[x][y] - 1)] = true;
        this.areaStapelHoehe[x][y] -= 1;
      }
      if (this.areaStapelInhalt[x][y][0] == 4)
      {
        this.areaStapelInhalt[x][y][(this.areaStapelHoehe[x][y] - 1)] = 0;
        this.areaStapelInvalid[x][y][(this.areaStapelHoehe[x][y] - 1)] = true;
        this.areaStapelHoehe[x][y] -= 1;
      }
      if (this.areaStapelInhalt[x][y][0] == 5)
      {
        this.areaStapelInhalt[x][y][(this.areaStapelHoehe[x][y] - 1)] = 0;
        this.areaStapelInvalid[x][y][(this.areaStapelHoehe[x][y] - 1)] = true;
        this.areaStapelHoehe[x][y] -= 1;
      }
      if (this.areaStapelInhalt[x][y][0] == 6)
      {
        this.areaStapelInhalt[x][y][(this.areaStapelHoehe[x][y] - 1)] = 0;
        this.areaStapelInvalid[x][y][(this.areaStapelHoehe[x][y] - 1)] = true;
        this.areaStapelHoehe[x][y] -= 1;
      }
      if (this.areaStapelInhalt[x][y][0] == 7)
      {
        this.areaStapelInhalt[x][y][(this.areaStapelHoehe[x][y] - 1)] = 0;
        this.areaStapelInvalid[x][y][(this.areaStapelHoehe[x][y] - 1)] = true;
        this.areaStapelHoehe[x][y] -= 1;
      }
      if (this.areaStapelInhalt[x][y][0] == 8)
      {
        this.areaStapelInhalt[x][y][(this.areaStapelHoehe[x][y] - 1)] = 0;
        this.areaStapelInvalid[x][y][(this.areaStapelHoehe[x][y] - 1)] = true;
        this.areaStapelHoehe[x][y] -= 1;
      }
      if (this.areaStapelInhalt[x][y][0] == 9)
      {
        this.areaStapelInhalt[x][y][(this.areaStapelHoehe[x][y] - 1)] = 0;
        this.areaStapelInvalid[x][y][(this.areaStapelHoehe[x][y] - 1)] = true;
        this.areaStapelHoehe[x][y] -= 1;
      }
    }
  }
  
  byte getPart(int x, int y, int z)
  {
    byte ergeb;
    if ((x <= 0) || (x > this.areaBreite) || (y <= 0) || (y > this.areaLaenge))
    {
      ergeb = 0;
    }
    else
    {
      if ((this.areaStapelHoehe[x][y] <= 0) || (this.areaStapelHoehe[x][y] <= z)) {
        ergeb = 0;
      } else {
        ergeb = this.areaStapelInhalt[x][y][z];
      }
    }
    return ergeb;
  }
  
  int brickCount(int x, int y)
  {
    int ergeb = 0;
    if (getPart(x, y, 0) == 1 || getPart(x, y, 0) == 3 || getPart(x, y, 0) == 4 || getPart(x, y, 0) == 5 || getPart(x, y, 0) == 6 || getPart(x, y, 0) == 7 || getPart(x, y, 0) == 8  || getPart(x, y, 0) == 9) {
      ergeb = this.areaStapelHoehe[x][y];
    }
    return ergeb;
  }
  
  boolean isMaxTop(int x, int y)
  {
    return this.areaStapelHoehe[x][y] >= this.areaHoehe;
  }
  
  boolean isStone(int x, int y)
  {
    return getPart(x, y, 0) == 2;
  }
  
  boolean isBrick(int x, int y)
  {
    return getPart(x, y, 0) == 1;
  }
  
  void setMarker(int x, int y)
  {
    if (this.areaMarkiert[x][y] == false)
    {
      this.areaMarkiert[x][y] = true;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
    }
  }
  
  void deleteMarker(int x, int y)
  {
    if (this.areaMarkiert[x][y] != false)
    {
      this.areaMarkiert[x][y] = false;
      this.areaStapelInvalid[x][y][this.areaStapelHoehe[x][y]] = true;
    }
  }
  
  boolean isMarker(int x, int y)
  {
    return this.areaMarkiert[x][y];
  }
  
  int getRobotID(int x, int y)
  {
    int ergeb = 0;
    int anzRoboter = this.alleRoboter.size();
    Roboter robo = null;
    for (int i = 0; i < anzRoboter; i++)
    {
      robo = (Roboter)this.alleRoboter.get(i);
      if ((robo.PositionXGeben() == x) && (robo.PositionYGeben() == y)) {
        ergeb = robo.KennungGeben();
      }
    }
    return ergeb;
  }
  
  boolean isRobotInSight(int x, int y, char blickrichtung)
  {
    boolean ergeb = false;
    boolean abbruch = false;
    int richtung = "SWNO".indexOf(blickrichtung);
    while ((!ergeb) && (!abbruch) && (isInside(x, y)))
    {
      switch (richtung)
      {
      case 0: 
        y++; break;
      case 1: 
        x--; break;
      case 2: 
        y--; break;
      case 3: 
        x++; break;
      default: 
        y++;
      }
      if (isInside(x, y)) {
        if (getRobotID(x, y) > 0) {
          ergeb = true;
        } else {
          abbruch = (isBrick(x, y)) || (isStone(x, y));
        }
      }
    }
    return ergeb;
  }
  
  void setTopInvalid(int x, int y)
  {
    this.areaStapelInvalid[x][y][Math.max(this.areaStapelHoehe[x][y] - 1, 0)] = true;
  }
  
  class RoboDat
  {
    int posX = 0;
    int posY = 0;
    char direct = 'S';
    
    RoboDat(int x, int y, char b)
    {
      this.posX = x;
      this.posY = y;
      this.direct = b;
    }
  }
}
