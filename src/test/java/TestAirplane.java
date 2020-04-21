import airport.Airplane;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class TestAirplane {
    private static List<Airplane> airplanes;
    private static Semaphore terminals;


    @BeforeClass
    public static void setUp() {
        airplanes = new ArrayList<>();
        terminals = new Semaphore(1, true);
    }

    @Test
    public void shouldFinishWorkAirplane_whenNoAnotherAirplanesAndPassengers() throws InterruptedException {
        Airplane airplane = new Airplane(airplanes, 1, terminals, 1, 1);
        airplane.start();
        Thread.sleep(6000);
        Assert.assertEquals(Thread.State.TERMINATED, airplane.getState());
    }

    @Test
    public void shouldCallMethodsOfAirplanesInOrder_whenStartTwoAirplanesWithOneTerminal() throws InterruptedException {
        Airplane airplane1 = new Airplane(airplanes, 1, terminals, 1, 1);
        Airplane airplane2 = new Airplane(airplanes, 1, terminals, 2, 1);
        List<Airplane> airplanesSpy = Mockito.spy(ArrayList.class);
        airplane1.setAirplanesInTerminals(airplanesSpy);
        airplane2.setAirplanesInTerminals(airplanesSpy);
        airplane1.start();
        airplane2.start();
        Thread.sleep(20000);
        InOrder inOrder = Mockito.inOrder(airplanesSpy);
        inOrder.verify(airplanesSpy).add(Mockito.any(Airplane.class));
        inOrder.verify(airplanesSpy).remove(Mockito.any(Airplane.class));
        inOrder.verify(airplanesSpy).add(Mockito.any(Airplane.class));
        inOrder.verify(airplanesSpy).remove(Mockito.any(Airplane.class));
        inOrder.verify(airplanesSpy).remove(Mockito.any(Airplane.class));
        inOrder.verify(airplanesSpy).remove(Mockito.any(Airplane.class));
    }

}
