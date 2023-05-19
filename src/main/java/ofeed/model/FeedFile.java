package ofeed.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

//import ofeed.funcs.FeedFileFuncs;

public class FeedFile{
    public final static int ALGO_SAME_FOLDER= 0;
    public final static int ALGO_SAME_TAGS = 1;
    public final static int ALGO_ANY = 2;
    public final static int ALGO_BIGLIST= 3;
    public final static int HISTORY_MAX_LENGTH = 4;
    public final static int ALGMOD_META_SUCCESSOR = 5;
    public final static int ALGMOD_WORD_NAME = 6;
    public final static int ALGMOD_TIME_AGE = 7;

    public static final int TYPE_FILE = 0;
    public static final int TYPE_FOLDER = 1;
    public static final int TYPE_RECURSIVE_FOLDER = 2;
    public static final int TYPE_NEW_FEED = 3;

    private final String feedType;
    private File filesaveFile;

    private ArrayList<String> feedData = new ArrayList<String>();
    private ArrayList<Integer> settings = new ArrayList<Integer>();
    private ArrayList<File> feedOpenItems = new ArrayList<File>();
    
    private ArrayList<String> missingItems = new ArrayList<String>();
    private ArrayList<Path> folderPaths = new ArrayList<Path>();
    private ArrayList<String> missingFolders = new ArrayList<String>();
    private ArrayList<Path> folderRecurs = new ArrayList<Path>();
    
    private ArrayList<String> folderTags = new ArrayList<String>();
    private ArrayList<String> fileExtensions = new ArrayList<String>();
    
    private HashMap<Path, ArrayList<File>> folders_Files = new HashMap<Path, ArrayList<File>>();

    private HashMap<Path, ArrayList<String>> folders_Tags = new HashMap<Path, ArrayList<String>>();
    private HashMap<String, ArrayList<Path>> tags_Folders = new HashMap<String, ArrayList<Path>>(); 

    public FeedFile(File inFile, int type) throws Exception{
        if (type == 0){ feedType = "File";
            setsavePath(inFile);
            BufferedReader readfeed = new BufferedReader(new FileReader(inFile,StandardCharsets.UTF_8));
            feedData = new ArrayList<>(readfeed.lines().toList());
            readfeed.close();
            // 1: Open tabs (feedOpenItems)
            if(feedData.get(0).length() > 2)
            for (String oit : splitLine(feedData.get(0)))
            addOpenItem(Paths.get(oit).toFile());

            // 2: Supported formats (fileExtensions) Leave empty to accept all
            if(feedData.get(1).length() > 2)
            for (String ext : splitLine(feedData.get(1)))
            addExt(ext);

            // 3: Recursive folders (folderRecurs folderRecurs folder...)
            if(feedData.get(2).length() > 2)
            for (String rec : splitLine(feedData.get(2)))
            addRecurs(Paths.get(rec));

            // 4: AlgoOptions: sameFolder, sameTags, random, biglist size, history max size
            for (String rec : splitLine(feedData.get(3)))
            settings.add(Integer.valueOf(rec));
            
            // 5-6:empty/for later use

            // 7+: (folderPaths, folderTags, folders_Tags, tags_Folders)
            fillFolderPaths();
            fillFolderTags();
            fillHashVars();
            makeFoldersFilesHashmap();
        }
        else if (type == 1){ feedType = "Folder";
            settings.add(4);settings.add(4);settings.add(16);settings.add(60);settings.add(200);settings.add(1);settings.add(0);settings.add(0); //Default settings
            addPath(inFile.toPath());
            makeFoldersFilesHashmap();
        }
        else if (type == 2){ feedType = "RsFolder";
            settings.add(4);settings.add(4);settings.add(16);settings.add(60);settings.add(200);settings.add(1);settings.add(0);settings.add(0); //Default settings
            addPath(inFile.toPath());
            addRecurs(inFile.toPath());
            makeFoldersFilesHashmap();
        }
        else if (type == 3){ feedType = "File";
            settings.add(4);settings.add(4);settings.add(16);settings.add(60);settings.add(200);settings.add(1);settings.add(0);settings.add(0); //Default settings
        }
        else {
        feedType = "bad"; throw new Exception("Can't create feed file.\n\nWrong file input?");
        }
    }

