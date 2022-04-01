package main.yefancy.lfz.datacube.api.utils.tuples;

public class Tuple3<A, B, C> {
    public A x;
    public B y;
    public C z;

    public Tuple3(A left, B middle, C right) {
        this.x = left;
        this.y = middle;
        this.z = right;
    }
}
