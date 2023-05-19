package ofeed.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import ofeed.model.FeedFile;
import ofeed.model.TableModelTags;

public class EditTagsTable extends JPanel{
    private FeedFile feedInfo;
    private TableModelTags tagsTableModel;
    private JTable checkBoxTable;
    private JScrollPane scrollPane;
    
    public EditTagsTable(FeedFile inFeedFile) {
        this.setPreferredSize(new Dimension(800,600));

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        feedInfo = inFeedFile;

        updateTagsTableModel();
        checkBoxTable = new JTable(tagsTableModel);
        checkBoxTable.setAutoCreateRowSorter(true);
        checkBoxTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        makeSecondColumnWider(checkBoxTable);
        scrollPane = new JScrollPane(checkBoxTable);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0; gbc.gridy = 0;
        add(scrollPane,gbc);
    }

    public ArrayList<Path> getRecursiveFolders(){
        Object[][] freshData = tagsTableModel.getData();
        ArrayList<Path> paths = new ArrayList<Path>();
        for (int i = 0; i < tagsTableModel.getRowCount(); i += 1){
            if ((boolean) freshData[i][0])
                paths.add(Paths.get((String) freshData[i][1]));
        }
        return paths;
    }
    public HashMap<Path, ArrayList<String>> getFoldersTags(){
        Object[][] freshData = tagsTableModel.getData();
        HashMap<Path, ArrayList<String>> newFoldersTags = new HashMap<Path, ArrayList<String>>();
        for (int i = 0; i < tagsTableModel.getRowCount(); i += 1){
            ArrayList<String> tList = new ArrayList<String>();
            for (int j = 2; j < tagsTableModel.getColumnCount(); j += 1){
                if ((boolean) freshData[i][j])
                    tList.add((String) tagsTableModel.getColumnName(j));
            }
            newFoldersTags.put(Paths.get((String) freshData[i][1]), tList);
        }      
        return newFoldersTags;
    }
    private void updateTagsTableModel(){
        tagsTableModel = new TableModelTags(feedInfo.getPaths().size(), feedInfo.getTags().size()+1);
        String[] tags = new String[feedInfo.getTags().size()+2]; //make tags array
        tags[0] = "Recursive"; tags[1] = "  ";
        for (int i = 0; i < feedInfo.getTags().size(); i += 1) // fill tag names (top row)
            tags[i+2] = feedInfo.getTags().get(i);
        String[] paths = new String[feedInfo.getPaths().size()]; 
        for (int i = 0; i < feedInfo.getPaths().size(); i += 1) // prepare fill paths
            paths[i] = feedInfo.getPaths().get(i).toString();
        Object[][] data = new Object[paths.length][tags.length]; // new data for model
        for (int i = 0; i < paths.length; i += 1){ 
            for (int j = 0; j < tags.length; j += 1){
                if (j == 0) data[i][j] = (feedInfo.getRecurs().contains(Paths.get(paths[i]))); // set booleans
                else if (j == 1) data[i][j] = paths[i]; // fill paths
                else data[i][j] = (feedInfo.queryFoldersTags(Paths.get(paths[i])).contains(tags[j])); // set booleans
            }
        }
        tagsTableModel.setColumnNames(tags);
        tagsTableModel.setData(data);
    }
    
    private void makeSecondColumnWider(JTable table){
        TableColumn column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(450);
    }
}
