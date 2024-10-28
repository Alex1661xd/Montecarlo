public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java Client <master-ip> <totalPuntos>");
            System.out.println("Ejemplo: java Client 192.168.1.100 1000000");
            System.exit(1);
        }

        String masterIP = args[0];
        int totalPoints;

        try {
            totalPoints = Integer.parseInt(args[1]); // Cambiado de args[0] a args[1]
            if (totalPoints <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: El número de puntos debe ser un entero positivo");
            System.out.println("Uso: java Client <master-ip> <totalPuntos>");
            System.out.println("Ejemplo: java Client 192.168.1.100 1000000");
            System.exit(1);
            return;
        }

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args)) {
            // Modificar la conexión para usar la IP del master
            String masterProxy = "Master:default -h " + masterIP + " -p 10000";
            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy(masterProxy);
            MonteCarlo.MasterPrx master = MonteCarlo.MasterPrx.checkedCast(base);
            
            if(master == null) {
                throw new Error("No se pudo conectar al Master en " + masterIP);
            }

            System.out.println("\nIniciando cálculo de Pi con:");
            System.out.println("Conectado al Master en: " + masterIP);
            System.out.println("Total de puntos: " + formatNumber(totalPoints));

            long startTime = System.currentTimeMillis();
            
            try {
                double pi = master.calculatePi(totalPoints);
                long endTime = System.currentTimeMillis();
                double timeInSeconds = (endTime - startTime) / 1000.0;

                System.out.println("\nResultados:");
                System.out.println("El valor calculado de Pi es: " + pi);
                System.out.println("Tiempo de ejecución: " + String.format("%.2f", timeInSeconds) + " segundos");
                System.out.println("Error relativo: " + String.format("%.8f%%", Math.abs(pi - Math.PI) / Math.PI * 100));
            } catch (RuntimeException e) {
                System.out.println("\nError en el cálculo: " + e.getMessage());
                System.out.println("Verifique que el Master esté ejecutándose y que la IP sea correcta.");
            }
        } catch (com.zeroc.Ice.ConnectFailedException e) {
            System.out.println("\nError: No se pudo conectar al Master en " + masterIP);
            System.out.println("Verifique que:");
            System.out.println("1. La IP del Master sea correcta");
            System.out.println("2. El Master esté ejecutándose");
            System.out.println("3. El puerto 10000 esté abierto en el firewall del Master");
            System.out.println("4. Ambas computadoras tengan conectividad en la red");
        }
    }

    private static String formatNumber(int number) {
        return String.format("%,d", number);
    }
}