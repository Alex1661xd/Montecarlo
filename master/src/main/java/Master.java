public class Master {
    public static void main(String[] args) {
        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            // Cambiar el endpoint para aceptar conexiones de cualquier IP
            com.zeroc.Ice.ObjectAdapter adapter = 
                communicator.createObjectAdapterWithEndpoints("MasterAdapter", "default -h * -p 10000");
            com.zeroc.Ice.Object object = new MasterI();
            adapter.add(object, com.zeroc.Ice.Util.stringToIdentity("Master"));
            adapter.activate();

            System.out.println("Master iniciado en todas las interfaces. Esperando workers...");
            communicator.waitForShutdown();
        }
    }
}