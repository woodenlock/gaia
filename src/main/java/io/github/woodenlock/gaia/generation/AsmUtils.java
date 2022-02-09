package io.github.woodenlock.gaia.generation;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import io.github.woodenlock.gaia.util.NamingUtils;
import io.github.woodenlock.gaia.util.ReflectUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * asm工具类
 *
 * @author woodenlock
 * @date 2021-05-21 20:03:27
 */
public class AsmUtils implements Opcodes {

    /**
     * 字节码-构造函数
     **/
    public static final String SOURCE_INIT = "<init>";

    /**
     * 字节码-toString方法
     **/
    public static final String SOURCE_TO_STRING = NamingUtils.getRawProperty(Object::toString);

    /**
     * 字节码-append方法
     **/
    public static final String SOURCE_APPEND = "append";

    /**
     * 类全路径
     **/
    public static final String OBJECT_CLASS_PATH = getClassPath(Object.class);

    /**
     * StringBuilder类全路径
     **/
    public static final String SB_CLASS_PATH = getClassPath(StringBuilder.class);

    /**
     * 类全路径签名
     **/
    public static final String STRING_CLASS_SIGN = getClassBasicSignature(String.class);

    /**
     * 类类型名称构造器
     **/
    public static final Function<String, String> CLASS_SIGN_BUILDER =
        type -> String.format("(%s)%s", type, getClassBasicSignature(StringBuilder.class));

    /**
     * 获取类签名
     *
     * @param canonicalName 类信息的原始全路径
     * @return java.lang.String
     */
    public static String getClassBasicSignature(String canonicalName) {
        return null == canonicalName ? "" : "L" + getClassPath(canonicalName) + ";";
    }

    /**
     * 获取类签名
     *
     * @param clazz 目标类
     * @return java.lang.String
     */
    public static String getClassBasicSignature(Class<?> clazz) {
        return null == clazz ? "" : "L" + getClassPath(clazz) + ";";
    }

    /**
     * 获取类路径
     *
     * @param clazz 目标类
     * @return java.lang.String
     */
    public static String getClassPath(Class<?> clazz) {
        return null == clazz ? "" : getClassPath(clazz.getCanonicalName());
    }

    /**
     * 获取类路径
     *
     * @param canonicalName 类信息的原始全路径
     * @return java.lang.String
     */
    public static String getClassPath(String canonicalName) {
        return null == canonicalName ? "" : canonicalName.replaceAll("\\.", "/");
    }

    /**
     * 增加controller注解：{@link RestController}、{@link RequestMapping}，请求路径默认为实体映射类名称按照驼峰自动分割
     *
     * @param cw              类写入器
     * @param entityClassName 数据源映射类标准名称
     * @param requestSuffix 控制器请求前缀
     */
    protected static void appendControllerAnnotations(ClassWriter cw, String entityClassName, String requestSuffix) {
        //RestController注解
        AnnotationVisitor ann1 = cw.visitAnnotation(getClassBasicSignature(RestController.class), true);
        ann1.visitEnd();
        //RequestMapping注解
        ann1 = cw.visitAnnotation(getClassBasicSignature(RequestMapping.class), true);
        AnnotationVisitor ann2 = ann1.visitArray(NamingUtils.getRawProperty(RequestMapping::value));
        StringJoiner sj = new StringJoiner("/", "/", "");
        requestSuffix = null == requestSuffix ? null : requestSuffix.trim().replaceAll("/", "").replaceAll("\\.", "");
        if(DynamicModuleUtils.isNotBlank(requestSuffix)){
            sj.add(requestSuffix);
        }
        List<String> splits = NamingUtils.getSplitsByUpperCase(entityClassName);
        splits.forEach(s -> sj.add(NamingUtils.convertFirst(s, false)));
        ann2.visit(null, sj.toString());
        ann2.visitEnd();
        ann1.visitEnd();
    }

    /**
     * 组装全路径签名
     *
     * @param targetPackage   包路径
     * @param targetClassName 类名称
     * @return java.lang.String
     */
    protected static String getFullClassPath(String targetPackage, String targetClassName) {
        Assert.isTrue(DynamicModuleUtils.isNotBlank(targetPackage) && DynamicModuleUtils.isNotBlank(targetClassName),
            String.format("Failed to get full class path signature due to illegal params:[%s], [%s].", targetPackage,
                targetClassName));
        targetPackage = targetPackage.charAt(0) == '/' ? targetPackage.substring(1) : targetPackage;
        if (DynamicModuleUtils.isNotBlank(targetPackage)) {
            targetPackage += targetPackage.charAt(targetPackage.length() - 1) == '/' ? "" : "/";
        }
        return targetPackage + targetClassName;
    }

    /**
     * 生成基础的无参、成员变量带getter与setter方法的实体类对象的字节码
     *
     * @param targetPackage   目标类的存放路径
     * @param targetClassName 目标类的类名
     * @param fields          用于构造成员变量的字段信息集合
     * @return Class<?>
     */
    public static Class<?> generateBasicEntityBytes(String targetPackage, String targetClassName,
        List<Field> fields) {
        String fullClassPath = getFullClassPath(targetPackage, targetClassName);
        String cnc = "L" + fullClassPath.replaceAll("\\.", "/") + ";";

        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, fullClassPath, null, OBJECT_CLASS_PATH,
            new String[] {getClassPath(Serializable.class)});

        buildNoArgsConstructor(cw, targetClassName, OBJECT_CLASS_PATH, targetPackage);

        if (DynamicModuleUtils.isNotEmpty(fields)) {
            buildFieldsAccess(cw, fields, fullClassPath, cnc);
            buildToStringMethod(cw, fields, targetPackage, targetClassName);
        }

        cw.visitEnd();

