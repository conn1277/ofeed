package ofeed.model;
import java.io.File;

// 1) Feed keeps track of history and multiple tabs using Page objects.
// 2.1) Mainwindow will use this class to determine what to show
// 2.2) if page's item is empty or unavailable, mainwindow will show a 'biglist' from recommendations
public record Page(File item, File[] recommendations) {

}