package ofeed;

import java.io.File;

import javax.swing.UIManager;

import ofeed.funcs.WindowFuncs;

public class Main {
  public static void main(String[] args) {
    //Set UI to OS look-and-feel
    try{
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e){
      e.printStackTrace();
    }
    //Open Main Window (according to JavaDocs)
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            //WindowFuncs.newMainWindow(new File("/home/oh/Desktop/feedtest/nicefeed.feed"), 0);
            WindowFuncs.newBlankMainWindow();
    }});
  }
}
