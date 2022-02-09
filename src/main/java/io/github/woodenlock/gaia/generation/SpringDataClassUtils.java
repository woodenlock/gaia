package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.base.BaseMongodbController;
import io.github.woodenlock.gaia.base.BaseSpringDataController;
import io.github.woodenlock.gaia.base.BaseSpringDataService;
import io.github.woodenlock.gaia.base.BaseSpringDataServiceImpl;
import io.github.woodenlock.gaia.util.ReflectUtils;
import org.bson.types.ObjectId;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassWriter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * spring-data相关类生成工具类
 * 默认 mongodb使用{@link ObjectId}、solr使用String(uuid)、es使用String(guid) 作为主键
 *
 * @author woodenlock
 * @date 2021-05-21 21:03:27
 */
class SpringDataClassUtils extends AsmUtils {

    /**
     * 生成通用的Repository子类类对象
     *
     * @param entityClass 对应的实体类的类信息
     * @param supper      父类类型
     * @param key         主键类型
     * @param classPath   目标类路径
     * @param suffix      生成的Repository类后缀
     * @return java.lang.Class<?>
     * @see PagingAndSortingRepository
     */
    @SuppressWarnings("rawtypes")
    public static Class<?> generateRepository(Class<?> entityClass, Class<? extends Serializable> key,
        Class<? extends PagingAndSortingRepository> supper, String classPath, String suffix) {
        Class<?> result = null;
        if (null != entityClass && null != supper && null != key) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String fullPath = getFullClassPath(classPath, className);
            String superPath = getClassPath(supper);
            String signature = String.format("%sL%s<%s%s>;", getClassBasicSignature(Object.class), superPath,
                getClassBasicSignature(entityClass), getClassBasicSignature(key));
            cw.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, fullPath, signature, OBJECT_CLASS_PATH,
                new String[] {superPath});
            AnnotationVisitor av0 = cw.visitAnnotation(getClassBasicSignature(Repository.class), true);
            av0.visitEnd();
            cw.visitEnd();
            byte[] bytes = cw.toByteArray();
            result = ReflectUtils.load(fullPath, bytes, supper.getClassLoader());
        }

        return result;
    }

    /**
     * 生成MongoRepository子类类对象，当前写死主键为{@link ObjectId}类型
     *
     * @param entityClass 对应的实体类的类信息
     * @param classPath   目标类路径
     * @param suffix      生成的MongoRepository类后缀
     * @return java.lang.Class<?>
     * @see MongoRepository
     */
    public static Class<?> generateMongoRepository(Class<?> entityClass, String classPath, String suffix) {
        return generateRepository(entityClass, ObjectId.class, MongoRepository.class, classPath, suffix);
    }

    /**
     * 生成BaseMongodbService子类类对象
     *
     * @param entityClass 对应的实体类的类信息
     * @param key         主键类型
     * @param classPath   目标类路径
     * @param suffix      生成的类名后缀
     * @return java.lang.Class<?>
     * @see BaseSpringDataService
     */
    public static Class<?> generateServiceInterface(Class<?> entityClass, Class<? extends Serializable> key,
        String classPath, String suffix) {
        Class<?> result = null;
        if (null != entityClass) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String fullPath = getFullClassPath(classPath, className);
            String interfacePath = getClassPath(BaseSpringDataService.class);
            String signature = String.format("%sL%s<%s%s>;", getClassBasicSignature(Object.class), interfacePath,
                    getClassBasicSignature(entityClass), getClassBasicSignature(key));
            cw.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, fullPath, signature, OBJECT_CLASS_PATH,
                new String[] {interfacePath});
            cw.visitEnd();
            byte[] bytes = cw.toByteArray();

            result = ReflectUtils.load(fullPath, bytes, BaseSpringDataService.class.getClassLoader());
        }

        return result;
    }

    /**
     * 生成BaseMongodbServiceImpl子类类对象
     *
     * @param entityClass    对应的实体类的类信息
     * @param supper         父类的类信息
     * @param key            主键的类信息
     * @param classPath      目标类路径
     * @param repositoryPath Repository的类全路径
     * @param iServicePath   Service的类全路径
     * @param suffix         生成的类名后缀
     * @return java.lang.Class<?>
     * @see BaseSpringDataService
     * @see BaseSpringDataServiceImpl
     */
    public static Class<?> generateServiceImpl(Class<?> entityClass,
        @SuppressWarnings("rawtypes") Class<? extends BaseSpringDataServiceImpl> supper, Class<?> key, String classPath,
        String repositoryPath, String iServicePath, String suffix) {
        Class<?> result = null;
        if (null != entityClass && null != supper && null != key && null != repositoryPath && null != iServicePath) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String fullPath = getFullClassPath(classPath, className);
            String serviceImplPath = getClassPath(supper);
            String signature = String.format("L%s<%s%s%s>;%s", serviceImplPath, getClassBasicSignature(repositoryPath),
                getClassBasicSignature(entityClass), getClassBasicSignature(key), getClassBasicSignature(iServicePath));
            cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, fullPath, signature, serviceImplPath,
                new String[] {getClassPath(BaseSpringDataService.class)});

            buildNoArgsConstructor(cw, className, serviceImplPath, classPath);

            cw.visitEnd();
            byte[] bytes = cw.toByteArray();

            result = ReflectUtils.load(fullPath, bytes, supper.getClassLoader());
        }

        return result;
    }

    /**
     * 生成通用的spring controller类信息
     *
     * @param entityClass   实体类
     * @param classPath     生成类的路径
     * @param suffix        生成的类名后缀
     * @param voPath        视图对象类全路径
     * @param requestSuffix 控制器请求前缀
     * @return java.lang.Class<?>
     * @see BaseSpringDataController
     */
    @SuppressWarnings("rawtypes")
    public static Class<?> generateSpringController(Class<? extends BaseSpringDataController> superClass, Class<?> entityClass,
        String classPath, String suffix, String voPath, String requestSuffix) {
        Class<?> result = null;
        if (null != superClass && null != entityClass && null != voPath) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String entitySign = getClassBasicSignature(entityClass);
            String voSign = getClassBasicSignature(voPath);
            String fullPath = getFullClassPath(classPath, className);
            String parentClassPath = getClassPath(superClass);
            String sign = String.format("L%s<%s%s%s%s>;", parentClassPath, entitySign, voSign, voSign, voSign);
            cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, fullPath, sign, parentClassPath, null);

            appendControllerAnnotations(cw, entityClass.getSimpleName(), requestSuffix);

            buildNoArgsConstructor(cw, entityClass.getSimpleName() + suffix, parentClassPath, classPath);

            cw.visitEnd();

            byte[] bytes = cw.toByteArray();
            result = ReflectUtils.load(fullPath, bytes, superClass.getClassLoader());
        }

        return result;
    }

    /**
     * 生成通用的spring controller类信息
     *
     * @param entityClass   实体类
     * @param key           主键类型
     * @param classPath     生成类的路径
     * @param suffix        生成的类名后缀
     * @param voPath        视图对象类全路径
     * @param requestSuffix 控制器请求前缀
     * @return java.lang.Class<?>
     * @see BaseSpringDataController
     */
    public static Class<?> generateSpringController11(Class<?> entityClass, Class<?> key, String classPath, String suffix,
        String voPath, String requestSuffix) {
        Class<?> result = null;
        if (null != entityClass && null != key && null != voPath) {
            ClassWriter cw = new ClassWriter(0);
            String className = entityClass.getSimpleName() + suffix;
            String entitySign = getClassBasicSignature(entityClass);
            String voSign = getClassBasicSignature(voPath);
            String keySign = getClassBasicSignature(key);
            String fullPath = getFullClassPath(classPath, className);
            String parentClassPath = getClassPath(BaseSpringDataController.class);
            String sign = String.format("L%s<%s%s%s%s%s%s>;", parentClassPath, entitySign, voSign, voSign, voSign, keySign, keySign);
            cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, fullPath, sign, parentClassPath, null);

            appendControllerAnnotations(cw, entityClass.getSimpleName(), requestSuffix);

            buildNoArgsConstructor(cw, entityClass.getSimpleName() + suffix, parentClassPath, classPath);

            cw.visitEnd();

            byte[] bytes = cw.toByteArray();
            result = ReflectUtils.load(fullPath, bytes, BaseSpringDataController.class.getClassLoader());
        }

        return result;
    }
}