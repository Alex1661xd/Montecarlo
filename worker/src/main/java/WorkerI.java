public class WorkerI implements MonteCarlo.Worker {
    @Override
    public int calculatePoints(int numPoints, com.zeroc.Ice.Current current) {
        int pointsInCircle = 0;
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < numPoints; i++) {
            double x = random.nextDouble() * 2 - 1; // [-1, 1]
            double y = random.nextDouble() * 2 - 1; // [-1, 1]
            if (x*x + y*y <= 1) {
                pointsInCircle++;
            }
        }
        return pointsInCircle;
    }
}