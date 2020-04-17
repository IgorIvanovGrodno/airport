package airport;

import java.util.List;
import java.util.concurrent.Semaphore;

public class Airplane extends Thread {
    private List<Airplane> airplanesInTerminals;
    private List<Passenger> passengers;
    private int passengerCapacity;
    private int countFreeSeats;
    private Semaphore terminals;
    private int numberFlight;



    @Override
    public void run() {
        try {
            terminals.acquire();
            airplanesInTerminals.add(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        takeOff();
        terminals.release();
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
}
