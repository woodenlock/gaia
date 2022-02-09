package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础的mongodb service接口实现
 *
 * @author woodenlock
 * @date 2021/10/17 13:45
 */
@SuppressWarnings("unused")
public abstract class BaseMongodbServiceImpl<R extends MongoRepository<E, K>, E, K extends Serializable>
    extends BaseSpringDataServiceImpl<R, E, K> implements BaseSpringDataService<E, K> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private R repository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public <T> List<T> selectSelectiveByIds(Collection<K> ids, Class<T> type, String... props) {
        List<T> result = null;
        if (DynamicModuleUtils.isNotEmpty(ids) && null != type) {
            Query query = new Query();
            if (props.length != 0) {
                org.springframework.data.mongodb.core.query.Field field = query.fields();
                Arrays.stream(props).forEach(field::include);
            }
            query.addCriteria(new Criteria(getKeyName()).in(ids));
            result = mongoTemplate.find(query, type, ((MongoEntityInformation<?, ?>)info).getCollectionName());
        }

        return result;
    }

    @Override
    public List<K> selectIdSelective(E search) {
        Query query = null == search ? new Query() : new Query(new Criteria().alike(Example.of(search)));
        query.fields().include(getKeyName());

        return mongoTemplate.find(query, getEntityType()).stream().map(this::getKey).collect(Collectors.toList());
    }

    @Override
    public <T> List<T> selectSelective(E search, Class<T> type) {
        Query query = null == search ? new Query() : new Query(new Criteria().alike(Example.of(search)));
        return null == type ? new ArrayList<>() :
            mongoTemplate.find(query, type, ((MongoEntityInformation<?, ?>)info).getCollectionName());
    }

    @Override
    public long selectCount(E search) {
        return null == search ? getRepository().count() : repository.count(Example.of(search));
    }

    @Override
    public Page<E> selectPage(E search, Pageable pageable) {
        Page<E> result;
        if (null == pageable) {
            result = Page.empty();
        } else if (null == search) {
            result = repository.findAll(pageable);
        } else {
            result = repository.findAll(Example.of(search), pageable);
        }

        return result;
    }

    @Override
    protected String getKeyName() {
        return ((MongoEntityInformation<?, ?>)info).getIdAttribute();
    }
}