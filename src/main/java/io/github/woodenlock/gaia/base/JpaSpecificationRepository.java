package io.github.woodenlock.gaia.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * jpa自定义筛选数据源接口
 *
 * @author woodenlock
 * @date 2022/1/23 10:17
 */
public interface JpaSpecificationRepository<E, K> extends JpaRepository<E, K>, JpaSpecificationExecutor<E> {

}