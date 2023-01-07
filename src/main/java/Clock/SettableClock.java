package Clock;

import java.time.Instant;

public class SettableClock implements Clock {
    private Instant time;

    public SettableClock(Instant time) {
        this.time = time;
    }

    @Override
    public Instant now() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
