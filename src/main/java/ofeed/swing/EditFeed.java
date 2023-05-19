package ofeed.swing;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.JScrollPane;
import ofeed.funcs.WindowFuncs;
import ofeed.model.FeedFile;
import ofeed.model.OptionVars;

//Feed editor; opened when making a new feed or when editing existing one
public class EditFeed extends JPanel implements ActionListener{
    private FeedFile feedInfo;

    private EditTagsTable tagsTablePane;
    private EditSettings settingsChooser = new EditSettings();
    private PanelEnterText folderMenu, tagMenu, extMenu;
    private JFileChooser fileChoose = new JFileChooser();

    private DefaultListModel<String> model_paths, model_tags, model_exts;
    private JList<String> ls_paths, ls_tags, ls_exts;
    private JScrollPane scroll_paths, scroll_tags, scroll_exts;

    private JButton bt_path_add, bt_path_del, bt_tags_add, bt_tags_del, bt_exts_add, bt_exts_del;
    private JButton bt_tag, bt_settings;
    private JButton bt_save, bt_save_to, bt_close;
    
    public EditFeed(){
        try {
            feedInfo = new FeedFile(null, FeedFile.TYPE_NEW_FEED);
        } catch (Exception e) {
            WindowFuncs.catchIt(e);}
        setup();
    }
    
    public EditFeed(FeedFile inFeedFile) {
        feedInfo = inFeedFile;
        setup();
    }
    
