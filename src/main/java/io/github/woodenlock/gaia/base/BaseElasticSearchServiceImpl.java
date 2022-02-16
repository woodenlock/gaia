package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.util.ReflectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchEntityInformation;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基础的ES service接口实现
 *
 * @author woodenlock
 * @date 2021/10/17 13:45
 */
@SuppressWarnings("unused")
public abstract class BaseElasticSearchServiceImpl<R extends ElasticsearchRepository<E, K>, E, K extends Serializable>
    extends BaseSpringDataServiceImpl<R, E, K> implements BaseSpringDataService<E, K> {

    @Resource
    private ElasticsearchRestTemplate template;

    /**
     * 对应存储映射实体的索引信息
     */
    private IndexCoordinates index;

    @Override
    public <T> List<T> selectSelectiveByIds(Collection<K> ids, Class<T> type, String... props) {
        List<T> records = new ArrayList<>();
        if (null != ids && !ids.isEmpty()) {
            Set<String> stringIds = ids.stream().map(id -> Objects.toString(id, null)).collect(Collectors.toSet());
            NativeSearchQuery nav = new NativeSearchQueryBuilder().withIds(stringIds).build();
            Arrays.stream(props).forEach(nav::addFields);
            records = template.multiGet(nav, type, index);
        }

        return records;
    }

    @Override
    public List<K> selectIdSelective(E search) {
        Query query = buildQuery(search);
        query.addFields(getKeyName());

        return template.search(query, getEntityType(), index).get().map(h -> getKey(h.getContent()))
            .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> selectSelective(E search, Class<T> type) {
        if (null == type) {
            return new ArrayList<>();
        }

        Query query = buildQuery(search);
        ReflectUtils.getAllDeclaredFields(type).stream().map(Field::getName).forEach(query::addFields);

        return template.search(query, type, index).get().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @Override
    public long selectCount(E search) {
        return template.count(buildQuery(search), index);
    }

    @Override
    public Page<E> selectPage(E search, Pageable pageable) {
        if (null == pageable) {
            return Page.empty();
        }

        Query query = buildQuery(search);
        query.setPageable(pageable);

        SearchHits<E> hits = template.search(query, getEntityType(), index);
        List<E> list = hits.get().map(SearchHit::getContent).collect(Collectors.toList());
        long total = hits.getTotalHits();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    protected String getKeyName() {
        return ((ElasticsearchEntityInformation<?, ?>)info).getIdAttribute();
    }

    /**
     * 获取ES操作模板
     * @return org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate
     */
    protected ElasticsearchRestTemplate getTemplate() {
        return template;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        index = template.getIndexCoordinatesFor(getEntityType());
    }

    /**
     * 构建查询对象
     *
     * @param search 查询对象
     * @return org.springframework.data.elasticsearch.core.query
     */
    protected Query buildQuery(E search) {
        Criteria criteria = new Criteria();
        Query query = new CriteriaQuery(criteria);
        if (null != search) {
            ReflectUtils.getAllDeclaredFields(search.getClass()).forEach(field -> {
                Object exact;
                try {
                    exact = field.get(search);
                } catch (IllegalAccessException e) {
                    String temp = "Failed to build query from object:%s with class due to exception:%s";
                    throw new RuntimeException(String.format(temp, search, e.getMessage()));
                }
                if (null != exact) {
                    criteria.and(Criteria.where(field.getName()).is(exact));
                }
            });
        }

        return query;
    }
}