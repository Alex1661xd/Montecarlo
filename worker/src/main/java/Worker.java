public class Worker {
    public static void main(String[] args) {
        // Verificar argumentos para IP del master
        if (args.length < 1) {
            System.out.println("Uso: java Worker <master-ip> [puerto]");
            System.exit(1);
        }
        String masterIP = args[0];
        int port = args.length > 1 ? Integer.parseInt(args[1]) : getRandomPort();
        
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            // Configurar el endpoint del Worker para aceptar conexiones externas
            String endpoint = "default -h * -p " + port;
            com.zeroc.Ice.ObjectAdapter adapter = 
                communicator.createObjectAdapterWithEndpoints("WorkerAdapter" + port, endpoint);
            
            com.zeroc.Ice.Object worker = new WorkerI();
            adapter.add(worker, com.zeroc.Ice.Util.stringToIdentity("Worker" + port));
            adapter.activate();

            // Conectar al Master usando la IP proporcionada
            String masterProxy = "Master:default -h " + masterIP + " -p 10000";
            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy(masterProxy);
            MonteCarlo.MasterPrx master = MonteCarlo.MasterPrx.checkedCast(base);
            if(master == null) {
                throw new Error("Invalid master proxy");
            }

            MonteCarlo.WorkerPrx workerPrx = MonteCarlo.WorkerPrx.uncheckedCast(
                adapter.createProxy(com.zeroc.Ice.Util.stringToIdentity("Worker" + port))
            );
            
            master.registerWorker(workerPrx);
            System.out.println("Worker iniciado en puerto " + port + " y registrado con el Master en " + masterIP);
            
            communicator.waitForShutdown();
        }
    }
    
    private static int getRandomPort() {
        // Generar un puerto aleatorio entre 10001 y 20000
        return 10001 + new java.util.Random().nextInt(9999);
    }
}