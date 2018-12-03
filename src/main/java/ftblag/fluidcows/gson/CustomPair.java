package ftblag.fluidcows.gson;


public class CustomPair<L, R> {
    private L left;
    private R right;

    private CustomPair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> CustomPair<L, R> of(L left, R right) {
        return new CustomPair<L, R>(left, right);
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof CustomPair) {
            CustomPair oth = (CustomPair) obj;
            return (left.equals(oth.left) && right.equals(oth.right)) || (left.equals(oth.right) && right.equals(oth.left));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (left == null ? 0 : left.hashCode()) ^ (right == null ? 0 : right.hashCode());
    }
}
