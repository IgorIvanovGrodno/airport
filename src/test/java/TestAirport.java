import airport.Airport;
import org.junit.Assert;
import org.junit.Test;

public class TestAirport {
    private Airport airport = new Airport();

    @Test
    public void shouldFinishWorkAllThreadsOfAirport_whenStartWorkAirport() throws InterruptedException {
        int startCountThreads =Thread.getAllStackTraces().size();
        airport.startWork();
        Thread.sleep(20000);
        Assert.assertEquals(startCountThreads,  Thread.getAllStackTraces().size());
    }
}
