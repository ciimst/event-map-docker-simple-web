package com.imst.event.map.web.config.fts;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectOutput;
import org.springframework.core.NestedIOException;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;
 
public class FstDeserializerSerializer implements Serializer<Object>, Deserializer<Object> {
 
    private final FSTConfiguration fstConfiguration;
     
    public FstDeserializerSerializer(final ClassLoader classLoader) {
        fstConfiguration = FSTConfiguration.createDefaultConfiguration();
        fstConfiguration.setClassLoader(classLoader);
    }
 
    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        try{
            return fstConfiguration.getObjectInput(inputStream).readObject();
        }
        catch (ClassNotFoundException ex) {
            throw new NestedIOException("Failed to deserialize object type", ex);
        }
    }
 
    @Override
    public void serialize(Object object, OutputStream outputStream) throws IOException {
        // Do not close fstObjectOutput - that would prevent reuse and cause an error
        // see https://github.com/RuedigerMoeller/fast-serialization/wiki/Serialization
        @SuppressWarnings("resource")
        final FSTObjectOutput fstObjectOutput = fstConfiguration.getObjectOutput(outputStream);
        fstObjectOutput.writeObject(object);
        fstObjectOutput.flush();
    }
}