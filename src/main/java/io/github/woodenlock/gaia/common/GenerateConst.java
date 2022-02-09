package io.github.woodenlock.gaia.common;

/**
 * 动态模块的相关常量
 *
 * @author woodenlock
 * @date 2021/5/23 15:46
 */
@SuppressWarnings("unused")
public interface GenerateConst {

    /**
     * 配置的相关常量
     *
     * @author woodenlock
     * @date 2021/5/23 16:37
     */
    interface Config {

        /**
         * 配置前缀
         **/
        String PREFIX = "dynamic.module";

        /**
         * 忽略的属性
         **/
        String IGNORE_FIELDS = "ignoreFields";

        /**
         * 控制器默认的请求前缀
         **/
        String REQUEST_SUFFIX = "requestSuffix";
    }

    /**
     * 持久化的相关常量
     *
     * @author woodenlock
     * @date 2021/5/23 16:37
     */
    interface Persistence {

        /**
         * spring-boot-starter-data-jpa/hibernate
         **/
        String JPA = "Jpa";

        /**
         * MyBatis-plus
         **/
        String PLUS = "MyBatis-Plus";

        /**
         * spring-boot-starter-data-solr
         **/
        String SOLR = "Solr";

        /**
         * spring-boot-starter-data-mongodb
         **/
        String MONGODB = "MongoDB";

        /**
         * spring-boot-starter-data-elasticsearch
         **/
        String ELASTIC = "Elasticsearch";
    }

    /**
     * 组件的相关常量
     *
     * @author woodenlock
     * @date 2021/5/23 16:37
     */
    interface Component {

        /**
         * 视图对象
         **/
        String VO = "view-object";

        /**
         * 持久层接口
         **/
        String DAO = "dao";

        /**
         * service接口
         **/
        String SERVICE = "i-service";

        /**
         * service实现
         **/
        String IMP = "service-impl";

        /**
         * 控制器
         **/
        String CONTROLLER = "controller";
    }
}