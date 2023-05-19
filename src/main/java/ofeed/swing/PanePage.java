package ofeed.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.GroupLayout.Alignment;
import ofeed.model.Page;

public class PanePage extends JPanel{
    private final Mainwindow main; //install listener
    private JLabel itemTitleTab = new JLabel("");
    private JLabel itemTitleFull = new JLabel("");
    private JLabel creationLabel = new JLabel("");
    private JLabel modificationLabel = new JLabel("");
    private JLabel pathLabel = new JLabel("");
    private String index;
    private Page page;

    private boolean layoutHidden;
    private GroupLayout layout_biglist;
    private GroupLayout layout;

    private JPanel pane_recos = new JPanel(); 
    private JPanel pane_big = new JPanel(); 

    private JPanel preview = new JPanel();
    private JLabel previewLabel = new JLabel();
    private JTextPane dotDescription = new JTextPane();
    private JButton bt_prevHistory, bt_nextHistory, bt_OpenItem, bt_OpenFolder, bt_Random, bt_Shuffle;
    private JScrollPane scroll_desc, scroll_recs, scroll_big;

    private String audio[] = {".mp3", ".flac", ".wav", ".ogg", ".m4a"};
    private String video[] = {".mp4", ".mov", ".avi", ".webm", ".mkv", ".3gp"};
    private String text[]  = {".txt", ".pdf", ".epub", ".djvu", ".mobi"};

