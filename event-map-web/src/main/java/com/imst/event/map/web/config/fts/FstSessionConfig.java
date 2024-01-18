package com.imst.event.map.web.config.fts;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;
 
@Configuration
public class FstSessionConfig implements BeanClassLoaderAware {
 
    private ClassLoader classLoader;
 
    @Bean
    public ConversionService springSessionConversionService() {
        final FstDeserializerSerializer fstDeserializerSerializer = new FstDeserializerSerializer(classLoader);
 
        final GenericConversionService conversionService = new GenericConversionService();
        conversionService.addConverter(Object.class, byte[].class,
                new SerializingConverter(fstDeserializerSerializer));
        conversionService.addConverter(byte[].class, Object.class,
                new CustomDeserializingConverter(fstDeserializerSerializer));
        return conversionService;
    }
 
    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}