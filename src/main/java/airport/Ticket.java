package airport;

public class Ticket {
    private int number;
    private int date;

    public Ticket(int number, int date) {
        this.number = number;
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public int getDate() {
        return date;
    }
}
