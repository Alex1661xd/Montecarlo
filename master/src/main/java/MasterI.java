import java.util.*;
import com.zeroc.Ice.Current;

public class MasterI implements MonteCarlo.Master {
    private List<MonteCarlo.WorkerPrx> workers = new ArrayList<>();

    @Override
    public synchronized void registerWorker(MonteCarlo.WorkerPrx worker, Current current) {
        workers.add(worker);
        System.out.println("Nuevo worker registrado. Total workers: " + workers.size());
    }

    @Override
    public synchronized void removeWorker(MonteCarlo.WorkerPrx worker, Current current) {
        workers.remove(worker);
        System.out.println("Worker removido. Total workers: " + workers.size());
    }

    @Override
    public double calculatePi(int totalPoints, Current current) {
        if (workers.isEmpty()) {
            throw new RuntimeException("No hay workers disponibles para realizar el cálculo");
        }

        int numWorkers = workers.size();
        System.out.println("Iniciando cálculo con " + numWorkers + " workers");

        int pointsPerWorker = totalPoints / numWorkers;
        int remainingPoints = totalPoints % numWorkers;
        int totalPointsInCircle = 0;

        try {
            // Distribuir el trabajo entre los workers
            for (int i = 0; i < numWorkers; i++) {
                MonteCarlo.WorkerPrx worker = workers.get(i);
                // El último worker procesa los puntos restantes
                int workerPoints = pointsPerWorker + (i == numWorkers - 1 ? remainingPoints : 0);
                totalPointsInCircle += worker.calculatePoints(workerPoints);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular con los workers: " + e.getMessage());
        }

        return 4.0 * totalPointsInCircle / totalPoints;
    }
}