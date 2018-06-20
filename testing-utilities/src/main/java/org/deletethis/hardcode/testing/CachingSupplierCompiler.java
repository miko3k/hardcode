package org.deletethis.hardcode.testing;

import com.squareup.javapoet.TypeSpec;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

    CachingSupplierCompiler(SupplierCompiler other) {
        this.other = other;
    }

    @Override
    public <T> Supplier<T> get(TypeSpec typeSpec) {
        try {
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
