/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package slime.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Vector;

/**
 *
 * @author 
 */
public class RecursiveSearch
{

    public static File[] listFilesAsArray(File directory,FilenameFilter filter,boolean recurse)
    {
        Collection<File> files = listFiles(directory,filter, recurse);
        //Java4: Collection files = listFiles(directory, filter, recurse);
        File[] arr = new File[files.size()];
        return files.toArray(arr);
    }
    public static Collection<File> listFiles(
            // Java4: public static Collection listFiles(
            File directory,
            FilenameFilter filter,
            boolean recurse) {
        // List of files / directories
        Vector<File> files = new Vector<File>();
        // Java4: Vector files = new Vector();

        // Get files / directories in the directory
        File[] entries = directory.listFiles();

        // Go over entries
        for (File entry : entries) {
// Java4: for (int f = 0; f < files.length; f++) {
// Java4: 	File entry = (File) files[f];

            // If there is no filter or the filter accepts the
            // file / directory, add it to the list
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }

            // If the file is a directory and the recurse flag
            // is set, recurse into the directory
            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }

        // Return collection of files
        return files;
    }
}
