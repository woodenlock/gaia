package io.github.woodenlock.gaia;

import io.github.woodenlock.gaia.registrar.BeanRegistrar;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 单组件节点基本信息
 *
 * @author woodenlock
 * @date 2021/5/23 15:45
 */
@SuppressWarnings("unused")
class Node implements Serializable {

    /**
     * 生成类后缀
     */
    private String suffix;

    /**
     * 声明所在包路径(以“/”分割)
     */
    private String path;

    /**
     * 是否要生成类信息
     *
     * @see io.github.woodenlock.gaia.generation.ClassGenerator
     */
    private BooleanEnum generate;

    /**
     * 生成器类的全路径
     *
     * @see Node#getGenerate()
     */
    private String generatorPath;

    /**
     * 是否要作为bean注入spring容器
     *
     * @see BeanRegistrar
     */
    private BooleanEnum register;

    /**
     * spring bean注册器类的全路径
     *
     * @see Node#getRegister()
     */
    private String registrarPath;

    /**
     * 自定义属性配置，用于支持埋点
     */
    private Map<String, Object> customizes;

    public Node() {
        this.generate = this.register = BooleanEnum.IGNORE;
        customizes = new HashMap<>();
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BooleanEnum getGenerate() {
        return generate;
    }

    public void setGenerate(BooleanEnum generate) {
        this.generate = generate;
    }

    public String getGeneratorPath() {
        return generatorPath;
    }

    public void setGeneratorPath(String generatorPath) {
        this.generatorPath = generatorPath;
    }

    public BooleanEnum getRegister() {
        return register;
    }

    public void setRegister(BooleanEnum register) {
        this.register = register;
    }

    public String getRegistrarPath() {
        return registrarPath;
    }

    public void setRegistrarPath(String registrarPath) {
        this.registrarPath = registrarPath;
    }

    public Map<String, Object> getCustomizes() {
        return customizes;
    }

    public void setCustomizes(Map<String, Object> customizes) {
        this.customizes = customizes;
    }

    @Override
    public String toString() {
        return "{" + "\"suffix\":\"" + suffix + '\"' + ",\"path\":\"" + path + '\"' + ",\"generate\":" + generate
            + ",\"generatorPath\":\"" + generatorPath + '\"' + ",\"register\":" + register + ",\"registrarPath\":\""
            + registrarPath + '\"' + ",\"customizes\":" + customizes + "}";
    }
}