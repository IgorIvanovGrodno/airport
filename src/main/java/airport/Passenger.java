package airport;

import java.util.List;

public class Passenger extends Thread {
    private List<Airplane> airplanesInTerminals;
    private String name;
    private Ticket ticket;
    private int currentDate;
    private boolean inFlight;

    public Passenger(List<Airplane> airplanesInTerminals, String name, Ticket ticket) {
        this.airplanesInTerminals = airplanesInTerminals;
        this.name = name;
        this.ticket = ticket;
        this.currentDate=0;
        this.inFlight=false;
    }

    @Override
    public void run() {
        while(ticket.getDate()>currentDate) {
            //check is in flight
            while (inFlight) {
                try {
                    Thread.sleep(1000);
                    currentDate++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }

            //try boarding
            if(ticket.getDate()>currentDate) {
                for (Airplane airplane : airplanesInTerminals) {
                    if (airplane.getNumberFlight() == ticket.getNumber()) {
                        if(airplane.getCountFreeSeats() <= 0) break;
                        airplane.getPassengers().add(this);
                        airplane.putPassenger(this);
                        inFlight = true;
                        break;
                    }
                }
                changeTicket();
                currentDate++;
            }
        }

        //go out

    }



    public boolean isInFlight() {
        return inFlight;
    }

    public void setInFlight(boolean inFlight) {
        this.inFlight = inFlight;
    }

    private void changeTicket() {
    }
}
