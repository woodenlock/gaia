package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.function.SerializableFunction;
import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import io.github.woodenlock.gaia.util.NamingUtils;
import io.github.woodenlock.gaia.util.ReflectUtils;
import io.github.woodenlock.gaia.util.SpringAopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.support.PersistentEntityInformation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基础的spring-data相关包的通用service基础实现类
 * 提供与MBP相似的使用便利性
 *
 * @author woodenlock
 * @date 2021/10/17 13:42
 */
public abstract class BaseSpringDataServiceImpl<R extends CrudRepository<E, K>, E, K extends Serializable>
    implements BaseSpringDataService<E, K>, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(BaseSpringDataServiceImpl.class);

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private R repository;

    /**
     * 文档映射对象基本信息
     */
    PersistentEntityInformation<E, K> info = null;

    /**
     * 获取 文档映射对象主键名称
     *
     * @return java.lang.Class<E>
     */
    abstract protected String getKeyName();

    /**
     * 获取 文档映射对象类型
     *
     * @return java.lang.Class<E>
     */
    @SuppressWarnings("unused")
    protected Class<K> getKeyType() {
        return info.getIdType();
    }

    /**
     * 从原始对象中解析出id主键值
     *
     * @return java.lang.Class<E>
     */
    protected K getKey(E entity) {
        return info.getId(entity);
    }

    /**
     * 获取 文档映射对象类型
     *
     * @return java.lang.Class<E>
     */
    protected Class<E> getEntityType() {
        return info.getJavaType();
    }

    /**
     * 获取实际持有的对应的mongodb资源
     *
     * @return R
     */
    protected R getRepository() {
        return repository;
    }

    @Override
    public E getById(K id) {
        return null == id ? null : getRepository().findById(id).orElse(null);
    }

    @Override
    public List<E> selectByIds(Collection<K> ids) {
        return selectSelectiveByIds(ids, info.getJavaType());
    }

    @Override
    public List<E> selectSelectiveByIds(Collection<K> ids, Collection<SerializableFunction<?, ?>> props) {
        String[] arr = new String[] {};
        if (DynamicModuleUtils.isNotEmpty(props)) {
            List<String> names = new ArrayList<>(props.size());
            props.forEach(prop -> names.add(NamingUtils.getProperty(prop)));
            arr = names.toArray(arr);
        }

        return selectSelectiveByIds(ids, arr);
    }

    @Override
    public List<E> selectSelectiveByIds(Collection<K> ids, String... props) {
        return selectSelectiveByIds(ids, info.getJavaType(), props);
    }

    @Override
    public boolean insert(E record) {
        log.debug("Ready to insert record:{} by class:{}.", record, getClass().getCanonicalName());
        boolean result = false;
        if (null != record) {
            getRepository().save(record);
            result = true;
        }

        return result;
    }

    @Override
    public boolean insertBatch(Collection<E> records) {
        log.debug("Ready to insert batch records:{} by class:{}.", records, getClass().getCanonicalName());
        boolean result = false;
        if (DynamicModuleUtils.isNotEmpty(records)) {
            saveAll(records);
            result = true;
        }

        return result;
    }

    @Override
    public boolean save(E record) {
        log.debug("Ready to update record:{} by class:{}.", record, getClass().getCanonicalName());
        boolean result = false;
        if (null != record) {
            getRepository().save(record);
            result = true;
        }

        return result;
    }

    @Override
    public boolean saveBatch(Collection<E> records) {
        log.debug("Ready to update batch records:{} by class:{}.", records, getClass().getCanonicalName());
        boolean result = false;
        if (DynamicModuleUtils.isNotEmpty(records)) {
            saveAll(records);
            result = true;
        }

        return result;
    }

    @Override
    public boolean deleteById(K id) {
        log.debug("Ready to delete the record of key:{} by class:{}.", id, getClass().getCanonicalName());
        boolean result = false;
        if (null != id) {
            getRepository().deleteById(id);
            result = true;
        }

        return result;
    }

    @Override
    public boolean deleteBatch(Collection<E> records) {
        log.debug("Ready to delete batch record:{} by class:{}.", records, getClass().getCanonicalName());
        boolean result = false;
        if (DynamicModuleUtils.isNotEmpty(records)) {
            List<E> list = records.stream().filter(Objects::nonNull).collect(Collectors.toList());
            getRepository().deleteAll(list);
            result = true;
        }

        return result;
    }

    /**
     * 设置spring-data通用的成员属性信息
     *
     * @see org.springframework.data.mongodb.repository.support.SimpleMongoRepository
     * @see org.springframework.data.elasticsearch.repository.support.AbstractElasticsearchRepository
     * @see org.springframework.data.solr.repository.support.SimpleSolrRepository
     *
     * <p>
     * bad smell spring-data对于主键信息、基本查询构造只开放了少量的接口调用，故只能反射获取；
     * 依赖版本升级时可以查看是否有开放更多调用接口
     * </p>
     */
    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        try {
            Object origin = SpringAopUtils.getTarget(repository);
            Field infoField = ReflectUtils.getAllDeclaredFields(origin.getClass())
                .stream().filter(f -> "entityInformation".equals(f.getName())).findFirst().orElse(null);
            if(null == infoField) {
                throw new RuntimeException("Failed to init Elasticsearch service with entityInformation by:" + getClass().getCanonicalName());
            }
            infoField.setAccessible(true);
            info = (PersistentEntityInformation<E, K>)infoField.get(origin);
        } catch (Exception e) {
            String template = "Failed to set reflect info of class:%s due to exception:%s.";
            String error = String.format(template, getClass().getSimpleName(), e.getMessage());
            log.error(error);
            throw new RuntimeException(error);
        }
    }

    /**
     * 保存记录集合
     *
     * @param records 记录集合
     */
    private void saveAll(Collection<E> records) {
        if (DynamicModuleUtils.isNotEmpty(records)) {
            List<E> list = records.stream().filter(Objects::nonNull).collect(Collectors.toList());
            getRepository().saveAll(list);
        }
    }
}