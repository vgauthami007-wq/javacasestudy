public abstract class FineCalculator {
    protected double finePerDay = 10.0; // Example fine amount

    public abstract double calculateFine(int daysLate);
}

class LibraryFineCalculator extends FineCalculator {
    @Override
    public double calculateFine(int daysLate) {
        return (daysLate > 0) ? daysLate * finePerDay : 0.0;
    }
}