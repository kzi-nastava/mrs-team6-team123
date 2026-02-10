package rs.ac.uns.ftn.asd.Projekatsiit2023.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // App runs from workspace root, so uploads folder is in current directory
        String uploadPath = new java.io.File("./uploads").getAbsolutePath();
        System.out.println("Serving static files from: " + uploadPath);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