        return ReflectUtils.load(fullClassPath, cw.toByteArray(), null);
    }

    /**
     * 构造默认的无参构造函数
     *
     * @param cw              类写入器
     * @param className       目标类名称
     * @param parentClassPath 继承的父全路径类
     * @param classPath       实际的类所在包路径
     */
    protected static void buildNoArgsConstructor(ClassWriter cw, String className, String parentClassPath,
        String classPath) {
        if (null == cw || DynamicModuleUtils.isBlank(parentClassPath)) {
            return;
        }

        String fullPath = getFullClassPath(classPath, className);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, SOURCE_INIT, "()V", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, parentClassPath, SOURCE_INIT, "()V", false);
        mv.visitInsn(RETURN);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", "L" + fullPath + ";", null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    /**
     * 构建成员变量及其getter、setter，后续考虑额外处理boolean
     *
     * @param cw            目标写入器
     * @param fields        所有相关属性
     * @param fullClassPath 类全路径
     * @param cnc           类的字节码全限定名
     */
    private static void buildFieldsAccess(ClassWriter cw, List<Field> fields, String fullClassPath, String cnc) {
        fields.forEach(f -> {
            //属性
            String ftc = getClassBasicSignature(f.getType());
            FieldVisitor v = cw.visitField(ACC_PRIVATE, f.getName(), ftc, null, null);
            v.visitEnd();

            //get方法
            MethodVisitor m =
                cw.visitMethod(ACC_PUBLIC, "get" + NamingUtils.convertFirst(f.getName(), true), "()" + ftc, null, null);
            m.visitCode();
            Label l0 = new Label();
            m.visitLabel(l0);
            m.visitVarInsn(ALOAD, 0);
            m.visitFieldInsn(GETFIELD, fullClassPath, f.getName(), ftc);
            m.visitInsn(ARETURN);
            Label l1 = new Label();
            m.visitLabel(l1);
            m.visitLocalVariable("this", cnc, null, l0, l1, 0);
            m.visitMaxs(1, 1);
            m.visitEnd();

            //set方法
            m = cw.visitMethod(ACC_PUBLIC, "set" + NamingUtils.convertFirst(f.getName(), true), "(" + ftc + ")V", null,
                null);
            m.visitParameter(f.getName(), 0);
            m.visitCode();
            Label l3 = new Label();
            m.visitLabel(l3);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(PUTFIELD, fullClassPath, f.getName(), ftc);
            m.visitLabel(new Label());
            m.visitInsn(RETURN);
            Label l2 = new Label();
            m.visitLabel(l2);
            m.visitLocalVariable("this", cnc, null, l3, l2, 0);
            m.visitLocalVariable(f.getName(), ftc, null, l3, l2, 1);
            m.visitMaxs(2, 2);
            m.visitEnd();
        });
    }

    /**
     * 构建toString方法
     *
     * @param cw          目标写入器
     * @param fields      所有相关属性
     * @param packagePath 包路径
     * @param className   类名称
     */
    private static void buildToStringMethod(ClassWriter cw, List<Field> fields, String packagePath, String className) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, SOURCE_TO_STRING, "()" + STRING_CLASS_SIGN, null, null);
        mv.visitCode();
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitTypeInsn(NEW, SB_CLASS_PATH);
        mv.visitInsn(DUP);
        mv.visitLdcInsn(className + " [");
        mv.visitMethodInsn(INVOKESPECIAL, SB_CLASS_PATH, SOURCE_INIT, "(" + STRING_CLASS_SIGN + ")V", false);
        mv.visitVarInsn(ASTORE, 1);
        for (int i = 0; i < fields.size(); i++) {
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(NEW, SB_CLASS_PATH);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, SB_CLASS_PATH, SOURCE_INIT, "()V", false);
            mv.visitLdcInsn(fields.get(i).getName() + "=");
            mv.visitMethodInsn(INVOKEVIRTUAL, SB_CLASS_PATH, SOURCE_APPEND, CLASS_SIGN_BUILDER.apply(STRING_CLASS_SIGN),
                false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, getFullClassPath(packagePath, className), fields.get(i).getName(),
                getClassBasicSignature(fields.get(i).getType()));
            mv.visitMethodInsn(INVOKEVIRTUAL, SB_CLASS_PATH, SOURCE_APPEND,
                CLASS_SIGN_BUILDER.apply(getClassBasicSignature(Object.class)), false);
            mv.visitMethodInsn(INVOKEVIRTUAL, SB_CLASS_PATH, SOURCE_TO_STRING, "()" + STRING_CLASS_SIGN, false);
            mv.visitMethodInsn(INVOKEVIRTUAL, SB_CLASS_PATH, SOURCE_APPEND, CLASS_SIGN_BUILDER.apply(STRING_CLASS_SIGN),
                false);
            mv.visitInsn(POP);
            if (i == fields.size() - 1) {
                Label l8 = new Label();
                mv.visitLabel(l8);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitLdcInsn("]");
                mv.visitMethodInsn(INVOKEVIRTUAL, SB_CLASS_PATH, SOURCE_APPEND,
                    CLASS_SIGN_BUILDER.apply(STRING_CLASS_SIGN), false);
                mv.visitInsn(POP);
            } else {
                Label l5 = new Label();
                mv.visitLabel(l5);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitLdcInsn(",");
                mv.visitMethodInsn(INVOKEVIRTUAL, SB_CLASS_PATH, SOURCE_APPEND,
                    CLASS_SIGN_BUILDER.apply(STRING_CLASS_SIGN), false);
                mv.visitInsn(POP);
            }
        }
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, SB_CLASS_PATH, SOURCE_TO_STRING, "()" + STRING_CLASS_SIGN, false);
        mv.visitInsn(ARETURN);
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitMaxs(3, 2);
        mv.visitEnd();
    }
}