package ofeed.swing;

import java.awt.GraphicsEnvironment;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.GroupLayout.Alignment;

public class PanelEnterText extends JPanel{
    int textrows = 24;
    int textcols = 48;
    JLabel dialogue;
    JTextArea enterText;
    JScrollPane scrollPane;

    public PanelEnterText(String inText){
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        textrows = env.getMaximumWindowBounds().height/48;
        textcols = env.getMaximumWindowBounds().width/24;

        dialogue = new JLabel(inText);
        enterText = new JTextArea("",textrows,textcols);
        scrollPane = new JScrollPane(enterText);

        GroupLayout layout = new GroupLayout(this); this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER)
            .addComponent(dialogue)
            .addComponent(scrollPane)        
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(dialogue)
            .addComponent(scrollPane)  
        );
    }

    public String[] getText(){
        String[] retur = enterText.getText().split("\n"); enterText.setText("");
        return retur;
    }
}
