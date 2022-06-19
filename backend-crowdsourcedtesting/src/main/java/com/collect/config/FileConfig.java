package com.collect.config;

import com.collect.aop.ImageInterceptor;
import com.collect.util.strategy.MD5Strategy;
import com.collect.util.strategy.PathEncryptStrategy;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "file")
@Configuration
public class FileConfig implements WebMvcConfigurer {

    //文件存储根目录
    private String base;
    // 任务描述文件 task_decr+taskId/filename
    private String task_decr = "/file/publish/description";
    // 任务发布文件 task_publish+taskId/filename
    private String task_publish = "/file/publish/exe";
    // 任务提交文件 report_img+taskId/userId/filename
    private String report_img = "/file/report_img";

    private String wordcloud = "/file/TaskClusters";
    // 路径加密策略
    @Qualifier("MD5Strategy")
    @Autowired
    private PathEncryptStrategy pathEncryptStrategy;

    @PostConstruct
    void init() {
        if (base == null) {
            Properties properties = System.getProperties();
            base = properties.getProperty("user.dir");
        }
        task_decr = process(task_decr);
        task_publish = process(task_publish);
        report_img = process(report_img);
    }


    /**
     * 预处理path，防止path不符合命名规则
     * @param path，原始路径
     * @return 符合规则的路径
     */
    private String process(String path) {
        path = path.replace("/", File.separator).replace("\\", File.separator);
        if (!path.startsWith(File.separator)) {
            path = File.separator + path;
        }
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        return path;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ImageInterceptor()).addPathPatterns("/image/**");
    }

    /**
     * 将开放路径映射到准备的文件夹
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**").addResourceLocations("file:" + base + task_decr + File.separator)
                .addResourceLocations("file:" + base + task_publish + File.separator).addResourceLocations("file:" + base + report_img + File.separator)
                .addResourceLocations("file:"+base+wordcloud+File.separator)
        ;
    }
}