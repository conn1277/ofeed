package ofeed.swing;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import ofeed.funcs.WindowFuncs;
import ofeed.model.ButtonTabComponentX;
import ofeed.model.Feed;
import ofeed.model.FeedFile;
import ofeed.model.OptionVars;

//Main window for viewing .feeds
public class Mainwindow extends JPanel implements ActionListener, WindowListener, ComponentListener{
    private Feed feedModel;

    private EditSettings settingsChoose = new EditSettings();
    private JFileChooser feedChoose = new JFileChooser();
    private JFileChooser folderChoose = new JFileChooser();
    private JFileChooser fileChoose = new JFileChooser();

    private JMenuBar menu_bar;
    private JMenu menu_file, menu_options, menu_maketab;
    private JMenuItem menu_file_open, menu_file_open_new, menu_file_new_feed, menu_file_close;
    private JMenuItem menu_options_settings, menu_options_editfeed, menu_options_help, menu_file_open_folder, menu_file_open_new_folder;
    private JMenuItem  menu_maketab_choose, menu_maketab_biglist;
    private JSeparator menu_seperator_1, menu_seperator_2, menu_seperator_3, menu_seperator_4;
    
    private ArrayList<PanePage> pagePanels = new ArrayList<PanePage>();
    private JPanel filler = new JPanel();
    private JTabbedPane feed_tab_bar = new JTabbedPane();

