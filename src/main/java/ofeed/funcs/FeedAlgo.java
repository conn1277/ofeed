package ofeed.funcs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ofeed.model.FeedFile;
import ofeed.model.OptionVars;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Array;

public class FeedAlgo {
    final static Random randomGen = new Random();
    //Used in 'item view' (tab with item)
    public static File[] itemRecommendations(Path feedItem, FeedFile feedInfo, OptionVars inOption){
        File[] listSameFolder = makeSameFolderList(feedItem, feedInfo, inOption.numberOfSameFolder(), inOption.modSuccessor());
        File[] listSameTags = makeSameTagsList(feedItem, feedInfo, inOption.numberOfSameTags());
        File[] listItems = makeItemsList(feedInfo, inOption.numberOfAny());
        return combineRecs(listSameFolder, listSameTags, listItems);
    }
    //Used in 'biglist' (empty tab)
    public static File[] randomRecommendations(FeedFile feedInfo, OptionVars inOption){
        File[] listItems = makeItemsList(feedInfo, inOption.numberOfBigList());
        return listItems;
    }
    private static File[] combineRecs(File[] listOfSameFolder, File[] listOfSameTags, File[] listOfItems){
        //Length thus: (number of items from same folder) + (from within same tags) + (from random)
        File[] retur = new File[listOfSameFolder.length+listOfSameTags.length+listOfItems.length];
        //Add them all to the list
        for( int i = 0; i < listOfSameFolder.length; i += 1) retur[i] = listOfSameFolder[i];
        for( int i = 0; i < listOfSameTags.length; i += 1) retur[i+listOfSameFolder.length] = listOfSameTags[i];
        for( int i = 0; i < listOfItems.length; i += 1) retur[i+listOfSameFolder.length+listOfSameTags.length] = listOfItems[i];
        return retur;
    }

//MODIFICATION ALGO: if metadata name = 1, check if theree '2' in same folde. chacenee 100%%
//MODIFICATION ALGO: Pick random word (length > 5) from title and try to find match. Chjh8ugjance 10%
//MODIFICATION ALGO: Pick creation time not older than 3 year. Chance 10%
//HERE BE LISTS
    //List of items from folder
    public static File[] makeSameFolderList(Path feedItem, FeedFile feedInfo, int number, boolean getSuccessor){
        File[] sameFolderFiles = feedItem.getParent().toFile().listFiles(new FileFilter() {
            public boolean accept(File f){ if(f.isDirectory() || f.toString().endsWith(".description")) return false; return true;}});
        File[] retur = new File[number];
        for (int i = 0; i < number; i++){
            int randomIndex = randomGen.nextInt(0,sameFolderFiles.length);
            retur[i] = sameFolderFiles[randomIndex];
        }
        if (getSuccessor){
            File successor = successorFile(feedItem.toFile(), sameFolderFiles);
            if (successor != null)
                retur[0] = successor;
        }
        return retur;
    }
        // 0102 22 002 201 name,then metatagFileAttributes attr = Files.readAttributes(original.toPath(), BasicFileAttributes.class);
    
    private static File successorFile(File original, File[] files){
        String name = original.getName();
        name = name.replaceAll("[.]", " ");
        String[] nameWords = name.split(" ", 0);
        for (String w : nameWords){
            try{
                if(Integer.valueOf(w) != null){
                    int numZeros = 0;
                    String nextw = "";
                    for (char s : w.toCharArray()){
                        if (s == '0') numZeros += 1;
                        else break;}
                    if (w.charAt(w.length()-1) == '9' && numZeros > 0)
                        numZeros -= 1;
                    for (int i = 0; i < numZeros; i++)
                        nextw = nextw.concat("0");
                    nextw = nextw.concat(String.valueOf(Integer.valueOf(w)+1));
                    return likeliestWord(original, files, nextw);}}
            catch (Exception e){}}
        return null;
    }
    private static File likeliestWord(File original, File[] files, String word){
        ArrayList<File> possibleWords = new ArrayList<>();
        for (File f : files){
            String[] fileWords = f.getName().replaceAll("[.]", " ").split(" ", 0);
            for (String w : fileWords){
                if (w.equals(word)){
                    possibleWords.add(f);
                }}}
        possibleWords.remove(original);
        if (possibleWords.size() == 0)
                return null;
        else return possibleWords.get(randomGen.nextInt(0,possibleWords.size()));
    }

