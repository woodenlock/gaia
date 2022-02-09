package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.function.SerializableFunction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 基础的spring-data相关包的通用service接口定义
 * 提供与MBP相似的使用便利性
 *
 * @author woodenlock
 * @date 2021/10/17 13:42
 */
@SuppressWarnings("unused")
public interface BaseSpringDataService<E, K extends Serializable> {

    /**
     * 根据主键查询
     *
     * @param id id主键
     * @return 记录实体
     */
    E getById(K id);

    /**
     * 根据主键集合查询
     *
     * @param ids id主键集合
     * @return 记录实体集合
     */
    List<E> selectByIds(Collection<K> ids);

    /**
     * 根据主键集合查询自定义返回字段的结果集
     *
     * @param ids   id主键集合
     * @param props 属性名称集合
     * @return 记录实体集合
     */
    List<E> selectSelectiveByIds(Collection<K> ids, String... props);

    /**
     * 根据主键集合查询自定义返回字段的结果集
     *
     * @param ids   id主键集合
     * @param type  返回值类型
     * @param props 属性名称集合
     * @return 记录实体集合
     */
    <T> List<T> selectSelectiveByIds(Collection<K> ids, Class<T> type, String... props);

    /**
     * 根据主键集合查询自定义返回字段的结果集
     *
     * @param ids   id主键集合
     * @param props 属性集合
     * @return 记录实体集合
     */
    List<E> selectSelectiveByIds(Collection<K> ids, Collection<SerializableFunction<?, ?>> props);

    /**
     * 根据属性自定义搜索符合数据的主键集合
     *
     * @param search 自定义搜索对象
     * @return 记录实体主键集合
     */
    List<K> selectIdSelective(E search);

    /**
     * 根据属性自定义搜索
     *
     * @param search 自定义搜索对象
     * @param type   返回类型，不可为空
     * @return 记录实体集合
     */
    <T> List<T> selectSelective(E search, Class<T> type);

    /**
     * 根据属性自定义查询符合的记录数量
     *
     * @param search 自定义搜索对象
     * @return 符合的记录数量
     */
    long selectCount(E search);

    /**
     * 分页查询
     *
     * @param search   自定义搜索对象
     * @param pageable 分页信息
     * @return 分页结果
     */
    Page<E> selectPage(E search, Pageable pageable);

    /**
     * 新增记录
     *
     * @param record 目标记录
     * @return 是否操作成功
     */
    boolean insert(E record);

    /**
     * 批量新增记录
     *
     * @param records 目标记录集合
     * @return 是否操作成功
     */
    boolean insertBatch(Collection<E> records);

    /**
     * 保存记录
     *
     * @param record 目标记录
     * @return 是否操作成功
     */
    boolean save(E record);

    /**
     * 批量保存记录
     *
     * @param records 目标记录集合
     * @return 是否操作成功
     */
    boolean saveBatch(Collection<E> records);

    /**
     * 根据id主键删除
     *
     * @param id id主键
     * @return 是否操作成功
     */
    boolean deleteById(K id);

    /**
     * 批量删除记录
     *
     * @param records 目标记录集合
     * @return 是否操作成功
     */
    boolean deleteBatch(Collection<E> records);
}