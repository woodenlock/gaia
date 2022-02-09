package io.github.woodenlock.gaia;

/**
 * 部件，单个目标生成组件对象
 *
 * @author woodenlock
 * @date 2021/12/25 17:07
 */
@SuppressWarnings("unused")
public class Part extends Node {

    /**
     * 对应的持久化映射类
     */
    private Class<?> entity;

    /**
     * 目标生成类
     */
    private Class<?> clazz;

    /**
     * 对应注册到spring容器的bean的id
     */
    private String beanId;

    public Part() {
        super();
    }

    public Class<?> getEntity() {
        return entity;
    }

    public void setEntity(Class<?> entity) {
        this.entity = entity;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    @Override
    public String toString() {
        return "{" + "\"entity\":" + entity + ",\"clazz\":" + clazz + ",\"beanId\":" + beanId + ",\"suffix\":"
            + getSuffix() + ",\"path\":" + getPath() + ",\"generate\":" + getGenerate() + ",\"generatorPath\":"
            + getGeneratorPath() + ",\"register\":" + getRegister() + ",\"registrarPath\":" + getRegistrarPath()
            + ",\"customizes\":" + getCustomizes() + "}";
    }
}