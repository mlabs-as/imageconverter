package com.mobiletech.imageconverter.gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;

public class ScenarioSelect extends JFrame {

    private JPanel jContentPane = null;
    private JTextField heading = null;
    private JScrollPane scenarioSelector = null;
    /**
     * This is the default constructor
     */
    public ScenarioSelect() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(378, 282);
        this.setContentPane(getJContentPane());
        this.setTitle("JFrame");
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getHeading(), java.awt.BorderLayout.NORTH);
            jContentPane.add(getScenarioSelector(), java.awt.BorderLayout.SOUTH);
        }
        return jContentPane;
    }

    /**
     * This method initializes heading	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getHeading() {
        if (heading == null) {
            heading = new JTextField();
            heading.setText("Select Scenarios to include in test");
        }
        return heading;
    }

    /**
     * This method initializes scenarioSelector	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getScenarioSelector() {
        if (scenarioSelector == null) {
            scenarioSelector = new JScrollPane();            
        }
        return scenarioSelector;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