    private void setup(){
        this.setPreferredSize(new Dimension(800,600));

        fileChoose.setFileFilter(new FileFilter() {
            public boolean accept(File f){
                if (f.isDirectory() || f.toString().endsWith(".feed")) return true; return false;}
            public String getDescription(){ return new String(".feed files");}});
        bt_tag = new JButton("Edit Tags"); bt_tag.addActionListener(this);
        bt_settings = new JButton("Edit Settings"); bt_settings.addActionListener(this);
        bt_save = new JButton("Save"); bt_save.addActionListener(this);
        bt_save_to = new JButton("Save To"); bt_save_to.addActionListener(this);
        bt_close = new JButton("Close"); bt_close.addActionListener(this);
        folderMenu = new PanelEnterText("Enter folder(s). Every line is a new folder. Your path may not contain this sequence: \", \""); tagMenu = new PanelEnterText("Enter tag(s). Every line is a new tag."); extMenu = new PanelEnterText("Enter extension(s). Every line is a new extension. Leave empty to accept all.");

        model_paths = new DefaultListModel<String>(); for(Path p : feedInfo.getPaths()) model_paths.addElement(p.toString());
        model_tags = new DefaultListModel<String>(); for(String t : feedInfo.getTags()) model_tags.addElement(t);
        model_exts = new DefaultListModel<String>(); for(String e : feedInfo.getExts()) model_exts.addElement(e);
        ls_paths = new JList<String>(model_paths); ls_paths.setLayoutOrientation(JList.VERTICAL);
        bt_path_add = new JButton("Add Folders"); bt_path_add.addActionListener(this);
        bt_path_del = new JButton("Remove Folders"); bt_path_del.addActionListener(this);
        ls_tags = new JList<String>(model_tags); ls_tags.setLayoutOrientation(JList.VERTICAL_WRAP);
        bt_tags_add = new JButton("Add Tags"); bt_tags_add.addActionListener(this);
        bt_tags_del = new JButton("Remove Tags"); bt_tags_del.addActionListener(this);
        ls_exts = new JList<String>(model_exts); ls_exts.setLayoutOrientation(JList.VERTICAL_WRAP);
        
        scroll_paths = new JScrollPane(ls_paths); scroll_paths.setPreferredSize(new Dimension(750,180));
        scroll_tags = new JScrollPane(ls_tags); scroll_tags.setPreferredSize(new Dimension(750,120));
        scroll_exts = new JScrollPane(ls_exts); scroll_exts.setPreferredSize(new Dimension(750,120));
        bt_exts_add = new JButton("Add Extensions"); bt_exts_add.addActionListener(this);
        bt_exts_del = new JButton("Remove Extensions"); bt_exts_del.addActionListener(this);
        add(bt_path_add); add(bt_path_del); add(new JLabel("Folders must exist to be added to list")); add(scroll_paths);
        
        add(bt_tags_add); add(bt_tags_del); add(new JLabel("If the algorithm can't find tags, it will show random files")); add(scroll_tags);
        
        add(bt_exts_add); add(bt_exts_del); add(new JLabel("Leave empty to accept all extensions")); add(scroll_exts);
        
        add(bt_tag); add(bt_settings);
        
        add(bt_save); add(bt_save_to); add(bt_close);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String button = e.getActionCommand();
        if ("Edit Tags" == button){
            tagsTablePane = new EditTagsTable(feedInfo);
            if(JOptionPane.showConfirmDialog(this,tagsTablePane,"Edit Tags", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION){
                feedInfo.setFoldersTags(tagsTablePane.getFoldersTags());}
                feedInfo.setRecurs(tagsTablePane.getRecursiveFolders());
        }
        else if ("Edit Settings" == button){
            settingsChooser.setOptions(new OptionVars(feedInfo.getOptions(FeedFile.ALGO_SAME_FOLDER), feedInfo.getOptions(FeedFile.ALGO_SAME_TAGS), feedInfo.getOptions(FeedFile.ALGO_ANY), feedInfo.getOptions(FeedFile.ALGO_BIGLIST), feedInfo.getOptions(FeedFile.HISTORY_MAX_LENGTH), (feedInfo.getOptions(FeedFile.ALGMOD_META_SUCCESSOR) == 1), (feedInfo.getOptions(FeedFile.ALGMOD_WORD_NAME) == 1), (feedInfo.getOptions(FeedFile.ALGMOD_TIME_AGE) == 1)));
            if(JOptionPane.showConfirmDialog(this,settingsChooser,"Edit Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
                feedInfo.setOptions(new OptionVars(settingsChooser.getSameFolder(), settingsChooser.getSameTags(), settingsChooser.getAny(), settingsChooser.getBigList(), settingsChooser.getHistoryMax(), settingsChooser.getModSuccessor(), settingsChooser.getModFind(), settingsChooser.getModTime()));
        }
        else if ("Save" == button){
            /*if( model_paths.isEmpty() )
                JOptionPane.showMessageDialog(this, "You must add at least 1 path.","Add Path",JOptionPane.ERROR_MESSAGE);
            else */if( feedInfo.getsavePath() == null)
                saveToChooser();
            else
                writeFile();
        }
        else if ("Save To" == button){/*
            if( model_paths.isEmpty() )
                JOptionPane.showMessageDialog(this, "You must add at least 1 path.","Add Path",JOptionPane.ERROR_MESSAGE);
            else  */
                saveToChooser();
        }
        else if ("Close" == button){
            WindowFuncs.close(this);
        }
        else if ("Add Folders" == button){
            if(JOptionPane.showConfirmDialog(null,folderMenu,"Enter Folder(s)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
                for (String s : folderMenu.getText()) addPath(s);
        }
        else if ("Remove Folders" == button){
            if(JOptionPane.showConfirmDialog(null,folderMenu,"Enter Folder(s)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
                for (String s : folderMenu.getText()) delPath(s);
        }
        else if ("Add Tags" == button){
            if(JOptionPane.showConfirmDialog(null,tagMenu,"Enter Tag(s)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
                for (String s : tagMenu.getText()) addTag(s);
        }
        else if ("Remove Tags" == button){
            if(JOptionPane.showConfirmDialog(null,tagMenu,"Enter Tag(s)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
                for (String s : tagMenu.getText()) delTag(s);
        }
        else if ("Add Extensions" == button){
            if(JOptionPane.showConfirmDialog(null,extMenu,"Enter Extension(s)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
                for (String s : extMenu.getText()) addExt(s);
        }
        else if ("Remove Extensions" == button){
            if(JOptionPane.showConfirmDialog(null,extMenu,"Enter Extension(s)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION)
                for (String s : extMenu.getText()) delExt(s);
        }
    }
    private void delPath(String path){
        if(model_paths.size() > 0){
            feedInfo.delPath(Paths.get(path));
            model_paths.removeElement(path);}
        else
            JOptionPane.showMessageDialog(null, "You must have at least 1 path", "Add Path", JOptionPane.ERROR_MESSAGE);
    }
    private void addPath(String path){
        if(new File(path).exists()){
            if(!feedInfo.getPaths().contains(Paths.get(path))) feedInfo.addPath(Paths.get(path));
            if(!model_paths.contains(path)) model_paths.addElement(path);
        }
    }
    private void delTag(String tag){
        feedInfo.delTag(tag);
        model_exts.removeElement(tag);
    }
    private void addTag(String tag){
        if(!feedInfo.getTags().contains(tag)) feedInfo.addTag(tag);
        if(!model_tags.contains(tag)) model_tags.addElement(tag);
    }
    private void delExt(String ext){
        feedInfo.delExt(ext);
        model_exts.removeElement(ext);
    }
    private void addExt(String ext){
        if(!feedInfo.getExts().contains(ext)) feedInfo.addExt(ext);
        if(!model_exts.contains(ext)) model_exts.addElement(ext);
    }


    private void saveToChooser(){
        if(fileChoose.showSaveDialog(this) ==  JFileChooser.APPROVE_OPTION){
            String path = fileChoose.getSelectedFile().toString();
            if (!path.endsWith(".feed")) path = path+".feed";
            feedInfo.setsavePath(new File(path));
            writeFile();
        }
    }

    private void writeFile(){
        //Open feedInfo's SavePath and save there
        try(BufferedWriter write = new BufferedWriter(new FileWriter(feedInfo.getsavePath(), StandardCharsets.UTF_8))){
            feedInfo.getsavePath().createNewFile();
            write.write(feedInfo.toString());
        } catch (IOException e){
            WindowFuncs.catchIt(e);
            JOptionPane.showMessageDialog(this,"Input/Output Error\n\nOut of disk space? Missing permissions to folder? Folder deleted?","I/O Error",JOptionPane.ERROR_MESSAGE);
        }
    }
}