    public FeedFile(File inFile) throws Exception{
        this(inFile,0);
    }
    
    public String toString(){
            //Make new feedData
            feedData.clear();
            ArrayList<String> openItemS = new ArrayList<String>(); for (File p : feedOpenItems) openItemS.add(p.toString());
            feedData.add("\""+String.join("\", \"", openItemS)+"\"");
            feedData.add("\""+String.join("\", \"", fileExtensions)+"\"");
            ArrayList<String> folderRecurS = new ArrayList<String>(); for (Path p : folderRecurs) folderRecurS.add(p.toString());
            feedData.add("\""+String.join("\", \"", folderRecurS)+"\"");
            ArrayList<String> settingS = new ArrayList<String>(); for (Integer i : settings) settingS.add(i.toString());
            feedData.add("\""+String.join("\", \"", settingS)+"\"");
            /*  Unused lines */feedData.add(""); feedData.add(""); 
            //Line format: path tag tag
            for (Path Folder : folderPaths){
                String tmp = Folder.toString(); //path
                ArrayList<String> folderTags = folders_Tags.get(Folder); //tag tag
                for (int i = 0; i < folderTags.size(); i += 1){
                    tmp = tmp+"\", \""+folderTags.get(i);}
                feedData.add("\""+tmp+"\"");
            }
            String retur = "";
            for (String s : feedData)
                retur = retur+s+"\n";
            return retur;
    }
    
    public String getFeedType() { return feedType;
    }
//Query folder's tags
    public ArrayList<String> queryFoldersTags(Path folder){ if(folders_Tags.get(folder) == null) return new ArrayList<String>();
        else return folders_Tags.get(folder);
    }
    public void setFoldersTags(HashMap<Path, ArrayList<String>> hashMap){ folders_Tags = hashMap;
    }
//Query tag's folders
    public ArrayList<Path> queryTagsFolders(String tag){ if(tags_Folders == null) return new ArrayList<Path>();
        else return tags_Folders.get(tag);
    }
//Query folder's files
    public ArrayList<File> queryFoldersFiles(Path folder){ if(folders_Files == null) return new ArrayList<File>();
        else return folders_Files.get(folder);
    }
//Path
    public void addPath(Path path){ folderPaths.add(path);
    }
    public void delPath(Path path){ folderPaths.remove(path);
    }
    public ArrayList<Path> getPaths(){ return folderPaths;
    }
//Tags
    public void addTag(String inTag){ folderTags.add(inTag); Collections.sort(folderTags);
    }
    public void delTag(String inTag){ folderTags.remove(inTag);
    }
    public ArrayList<String> getTags(){ return folderTags;
    }
//Extensions
    public void addExt(String inExt){ fileExtensions.add(inExt); Collections.sort(fileExtensions);
    }
    public void delExt(String inExt){ fileExtensions.remove(inExt);
    }
    public ArrayList<String> getExts(){ return fileExtensions;
    }
//Recursive folders
    public void addRecurs(Path inRe){ folderRecurs.add(inRe);
    }
    public void setRecurs(ArrayList<Path> inRe){ folderRecurs = inRe;
    }
    public ArrayList<Path> getRecurs(){ return folderRecurs;
    }
//Set path to this .feed
    public void setsavePath(File inFile){ filesaveFile = inFile;
    }
    public File getsavePath(){ return filesaveFile;
    }
//Last viewed item
    public void addOpenItem(File inPath){ if(inPath != null && inPath.exists()) feedOpenItems.add(inPath); else missingItems.add(inPath.toString());
    }
    public void delOpenItem(File inPath){ feedOpenItems.remove(inPath);
    }
    public ArrayList<File> getOpenItems(){ return feedOpenItems;
    }
//For Warning message
    public ArrayList<String> getMissingFolders(){ return missingFolders;
    }
    public ArrayList<String> getMissingItems(){ return missingItems;
    }
//Options
    public void setOptions(OptionVars options){
        settings.set(0, options.numberOfSameFolder()); settings.set(1, options.numberOfSameTags()); settings.set(2, options.numberOfAny()); settings.set(3, options.numberOfBigList()); settings.set(4, options.historyMaxLength());
        if( options.modSuccessor()) settings.set(5, 1); else settings.set(5, 0);
        if( options.modName()) settings.set(6, 1); else settings.set(6, 0);
        if( options.modTime()) settings.set(7, 1); else settings.set(7, 0);
    }
    public int getOptions(int option){ return settings.get(option);
    }
  /////////////////////////////////////////
 // private functions                   /////
//line format: path tag tag tag tag ..   //
    
