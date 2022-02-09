package io.github.woodenlock.gaia;

/**
 * Boolean相关的枚举常量，为了绕过注解不可为null的限制
 *
 * @author woodenlock
 * @date 2021/5/23 16:37
 */
@SuppressWarnings("unused")
public enum BooleanEnum {
    /**
     * 对应true
     **/
    TRUE(true),

    /**
     * 对应false
     **/
    FALSE(false),

    /**
     * 忽略
     **/
    IGNORE(null);

    /**
     * 对应的布尔值
     */
    private final Boolean value;

    BooleanEnum(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public static BooleanEnum of(Boolean value) {
        if (null == value) {
            return BooleanEnum.IGNORE;
        } else if (value) {
            return BooleanEnum.TRUE;
        }

        return BooleanEnum.FALSE;
    }
}