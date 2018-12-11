package javakarol;

import java.awt.Point;
import java.awt.Toolkit;

public class Roboter
{
  private int positionX;
  private int positionY;
  private char blickrichtung = 'S';
  public static Welt meineWelt;
  private int sprunghoehe = 1;
  private int rucksackInhalt = 0;
  private int rucksackMaximum = 0;
  private boolean rucksackPruefen = false;
  private int verzoegerung = 300;
  private int kennung;
  private boolean sichtbar;
  private int startX;
  private int startY;
  private char startBlickrichtung;
  
  public Roboter(int startX, int startY, char startBlickrichtung, Welt inWelt)
  {
    this.meineWelt = inWelt;
    this.positionX = Math.min(Math.max(startX, 0), this.meineWelt.areaBreite);
    this.positionY = Math.min(Math.max(startY, 0), this.meineWelt.areaLaenge);
    if ("SWNO".indexOf(startBlickrichtung) >= 0) {
      this.blickrichtung = startBlickrichtung;
    }
    this.kennung = this.meineWelt.roboterAnmelden(this, true);
    this.startX = startX;
    this.startY = startY;
    this.startBlickrichtung = startBlickrichtung;
    this.sichtbar = true;
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public Roboter(Welt inWelt)
  {
    this.meineWelt = inWelt;
    
    this.positionX = this.meineWelt.roboterDatenAbholen().posX;
    this.positionY = this.meineWelt.roboterDatenAbholen().posY;
    this.blickrichtung = this.meineWelt.roboterDatenAbholen().direct;
    this.kennung = this.meineWelt.roboterAnmelden(this, true);
    this.startX = this.positionX;
    this.startY = this.positionY;
    this.startBlickrichtung = this.blickrichtung;
    this.sichtbar = true;
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public String toString()
  {
    String teil1 = "Roboter" + this.kennung + " an der Stelle (" + 
      this.positionX + ";" + this.positionY + ") mit Blickrichtung " + this.blickrichtung;
    String teil2 = " und Rucksackinhalt " + this.rucksackInhalt;
    if (this.rucksackPruefen) {
      return teil1 + teil2;
    }
    return teil1;
  }
  
  public void VerzoegerungSetzen(int msec)
  {
    this.verzoegerung = Math.abs(msec);
  }
  
  public void SprunghoeheSetzen(int neueHoehe)
  {
    this.sprunghoehe = Math.max(neueHoehe, 1);
  }
  
  public void RucksackMaximumSetzen(int maxZiegel)
  {
    this.rucksackMaximum = Math.abs(maxZiegel);
    this.rucksackPruefen = (this.rucksackMaximum > 0);
  }
  
  public void UnsichtbarMachen()
  {
    if (this.sichtbar)
    {
      this.meineWelt.roboterAbmelden(this);
      this.sichtbar = false;
      this.meineWelt.paintWorld();
    }
  }
  
  public void SichtbarMachen()
  {
    if (!this.sichtbar)
    {
      this.meineWelt.roboterAnmelden(this, false);
      this.sichtbar = true;
      this.meineWelt.paintWorld();
    }
  }
  
  public void Schritt()
  {
    int neuX = getPositionVorne().x;
    int neuY = getPositionVorne().y;
    if (!this.meineWelt.isInside(neuX, neuY))
    {
      this.meineWelt.fehlerMelden(toString() + " ist an der Wand angestoßen.");
      throw new RuntimeException("Roboterbewegung nicht moeglich.");
    }
    if (this.meineWelt.isStone(neuX, neuY))
    {
      this.meineWelt.fehlerMelden(toString() + " ist am Quader angestoßen.");
      throw new RuntimeException("Roboterbewegung nicht moeglich.");
    }
    if (this.meineWelt.getRobotID(neuX, neuY) > 0)
    {
      this.meineWelt.fehlerMelden(toString() + " ist an anderem Roboter angesto?en.");
      throw new RuntimeException("Roboterbewegung nicht moeglich.");
    }
    if (Math.abs(this.meineWelt.brickCount(this.positionX, this.positionY) - this.meineWelt.brickCount(neuX, neuY)) > this.sprunghoehe)
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht so hoch/tief springen.");
      throw new RuntimeException("Roboter kann nicht so hoch/tief springen.");
    }
    this.meineWelt.setTopInvalid(this.positionX, this.positionY);
    this.positionX = neuX;
    this.positionY = neuY;
    this.meineWelt.setTopInvalid(this.positionX, this.positionY);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void LinksDrehen()
  {
    switch (this.blickrichtung)
    {
    case 'N': 
      this.blickrichtung = 'W';
      break;
    case 'S': 
      this.blickrichtung = 'O';
      break;
    case 'O': 
      this.blickrichtung = 'N';
      break;
    case 'W': 
      this.blickrichtung = 'S';
    }
    this.meineWelt.setTopInvalid(this.positionX, this.positionY);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void RechtsDrehen()
  {
    switch (this.blickrichtung)
    {
    case 'N': 
      this.blickrichtung = 'O';
      break;
    case 'S': 
      this.blickrichtung = 'W';
      break;
    case 'O': 
      this.blickrichtung = 'S';
      break;
    case 'W': 
      this.blickrichtung = 'N';
    }
    this.meineWelt.setTopInvalid(this.positionX, this.positionY);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void Hinlegen()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    if (!this.meineWelt.isInside(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht hinlegen, er steht vor der Wand.");
      throw new RuntimeException("Roboter kann nicht hinlegen.");
    }
    if (this.meineWelt.isStone(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht hinlegen, er steht vor einem Quader.");
      throw new RuntimeException("Roboter kann nicht hinlegen.");
    }
    if (this.meineWelt.getRobotID(vorneX, vorneY) > 0)
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht hinlegen, er steht vor einem anderen Roboter.");
      throw new RuntimeException("Roboter kann nicht hinlegen.");
    }
    if (this.meineWelt.isMaxTop(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht hinlegen, die maximale Stapelhöhe ist erreicht.");
      throw new RuntimeException("Roboter kann nicht hinlegen.");
    }
    if (this.rucksackPruefen)
    {
      if (this.rucksackInhalt < 1)
      {
        this.meineWelt.fehlerMelden(toString() + " hat keine Ziegel mehr zum Hinlegen.");
        throw new RuntimeException("Roboter kann nicht hinlegen.");
      }
      this.rucksackInhalt -= 1;
    }
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)1);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void Hinlegen(String Farbe){
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    if (!this.meineWelt.isInside(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht hinlegen, er steht vor der Wand.");
      throw new RuntimeException("Roboter kann nicht hinlegen.");
    }
    if (this.meineWelt.isStone(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht hinlegen, er steht vor einem Quader.");
      throw new RuntimeException("Roboter kann nicht hinlegen.");
    }
    if (this.meineWelt.getRobotID(vorneX, vorneY) > 0)
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht hinlegen, er steht vor einem anderen Roboter.");
      throw new RuntimeException("Roboter kann nicht hinlegen.");
    }
    if (this.meineWelt.isMaxTop(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht hinlegen, die maximale Stapelhöhe ist erreicht.");
      throw new RuntimeException("Roboter kann nicht hinlegen.");
    }
    if (this.rucksackPruefen)
    {
      if (this.rucksackInhalt < 1)
      {
        this.meineWelt.fehlerMelden(toString() + " hat keine Ziegel mehr zum Hinlegen.");
        throw new RuntimeException("Roboter kann nicht hinlegen.");
      }
      this.rucksackInhalt -= 1;
    }
    switch(Farbe){
    case "rot":
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)1);
    break;
    case "gelb":
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)3);
    break;
    case "blau":
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)4);
    break;
    case "gruen":
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)5);
    break;
    case "durchsichtig":
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)6);
    break;
    case "grau":
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)7);
    break;
    case "schwarz":
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)8);
    break;
    case "weiss":
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)9);
    break;
    default:
    this.meineWelt.fehlerMelden(toString() + " kann diesen Ziegel nicht finden: " + Farbe);
    throw new RuntimeException("Karol kann diesen Ziegel nicht finden: " + Farbe);
    }
    
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  
  
  public void Aufheben()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    if (!this.meineWelt.isInside(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht aufheben, er steht vor der Wand.");
      throw new RuntimeException("Roboter kann nicht aufheben.");
    }
    if (this.meineWelt.isStone(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht aufheben, er steht vor einem Quader.");
      throw new RuntimeException("Roboter kann nicht aufheben.");
    }
    if (this.meineWelt.getRobotID(vorneX, vorneY) > 0)
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht aufheben, er steht vor einem anderen Roboter.");
      throw new RuntimeException("Roboter kann nicht aufheben.");
    }
    if (this.meineWelt.brickCount(vorneX, vorneY) < 1)
    {
      this.meineWelt.fehlerMelden(toString() + " kann nicht aufheben, vor ihm liegen keine Ziegel.");
      throw new RuntimeException("Roboter kann nicht aufheben.");
    }
    if (this.rucksackPruefen)
    {
      if (this.rucksackInhalt >= this.rucksackMaximum)
      {
        this.meineWelt.fehlerMelden(toString() + " kann nicht aufheben, das maximale Tragverm?gen ist erreicht.");
        throw new RuntimeException("Roboter kann nicht aufheben.");
      }
      this.rucksackInhalt += 1;
    }
    this.meineWelt.pop(vorneX, vorneY);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void QuaderAufstellen()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    if (!this.meineWelt.isInside(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann Quader nicht aufstellen, er steht vor der Wand.");
      throw new RuntimeException("Roboter kann Quader nicht aufstellen.");
    }
    if (this.meineWelt.isStone(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann Quader nicht aufstellen, er steht vor einem Quader.");
      throw new RuntimeException("Roboter kann Quader nicht aufstellen.");
    }
    if (this.meineWelt.getRobotID(vorneX, vorneY) > 0)
    {
      this.meineWelt.fehlerMelden(toString() + " kann Quader nicht aufstellen, er steht vor einem anderen Roboter.");
      throw new RuntimeException("Roboter kann Quader nicht aufstellen.");
    }
    if (this.meineWelt.brickCount(vorneX, vorneY) > 0)
    {
      this.meineWelt.fehlerMelden(toString() + " kann Quader nicht aufstellen, vor ihm liegen Ziegel.");
      throw new RuntimeException("Roboter kann Quader nicht aufstellen.");
    }
    if (this.meineWelt.isMarker(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann Quader nicht aufstellen, vor ihm ist eine Marke.");
      throw new RuntimeException("Roboter kann Quader nicht aufstellen.");
    }
    this.meineWelt.getClass();this.meineWelt.push(vorneX, vorneY, (byte)2);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void QuaderEntfernen()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    if (!this.meineWelt.isInside(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann Quader nicht entfernen, er steht vor der Wand.");
      throw new RuntimeException("Roboter kann Quader nicht entfernen.");
    }
    if (this.meineWelt.getRobotID(vorneX, vorneY) > 0)
    {
      this.meineWelt.fehlerMelden(toString() + " kann Quader nicht entfernen., er steht vor einem anderen Roboter.");
      throw new RuntimeException("Roboter kann Quader nicht entfernen.");
    }
    if (!this.meineWelt.isStone(vorneX, vorneY))
    {
      this.meineWelt.fehlerMelden(toString() + " kann Quader nicht entfernen., er steht vor keinem Quader.");
      throw new RuntimeException("Roboter kann Quader nicht entfernen..");
    }
    this.meineWelt.pop(vorneX, vorneY);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void MarkeSetzen()
  {
    this.meineWelt.setMarker(this.positionX, this.positionY);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void MarkeLoeschen()
  {
    this.meineWelt.deleteMarker(this.positionX, this.positionY);
    this.meineWelt.paintWorld();
    wartenIntern();
  }
  
  public void TonErzeugen()
  {
    Toolkit.getDefaultToolkit().beep();
  }
  
  public void Warten(long dauer)
  {
    try
    {
      Thread.sleep((dauer * 1000));
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public void MeldungAusgeben(String was)
  {
    this.meineWelt.fehlerMelden(toString() + " sagt: " + was);
  }
  
  public boolean IstWand()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    
    return (!this.meineWelt.isInside(vorneX, vorneY)) || (this.meineWelt.isStone(vorneX, vorneY));
  }
  
  public boolean IstZiegel()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    
    return this.meineWelt.isBrick(vorneX, vorneY);
  }
  
  public boolean IstZiegelLinks()
  {
    int linksX = getPositionLinks().x;
    int linksY = getPositionLinks().y;
    
    return this.meineWelt.isBrick(linksX, linksY);
  }
  
  public boolean IstZiegelRechts()
  {
    int rechtsX = getPositionRechts().x;
    int rechtsY = getPositionRechts().y;
    
    return this.meineWelt.isBrick(rechtsX, rechtsY);
  }
  
  public boolean IstMarke()
  {
    return this.meineWelt.isMarker(this.positionX, this.positionY);
  }
  
  public boolean IstRoboter()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    
    return this.meineWelt.getRobotID(vorneX, vorneY) > 0;
  }
  
  public boolean IstRoboterInSicht()
  {
    return this.meineWelt.isRobotInSight(this.positionX, this.positionY, this.blickrichtung);
  }
  
  public boolean IstBlickNorden()
  {
    return this.blickrichtung == 'N';
  }
  
  public boolean IstBlickSueden()
  {
    return this.blickrichtung == 'S';
  }
  
  public boolean IstBlickOsten()
  {
    return this.blickrichtung == 'O';
  }
  
  public boolean IstBlickWesten()
  {
    return this.blickrichtung == 'W';
  }
  
  public boolean IstRucksackVoll()
  {
    return this.rucksackInhalt >= this.rucksackMaximum;
  }
  
  public boolean IstRucksackLeer()
  {
    return this.rucksackInhalt == 0;
  }
  
  public boolean HatZiegelImRucksack()
  {
    return this.rucksackInhalt > 0;
  }
  
  public int PositionXGeben()
  {
    return this.positionX;
  }
  
  public int PositionYGeben()
  {
    return this.positionY;
  }
  
  public char BlickrichtungGeben()
  {
    return this.blickrichtung;
  }
  
  int getBlickrichtungNr()
  {
    return "SWNO".indexOf(this.blickrichtung);
  }
  
  public int SprungshoeheGeben()
  {
    return this.sprunghoehe;
  }
  
  public int KennungGeben()
  {
    return this.kennung;
  }
  
  public boolean SichtbarkeitGeben()
  {
    return this.sichtbar;
  }
  
  public int AnzahlZiegelRucksackGeben()
  {
    return this.rucksackInhalt;
  }
  
  public int AnzahlZiegelVorneGeben()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    
    return this.meineWelt.brickCount(vorneX, vorneY);
  }
  
  public int RoboterVorneKennungGeben()
  {
    int vorneX = getPositionVorne().x;
    int vorneY = getPositionVorne().y;
    
    return this.meineWelt.getRobotID(vorneX, vorneY);
  }
  
  private void wartenIntern()
  {
    try
    {
      Thread.sleep(this.verzoegerung);
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  private Point getPositionVorne()
  {
    Point pos = new Point(this.positionX, this.positionY);
    switch (this.blickrichtung)
    {
    case 'N': 
      pos.y -= 1; break;
    case 'S': 
      pos.y += 1; break;
    case 'O': 
      pos.x += 1; break;
    case 'W': 
      pos.x -= 1;
    }
    return pos;
  }
  
  private Point getPositionLinks()
  {
    Point pos = new Point(this.positionX, this.positionY);
    switch (this.blickrichtung)
    {
    case 'N': 
      pos.x -= 1; break;
    case 'S': 
      pos.x += 1; break;
    case 'O': 
      pos.y -= 1; break;
    case 'W': 
      pos.y += 1;
    }
    return pos;
  }
  
  private Point getPositionRechts()
  {
    Point pos = new Point(this.positionX, this.positionY);
    switch (this.blickrichtung)
    {
    case 'N': 
      pos.x += 1; break;
    case 'S': 
      pos.x -= 1; break;
    case 'O': 
      pos.y += 1; break;
    case 'W': 
      pos.y -= 1;
    }
    return pos;
  }
  
  void reset()
  {
    int k = this.meineWelt.getRobotID(this.startX, this.startY);
    if ((this.meineWelt.isInside(this.startX, this.startY)) && 
      (!this.meineWelt.isStone(this.startX, this.startY)) && (
      (k == this.kennung) || (k == 0)))
    {
      this.positionX = this.startX;
      this.positionY = this.startY;
      this.blickrichtung = this.startBlickrichtung;
    }
    else
    {
      for (int x = 1; x < this.meineWelt.areaBreite; x++) {
        for (int y = 1; y < this.meineWelt.areaLaenge; y++)
        {
          k = this.meineWelt.getRobotID(x, y);
          if ((this.meineWelt.isInside(x, y)) && 
            (!this.meineWelt.isStone(x, y)) && (
            (k == this.kennung) || (k == 0)))
          {
            this.positionX = x;
            this.positionY = y;
            this.blickrichtung = this.startBlickrichtung;
          }
        }
      }
    }
  }
}
