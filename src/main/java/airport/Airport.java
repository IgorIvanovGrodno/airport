package airport;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

public class Airport {
    private final static Logger LOGGER = Logger.getLogger(Airport.class);
    private final Exchanger<Ticket> TICKET_EXCHANGER = new Exchanger<>();
    private List<Airplane> airplanesInTerminals;
    private Semaphore terminals;


    public Airport() {
        this.airplanesInTerminals = new ArrayList<>();
        this.terminals = new Semaphore(3, true);
    }

    public void startWork(){
        LOGGER.debug("Start work airport");
        for(int i=0; i<5; i++){
            new Airplane(airplanesInTerminals, 1, terminals, i, 1).start();
        }
        for(int i=0; i<5; i++){
            new Passenger(airplanesInTerminals, "Vany"+i, new Ticket(i, i+1), TICKET_EXCHANGER).start();
        }
    }

}
