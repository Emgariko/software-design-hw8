package statistics;

import Clock.Clock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.binarySearch;

public class EventStatisticImpl implements EventStatistic{
    private final Clock clock;
    private final EventStatisticStorage storage = new EventStatisticStorage();

    public EventStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        storage.addEvent(name, clock.now());
    }

    @Override
    public double getEventStatisticByName(String name) {
        return storage.getEventStatisticByName(name, clock.now());
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        return storage.getAllEventStatistic(clock.now());
    }

    @Override
    public void printStatistic() {
        storage.printStatistic(clock.now());
    }

    private static class EventStatisticStorage {
        private final Map<String, EventStatisticData> events = new HashMap<>();

        public void addEvent(String name, Instant time) {
            EventStatisticData data = events.get(name);
            if (data == null) {
                data = new EventStatisticData();
                events.put(name, data);
            }
            data.addEvent(time);
        }

        public double getEventStatisticByName(String name, Instant time) {
            EventStatisticData data = events.get(name);
            if (data == null) {
                return 0;
            }
            return data.getEventStatistic(time.minus(1, ChronoUnit.HOURS));
        }

        public Map<String, Double> getAllEventStatistic(Instant time) {
            Map<String, Double> result = new HashMap<>();
            for (Map.Entry<String, EventStatisticData> entry : events.entrySet()) {
                result.put(entry.getKey(), entry.getValue().getEventStatistic(time.minus(1, ChronoUnit.HOURS)));
            }
            return result;
        }

        public void printStatistic(Instant time) {
            for (Map.Entry<String, EventStatisticData> entry : events.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue().getEventStatistic(time));
            }
        }
    }

    private static class EventStatisticData {
        private final ArrayList<Instant> times = new ArrayList<>();

        public void addEvent(Instant time) {
            times.add(time);
        }

        public double getEventStatistic(Instant time) {
            int index = binarySearch(times, time);
            if (index < 0) {
                index = -index - 1;
            } else {
                index++;
            }
            return (times.size() - index) / 60.0;
        }
    }
}
