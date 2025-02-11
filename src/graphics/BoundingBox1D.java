package graphics;

public record BoundingBox1D(double left, double right) {
    public static BoundingBox1D centered(double width, double center) {
        return new BoundingBox1D(center - width / 2, center + width / 2);
    }

    public double center() {
        return (left + right) / 2;
    }

    public double width() {
        return right - left;
    }

    public BoundingBox1D shiftedBy(double offset) {
        return new BoundingBox1D(left + offset, right + offset);
    }

    public static BoundingBox1D containing(BoundingBox1D... boundingBoxes) {
        double left = Double.POSITIVE_INFINITY, right = Double.NEGATIVE_INFINITY;
        for (BoundingBox1D boundingBox : boundingBoxes) {
            if (boundingBox != null) {
                left = Math.min(left, boundingBox.left());
                right = Math.max(right, boundingBox.right());
            }
        }
        return new BoundingBox1D(left, right);
    }
}
