package airport;

import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public class Airplane extends Thread {
    private final static Logger LOGGER = Logger.getLogger(Airplane.class);

    static {
        PropertyConfigurator.configure("./src/main/resources/log4j.properties");
    }

    private List<Airplane> airplanesInTerminals;
    private List<Passenger> passengers;
    private int passengerCapacity;
    private volatile int countFreeSeats;
    private Semaphore terminals;
    private int numberFlight;
    private int countFlights;
    private Lock lockForPutPassenger;

    public Airplane(List<Airplane> airplanesInTerminals, int passengerCapacity, Semaphore terminals, int numberFlight, int countFlights) {
        this.airplanesInTerminals = airplanesInTerminals;
        this.passengerCapacity = passengerCapacity;
        this.terminals = terminals;
        this.numberFlight = numberFlight;
        this.countFlights = countFlights;
        this.countFreeSeats=0;
        passengers=new ArrayList<>();
        lockForPutPassenger=new ReentrantLock();
    }

    @Override
    public void run() {
        LOGGER.debug("start work AIRPLANE #"+numberFlight);
        while (countFlights>0){
            LOGGER.debug("fly AIRPLANE #"+numberFlight);

            try {
                landing();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted exception in time landing. StackTrace: "+ Arrays.stream(Thread.currentThread().getStackTrace()).collect(Collectors.toList()));
                Thread.currentThread()
                        .interrupt();
            }

            disembarkationOfPassengers();

            try {
                boardingPassengers();
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted exception in time boarding. StackTrace: "+ Arrays.stream(Thread.currentThread().getStackTrace()).collect(Collectors.toList()));
                Thread.currentThread()
                        .interrupt();
            }

            takeOff();
                countFlights--;
        }
        try {
            endWork();
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted exception in time finish airplane's work. StackTrace: "+ Arrays.stream(Thread.currentThread().getStackTrace()).collect(Collectors.toList()));
            terminals.release();
            Thread.currentThread()
                    .interrupt();
        }
        LOGGER.debug("end work AIRPLANE #"+numberFlight);
    }

    public boolean putPassenger(Passenger passenger){
        LOGGER.debug("Passenger #"+passenger.getName()+" try take a seat");
        boolean result=false;
        try {
            if(lockForPutPassenger.tryLock(5, TimeUnit.SECONDS)){
                if(countFreeSeats>0) {
                    passengers.add(passenger);
                    countFreeSeats--;
                    result = true;
                    LOGGER.debug("Passenger #"+passenger.getName()+" has sat");
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted exception in time put passenger. StackTrace: "+ Arrays.stream(Thread.currentThread().getStackTrace()).collect(Collectors.toList()));
            Thread.currentThread().interrupt();
        }finally {
            lockForPutPassenger.unlock();
            return result;
        }
    }

    public List<Airplane> getAirplanesInTerminals() {
        return airplanesInTerminals;
    }

    public void setAirplanesInTerminals(List<Airplane> airplanesInTerminals) {
        this.airplanesInTerminals = airplanesInTerminals;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public Semaphore getTerminals() {
        return terminals;
    }

    public void setTerminals(Semaphore terminals) {
        this.terminals = terminals;
    }

    public int getCountFlights() {
        return countFlights;
    }

    public void setCountFlights(int countFlights) {
        this.countFlights = countFlights;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public int getNumberFlight() {
        return numberFlight;
    }

    public void setNumberFlight(int numberFlight) {
        this.numberFlight = numberFlight;
    }

    public int getCountFreeSeats() {
        return countFreeSeats;
    }

    public void setCountFreeSeats(int countFreeSeats) {
        this.countFreeSeats = countFreeSeats;
    }

    private void landing() throws InterruptedException {
        LOGGER.debug("try landing AIRPLANE #"+numberFlight+"");
        terminals.acquire();
        airplanesInTerminals.add(this);
        LOGGER.debug("landing AIRPLANE #"+numberFlight);
    }

    private void disembarkationOfPassengers() {
        LOGGER.debug("start disembarkation of passengers AIRPLANE #"+numberFlight);
        passengers.stream().forEach(p->p.setInFlight(false));
        countFreeSeats = passengerCapacity;
        LOGGER.debug("finish disembarkation of passengers AIRPLANE #"+numberFlight);
    }

    private void boardingPassengers() throws InterruptedException {
        int waitingTime =3;
        while(countFreeSeats>0&&waitingTime>0){
                Thread.sleep(1000);
                waitingTime--;
        }
    }

    private void takeOff() {
        airplanesInTerminals.remove(this);
        LOGGER.debug("take off AIRPLANE #"+numberFlight);
        terminals.release();
    }

    private void endWork() throws InterruptedException {
        landing();
        disembarkationOfPassengers();
        airplanesInTerminals.remove(this);
        terminals.release();

    }
}
