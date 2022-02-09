package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import io.github.woodenlock.gaia.util.ReflectUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.RequestMethod;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.data.solr.repository.query.SolrEntityInformation;
import org.springframework.lang.Nullable;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 基础的solr service接口实现
 *
 * @author woodenlock
 * @date 2021/10/17 13:45
 */
@SuppressWarnings("unused")
public abstract class BaseSolrServiceImpl<R extends SolrCrudRepository<E, K>, E, K extends Serializable>
    extends BaseSpringDataServiceImpl<R, E, K> implements BaseSpringDataService<E, K> {

    @Resource
    private SolrTemplate solrTemplate;

    /**
     * SolrTemplate的基础默认查询方法
     * 详见 SolrTemplate#querySolr(String, SolrDataQuery, Class, RequestMethod)
     */
    private static final Method QUERY_SOLR_METHOD;

    static {
        try {
            QUERY_SOLR_METHOD = SolrTemplate.class
                .getDeclaredMethod("querySolr", String.class, SolrDataQuery.class, Class.class, RequestMethod.class);
            QUERY_SOLR_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new Error(String.format("Failed to init the class:%s when getting method:querySolr from SolrTemplate "
                + "by reflection due to exception:%s", BaseSolrServiceImpl.class, e.getMessage()));
        }
    }

    @Override
    public <T> List<T> selectSelectiveByIds(Collection<K> ids, Class<T> type, String... props) {
        List<T> result = new ArrayList<>();
        if (DynamicModuleUtils.isNotEmpty(ids) && null != type) {
            Query query = new SimpleQuery(new Criteria(getKeyName()).in(ids));
            if (props.length != 0) {
                Arrays.stream(props).filter(DynamicModuleUtils::isNotBlank)
                    .forEach(field -> query.addProjectionOnField(new SimpleField(field.trim())));
            }
            result = querySolr(query, type, RequestMethod.GET);
        }

        return result;
    }

    @Override
    public List<K> selectIdSelective(E search) {
        Query query = buildQuery(search).addProjectionOnField(new SimpleField(getKeyName()));
        return querySolr(query, getEntityType(), RequestMethod.GET).stream().map(this::getKey)
            .collect(Collectors.toList());
    }

    @Override
    public <T> List<T> selectSelective(E search, Class<T> type) {
        return null == type ? new ArrayList<>() : querySolr(buildQuery(search), type, RequestMethod.GET);
    }

    @Override
    public long selectCount(E search) {
        return null == search ? getRepository().count() :
            solrTemplate.count(((SolrEntityInformation<?, ?>)info).getCollectionName(), buildQuery(search));
    }

    @Override
    public Page<E> selectPage(E search, Pageable pageable) {
        Page<E> result;
        if (null == pageable) {
            result = Page.empty();
        } else {
            String collection = ((SolrEntityInformation<?, ?>)info).getCollectionName();
            result = solrTemplate.query(collection, buildQuery(search)
                    .setPageRequest(PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort())),
                getEntityType());
        }

        return result;
    }

    @Override
    protected String getKeyName() {
        return ((SolrEntityInformation<?, ?>)info).getIdAttribute();
    }

    /**
     * 暴露 SolrTemplate的基础默认查询方法
     *
     * @param query         查询对象
     * @param clazz         返回的对象类型
     * @param requestMethod 请求方式
     * @return java.util.List<T>
     */
    @SuppressWarnings("SameParameterValue")
    protected <T> List<T> querySolr(SolrDataQuery query, Class<T> clazz, @Nullable RequestMethod requestMethod) {
        List<T> result = new ArrayList<>();
        if (null != clazz && null != query) {
            try {
                String collection = ((SolrEntityInformation<?, ?>)info).getCollectionName();
                QueryResponse response =
                    (QueryResponse)QUERY_SOLR_METHOD.invoke(solrTemplate, collection, query, clazz, requestMethod);
                if (response.getResults().size() > 0) {
                    result = solrTemplate.getConverter().read(response.getResults(), clazz);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                String temp = "Failed to query solr:%s with class due to exception:%s";
                throw new RuntimeException(String.format(temp, query.getCriteria(), e.getMessage()));
            }
        }

        return result;
    }

    /**
     * 构建查询对象
     * @see org.springframework.data.solr.core.query.SimpleQuery
     *
     * @param search 查询对象
     * @return org.springframework.data.solr.core.query.Query
     */
    protected Query buildQuery(E search) {
        Query query = new SimpleQuery();
        if (null != search) {
            ReflectUtils.getAllDeclaredFields(search.getClass()).forEach(field -> {
                try {
                    Optional.ofNullable(field.get(search))
                        .ifPresent(value -> query.addCriteria(Criteria.where(field.getName()).is(value)));
                } catch (IllegalAccessException e) {
                    String temp = "Failed to build query from object:%s with class due to exception:%s";
                    throw new RuntimeException(String.format(temp, search, e.getMessage()));
                }
            });
        }
        if (null == query.getCriteria()) {
            query.addCriteria(new SimpleStringCriteria("*:*"));
        }

        return query;
    }
}