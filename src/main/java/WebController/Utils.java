package WebController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by pm on 1/5/16.
 */
public class Utils {

    public static void deleteFile(String path, String contains) {
        //contains=="" --> removes all files and empty dirs
        File dir = new File(path);
        for (File f : dir.listFiles()) {
            if (f.getName().contains(contains))
                f.delete();
        }
    }

    public static void setExecutable(String filename) {
        File f = new File(filename);
        f.setExecutable(true);
    }


    @Deprecated
    public static String removeFile(String filename) {
        try {
            //way to run with *'s
            Process tr = Runtime.getRuntime().exec(new String[]{"sh", "-c", "rm " + filename});
        } catch (Exception e) {
            return e.toString();
        }
        return null;
    }

    @Deprecated
    public static String makeExecutable(String filename) {
        try {
            Process tr = Runtime.getRuntime().exec( new String[]{"chmod", "+x", filename} );
        } catch (IOException e) {
            return e.toString();
        }
        return null;
    }


}
