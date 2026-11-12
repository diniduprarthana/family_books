package com.familybooks.config;

import com.familybooks.service.BookCoverStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final BookCoverStorageService bookCoverStorageService;

    public WebConfig(BookCoverStorageService bookCoverStorageService) {
        this.bookCoverStorageService = bookCoverStorageService;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/error/403").setViewName("error/403");
        registry.addViewController("/error/404").setViewName("error/404");
        registry.addViewController("/error/500").setViewName("error/500");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = bookCoverStorageService.getUploadRoot().toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/books/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
