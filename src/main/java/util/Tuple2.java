package util;

/**
 * @author sugan
 * @since 2016-05-16.
 */
public class Tuple2<T1, T2> {

    private T1 _1;
    private T2 _2;

    public Tuple2(T1 _1, T2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public T1 _1() {
        return _1;
    }

    public T2 _2() {
        return _2;
    }


    @Override
    public String toString() {
        return "(" + _1 + "," + _2 + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple2)) return false;

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

        if (_1 != null ? !_1.equals(tuple2._1) : tuple2._1 != null) return false;
        return _2 != null ? _2.equals(tuple2._2) : tuple2._2 == null;

    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        return result;
    }
}

