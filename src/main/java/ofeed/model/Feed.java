package ofeed.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
/*hfh7rjnnnfbwhssssQQQQQQUGJT22UVGZT5Z4G ZU5ZE5ZGZEETEZUTHZTU5THZUTRGRDURRIJGKRJ
ik66666666666vvvvvvvvvvvhnhhuuhbuhhcvhbhfvfjzyy7guoh toj5zreddljcjdzsszzf7jfu8ri5<9fzzgzgzuuhgbh 
 b  b    b 
 
 */

import ofeed.funcs.FeedAlgo;

public class Feed {
    private FeedFile feedInfo;
    private int tabIndex = 0;
    private OptionVars options;
    protected Path[] pathFilesFolder, pathFilesTags, pathFilesAny;
    
    private ArrayList<Integer> historyIndex = new ArrayList<Integer>();
    private ArrayList<ArrayList<Page>> history = new ArrayList<ArrayList<Page>>(); //first dimension is Tab, second dimension is history
    private ArrayList<String> missingPaths = new ArrayList<String>();
    private ArrayList<String> missingItems = new ArrayList<String>();
    
    public Feed(File feedFileinto, int feedType) throws Exception{
        updateFeedFile(new FeedFile(feedFileinto,feedType));
        updateOptions(new OptionVars(feedInfo.getOptions(FeedFile.ALGO_SAME_FOLDER), feedInfo.getOptions(FeedFile.ALGO_SAME_TAGS), feedInfo.getOptions(FeedFile.ALGO_ANY), feedInfo.getOptions(FeedFile.ALGO_BIGLIST), feedInfo.getOptions(FeedFile.HISTORY_MAX_LENGTH), (feedInfo.getOptions(FeedFile.ALGMOD_META_SUCCESSOR) == 1), (feedInfo.getOptions(FeedFile.ALGMOD_WORD_NAME) == 1), (feedInfo.getOptions(FeedFile.ALGMOD_TIME_AGE) == 1)));
        //If feedItem is bad, show biglist
        //ask to delete missing from .feed file?
    }

    private File getRandomItem(){
        return FeedAlgo.makeItemsList(feedInfo,1)[0];
    }
    private File[] getRandomRecos(){
        return FeedAlgo.randomRecommendations(feedInfo, options);  //here be algos
    }
    private File[] getItemRecos(Path inItem){
        return FeedAlgo.itemRecommendations(inItem, feedInfo, options);  //here be algos
    }
    /*
     * Activates when the "Random" button is pressed
     */
    public void randomPage(){
        newPage(getRandomItem());
    }
    /*
     * Activates when the "Shuffle" button is pressed
     */
    public void shuffleRecommendations(){
        File[] newrecos = getItemRecos(getPage().item().toPath());
        history.get(tabIndex).set(historyIndex.get(tabIndex), new Page(getPage().item(), newrecos));
    }
    /*
     * Changes the feed file with a new one
     * (used when constructing feed & after editing the feed file)
     */

    public void updateFeedFile(FeedFile inFeedInfo) throws Exception{
        //check if feedInfo.setupFolderFileHashmap could load any files
        int totalFiles = 0;
        for (Path folder : inFeedInfo.getPaths()){
            totalFiles += inFeedInfo.queryFoldersFiles(folder).size();}
        if(totalFiles == 0){
            throw new Exception("Empty folder");}
        //fill warning message with missing folders
        missingPaths.clear();
        for(String folder : inFeedInfo.getMissingFolders()) missingPaths.add(folder);
        for(String item : inFeedInfo.getMissingItems()) missingItems.add(item);
        feedInfo = inFeedInfo;
    }
    /*
     * ditto
     */
    public void updateOptions(OptionVars newvars){
        options = newvars;
    }
    public OptionVars getOptions(){
        return options;
    }

    public void switchTab(int i){
        tabIndex = i;
    }
    public void newEmptyTab(){
        tabIndex = history.size();
        historyIndex.add(0);
        history.add(new ArrayList<Page>());
        newEmptyPage(); 
    }
    public void newTab(File newItem){
        tabIndex = history.size();
        historyIndex.add(0);
        history.add(new ArrayList<Page>());
        newPage(newItem);
    }
    public void closeTab(int i){
        historyIndex.remove(i);
        history.remove(i);
    }
    /*
     * Adds a new 'empty' page to most recent history (of tab number #tabIndex)
     * (if a page's item is empty or unavailable, mainwindow will show a 'biglist' from recommendations)
     */
    public void newEmptyPage(){
        File[] newRecos = getRandomRecos();
        history.get(tabIndex).add(historyIndex.get(tabIndex), new Page(null, newRecos));
        historyIndex.set(tabIndex,0);
    }
    /*
     * Adds a new page to most recent history (of tab number #tabIndex)
     */
    public Page newPage(File newItem){
        // fill up history with newitem and fresh recos
        File[] newRecos = getItemRecos(newItem.toPath());
        history.get(tabIndex).add(historyIndex.get(tabIndex), new Page(newItem, newRecos));
        // delete irrelevant history (in front of index) (then after maxlength)
        for (int i = 0; i < historyIndex.get(tabIndex); i += 1)
            history.get(tabIndex).remove(0);
        if ( options.historyMaxLength() < history.get(tabIndex).size() )
            history.get( tabIndex ).remove( history.get(tabIndex).size()-1);
        //set index 0, return the new page
        historyIndex.set(tabIndex, 0);
        return getPage();
    }
    /*
     * Retrieves previous page from tabIndex's history
     */
    public Page backPage(){ 
        if(checkPrevHistory()) historyIndex.set(tabIndex,(historyIndex.get(tabIndex)+1));
        return getPage();
    }
    public boolean checkPrevHistory(){
        return (historyIndex.get(tabIndex)+1 != history.get(tabIndex).size());
    }
    /*
     * Retrieves next page from tabIndex's history
     */
    public Page nextPage(){
        if(checkNextHistory()) historyIndex.set(tabIndex,(historyIndex.get(tabIndex)-1));
        return getPage();
    }
    public boolean checkNextHistory(){
        return (historyIndex.get(tabIndex) != 0);
    }

    public FeedFile getFeedFile(){
        return feedInfo;
    }
    public void saveOpen(File[] openList){
        File[] clearItems = new File[feedInfo.getOpenItems().size()]; int i = 0;
        for (File f : feedInfo.getOpenItems()){
            clearItems[i] = f; i++;}
        for (File f : clearItems){
            feedInfo.delOpenItem(f);}
        for (File f : openList){
            if(f != null) feedInfo.addOpenItem(f);}
        try(BufferedWriter write = new BufferedWriter(new FileWriter(feedInfo.getsavePath(), StandardCharsets.UTF_8))){
            feedInfo.getsavePath().createNewFile();
            write.write(feedInfo.toString());
        } catch (Exception e){
        }
    }
    /*
     * Retrieves page
     */
    public Page getPage(){ 
        return history.get(tabIndex).get(historyIndex.get(tabIndex));
    }
    public ArrayList<String> getMissingPaths(){
        return missingPaths;
    }
    public ArrayList<String> getMissingItems(){
        return missingItems;
    }
}
