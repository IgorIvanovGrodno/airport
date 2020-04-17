package airport;

import java.util.List;

public class Passenger extends Thread {
    private List<Airplane> airplanesInTerminals;
    private int ticket;
    private int dateTicket;
    private boolean inFlight=false;

    @Override
    public void run() {
        while (!inFlight&&dateTicket>0){
            for (Airplane airplane: airplanesInTerminals) {
                if(airplane.getNumberFlight()==ticket&&airplane.getCountFreeSeats()>0){
                    airplane.getPassengers().add(this);
                    inFlight=true;
                    break;
                }
            }
            //условия для ухода из аэропорта
            //условие обмена билетами
            //sleap
            dateTicket--;
        }

    }
}