    //Image thumbnail
    //Change image size
    //Metadata
    //write statistics "X files loaded"
    //Path.getFileName()
    public Mainwindow(File inPathFeed, int feedType){
        setupWindow();
        openFeed(inPathFeed, feedType);
    }
    public Mainwindow(){
        setupWindow();
    }
    private void setupWindow(){
        //LISTENER
        this.addComponentListener(this);
        //Set a filter which only accepts ".feed" files
        feedChoose.setFileFilter(new FileFilter() {
             public boolean accept(File f){
                if (f.isDirectory() || f.toString().endsWith(".feed")) return true; return false;}
            public String getDescription(){ return new String(".feed files");}});
        fileChoose.setFileFilter(new FileFilter() {
            public boolean accept(File f){
                if (!f.toString().endsWith(".description")) return true; return false;}
                public String getDescription(){ return new String("Any files, except .description");}});
        folderChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //TOP BAR
        menu_bar = new JMenuBar();
        menu_file = new JMenu("File"); menu_bar.add(menu_file);
            menu_file_new_feed = new JMenuItem("New Feed"); setUpMenuItem(menu_file_new_feed, menu_file);
            menu_seperator_1 = new JSeparator(); setUpMenuItem(menu_seperator_1, menu_file);
            menu_file_open = new JMenuItem("Open Feed"); setUpMenuItem(menu_file_open, menu_file);
            menu_file_open_new = new JMenuItem("Open New Feed"); setUpMenuItem(menu_file_open_new,menu_file);
            menu_seperator_2 = new JSeparator(); setUpMenuItem(menu_seperator_2, menu_file);
            menu_file_open_folder = new JMenuItem("Open Folder as Feed"); setUpMenuItem(menu_file_open_folder, menu_file);
            menu_file_open_new_folder = new JMenuItem("Open Folder as New Feed"); setUpMenuItem(menu_file_open_new_folder, menu_file);
            menu_file_open_folder = new JMenuItem("Open Folder as Feed (recursively)"); setUpMenuItem(menu_file_open_folder, menu_file);
            menu_file_open_new_folder = new JMenuItem("Open Folder as New Feed (recursively)"); setUpMenuItem(menu_file_open_new_folder, menu_file);
            menu_seperator_3 = new JSeparator(); setUpMenuItem(menu_seperator_3, menu_file);
            menu_file_close = new JMenuItem("Close"); setUpMenuItem(menu_file_close, menu_file);
        menu_options = new JMenu("Options"); menu_bar.add(menu_options);
            menu_options_editfeed = new JMenuItem("Edit Feed"); setUpMenuItem(menu_options_editfeed,menu_options); menu_options_editfeed.setEnabled(false);
            menu_options_settings = new JMenuItem("Settings"); setUpMenuItem(menu_options_settings,menu_options); menu_options_settings.setEnabled(false);
            menu_seperator_4 = new JSeparator(); setUpMenuItem(menu_seperator_4, menu_options);
            menu_options_help = new JMenuItem("Help"); setUpMenuItem(menu_options_help,menu_options);
        menu_maketab = new JMenu("Make Tab"); menu_bar.add(menu_maketab); menu_maketab.setEnabled(false);
            menu_maketab_biglist = new JMenuItem("New Empty Tab"); setUpMenuItem(menu_maketab_biglist,menu_maketab);
            menu_maketab_choose = new JMenuItem("File to Tab"); setUpMenuItem(menu_maketab_choose,menu_maketab);
        //MAKE LAYOUT
        GridBagLayout layout = new GridBagLayout(); this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        this.setPreferredSize(new Dimension(800, 600));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;
        add(menu_bar,gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1;
        feed_tab_bar.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        add(feed_tab_bar,gbc);
        feed_tab_bar.addTab("Waiting for .feed file", filler);
    }

    @Override
    public void actionPerformed(ActionEvent a){
        String button = a.getActionCommand();
        // Buttons inside Pagetab
        if (button == "Open Item"){
            try{
                int tabIndex = feed_tab_bar.getSelectedIndex(); feedModel.switchTab(tabIndex);
                Desktop.getDesktop().open(feedModel.getPage().item());
            }catch(IOException e){ WindowFuncs.catchIt(e); JOptionPane.showMessageDialog(this,"Default application couldn't be opened","Error",JOptionPane.ERROR_MESSAGE);}
        }
        else if (button.startsWith("NEWPAGE")){
            int feedSelIndex = feed_tab_bar.getSelectedIndex(); feedModel.switchTab(feedSelIndex);
            File freshFile = new File((String) button.subSequence(7, button.length())); //The filename waits after "Switch Page"
            pagePanels.get(feedSelIndex).setPage(feedModel.newPage(freshFile), feedModel.checkPrevHistory(), feedModel.checkNextHistory());
            feed_tab_bar.getComponentAt(feedSelIndex).setName(button);
            feed_tab_bar.setTitleAt(feedSelIndex, pagePanels.get(feedSelIndex).getTitle()); //name of tab shortens to first 16 characters
        }
        else if (button == "Shuffle"){
            int tabIndex = feed_tab_bar.getSelectedIndex(); feedModel.switchTab(tabIndex);
            feedModel.shuffleRecommendations();
            updateTab(tabIndex);
        }
        else if (button == "Random"){
            int tabIndex = feed_tab_bar.getSelectedIndex(); feedModel.switchTab(tabIndex);
            feedModel.randomPage();
            updateTab(tabIndex);
        }
        else if (button == "<="){
            int tabIndex = feed_tab_bar.getSelectedIndex(); feedModel.switchTab(tabIndex);
            feedModel.backPage();
            updateTab(tabIndex);
        }
        else if (button == "=>"){
            int tabIndex = feed_tab_bar.getSelectedIndex(); feedModel.switchTab(tabIndex);
            feedModel.nextPage();
            updateTab(tabIndex);
        }
        //Buttons on tab
        else if (button.startsWith("Closetab")){
            if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this, "Do you really want to close this tab?", "Close Tab", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)){
                int i = Integer.valueOf(button.substring(8));
                if (i != -1) {
                    closeTab(i);
                }
            }
        }
        /*else if ("<" == button){
            //read tab index from actionemssage: custom actionmessage: inside func -> call different actionCommand -> send actionCommand here
            moveTableft(0);
        }
        else if (">" == button){
            moveTabright(0);
        }*/
        //File menu
        else if ("New Feed" == button){
            WindowFuncs.newEmptyFeedEditor();
        }
        else if ("Open Feed" == button){
            if(feedChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                closeAllTabs();
                openFeed(feedChoose.getSelectedFile(), FeedFile.TYPE_FILE);}
                
        }
        else if ("Open New Feed" == button){
            if(feedChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                WindowFuncs.newMainWindow(feedChoose.getSelectedFile(), FeedFile.TYPE_FILE);
        }
        else if ("Open Folder as New Feed" == button){
            if(folderChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                WindowFuncs.newMainWindow(folderChoose.getSelectedFile(), FeedFile.TYPE_FOLDER);
        }
        else if ("Open Folder as Feed" == button){
            if(folderChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                closeAllTabs();
                openFeed(folderChoose.getSelectedFile(), FeedFile.TYPE_FOLDER);}
                
        }
        else if ("Open Folder as Feed (recursively)" == button){
            if(folderChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                closeAllTabs();
                openFeed(folderChoose.getSelectedFile(), FeedFile.TYPE_RECURSIVE_FOLDER);}
            
        }
        else if ("Open Folder as New Feed (recursively)" == button){
            if(folderChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                WindowFuncs.newMainWindow(folderChoose.getSelectedFile(), FeedFile.TYPE_RECURSIVE_FOLDER);
        }
        else if ("Close" == button){
            WindowFuncs.close(this);
        }
        //Options menu
        else if ("Edit Feed" == button){
            if(feedModel != null) WindowFuncs.newFeedEditor(feedModel.getFeedFile());
        }
        else if ("Settings" == button){
            settingsChoose.setOptions(feedModel.getOptions());
            if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this,settingsChoose,"Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE))
                feedModel.updateOptions(new OptionVars(settingsChoose.getSameFolder(), settingsChoose.getSameTags(), settingsChoose.getAny(), settingsChoose.getBigList(), settingsChoose.getHistoryMax(), settingsChoose.getModSuccessor(), settingsChoose.getModFind(), settingsChoose.getModTime()));
        }
        else if ("Help" == button){
            JOptionPane.showMessageDialog(this,"May 2023\n\nmade by C0","Help",JOptionPane.INFORMATION_MESSAGE);
        }
        //Tab menu
        else if ("New Empty Tab" == button){
            makeEmptyTab();
        }
        else if ("File to Tab" == button){
            if(fileChoose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                makeTab(fileChoose.getSelectedFile());}
        }
    }
    
    //Functions for tab buttons
    private void makeEmptyTab(){
        //update feedModel
        feedModel.newEmptyTab();
        //update pagePanels
        PanePage newPane= new PanePage(this, feed_tab_bar.getTabCount()/*-1*/);
        newPane.setPage(feedModel.getPage(), feedModel.checkPrevHistory(), feedModel.checkNextHistory());
        newPane.setSize(feed_tab_bar.getWidth(), feed_tab_bar.getHeight()); pagePanels.add(newPane);
        //make new tab with pagepanel
        feed_tab_bar.addTab("New Tab",newPane);
        feed_tab_bar.setTabComponentAt(feed_tab_bar.getTabCount()-1, new ButtonTabComponentX(this,feed_tab_bar));
    }
    private void makeTab(File inFile){
        feedModel.newTab(inFile);

        PanePage newPane = new PanePage(this, feed_tab_bar.getTabCount());
        newPane.setPage(feedModel.newPage(inFile), false, false);
        newPane.setSize(feed_tab_bar.getWidth(), feed_tab_bar.getHeight());
        pagePanels.add(newPane);

        feed_tab_bar.addTab(".",newPane); 
        feed_tab_bar.setTitleAt(feed_tab_bar.getTabCount()-1, pagePanels.get(feed_tab_bar.getTabCount()-1).getTitle());
        feed_tab_bar.setTabComponentAt(feed_tab_bar.getTabCount()-1, new ButtonTabComponentX(this,feed_tab_bar));
    }
    private void closeTab(int i){
        feedModel.closeTab(pagePanels.get(i).getIndex());
        for(PanePage a : pagePanels) if(a.getIndex() > i) a.setIndex(a.getIndex()-1);
        feed_tab_bar.remove(i);
        pagePanels.remove(i);
    }
    /*private void moveTableft(int tabPosition){
        if (tabPosition != 0) {
            for (PanePage p : pagePanels){
                if(p.getIndex() > tabPosition)
                    p.setIndex(p.getIndex()+1);
                else if (p.getIndex() == tabPosition)
                    p.setIndex(p.getIndex()-1);
            }
            //remake tab_bar and add tabs according to index
        }
    }
    private void moveTabright(int tabPosition){
    }*/
    private void closeAllTabs(){
        //for (PanePage a : pagePanels) feedModel.closeTab(0);
        feed_tab_bar.removeAll();
        pagePanels.clear();
    }

    private void updateTab (int tabIndex){
        pagePanels.get(tabIndex).setPage(feedModel.getPage(), feedModel.checkPrevHistory(), feedModel.checkNextHistory());
        feed_tab_bar.setTitleAt(tabIndex, pagePanels.get(tabIndex).getTitle());
    }
    private void openFeed(File inFeedFile, int feedType){
        try {
            feedModel = new Feed(inFeedFile, feedType); //if success, assign new to current feed
            menu_options_editfeed.setEnabled(true); menu_options_settings.setEnabled(true); //these buttons depend on a loaded feedfile
            menu_maketab.setEnabled(true);
            closeAllTabs();
            if (feedModel.getFeedFile().getOpenItems() == null || feedModel.getFeedFile().getOpenItems().size() == 0)
                makeEmptyTab();
            else for (File s : feedModel.getFeedFile().getOpenItems())
                makeTab(s);
            //Show Warnings
            if (feedModel.getMissingPaths().size() > 0){
                String missingPaths = new String("");
                for (String p : feedModel.getMissingPaths()) missingPaths += "\n"+p;
                JOptionPane.showMessageDialog(this,"Can't find these folders:\n"+missingPaths,"Missing folder(s)",JOptionPane.INFORMATION_MESSAGE);}
            if (feedModel.getMissingItems().size() > 0){
                String missingItems = new String("");
                for (String i : feedModel.getMissingItems()) missingItems += "\n"+i;
                JOptionPane.showMessageDialog(this,"Can't open these tabs:\n"+missingItems,"Missing file(s)",JOptionPane.INFORMATION_MESSAGE);}
            //Catch  Errors
            } catch(Exception e){
                WindowFuncs.catchIt(e);
                if      (e.getMessage().startsWith("No files found")){
                    JOptionPane.showMessageDialog(this,"No files found.\n\nCheck this feed file's list of folders and/or allowed extensions.","No items found",JOptionPane.ERROR_MESSAGE);}
                else if (e.getMessage().startsWith("Empty folder")){
                    JOptionPane.showMessageDialog(this,"Empty folder","No items found",JOptionPane.ERROR_MESSAGE);}
                else if (e instanceof FileNotFoundException){
                    JOptionPane.showMessageDialog(this,"Can't find .feed file.","No .feed file",JOptionPane.ERROR_MESSAGE);}
                else{
                    JOptionPane.showMessageDialog(this,"","Unknown Error",JOptionPane.ERROR_MESSAGE);}
            }
    }
    //private void updateFeed(){
        //read feedfile without deleting all tabs From Settings.java}

    private void setUpMenuItem(JComponent item, JMenu addto){
        if(item instanceof AbstractButton){
            WindowFuncs.setUpItemAction((AbstractButton) item, (ActionListener) this);}
        addto.add(item);
    }
    @Override
    public void componentResized(ComponentEvent e) {
        this.filler.setPreferredSize(new Dimension(super.getWidth(),super.getHeight()-this.menu_bar.getHeight()*2));
        this.menu_bar.setPreferredSize(new Dimension(super.getWidth(),this.menu_bar.getHeight()));
        this.feed_tab_bar.setPreferredSize(new Dimension(super.getWidth(),super.getHeight()-this.menu_bar.getHeight()*2));
        for (PanePage panel : pagePanels){
            panel.componentResized();}
    }
    @Override
    public void windowClosing(WindowEvent e) {
        WindowFuncs.close(this);
    }
    @Override
    public void windowClosed(WindowEvent e) {
        if(feedModel != null){
            File[] openList = new File[pagePanels.size()];
            for (int i = 0; i < pagePanels.size(); i += 1){
                feedModel.switchTab(i);
                if(feedModel.getPage().item() != null)
                    openList[i] = feedModel.getPage().item();
            }
            feedModel.saveOpen(openList);
        }
    }
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
    @Override
    public void componentMoved(ComponentEvent e) {}
    @Override
    public void componentShown(ComponentEvent e) {}
    @Override
    public void componentHidden(ComponentEvent e) {}
}