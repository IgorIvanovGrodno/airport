package airport;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class Passenger extends Thread {
    private final static Logger LOGGER = Logger.getLogger(Passenger.class);

    static {
        PropertyConfigurator.configure("./src/main/resources/log4j.properties");
    }

    private List<Airplane> airplanesInTerminals;
    private String name;
    private Ticket ticket;
    private int currentDate;
    private boolean inFlight;
    private Exchanger<Ticket> ticketExchanger;

    public Passenger(List<Airplane> airplanesInTerminals, String name, Ticket ticket, Exchanger<Ticket> ticketExchanger) {
        this.airplanesInTerminals = airplanesInTerminals;
        this.name = name;
        this.ticket = ticket;
        this.currentDate=0;
        this.inFlight=false;
        this.ticketExchanger=ticketExchanger;
    }

    @Override
    public void run() {
        LOGGER.debug("PASSENGER #"+name+" in airport");
        while(ticket.getDate()>currentDate||inFlight) {

            //check is in flight
            while (inFlight) {
                try {
                    LOGGER.debug("PASSENGER #"+name+" in flight");
                    Thread.sleep(2000);
                    currentDate++;
                } catch (InterruptedException e) {
                    LOGGER.error("Passenger's interrupted exception in time flying. StackTrace: "+ Arrays.stream(Thread.currentThread().getStackTrace()).collect(Collectors.toList()));
                    Thread.currentThread().interrupt();
                }
            }

            LOGGER.debug("PASSENGER #"+name+" landing");

            //try boarding
            if(ticket.getDate()>currentDate) {
                LOGGER.debug("PASSENGER #"+name+" try boarding");
                for (Airplane airplane : airplanesInTerminals) {
                    if(airplane.getNumberFlight()==ticket.getNumber()&&airplane.putPassenger(this)) {
                        LOGGER.debug("PASSENGER #"+name+" take seat in airplane");
                        inFlight=true;
                        break;
                    }
                }

            //try change ticket if didn't try boarding
                if(!inFlight&&ticket.getDate()>currentDate) {
                    try {
                        LOGGER.debug("PASSENGER #"+name+" try change ticket");
                        changeTicket();
                    } catch (InterruptedException e) {
                        LOGGER.error("Passenger's interrupted exception in time changing ticket. StackTrace: "+ Arrays.stream(Thread.currentThread().getStackTrace()).collect(Collectors.toList()));
                        Thread.currentThread().interrupt();
                    }catch (TimeoutException e) {
                        LOGGER.debug("Passenger #"+name+" stop wait changing ticket");
                        break;
                    }
                }

                currentDate++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.error("Passenger's interrupted exception in time waiting. StackTrace: "+ Arrays.stream(Thread.currentThread().getStackTrace()).collect(Collectors.toList()));
                    Thread.currentThread().interrupt();
                }
            }
        }

        //go out
        LOGGER.debug("PASSENGER #"+name+" go out");
    }



    public boolean isInFlight() {
        return inFlight;
    }

    public void setInFlight(boolean inFlight) {
        this.inFlight = inFlight;
    }

    private void changeTicket() throws InterruptedException, TimeoutException {
        ticketExchanger.exchange(ticket, 5, TimeUnit.SECONDS);
        LOGGER.debug("PASSENGER #"+name+" has changed ticket");
    }
}
