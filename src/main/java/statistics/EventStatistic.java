package statistics;

import java.util.Map;

public interface EventStatistic {
    // add event with name 'name'
    void incEvent(String name);

    // get request per minute statistic for events with name 'name' in last hour
    double getEventStatisticByName(String name);

    // get request per minute statistic for all events in last hour
    Map<String, Double> getAllEventStatistic();

    // print statistic(rpm) for all events in last hour
    void printStatistic();
}
