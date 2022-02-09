package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.util.ReflectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 基础的jpa service接口实现
 *
 * @author woodenlock
 * @date 2021/10/17 13:45
 */
@SuppressWarnings("unused")
public abstract class BaseJpaServiceImpl<R extends JpaSpecificationRepository<E, K>, E, K extends Serializable>
    extends BaseSpringDataServiceImpl<R, E, K> implements BaseSpringDataService<E, K> {

    @Override
    public <T> List<T> selectSelectiveByIds(Collection<K> ids, Class<T> type, String... props) {
        Specification<E> spec = (root, query, builder) -> {
            setSelect(query, root, props);
            CriteriaBuilder.In<Object> in = builder.in(root.get(getKeyName()));
            ids.forEach(in::value);
            return builder.and(in);
        };

        return ReflectUtils.conversionList(getRepository().findAll(spec), type);
    }

    @Override
    public List<K> selectIdSelective(E search) {
        return getRepository().findAll(buildSpecification(search, getKeyName())).stream().map(this::getKey)
            .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> selectSelective(E search, Class<T> type) {
        if (null == type) {
            return new ArrayList<>();
        }

        String[] props = ReflectUtils.getAllDeclaredFields(type).stream().map(Field::getName).toArray(String[]::new);
        List<E> records = getRepository().findAll(buildSpecification(search, props));

        return ReflectUtils.conversionList(records, type);
    }

    @Override
    public long selectCount(E search) {
        return getRepository().count(buildSpecification(search));
    }

    @Override
    public Page<E> selectPage(E search, Pageable pageable) {
        return null == pageable ? Page.empty() : getRepository().findAll(buildSpecification(search), pageable);
    }

    /**
     * 构造搜索配置对象
     *
     * @param search 属性筛选对象
     * @param props  需要查询的属性，不传查全部
     * @return org.springframework.data.jpa.domain.Specification<E>
     */
    protected Specification<E> buildSpecification(E search, String... props) {
        return (root, query, builder) -> {
            if (null != search) {
                ReflectUtils.getAllDeclaredFields(search.getClass()).forEach(f -> {
                    try {
                        Optional.ofNullable(f.get(search))
                            .ifPresent(value -> query.where(builder.equal(root.get(f.getName()), value)));
                    } catch (IllegalAccessException e) {
                        String temp = "Failed to build query of:%s with field:%s when querying due to exception:%s";
                        throw new RuntimeException(String.format(temp, search, f.getName(), e.getMessage()));
                    }
                });
            }
            setSelect(query, root, props);

            return query.getRestriction();
        };
    }

    /**
     * 查询对象设置搜索字段
     *
     * @param query 查询对象
     * @param root  关联查询节点
     * @param props 属性名数组
     */
    private void setSelect(CriteriaQuery<?> query, Root<?> root, String... props) {
        if (props.length != 0) {
            Path<?>[] names = new Path[props.length];
            for (int i = 0; i < props.length; i++) {
                names[i] = root.get(props[i]);
            }
            query.multiselect(names);
        }
    }
}