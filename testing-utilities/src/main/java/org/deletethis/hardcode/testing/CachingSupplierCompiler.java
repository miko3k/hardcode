package org.deletethis.hardcode.testing;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

/**
 *A {@link SupplierCompiler}, that caches results on the filesystem or delegates further.
 *
 * <p>File name is a hash of entire source code, and contains serialized representation of the object, that would
 * be obtained via supplier.
 *
 * <p>On some machines, compilation may take 1-2 seconds. In this case, using cache yields significant
 * performance boost. On the other hand, some machines may benefit very little. Therefore it might be useful
 * to make using the cache somewhat configurable.
 *
 */
public class CachingSupplierCompiler implements SupplierCompiler {
    private static final String HEX = "0123456789ABCDEF";

    private String sha(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA1");
            byte[] array = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder bld = new StringBuilder(array.length*2);
            for(byte b: array) {
                bld.append(HEX.charAt((b>>4)&0xF));
                bld.append(HEX.charAt(b&0xF));
            }
            return bld.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private final SupplierCompiler other;

    CachingSupplierCompiler(SupplierCompiler other) {
        this.other = other;
    }

    @Override
    public <T> Supplier<T> get(List<TypeSpec> typeSpec) {
        try {
            // toString prints out all its elements, should be good enough
            String name = sha(typeSpec.toString());
            File f = TempFileFactory.getInstance().createFile(name);

            if(f.exists()) {
                try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)))) {
                    Object object = in.readObject();
                    @SuppressWarnings("unchecked")
                    T result = (T) object;
                    return () -> result;
                } catch (ObjectStreamException ex) {
                    // something wrong with deserialization, let's just print stacktrace and continue
                    ex.printStackTrace();
                }
            }
            // let's write out source code - can be useful even if actual compilation fails
            for(TypeSpec ts: typeSpec) {
                File fsrc = TempFileFactory.getInstance().createFile(name + "-" + ts.name + ".java");
                try (FileWriter fw = new FileWriter(fsrc)) {
                    JavaFile.builder(ActualSupplierCompiler.PACKAGE_NAME, ts).build().writeTo(fw);
                }
            }

            Supplier<T> supp = other.get(typeSpec);
            T result = supp.get();

            // use a byte array first, we do not want to leave junk in the file in case something fails
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            new ObjectOutputStream(bytes).writeObject(result);

            // serialization was successful, let's write it out
            try (FileOutputStream fos = new FileOutputStream(f)) {
                fos.write(bytes.toByteArray());
            }
            return () -> result;
        } catch(IOException|ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
