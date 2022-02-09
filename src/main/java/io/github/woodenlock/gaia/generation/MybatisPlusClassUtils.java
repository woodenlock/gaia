package io.github.woodenlock.gaia.generation;

import org.springframework.asm.ClassWriter;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.woodenlock.gaia.base.BaseMyBatisPlusController;
import io.github.woodenlock.gaia.util.ReflectUtils;

/**
 * MPB相关类生成工具类
 *
 * @author woodenlock
 * @date 2021-05-21 21:03:27
 */
class MybatisPlusClassUtils extends AsmUtils {

    /**
     * 生成BaseMapper子类类对象
     *
     * @param entityClass 对应的实体类的类信息
     * @param classPath   目标类路径
     * @param suffix      生成的BaseMapper类后缀
     * @return java.lang.Class<?>
     * @see BaseMapper
     */
    public static Class<?> generateMapperClass(Class<?> entityClass, String classPath, String suffix) {
        Class<?> result = null;
        if (null != entityClass) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String fullPath = getFullClassPath(classPath, className);
            String signature = String
                .format("%sL%s<%s>;", getClassBasicSignature(Object.class), getClassPath(BaseMapper.class),
                    getClassBasicSignature(entityClass));
            cw.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, fullPath, signature, OBJECT_CLASS_PATH,
                new String[] {getClassPath(BaseMapper.class)});
            cw.visitEnd();
            byte[] bytes = cw.toByteArray();
            result = ReflectUtils.load(fullPath, bytes, BaseMapper.class.getClassLoader());
        }

        return result;
    }

    /**
     * 生成IService子类类对象
     *
     * @param entityClass 对应的实体类的类信息
     * @param classPath   目标类路径
     * @param suffix      生成的类名后缀
     * @return java.lang.Class<?>
     * @see IService
     */
    public static Class<?> generateMbpServiceInterface(Class<?> entityClass, String classPath, String suffix) {
        Class<?> result = null;
        if (null != entityClass) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String fullPath = getFullClassPath(classPath, className);
            String signature = String
                .format("%sL%s<%s>;", getClassBasicSignature(Object.class), getClassPath(IService.class),
                    getClassBasicSignature(entityClass));
            cw.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, fullPath, signature, OBJECT_CLASS_PATH,
                new String[] {getClassPath(IService.class)});
            cw.visitEnd();
            byte[] bytes = cw.toByteArray();

            result = ReflectUtils.load(fullPath, bytes, IService.class.getClassLoader());
        }

        return result;
    }

    /**
     * 生成ServiceImpl子类类对象
     *
     * @param entityClass    对应的实体类的类信息
     * @param classPath      目标类路径
     * @param repositoryPath MongoRepository的类全路径
     * @param iServicePath   Service的类全路径
     * @param suffix         生成的类名后缀
     * @return java.lang.Class<?>
     * @see ServiceImpl
     */
    public static Class<?> generateMbpServiceImplClass(Class<?> entityClass, String classPath, String repositoryPath,
        String iServicePath, String suffix) {
        Class<?> result = null;
        if (null != entityClass && null != repositoryPath && null != iServicePath) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String fullPath = getFullClassPath(classPath, className);
            String serviceImplPath = getClassPath(ServiceImpl.class);
            String signature = String.format("L%s<%s%s>;%s", serviceImplPath, getClassBasicSignature(repositoryPath),
                getClassBasicSignature(entityClass), getClassBasicSignature(iServicePath));
            cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, fullPath, signature, serviceImplPath,
                new String[] {getClassPath(IService.class)});

            buildNoArgsConstructor(cw, className, serviceImplPath, classPath);

            cw.visitEnd();
            byte[] bytes = cw.toByteArray();

            result = ReflectUtils.load(fullPath, bytes, ServiceImpl.class.getClassLoader());
        }

        return result;
    }

    /**
     * 生成controller类信息
     *
     * @param entityClass   实体类
     * @param classPath     生成类的路径
     * @param suffix        生成的类名后缀
     * @param voPath        视图对象类全路径
     * @param requestSuffix 控制器请求前缀
     * @return java.lang.Class<?>
     * @see BaseMyBatisPlusController
     */
    public static Class<?> generateMbpControllerClass(Class<?> entityClass, String classPath, String suffix,
        String voPath, String requestSuffix) {
        Class<?> result = null;
        if (null != entityClass && null != voPath) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String entitySign = getClassBasicSignature(entityClass);
            String voSign = getClassBasicSignature(voPath);
            String fullPath = getFullClassPath(classPath, className);
            String parentClassPath = getClassPath(BaseMyBatisPlusController.class);
            String sign = String
                .format("L%s<%s%s%s%s>;", getClassPath(BaseMyBatisPlusController.class), entitySign, voSign,
                    voSign, voSign);
            cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, fullPath, sign, parentClassPath, null);

            appendControllerAnnotations(cw, entityClass.getSimpleName(), requestSuffix);

            buildNoArgsConstructor(cw, entityClass.getSimpleName() + suffix, parentClassPath, classPath);

            cw.visitEnd();

            byte[] bytes = cw.toByteArray();
            result = ReflectUtils.load(fullPath, bytes, BaseMyBatisPlusController.class.getClassLoader());
        }

        return result;
    }
}