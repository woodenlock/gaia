package io.github.woodenlock.gaia;

import io.github.woodenlock.gaia.annotation.EnableDynamicModules;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 静态模块路由配置
 *
 * @author woodenlock
 * @date 2021-05-21 22:30:54
 */
class DynamicModuleProperties implements Serializable {

    /**
     * 包含的实体类集合(类不可重名)
     */
    private String[] includes;

    /**
     * 排除的实体类集合
     */
    private String[] excludes;

    /**
     * 默认的基础包路径(以“/”分割)，默认为动态模块启用注解{@link EnableDynamicModules}入口类所在的包路径
     */
    private String defaultPrefixPath;

    /**
     * 默认生效的持久化类型
     */
    private String defaultPersistence;

    /**
     * 默认生效的组件类型集合
     */
    private Set<String> defaultComponents;

    /**
     * 默认的偏好配置集合，不推荐用户重置
     */
    private Preference[] defaultPreferences;

    /**
     * 偏好配置集合，允许用户自定义自己的偏好
     */
    private List<Preference> preferences;

    public String[] getIncludes() {
        return includes;
    }

    public void setIncludes(String[] includes) {
        this.includes = includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public void setExcludes(String[] excludes) {
        this.excludes = excludes;
    }

    public String getDefaultPrefixPath() {
        return defaultPrefixPath;
    }

    public void setDefaultPrefixPath(String defaultPrefixPath) {
        this.defaultPrefixPath = defaultPrefixPath;
    }

    public String getDefaultPersistence() {
        return defaultPersistence;
    }

    public void setDefaultPersistence(String defaultPersistence) {
        this.defaultPersistence = defaultPersistence;
    }

    public Set<String> getDefaultComponents() {
        return defaultComponents;
    }

    public void setDefaultComponents(Set<String> defaultComponents) {
        this.defaultComponents = defaultComponents;
    }

    public Preference[] getDefaultPreferences() {
        return defaultPreferences;
    }

    public void setDefaultPreferences(Preference[] defaultPreferences) {
        this.defaultPreferences = defaultPreferences;
    }

    public List<Preference> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<Preference> preferences) {
        this.preferences = preferences;
    }

    @Override
    public String toString() {
        return "{" + "\"includes\":" + Arrays.toString(includes) + ",\"excludes\":" + Arrays.toString(excludes)
            + ",\"defaultPrefixPath\":\"" + defaultPrefixPath + "\",\"defaultPersistence\":\"" + defaultPersistence
            + "\",\"defaultComponents\":" + defaultComponents + ",\"preferences\":" + Arrays
            .toString(defaultPreferences) + ",\"preferences\":" + preferences + "}";
    }
}