    private boolean extFilter(File file){
        if (fileExtensions.size() == 0 || fileExtensions == null)
            return true;
        for (String ext : fileExtensions){
        if(file.getName().endsWith("."+ext)) return true;
        } return false;
    }

    private void fillFolderPaths(){
        for (String line : feedData.subList(6,feedData.size())){
            String[] line_arr = splitLine(line);
            if(new File(Paths.get(line_arr[0]).toString()).isDirectory())
                folderPaths.add(Paths.get(line_arr[0]));
            else
                missingFolders.add(line_arr[0]);
        }
    }
    private void fillFolderTags(){
        for (String line : feedData.subList(6,feedData.size())){
            String[] line_arr = splitLine(line);
            for (int t = 1; t < line_arr.length; t += 1){ //for each tag
                if( !(folderTags.contains(line_arr[t]))){
                    folderTags.add(line_arr[t]);}
                }
        }
    }
    private void fillHashVars(){
        //clear tags_folders
        for (String tag : folderTags) tags_Folders.put(tag,new ArrayList<Path>());
        //folders_tags & tags_folders
        for (String line : feedData.subList(6,feedData.size())){
            String[] line_arr = splitLine(line);

            //Fill (folders_tags)
            ArrayList<String> temp_tags = new ArrayList<String>();
            for (int t = 1; t < line_arr.length; t += 1)
                temp_tags.add(line_arr[t]);
            folders_Tags.put(Paths.get(line_arr[0]), temp_tags);
            //Fill (tags_folders)
            for (int t = 1; t < line_arr.length; t += 1){
                ArrayList<Path> temp_paths = new ArrayList<Path>();
                for(Path prev_folder : tags_Folders.get(line_arr[t]))
                    temp_paths.add(prev_folder);//gather all older paths of tag
                temp_paths.add(Paths.get(line_arr[0])); //Update pathlist with new path
                tags_Folders.put(line_arr[t], temp_paths);
            }
        }
    }
    public void makeFoldersFilesHashmap(){
        folders_Files.clear();
        for (Path folder : folderPaths){
            if(folder.toFile().isDirectory()){
                ArrayList<File> tFiles = new ArrayList<File>();
                //If recursive is on, go through each folder and add files within
                if (folderRecurs.contains(folder))
                    tFiles = recursiveMadness(tFiles,folder.toFile());
                //otherwise it's enough for files within this folder to get added
                else for (File file : folder.toFile().listFiles()){
                    if(file.isFile() && extFilter(file) && !file.getName().endsWith(".description")) tFiles.add(file);}
                folders_Files.put(folder, tFiles);
            }
        }
    }
    private ArrayList<File> recursiveMadness(ArrayList<File> filelist,File folder){
        for (File file : folder.listFiles()){
          if(file.isFile() && extFilter(file) && !file.getName().endsWith(".description")) filelist.add(file);
          if(file.isDirectory() && !(folderPaths.contains(file.toPath())))
            filelist = recursiveMadness(filelist,file);
        } return filelist;
    }
    private String[] splitLine(String line){
        return line.substring(1,line.length()-1).split("\", \"");
    }
}
