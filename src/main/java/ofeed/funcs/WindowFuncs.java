package ofeed.funcs;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ofeed.model.FeedFile;
import ofeed.swing.*;

public class WindowFuncs {

    /* Open a new feed */
    public static void newMainWindow(File inFeedFile, int feedType){
        genericWindow(new JFrame(), "O-Feed", new Mainwindow(inFeedFile, feedType));
    }
    /* Open a new feed */
    public static void newBlankMainWindow(){
        genericWindow(new JFrame(), "O-Feed", new Mainwindow());
    }
    /* Edit a new feed */
    public static void newEmptyFeedEditor(){
        genericWindow(new JFrame(), "O-Feed - Feed Editor", new EditFeed());
    }
    /* Edit a feed */
    public static void newFeedEditor(FeedFile inFeedFile){
        genericWindow(new JFrame(), "O-Feed - Feed Editor", new EditFeed(inFeedFile));
    }
    public static void genericWindow(JFrame frame, String name, Container container){
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(container);
        frame.setTitle(name);
        frame.pack();
        frame.setVisible(true);
        if(name == "O-Feed"){
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener((WindowListener) container);
        }
        try {
            frame.setIconImage(ImageIO.read(frame.getClass().getResource("/logo.png")));}
        catch (Exception e) {
            catchIt(e);}
        //minimumSize(frame,container);
    }
    /* Closes a window */
    public static void close(Component c){
        SwingUtilities.getWindowAncestor(c).dispose();
    }

    public static void setUpItemAction(AbstractButton item, ActionListener action){
        item.addActionListener(action);
        item.setActionCommand(item.getName());
    }
    public static void setUpItemAction(AbstractButton item, String actionName, ActionListener action){
        item.addActionListener(action);
        item.setActionCommand(actionName);
    }

    /* Shortcut for generic catching code */
    public static void catchIt(Exception e){
    e.printStackTrace();
    System.err.println("getCause = " + e.getCause());
    }
}
