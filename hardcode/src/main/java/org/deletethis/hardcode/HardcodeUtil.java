package org.deletethis.hardcode;

import com.squareup.javapoet.JavaFile;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HardcodeUtil {
    private HardcodeUtil() { }

    private static String stripNumbers(String str) {
        int len = str.length();
        // only normal numbers, we don't use Character.isSomething
        while(len > 0 && (str.charAt(len-1) >= '0' && str.charAt(len-1) <= '9'))
            len--;

        return str.substring(0,len);
    }

    public static void writeJavaFiles(File directory, List<JavaFile> javaFiles) {
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

        directory = new File(directory, pkg.replace('.', File.separatorChar));
        if(!directory.exists())
            return;
        if(!directory.isDirectory())
            return;

        directory.listFiles(new FileFilter() {
                                @Override
                                public boolean accept(File pathname) {
                                    System.out.println(pathname);
                                    return false;
                                }
                            });

    }
}
