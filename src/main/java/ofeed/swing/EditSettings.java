package ofeed.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;

import ofeed.funcs.WindowFuncs;
import ofeed.model.OptionVars;

public class EditSettings extends JPanel implements ActionListener {

    private OptionVars options;

    private JLabel name_SameFolder, name_SameTags, name_Any, name_BigList, name_HistoryMax, name_ModSuccessor, name_ModFind, name_ModTime;
    private JSpinner spin_SameFolder, spin_SameTags, spin_Any, spin_BigList, spin_HistoryMax;
    private JCheckBox check_ModSuccessor, check_ModFind, check_ModTime;

    public EditSettings() {
        super.setMinimumSize(new Dimension(200,200));

        //this.set//nonresizable
        spin_SameFolder = new JSpinner(new SpinnerNumberModel (0, 0, 999, 1));
        spin_SameFolder.setToolTipText("Number of files recommended from within the same folder.");
        name_SameFolder = new JLabel("Same Folder Recommendations"); name_SameFolder.setLabelFor(spin_SameFolder);
        spin_SameTags = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        spin_SameTags.setToolTipText("Number of files recommended from within the same tags");
        name_SameTags = new JLabel("Same Tag Recommendations"); name_SameTags.setLabelFor(spin_SameTags);
        spin_Any = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        spin_Any.setToolTipText("Number of files recommended from any of this feed file's folders");
        name_Any = new JLabel("Random Recommendations"); name_Any.setLabelFor(spin_Any);
        spin_BigList = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        spin_BigList.setToolTipText("Number of files shown on the frontpage");
        name_BigList = new JLabel("Frontpage Recommendations"); name_BigList.setLabelFor(spin_BigList);
        spin_HistoryMax = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        spin_HistoryMax.setToolTipText("Maximum pages each tab can remember and go back to");
        name_HistoryMax = new JLabel("History Max Length"); name_HistoryMax.setLabelFor(spin_HistoryMax);

        check_ModSuccessor = new JCheckBox(); check_ModSuccessor.setSelected(false);
        check_ModSuccessor.setToolTipText("Amongst recommendations, one entry deemed as successor (according to metadata number) is addded");
        name_ModSuccessor = new JLabel("Enable successor mod"); name_ModSuccessor.setLabelFor(check_ModSuccessor);
        check_ModFind = new JCheckBox(); check_ModSuccessor.setSelected(true);
        check_ModFind.setToolTipText("Amongst recommendations, some entries with similiar words in the name will be added");
        name_ModFind = new JLabel("Enable name mod"); name_ModFind.setLabelFor(check_ModFind);
        check_ModTime = new JCheckBox(); check_ModSuccessor.setSelected(true);
        check_ModTime.setToolTipText("Amongst recommendations, some entries will be within 2 years timespan of current item");
        name_ModTime = new JLabel("Enable time mod"); name_ModTime.setLabelFor(check_ModTime);

        //Setup menu
        GroupLayout layout = new GroupLayout(this); this.setLayout(layout);
        layout.setAutoCreateGaps(true); layout.setAutoCreateContainerGaps(true);
        layout.linkSize(SwingConstants.HORIZONTAL, name_SameFolder, name_SameTags); layout.linkSize(SwingConstants.HORIZONTAL, name_SameTags, name_Any);  layout.linkSize(SwingConstants.HORIZONTAL, name_Any, name_BigList);  layout.linkSize(SwingConstants.HORIZONTAL, name_BigList, name_HistoryMax);
        layout.linkSize(SwingConstants.HORIZONTAL, spin_SameFolder, spin_SameTags); layout.linkSize(SwingConstants.HORIZONTAL, spin_SameTags, spin_Any);  layout.linkSize(SwingConstants.HORIZONTAL, spin_Any, spin_BigList);  layout.linkSize(SwingConstants.HORIZONTAL, spin_BigList, spin_HistoryMax);
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addComponent(name_SameFolder)
                .addComponent(spin_SameFolder))
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addComponent(name_SameTags)
                .addComponent(spin_SameTags))
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addComponent(name_Any)
                .addComponent(spin_Any))
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addComponent(name_BigList)
                .addComponent(spin_BigList))
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addComponent(name_HistoryMax)
                .addComponent(spin_HistoryMax))
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addComponent(name_ModSuccessor)
                .addComponent(check_ModSuccessor))
            /*.addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addComponent(name_ModFind)
                .addComponent(check_ModFind))
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addComponent(name_ModTime)
                .addComponent(check_ModTime))*/
        );
        layout.setHorizontalGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
                .addComponent(name_SameFolder)
                .addComponent(spin_SameFolder))
            .addGroup(layout.createSequentialGroup()
                .addComponent(name_SameTags)
                .addComponent(spin_SameTags))
            .addGroup(layout.createSequentialGroup()
                .addComponent(name_Any)
                .addComponent(spin_Any))
            .addGroup(layout.createSequentialGroup()
                .addComponent(name_BigList)
                .addComponent(spin_BigList))
            .addGroup(layout.createSequentialGroup()
                .addComponent(name_HistoryMax)
                .addComponent(spin_HistoryMax))
            .addGroup(layout.createSequentialGroup()
                .addComponent(name_ModSuccessor)
                .addComponent(check_ModSuccessor))
            /*.addGroup(layout.createSequentialGroup()
                .addComponent(name_ModFind)
                .addComponent(check_ModFind))
            .addGroup(layout.createSequentialGroup()
                .addComponent(name_ModTime)
                .addComponent(check_ModTime)) */
          )
        );
    }
    public void setOptions(OptionVars optionsInV){
        options = optionsInV;
        spin_SameFolder.setValue(options.numberOfSameFolder());
        spin_SameTags.setValue(options.numberOfSameTags());
        spin_Any.setValue(options.numberOfAny());
        spin_BigList.setValue(options.numberOfBigList());
        spin_HistoryMax.setValue(options.historyMaxLength());
        check_ModSuccessor.setSelected(options.modSuccessor());
        check_ModFind.setSelected(options.modName());
        check_ModTime.setSelected(options.modTime());
    }
    /*@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        /*if (Openvar) {
            JOptionPane bob = new JOptionPane(g2);
            JOptionPane.showMessageDialog(bob, "heaew" ,"no" ,JOptionPane.ERROR_MESSAGE);
        }
    }*/
    @Override
    public void actionPerformed(ActionEvent a) {
        if ("Save" == a.getActionCommand()) {
            options = new OptionVars((int) spin_SameFolder.getValue(), (int) spin_SameTags.getValue(), (int) spin_Any.getValue(), (int) spin_BigList.getValue(), (int) spin_HistoryMax.getValue(), (boolean) check_ModSuccessor.isSelected(), (boolean) check_ModFind.isSelected(), (boolean) check_ModTime.isSelected());
            WindowFuncs.close(this);
        }
        if ("Cancel" == a.getActionCommand()) {
            WindowFuncs.close(this);
        }
    }
    
    public int getSameFolder(){ return (int) spin_SameFolder.getValue();
    }
    public int getSameTags(){ return (int) spin_SameTags.getValue();
    }
    public int getAny(){ return (int) spin_Any.getValue();
    }
    public int getBigList(){ return (int) spin_BigList.getValue();
    }
    public int getHistoryMax(){ return (int) spin_HistoryMax.getValue();
    }
    public boolean getModSuccessor(){ return (boolean) check_ModSuccessor.isSelected();
    }
    public boolean getModFind(){ return (boolean) check_ModFind.isSelected();
    }
    public boolean getModTime(){ return (boolean) check_ModTime.isSelected();
    }
}