    //List of items from same tags
    public static File[] makeSameTagsList(Path feedItem, FeedFile feedInfo, int number){
        File owningFolder = findParentFolder(feedItem.getParent().toFile(), feedInfo);
        if ((owningFolder != null) && feedInfo.queryFoldersTags(owningFolder.toPath()).size() > 0){ //Is a parent folder part of feedfile? Does this folder have any tags? Else, pick random
            File[] retur = new File[number];
            List<String> tags = feedInfo.queryFoldersTags(owningFolder.toPath());
            ArrayList<Path> tagFolders = new ArrayList<Path>();
            for (String s : tags){
                for (Path x : feedInfo.queryTagsFolders(s)){
                    if(!(tagFolders.contains(x))) tagFolders.add(x);
                }
            }
            int totalFiles = 0;
            for (Path folder : tagFolders){
                totalFiles += feedInfo.queryFoldersFiles(folder).size();}
            for (int i = 0; i < number; i++){
                retur[i] = grabRandomPath(tagFolders, feedInfo, randomGen.nextInt(0,totalFiles));}
            return retur;}
        else return(makeItemsList(feedInfo, number));
    }
    //List of random items
    public static File[] makeItemsList(FeedFile feedInfo, int number){
        File[] retur = new File[number];
        int totalFiles = 0;
        for (Path folder : feedInfo.getPaths()){
            totalFiles += feedInfo.queryFoldersFiles(folder).size();}
        for (int i = 0; i < number; i += 1){
            retur[i] = grabRandomPath(feedInfo.getPaths(), feedInfo, randomGen.nextInt(0,totalFiles));
        }
        return retur;
    }
    /*List of items according to filter (for mod funcs)
    private static File grabRandomPath(ArrayList<Path> folders, FeedFile feedInfo, int randomIndex){
        for (Path folder : folders){
            List<File> epicface = feedInfo.queryFoldersFiles(folder);
            if(randomIndex >= epicface.size()){
                randomIndex -= epicface.size();}
            if(randomIndex < epicface.size()){
                return epicface.get(randomIndex);
        }} return null;
}*/
//Grab a single, random path from list
    private static File grabRandomPath(ArrayList<Path> folders, FeedFile feedInfo, int randomIndex){
                for (Path folder : folders){
                    List<File> epicface = feedInfo.queryFoldersFiles(folder);
                    if(randomIndex >= epicface.size()){
                        randomIndex -= epicface.size();}
                    if(randomIndex < epicface.size()){
                        return epicface.get(randomIndex);
                }} return null;
    }
//Figure out what folder this file belongs to; works even recursively. If no folder belongs to feedfile, return null.
    private static File findParentFolder(File directory, FeedFile feedInfo){
        File retur = null;
        if(feedInfo.getPaths().contains(directory.toPath())) retur = directory;
        else if(directory.getParentFile() == null);
        else retur = findParentFolder(directory.getParentFile(),feedInfo);
        return retur;
    }
    /*private static boolean checkForDupe(Path[] items, int number){
        Path[] tempItems = new Path[number];
        for (int i = 0; i < number; i += 1){
            //get new item
            Path newItem = pickItem(items);
            //check if item is a dupe
            int j = 0;
            while(Arrays.binarySearch(items, newItem) >= 0){
                j += 1; if(j == 19) break; newItem = pickItem(items);}
            //add item
            tempItems[i] = (newItem);
        }
        return tempItems;
    }*/
}
