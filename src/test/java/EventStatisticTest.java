import Clock.Clock;
import org.junit.Assert;
import org.junit.Test;
import statistics.EventStatisticImpl;
import Clock.SettableClock;

import java.time.Instant;
import java.util.Map;

public class EventStatisticTest {
    Instant now = Instant.parse("2018-01-01T00:00:00.00Z");

    @Test
    public void simpleTest() {
        Clock settableClock = new SettableClock(now);
        var eventStatisticImpl = new EventStatisticImpl(settableClock);

        eventStatisticImpl.incEvent("test");
        Assert.assertEquals((double) 1 / 60, eventStatisticImpl.getEventStatisticByName("test"), 0.000001);
    }

    @Test
    public void testEmpty() {
        Clock settableClock = new SettableClock(now);
        var eventStatisticImpl = new EventStatisticImpl(settableClock);

        Assert.assertEquals(0, eventStatisticImpl.getEventStatisticByName("test"), 0.000001);
    }

    @Test
    public void testMultiEvents() {
        SettableClock settableClock = new SettableClock(now);
        var eventStatisticImpl = new EventStatisticImpl(settableClock);

        Instant nowCopy = now;
        for (int i = 0; i < 60; i++) {
            eventStatisticImpl.incEvent("every_second");
            nowCopy = nowCopy.plusSeconds(1);
            settableClock.setTime(nowCopy);
        }

        for (int i = 0; i < 30; i++) {
            eventStatisticImpl.incEvent("every_two_seconds");
            nowCopy = nowCopy.plusSeconds(2);
            settableClock.setTime(nowCopy);
        }

        Assert.assertEquals(1, eventStatisticImpl.getEventStatisticByName("every_second"), 0.000001);
        Assert.assertEquals(0.5, eventStatisticImpl.getEventStatisticByName("every_two_seconds"), 0.000001);
        Assert.assertEquals(0, eventStatisticImpl.getEventStatisticByName("not_exist"), 0.000001);

        nowCopy = nowCopy.plusSeconds(59 * 60 - 1);
        settableClock.setTime(nowCopy);
        Assert.assertEquals(0, eventStatisticImpl.getEventStatisticByName("every_second"), 0.000001);
        Assert.assertEquals(0.5, eventStatisticImpl.getEventStatisticByName("every_two_seconds"), 0.000001);

        nowCopy = nowCopy.plusSeconds(30);
        settableClock.setTime(nowCopy);
        Assert.assertEquals(0.25, eventStatisticImpl.getEventStatisticByName("every_two_seconds"), 0.000001);

        nowCopy = nowCopy.plusSeconds(30);
        settableClock.setTime(nowCopy);
        Assert.assertEquals(0, eventStatisticImpl.getEventStatisticByName("every_two_seconds"), 0.000001);

        var allEventsStatistic = eventStatisticImpl.getAllEventStatistic();
        Assert.assertEquals(Map.of("every_second", 0.0, "every_two_seconds", 0.0), allEventsStatistic);

        for (int i = 0; i < 2; i++) {
            eventStatisticImpl.incEvent("third_event");
            allEventsStatistic = eventStatisticImpl.getAllEventStatistic();
            Assert.assertEquals(Map.of("every_second", 0.0, "every_two_seconds", 0.0, "third_event", (double) 1 / 60), allEventsStatistic);
            nowCopy = nowCopy.plusSeconds(60 * 60);
            settableClock.setTime(nowCopy);
        }

        allEventsStatistic = eventStatisticImpl.getAllEventStatistic();
        Assert.assertEquals(Map.of("every_second", 0.0, "every_two_seconds", 0.0, "third_event", 0.0), allEventsStatistic);
    }
}
