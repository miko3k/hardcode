package org.deletethis.hardcode.testing;

import java.io.File;

class TempFileFactory {
    private static final String SUBDIRECTORY = "hc-test-cache";

    private static class LazyHolder {
        // https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
        static final TempFileFactory INSTANCE = new TempFileFactory();
    }

    static TempFileFactory getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final File tmpDir;

    private TempFileFactory() {
        String str = System.getProperty("java.io.tmpdir");
        if(str == null || str.isEmpty()) {
            throw new IllegalStateException("no temp dir?");
        }
        File f = new File(str);
        if(!f.isDirectory() || !f.exists()) {
            throw new IllegalStateException("no temp dir?");
        }
        f = new File(f, SUBDIRECTORY);
        //noinspection ResultOfMethodCallIgnored
        f.mkdirs();

        this.tmpDir = f;
    }

    File createFile(String name) {
        return new File(tmpDir, name);
    }
}
