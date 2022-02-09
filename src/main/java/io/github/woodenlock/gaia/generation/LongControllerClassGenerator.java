package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.base.BaseLongController;
import io.github.woodenlock.gaia.base.BaseSpringDataController;

/**
 * 基于Long主键的spring controller类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see BaseLongController
 */
@SuppressWarnings("unused")
public class LongControllerClassGenerator extends BaseSpringControllerClassGenerator {

    @Override
    @SuppressWarnings("rawtypes")
    protected Class<? extends BaseSpringDataController> getSuperClass() {
        return BaseLongController.class;
    }
}