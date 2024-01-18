//package com.imst.event.map.web.config;
//
//import java.util.Collections;
//import java.util.List;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.ByteArrayHttpMessageConverter;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebMvcMessageConvertersConfiguration implements WebMvcConfigurer {
//
//	@Override
//	public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
//		ByteArrayHttpMessageConverter byteArrayConverter = new ByteArrayHttpMessageConverter();
//		byteArrayConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.valueOf("image/tiff")));
//		messageConverters.add(byteArrayConverter);
//	}
//}