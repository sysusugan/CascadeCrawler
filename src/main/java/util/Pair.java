package util;

import java.io.Serializable;
import java.util.Map;

/**
 * 二元组封装类
 */
public class Pair<T1, T2> implements Map.Entry<T1, T2>, Serializable {
    /**
     * 元组的第一个元素
     */
    public T1 first = null;
    /**
     * 元组的第二个元素
     */
    public T2 second = null;

    /**
     * 构造二元组
     */
    public Pair(T1 t1, T2 t2) {
        first = t1;
        second = t2;
    }

    /**
     * 返回二元组中的第一个元素
     *
     * @return 第一个元素
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * 返回二元组中的第二个元素
     *
     * @return 第二个元素
     */
    public T2 getSecond() {
        return second;
    }

    private static boolean equals(Object x, Object y) {
        return (x == null && y == null) || (x != null && x.equals(y));
    }

    /**
     * 返回二元组中的第一个元素
     *
     * @return 第一个元素
     */
    @Override
    public T1 getKey() {
        return getFirst();
    }

    /**
     * 返回二元组中的第二个元素
     *
     * @return 第二个元素
     */
    @Override
    public T2 getValue() {
        return getSecond();
    }

    /**
     * 根据给定的值设置二元组中第二个元素的值
     *
     * @param value 给定的值
     * @return 二元组中第二个元素更新后的值
     */
    @Override
    public T2 setValue(T2 value) {
        return this.second = value;
    }

    /**
     * 判断两个元组是否相等
     *
     * @return true if equal
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        return other instanceof Pair
                && equals(first, ((Pair<T1, T2>) other).first)
                && equals(second, ((Pair<T1, T2>) other).second);
    }

    @Override
    public int hashCode() {
        if (first == null)
            return (second == null) ? 0 : second.hashCode() + 1;
        else if (second == null)
            return first.hashCode() + 2;
        else
            return first.hashCode() * 17 + second.hashCode();
    }

    @Override
    public String toString() {
        return "{" + getFirst() + "," + getSecond() + "}";
    }
}
