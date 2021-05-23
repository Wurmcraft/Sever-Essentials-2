package io.wurmatron.serveressentials.tests.routes;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.TrackedStat;
import io.wurmatron.serveressentials.tests.sql.TestStatistics;
import io.wurmatron.serveressentials.tests.utils.HTTPRequests;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestStatisticRoutes {

    @BeforeAll
    public static void setup() throws IOException, SQLException {
        ServerEssentialsRest.main(new String[]{});
        ServerEssentialsRest.config.server.host = "localhost";
        HTTPRequests.BASE_URL = "http://" + ServerEssentialsRest.config.server.host + ":" + ServerEssentialsRest.config.server.port + "/";
        // TODO Add Authentication or force config.testing when running tests
    }

    @Test
    @Order(1)
    public void testAddStatistic() throws IOException {
        TrackedStat stat = HTTPRequests.postWithReturn("statistics", TestStatistics.TEST_STAT, TrackedStat.class);
        assertNotNull(stat, "Tracked Stat is not null");
        // Check for added entry
        TrackedStat[] stats = HTTPRequests.get("statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
        boolean exists = false;
        for (TrackedStat s : stats)
            if (s.equals(stat))
                exists = true;
        assertTrue(exists, "Entry was created");
    }

    @Test
    @Order(2)
    public void testGetStatistic() throws IOException {
        TrackedStat[] stats = HTTPRequests.get("statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
        boolean exists = false;
        for (TrackedStat s : stats)
            if (s.equals(TestStatistics.TEST_STAT))
                exists = true;
        assertTrue(exists, "Entry was created");
    }

    @Test
    @Order(2)
    public void testOverrideStatistic() throws IOException {
        TestStatistics.TEST_STAT.serverID = "Test2";
        HTTPRequests.put("statistics", TestStatistics.TEST_STAT);
        // Check for update
        TrackedStat[] stats = HTTPRequests.get("statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
        boolean exists = false;
        for (TrackedStat s : stats)
            if (s.equals(TestStatistics.TEST_STAT))
                exists = true;
        assertTrue(exists, "Entry was Updated");
    }

    @Test
    @Order(2)
    public void testPatchStatistic() throws IOException {
        TestStatistics.TEST_STAT.eventData = "{\"data\": 5}";
        HTTPRequests.patch("statistics/event-data", TestStatistics.TEST_STAT);
        // Check if it was updated
        TrackedStat[] stats = HTTPRequests.get("statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
        boolean exists = false;
        for (TrackedStat s : stats)
            if (s.equals(TestStatistics.TEST_STAT))
                exists = true;
        assertTrue(exists, "Entry was Patched");
    }

    @Test
    @Order(3)
    public void testDeleteStatistic() throws IOException {
        TrackedStat deletedStat = HTTPRequests.deleteWithReturn("statistics", TestStatistics.TEST_STAT, TrackedStat.class);
        assertNotNull(deletedStat, "Deleted Stat is returned");
        // Check if make sure it was deleted
        TrackedStat[] stats = HTTPRequests.get("statistics?uuid=" + TestStatistics.TEST_STAT.uuid, TrackedStat[].class);
        boolean exists = false;
        for (TrackedStat s : stats)
            if (s.equals(TestStatistics.TEST_STAT))
                exists = true;
        assertFalse(exists, "Entry was Removed");
    }
}