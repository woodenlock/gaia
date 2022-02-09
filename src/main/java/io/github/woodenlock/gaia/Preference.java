package io.github.woodenlock.gaia;

/**
 * 动态模块的偏好
 *
 * @author woodenlock
 * @date 2021/5/25 15:45
 */
@SuppressWarnings("unused")
class Preference extends Node {

    /**
     * 持久化约束
     */
    private String persistence;

    /**
     * 组件约束
     */
    private String component;

    /**
     * 排序值，数字越大优先级越高
     */
    private int order;

    public Preference() {
        super();
    }

    public String getPersistence() {
        return persistence;
    }

    public void setPersistence(String persistence) {
        this.persistence = persistence;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "{" + "\"suffix\":\"" + getSuffix() + '\"' + ",\"path\":\"" + getPath() + '\"' + ",\"generate\":"
            + getGenerate() + ",\"generatorPath\":\"" + getGeneratorPath() + '\"' + ",\"register\":" + getRegister()
            + ",\"registrarPath\":\"" + getRegistrarPath() + '\"' + ",\"persistence\":\"" + persistence + '\"'
            + ",\"component\":\"" + component + '\"' + ",\"order\":" + order + ",\"customizes\":" + getCustomizes() + "}";
    }
}