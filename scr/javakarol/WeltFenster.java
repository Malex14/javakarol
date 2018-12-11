package javakarol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;
import javax.*;
import java.*;


public class WeltFenster
{
  private Welt welt;
  private JFrame fenster;
  private WeltAnzeige anzeigeWelt;
  private JTextArea fehlerText;
  Color background_color = new Color(0x404040);
  WeltFenster(Welt welt)
  {
    this.welt = welt;
    
    this.fenster = new JFrame("JavaKarol");
    this.fenster.setLocation(50, 50);
    this.fenster.setResizable(true);
    this.fenster.setMinimumSize(new Dimension(200, 200));
    this.fenster.setDefaultCloseOperation(3);
    
    JPanel contentPane = (JPanel)this.fenster.getContentPane();
    contentPane.setBorder(new EmptyBorder(6, 6, 6, 6));
    //contentPane.setLayout(new BorderLayout(6, 6));
    contentPane.setBackground(background_color);
    this.anzeigeWelt = new WeltAnzeige(this.welt);
    JScrollPane scrollPane = new JScrollPane(this.anzeigeWelt);
    //scrollPane.setBorder(new EtchedBorder());
    scrollPane.getViewport().setBackground(background_color);
    
    contentPane.add(scrollPane, "Center");
    
    this.fehlerText = new JTextArea();
    //this.fehlerText.setBorder(BorderFactory.createLineBorder(new Color(0x505050), 2));
    this.fehlerText.setBorder(new EmptyBorder(6, 6, 6, 6));
    this.fehlerText.setEnabled(false);
    this.fehlerText.setDisabledTextColor(Color.WHITE);
    this.fehlerText.setBackground(background_color);
    this.fehlerText.setFont(new Font("Arial", 0, 12));
    this.fehlerText.setLineWrap(true);
    this.fehlerText.setWrapStyleWord(true);
    this.fehlerText.setText("");
    this.fehlerText.setPreferredSize(new Dimension(this.anzeigeWelt.getWidth(), 40));
    contentPane.add(this.fehlerText, "South");
    
    this.fenster.pack();
    this.fenster.setVisible(true);
  }
  
  public void ganzZeichnen()
  {
    this.anzeigeWelt.zeichneWeltGanz();
    this.fenster.toFront();
  }
  
  public void zeichnen()
  {
    this.anzeigeWelt.zeichneWelt();
  }
  
  public void fehlerAusgabe(String t)
  {
    this.fehlerText.setText(t);
    this.fenster.toFront();
  }
  
  public void bildAusgabe(File f, String fileTyp)
  {
    this.anzeigeWelt.paintToFile(f, fileTyp);
  }
}