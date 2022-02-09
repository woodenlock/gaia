package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import io.github.woodenlock.gaia.common.GenerateConst;
import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import io.github.woodenlock.gaia.util.ReflectUtils;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认的对象视图类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 */
@SuppressWarnings("unused")
public class ViewObjectClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        String path = part.getPath();
        String suffix = part.getSuffix();
        Class<?> entity = part.getEntity();
        if (DynamicModuleUtils.isBlank(path)) {
            throw new RuntimeException("Failed to generate object class due to illegal params:" + part);
        }

        List<String> ignores = new ArrayList<>();
        Object configs = part.getCustomizes().get(GenerateConst.Config.IGNORE_FIELDS);
        if (configs instanceof Map) {
            ((Map<?, ?>)configs).values().stream().map(Object::toString).forEach(ignores::add);
        }
        List<Field> fields =
            ReflectUtils.getAllDeclaredFields(entity).stream().filter(f -> !ignores.contains(f.getName()))
                .collect(Collectors.toList());

        return AsmUtils.generateBasicEntityBytes(path, entity.getSimpleName() + suffix, fields);
    }
}