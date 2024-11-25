package br.edu.ifnmg;

public class RunResults {
    private long timeElapsedInNanos;
    private long recursiveCalls;
    private boolean sucessful;

    public long getTimeElapsed() {
        return timeElapsedInNanos;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsedInNanos = timeElapsed;
    }

    public long getRecursiveCalls() {
        return recursiveCalls;
    }

    public void setRecursiveCalls(long recursiveCalls) {
        this.recursiveCalls = recursiveCalls;
    }

    public boolean isSucessful() {
        return sucessful;
    }

    public void setSucessful(boolean sucessful) {
        this.sucessful = sucessful;
    }
}