    public PanePage(Mainwindow mainin, int indexin) {
        main = mainin;
        index = String.valueOf(indexin);
        setSize(new Dimension(800, 600));
        Arrays.sort(audio); Arrays.sort(video); Arrays.sort(text);
        //TODO: Make button WHich Load a chosenfile to feed (if not in any feed's folder, generate only randoms)
        //TODO: make "Biglist" button
        bt_OpenItem = new JButton("Open Item"); bt_OpenItem.addActionListener(main);
        bt_OpenFolder = new JButton("Open Folder"); bt_OpenFolder.addActionListener(main);
        bt_OpenItem.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.OPEN)); //Allow opening with defautl applications
        bt_OpenFolder.setEnabled(Desktop.getDesktop().isSupported(Desktop.Action.OPEN));
        bt_Random = new JButton("Random"); bt_Random.addActionListener(main); //Go to a random item from everywhere
        bt_Shuffle = new JButton("Shuffle"/*+index*/); bt_Shuffle.addActionListener(main); //Pull new recommendations for current page
        bt_prevHistory = new JButton("<="); bt_prevHistory.addActionListener(main);
        bt_nextHistory = new JButton("=>"); bt_nextHistory.addActionListener(main);

        preview = new JPanel();
        dotDescription = new JTextPane(); dotDescription.setEditable(false);
        scroll_desc = new JScrollPane(dotDescription);
        scroll_big = new JScrollPane(pane_big); scroll_big.getVerticalScrollBar().setUnitIncrement(30); scroll_big.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll_recs = new JScrollPane(pane_recos); scroll_recs.getVerticalScrollBar().setUnitIncrement(20); scroll_recs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        layout = new GroupLayout(this); layout_biglist = new GroupLayout(this);
        layout_biglist.setHorizontalGroup(layout_biglist.createSequentialGroup()
            .addComponent(scroll_big)
        );
        layout_biglist.setVerticalGroup(layout_biglist.createParallelGroup(Alignment.CENTER)
            .addComponent(scroll_big)
        );

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(Alignment.CENTER)
              .addGroup(layout.createParallelGroup(Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(bt_prevHistory)
                    .addComponent(bt_nextHistory))
                .addComponent(preview)
                .addGap(6)
                .addComponent(itemTitleFull)
                .addGap(10)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(bt_OpenItem)
                    .addComponent(bt_OpenFolder)
                    .addComponent(bt_Random)
                    .addComponent(bt_Shuffle))
                    .addGap(5)
                .addComponent(pathLabel)
                .addComponent(creationLabel)
                .addComponent(modificationLabel)
                .addGap(5)
                .addComponent(scroll_desc)
                .addGap(9))
            )
            .addComponent(scroll_recs)
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(bt_prevHistory)
                    .addComponent(bt_nextHistory))
                .addComponent(preview)
                .addGap(6)
                .addComponent(itemTitleFull)
                .addGap(10)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(bt_OpenItem)
                    .addComponent(bt_OpenFolder)
                    .addComponent(bt_Random)
                    .addComponent(bt_Shuffle))
                .addGap(5)
                .addComponent(pathLabel)
                .addComponent(creationLabel)
                .addComponent(modificationLabel)
                .addGap(5)
                .addComponent(scroll_desc)
                .addGap(9))
                
            )
            .addComponent(scroll_recs)
        );
    }
    public void setIndex(int indexin){ //replace with saner
        index = String.valueOf(indexin);
    }
    public int getIndex(){
        return Integer.valueOf(index);
    }
    public String getTitle(){ 
        return itemTitleTab.getText();
    }
    public void setPage(Page pagein, boolean prevHistoryBoolean, boolean nextHistoryBoolean){
        page = pagein;
        updateTabTitle();
        redraw(prevHistoryBoolean, nextHistoryBoolean);
    }
    private void updateTabTitle(){
        if( page.item() == null ){
            itemTitleFull.setText("");
            itemTitleTab.setText("New Tab");}
        else{
            String title = page.item().getName();
            itemTitleFull.setText(shortenTitle(title,80));
            itemTitleTab.setText(shortenTitle(title,12));}
    }

    private void redraw(boolean prevHistory, boolean nextHistory){
        if (page.item() == null)  viewBigList();
        else viewPage(prevHistory, nextHistory);
    }
    private void viewBigList(){
        hideLayout(); componentResized();
        pane_big.removeAll();
        int tableNums = page.recommendations().length;
        pane_recos.setPreferredSize(new Dimension((this.getHeight()/5)+15, ((this.getHeight()/5)+15)*tableNums));
        for (File fi : page.recommendations()){
            JButton newButton = new JButton(shortenTitle(fi.getName(),20));
            newButton.setName(fi.toString());
            newButton.setVerticalTextPosition(AbstractButton.BOTTOM);
            newButton.setHorizontalTextPosition(AbstractButton.CENTER);
            newButton.setPreferredSize(new Dimension(2*this.getHeight()/5, 2*this.getHeight()/5));
            newButton.setMargin(new Insets(10,10,0,10));
            ImageIcon recImage = getScaledImage(fi, 2*(this.getHeight()/5)-50, 2*(this.getHeight()/5)-50);
            newButton.setIcon(recImage);
            newButton.setActionCommand("NEWPAGE"+fi.toPath().toString());
            newButton.addActionListener(main);
            
            pane_big.add(newButton);
        } 
    }
    private void viewPage(boolean prevHistory, boolean nextHistory){
        readDescription();
        readMetadata();
        makeRecommendations();
        bt_prevHistory.setEnabled(prevHistory);
        bt_nextHistory.setEnabled(nextHistory);
        showLayout(); componentResized();
    }

    private void readMetadata(){
        pathLabel.setText(""); creationLabel.setText(""); modificationLabel.setText("");
        try {
            pathLabel.setText(shortenTitle(page.item().getParentFile().toString(),100));
            BasicFileAttributes attr = Files.readAttributes(page.item().toPath(), BasicFileAttributes.class);
            creationLabel.setText("Date of creation:  "+attr.creationTime().toString().substring(0,10)+" "+attr.creationTime().toString().substring(11,19));
            modificationLabel.setText("Last modified:     "+attr.lastModifiedTime().toString().substring(0,10)+" "+attr.creationTime().toString().substring(11,19));
        } catch(Exception e){ }
    }

    private void readDescription(){
        try {
            String descFile = dotDescCheck(); String desc = new String("");
            BufferedReader readfeed = new BufferedReader(new FileReader(new File(descFile),StandardCharsets.UTF_8));
            ArrayList<String> lines = new ArrayList<String>(readfeed.lines().toList());
            for (String s : lines)
                desc += s+"\n";
            readfeed.close();
            dotDescription.setText(desc);
        } catch(Exception e){ dotDescription.setText("");}
    }
    private String dotDescCheck() throws Exception{
        FileSystem filesys = FileSystems.getDefault();
        String descPath = page.item().getParent()+filesys.getSeparator()+page.item().getName().substring(0,page.item().getName().lastIndexOf("."))+".description";
            return descPath;
    }
    private void makeRecommendations(){
        pane_recos.removeAll();
        for (File fi : page.recommendations()){
            JButton newButton = new JButton(shortenTitle(fi.getName(), 16));
            newButton.setName(fi.toString());
            newButton.setVerticalTextPosition(AbstractButton.BOTTOM);
            newButton.setHorizontalTextPosition(AbstractButton.CENTER);
            newButton.setActionCommand("NEWPAGE"+fi.toPath().toString());
            newButton.addActionListener(main);
            pane_recos.add(newButton);
        }
    }
    private void drawRecommendations(){
        int tableNums = page.recommendations().length;
        pane_recos.setPreferredSize(new Dimension((this.getHeight()/5)+15, ((this.getHeight()/5)+15)*tableNums));

        ArrayList<JButton> buttonList = new ArrayList<>();
            for (Component comp : pane_recos.getComponents()){
                if (comp instanceof JButton)
                    buttonList.add((JButton) comp);}
            for (JButton button : buttonList){
                button.setPreferredSize(new Dimension(this.getHeight()/5, this.getHeight()/5+15));
                button.setIcon(getScaledImage(new File(button.getName()), (this.getHeight()/5)-30, (this.getHeight()/5)-30));}
    }
    private void drawImage(){
        try{
            ImageIcon imageCenter = getScaledImage(page.item(), preview.getMaximumSize().width, preview.getMaximumSize().height); // figure out the current height/width please
            previewLabel.setIcon(imageCenter);
        } catch (Exception e) { previewLabel = new JLabel();}
        preview.removeAll(); preview.add(previewLabel);
    }
    private ImageIcon getScaledImage(File fi, int w, int h){
        //fit srcimage into jpanel box
        ImageIcon srcImg = new ImageIcon(fi.toString());
        
        if (srcImg.getIconWidth() > 0 && srcImg.getIconHeight() > 0){
            double resizeRatio = 1.00;
            double widthRatio = (double) srcImg.getIconWidth() / (double) w;
            double heightRatio = (double) srcImg.getIconHeight() / (double) h;
            if(widthRatio > heightRatio) //which side is relatively longer?
                resizeRatio = 1.00/widthRatio;
            else
                resizeRatio = 1.00/heightRatio;
            /*if(srcImg.getIconHeight() > srcImg.getIconWidth())
                if(h > w)
                     resizeRatio = (double) h / (double) srcImg.getIconHeight();
                else resizeRatio = (double) srcImg.getIconHeight() / (double) h;
            else
                if(h > w)
                     resizeRatio = (double) w / (double) srcImg.getIconWidth();
                else resizeRatio = (double) srcImg.getIconWidth() / (double) w;*/
            int resizedHeight = (int) (((double) srcImg.getIconHeight()) * resizeRatio);
            int resizedWidth = (int) (((double) srcImg.getIconWidth()) * resizeRatio);
            return scaleImage(srcImg.getImage(), resizedWidth, resizedHeight); }
        else{
            String extension = getExtension(fi.toString());
            if (extension == null){
                try { return scaleImage(ImageIO.read(this.getClass().getResource("/unknown.png")), h, h);}
                catch (IOException e) {return new ImageIcon();}
            }
            else if (Arrays.binarySearch(audio, extension) > 0){
                 try { return scaleImage(ImageIO.read(this.getClass().getResource("/audio.png")), h, h);}
                catch (IOException e) {return new ImageIcon();}
            }
            else if (Arrays.binarySearch(video, extension) > 0){
                try { return scaleImage(ImageIO.read(this.getClass().getResource("/video.png")), h, h);}
                catch (IOException e) {return new ImageIcon();}
            }
            else if (Arrays.binarySearch(text, extension) > 0){
                try { return scaleImage(ImageIO.read(this.getClass().getResource("/text.png")), h, h);} 
                catch (IOException e) {return new ImageIcon();}
            }
            else try { return scaleImage(ImageIO.read(this.getClass().getResource("/unknown.png")), h, h);} 
                catch (IOException e) {return new ImageIcon();}
            }
    }
    private ImageIcon scaleImage(Image image, int w, int h){
        BufferedImage resizedimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedimage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(image, 0, 0, w, h, Color.WHITE, null);
        g2.dispose();
        return new ImageIcon(resizedimage);
    }

    private String shortenTitle(String it, int max){
        if (it.length() > max){
            if (page.item() != null){
                String extension = getExtension(page.item().getName());
                if (extension != null && extension.length()+2 < max){
                    return it.substring(0,max-extension.length()-2)+".."+extension;}
                else return it.substring(0, max-3)+"...";}
            else return it.substring(0, max-3)+"...";}
        else return it;
    }
    private String getExtension(String file){
        if (file != null && file.contains("."))
            return file.substring(file.lastIndexOf("."));
        else return null;
    }

    private void showLayout(){
        hideLayout();
            scroll_big.setVisible(false);
            scroll_recs.setVisible(true);
            preview.setVisible(true);
            scroll_desc.setVisible(true);
            pane_recos.setVisible(true);
            pane_big.setVisible(false);
        bt_prevHistory.setVisible(true);
        bt_nextHistory.setVisible(true);
        bt_OpenItem.setVisible(true);
        bt_Random.setVisible(true);
        bt_Shuffle.setVisible(true);
        this.setLayout(layout);
        layoutHidden = false;
    }
    private void hideLayout(){
            scroll_big.setVisible(true);
            scroll_recs.setVisible(false);
            preview.setVisible(false);
            scroll_desc.setVisible(false);
            pane_recos.setVisible(false);
            pane_big.setVisible(true);
        bt_prevHistory.setVisible(false);
        bt_nextHistory.setVisible(false);
        bt_OpenItem.setVisible(false);
        bt_Random.setVisible(false);
        bt_Shuffle.setVisible(false);
        this.setLayout(layout_biglist); 
        layoutHidden = true;
    }

    public void componentResized() {
        if(layoutHidden){
            ArrayList<JButton> buttonList = new ArrayList<>();
            for (Component comp : pane_big.getComponents()){
                if (comp instanceof JButton)
                    buttonList.add((JButton) comp);}
            for (JButton button : buttonList){
                button.setPreferredSize(new Dimension(2*this.getHeight()/5, 2*this.getHeight()/5));
                button.setIcon(getScaledImage(new File(button.getName()), 2*(this.getHeight()/5)-50, 2*(this.getHeight()/5)-50));}
            int buttonPerRow = this.getWidth()/(this.getHeight()/4);
            int buttonNum = (page.recommendations().length/buttonPerRow);
            if ((page.recommendations().length%buttonPerRow) != 0) buttonNum += 1;
            pane_big.setPreferredSize(new Dimension(this.getWidth(),(5+this.getHeight()/4)*buttonNum));
            }
        else{
            scroll_recs.setMaximumSize(new Dimension(this.getWidth()/4, this.getHeight()));
            scroll_desc.setMaximumSize(new Dimension(this.getWidth()/2, this.getHeight()/3));
            preview.setMaximumSize(new Dimension((2*this.getWidth())/3, this.getHeight()/2));
            drawImage();
            drawRecommendations();
            }
        }
}
