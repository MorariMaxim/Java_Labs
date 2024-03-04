import java.time.LocalDate;
import java.time.LocalTime;

public class Main {

    public static void main(String args[]){

        LocalTime lt1 = LocalTime.of(8, 0);

        

        Depot depot1 = new Depot("depot1");

        Truck vehicle1 = new Truck("truck1");

        Depot depot2 = new Depot("depot2");

        Drone vehicle2 = new Drone("drone1");

        depot1.setVehicles( new Vehicle[]{vehicle1});

        depot2.setVehicles(new Vehicle[]{vehicle2});

        Client client1 = new Client("Maxim",10,ClientType.PREMIUM);

        Client client2 = new Client("Vladislav",5,ClientType.REGULAR);

        Problem problem = new Problem();

        problem.setClients(new Client[]{client1,client2});
        problem.setDepots(new Depot[]{depot1,depot2});

        problem.solve();


        for (Vehicle v : problem.getVehicles()) {

            System.out.println(v.getName());
        }
    }
}