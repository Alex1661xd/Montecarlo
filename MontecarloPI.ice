module MonteCarlo {
    interface Worker {
        int calculatePoints(int numPoints);
    };

    interface Master {
        double calculatePi(int totalPoints);
        void registerWorker(Worker* worker);
        void removeWorker(Worker* worker);
    };
    
}