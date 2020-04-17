package airport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class Airplane extends Thread {
    private List<Airplane> airplanesInTerminals;
    private List<Passenger> passengers;
    private int passengerCapacity;
    private int countFreeSeats;
    private Semaphore terminals;
    private int numberFlight;
    private int countFlights;

    public Airplane(List<Airplane> airplanesInTerminals, int passengerCapacity, Semaphore terminals, int numberFlight, int countFlights) {
        this.airplanesInTerminals = airplanesInTerminals;
        this.passengerCapacity = passengerCapacity;
        this.terminals = terminals;
        this.numberFlight = numberFlight;
        this.countFlights = countFlights;
        this.countFreeSeats=passengerCapacity;
        passengers=new ArrayList<>();
    }

    @Override
    public void run() {
        while (countFlights>0){
            try {
                landing();
                disembarkationOfPassengers();
                boardingPassengers();
                takeOff();
                countFlights--;
            } catch (InterruptedException e) {
                Thread.currentThread()
                        .interrupt();
                e.printStackTrace();
            }
        }
    }

    public void putPassenger(Passenger passenger){
        passengers.add(passenger);
        countFreeSeats--;
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
        terminals.acquire();
        airplanesInTerminals.add(this);
    }

    private void disembarkationOfPassengers() {
        passengers.stream().forEach(p->p.setInFlight(false));
        countFreeSeats = passengerCapacity;
    }

    //добавить проверку времени вылета
    private void boardingPassengers() throws InterruptedException {
        while(countFreeSeats>0){
                Thread.sleep(1000);

        }
    }

    private void takeOff() {
        airplanesInTerminals.remove(this);
        terminals.release();
    }
}
