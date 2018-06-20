package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class CachingSupplierCompiler implements SupplierCompiler {
    private String sha(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA1");
            byte[] array = md.digest(md5.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(array);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private final SupplierCompiler other;
    private final File tmpDir;

    CachingSupplierCompiler(SupplierCompiler other) {
        this.other = other;
        String str = System.getProperty("java.io.tmpdir");
        if(str == null || str.isEmpty()) {
            throw new IllegalStateException("no temp dir?");
        }
        File f = new File(str);
        if(!f.isDirectory() || !f.exists()) {
            throw new IllegalStateException("no temp dir?");
        }
        f = new File(f, "hc-test");
        //noinspection ResultOfMethodCallIgnored
        f.mkdirs();

        this.tmpDir = f;
    }

    @Override
    public <T> Supplier<T> get(TypeSpec typeSpec) {
        try {
            String name = sha(typeSpec.toString());
            File f = new File(tmpDir, name);
            if (f.exists()) {
                try(ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)))) {
                    Object object = in.readObject();
                    @SuppressWarnings("unchecked")
                    T result = (T) object;
                    return () -> result;
                }
            } else {
                Supplier<T> supp = other.get(typeSpec);
                T result = supp.get();

                // use a byte array first, we do not want to leave junk in the file in case something fails
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                new ObjectOutputStream(bytes).writeObject(result);

                // serialization was successful, let's write it out
                try(FileOutputStream fos = new FileOutputStream(f)) {
                    fos.write(bytes.toByteArray());
                }
                return () -> result;
            }
        } catch(IOException|ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
