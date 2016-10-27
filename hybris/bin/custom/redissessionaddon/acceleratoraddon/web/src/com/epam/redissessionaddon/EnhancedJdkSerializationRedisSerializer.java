package com.epam.redissessionaddon;

import org.springframework.core.NestedIOException;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * This enhanced version of {@link JdkSerializationRedisSerializer} is required because Spring Session doesn't use
 * Spring Framework {@link DefaultDeserializer#DefaultDeserializer(java.lang.ClassLoader)} method for instantiation.
 * Without this new (Spring Framework Core 4.2+) feature Spring Session has troubles with two hybris class loaders:
 * <ul>
 *     <li>{@link de.hybris.bootstrap.loader.PlatformInPlaceClassLoader}</li>
 *     <li>{@link de.hybris.tomcat.HybrisWebappLoader}</li>
 * </ul>
 *
 * This is a typical issue with Java Serialization, see a comment on StackOverflow:
 * <a href="http://stackoverflow.com/a/36228195/1696297"/>Difference between thread's
 * context class loader and normal classloader</a>}
 *
 * TODO: check Spring Session 1.3 or propose PR to fix this issue.
 */
public class EnhancedJdkSerializationRedisSerializer extends JdkSerializationRedisSerializer {
    public EnhancedJdkSerializationRedisSerializer() {
        super(new SerializingConverter(), new DeserializingConverter(new EnhancedDeserializer()));
    }

    private static class EnhancedDeserializer implements Deserializer<Object> {
        @Override
        public Object deserialize(InputStream inputStream) throws IOException {
            final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                return deserializeInternal(inputStream);
            } finally {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }

        /**
         * Reads the input stream and deserializes into an object.
         * @see ObjectInputStream#readObject()
         */
        private Object deserializeInternal(InputStream inputStream) throws IOException {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            try {
                return objectInputStream.readObject();
            }
            catch (ClassNotFoundException ex) {
                throw new NestedIOException("Failed to deserialize object type", ex);
            }
        }
    }
}
