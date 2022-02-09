package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.base.BaseSpringDataController;
import io.github.woodenlock.gaia.base.BaseStringController;

/**
 * 基于String主键的spring controller类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see BaseStringController
 */
@SuppressWarnings("unused")
public class StringControllerClassGenerator extends BaseSpringControllerClassGenerator {

    @Override
    @SuppressWarnings("rawtypes")
    protected Class<? extends BaseSpringDataController> getSuperClass() {
        return BaseStringController.class;
    }
}