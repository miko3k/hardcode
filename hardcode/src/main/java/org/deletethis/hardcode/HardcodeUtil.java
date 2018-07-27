package org.deletethis.hardcode;

import com.squareup.javapoet.JavaFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class HardcodeUtil {
    private HardcodeUtil() { }

    private static String stripNumbers(String str) {
        int len = str.length();
        // only normal numbers, we don't use Character.isSomething
        while(len > 0 && (str.charAt(len-1) >= '0' && str.charAt(len-1) <= '9'))
            len--;

        return str.substring(0,len);
    }

    private static class CommonPackage {
        private final String className;
        private final String packageName;

        CommonPackage(List<JavaFile> javaFiles) {
            String pkg = null;
            String baseName = null;
            boolean first = true;

            for(JavaFile javaFile: javaFiles) {
                String p = javaFile.packageName;
                String n = stripNumbers(javaFile.typeSpec.name);

                if(first) {
                    pkg = p;
                    baseName = n;
                } else {
                    if(!pkg.equals(p)) {
                        throw new IllegalArgumentException("all java files should be in the same package: "
                                + pkg + ", have: " + p);
                    }

                    if(!baseName.equals(n)) {
                        throw new IllegalArgumentException("all java files should have the same class name: "
                                + baseName + ", have: " + n);
                    }
                }
                first = false;
            }
            if(first) {
                throw new IllegalArgumentException("empty list given");
            }

            this.className = baseName;
            this.packageName = pkg;
        }
    }

    /**
     * Writes files into a specified directory. All files must be in the same package and must have same name,
     * apart from number suffix. This method writes all of the files and deletes any other possible files
     * in target directory with other suffixes.
     *
     * @param directory destiantion directory
     * @param javaFiles list of files, must be in the same package and similarly named
     * @throws IOException when an IO error occurs
     */

    public static void writeJavaFiles(File directory, List<JavaFile> javaFiles) throws IOException {
        CommonPackage c = new CommonPackage(javaFiles);

        File pkgdirectory = new File(directory, c.packageName.replace('.', File.separatorChar));
        if(!pkgdirectory.exists()) {
            for(JavaFile jf: javaFiles) {
                jf.writeTo(directory);
            }
            return;
        }

        if(!pkgdirectory.isDirectory()) {
            throw new IllegalArgumentException("not a directory: " + directory);
        }

        File[] files = pkgdirectory.listFiles(
                (dir, name) -> name.toLowerCase(Locale.US).endsWith(".java")
                        && stripNumbers(name.substring(0, name.length()-5)).equals(c.className));

        if(files == null)
            throw new IOException("unable to list files in target directory");

        for(File f: files) {
            if(!f.delete()) {
                throw new IOException("unable to delete file: " + f);
            }
        }
        for(JavaFile jf: javaFiles) {
            jf.writeTo(directory);
        }
    }
}
