import airport.Airplane;
import airport.Passenger;
import airport.Ticket;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;


public class TestPassenger {
    private  static Exchanger<Ticket> exchanger;
    private  static List<Airplane> airplanesInAirport;

    @BeforeClass
    public static void setUp() {
        exchanger = new Exchanger<>();
        airplanesInAirport = new ArrayList<>();
    }

    @Test
    public void shouldReturnChangedTicket_whenRunPassengersWithoutAirplanes() throws InterruptedException {
        Passenger passenger1 = new Passenger(airplanesInAirport, "Jack", new Ticket(1,1), exchanger);
        Passenger passenger2 = new Passenger(airplanesInAirport, "Jack", new Ticket(2,1), exchanger);
        passenger1.start();
        passenger2.start();
        Thread.sleep(6000);
        Assert.assertEquals(2,passenger1.getTicket().getNumber());
    }

    @Test
    public void shouldCallPutPassengerMethodOfAirplane_whenRunPassenger() throws InterruptedException {
        Passenger passenger = new Passenger(airplanesInAirport, "Jack", new Ticket(2,1), exchanger);
        Airplane airplane = new Airplane(airplanesInAirport, 1, new Semaphore(1), passenger.getTicket().getNumber(), 1);
        Airplane airplaneSpy = Mockito.spy(airplane);
        Mockito.when(airplaneSpy.getNumberFlight()).thenReturn(passenger.getTicket().getNumber());
        Mockito.when(airplaneSpy.putPassenger(passenger)).thenReturn(true);
        passenger.getAirplanesInTerminals().add(airplaneSpy);
        passenger.start();
        Thread.sleep(6000);
        passenger.getAirplanesInTerminals().remove(airplaneSpy);
        Mockito.verify(airplaneSpy).putPassenger(Mockito.any(Passenger.class));
    }

    @Test
    public void shouldPassengerFinishWork_whenNoPlanesAndNoAnotherPassenger() throws InterruptedException {
        Passenger passenger = new Passenger(airplanesInAirport, "Jack", new Ticket(2,1), exchanger);
        passenger.start();
        Thread.sleep(6000);
        Assert.assertEquals(Thread.State.TERMINATED, passenger.getState());
    }

}
