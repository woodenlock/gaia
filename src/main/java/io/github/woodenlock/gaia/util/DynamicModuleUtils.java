package io.github.woodenlock.gaia.util;

import java.util.Collection;

/**
 * 一些杂七杂八发的工具类
 *
 * @author woodenlock
 * @date 2021/12/25 16:08
 */
public class DynamicModuleUtils {

    /**
     * 字符序列为空
     *
     * @param cs 字符序列
     * @return boolean
     */
    public static boolean isBlank(CharSequence cs) {
        return !isNotBlank(cs);
    }

    /**
     * 字符序列不为空
     *
     * @param cs 字符序列
     * @return boolean
     */
    public static boolean isNotBlank(CharSequence cs) {
        if (null != cs && cs.length() != 0) {
            for (int i = 0; i < cs.length(); i++) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 集合为空
     *
     * @param coll 目标集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * 集合不为空
     *
     * @param coll 目标集合
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }
}