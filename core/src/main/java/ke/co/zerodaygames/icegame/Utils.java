package ke.co.zerodaygames.icegame;

public class Utils {
    /**
     * Provides a random number within the provided range.
     * @param min the lower bound (inclusive)
     * @param max the upper bound (inclusive)
     * @return a random number within the provided bounds
     */
    public static double random(float min, float max) {
        return min + Math.random() * (max - min);
    }
}
