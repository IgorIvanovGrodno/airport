package airport;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return number == ticket.number &&
                date == ticket.date;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, date);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "number=" + number +
                ", date=" + date +
                '}';
    }
}
