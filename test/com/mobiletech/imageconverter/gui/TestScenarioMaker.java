package com.mobiletech.imageconverter.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TestScenarioMaker extends JFrame implements ActionListener{
    final   String[]    formats =        
    {
        "All",
        "Jpeg",
        "Gif (All)",
        "Gif (Animated Only)",
        "Gif (Not Animated)",
        "Png",
        "Bmp",
        "WBmp",
        "Tif"
    };
    JTextField name = new JTextField(10);
    JComboBox format = new JComboBox();
    
    public TestScenarioMaker(){
        super("Event Handler Demo");
        init();
    }
    
    private void init(){
        this.setLayout(new GridLayout(2, 3));
        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        for( int iCtr = 0; iCtr < formats.length; iCtr++ )
            format.addItem( formats[iCtr] );        
        
        this.getContentPane().add(new JLabel("Scenario Name"));
        this.getContentPane().add(name);
        this.getContentPane().add(new JLabel("Run for image format"));
        this.getContentPane().add(format);
        
        //this.pack();
        this.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();
     /*
      if (source == pressme)
      {
        
      }
      */
    }
    
    public static void main(String[] args) {new TestScenarioMaker();}
}
