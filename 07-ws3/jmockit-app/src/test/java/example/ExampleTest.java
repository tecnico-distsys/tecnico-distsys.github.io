package example;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.*;


/**
 *  Test suite
 */
public class ExampleTest {

    // tests

    /**
     *  In this test the News and Weather objects are mocked
     *  to allow testing the Example object.
     */
    @Test
    public void testMocks(
        @Mocked final News news,
        @Mocked final Weather weather)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String NAME = "friend";
        final String LOCATION = "Lisbon, Portugal";

        final String NEWS_STORY = "This is the top story";

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new Expectations() {{
            news.getTopStory(LOCATION); result = NEWS_STORY;
            weather.getTemperature(LOCATION); result = 22;
        }};

        // Unit under test is exercised.
        Example example = new Example(news, weather);
        String greeting = example.greet(NAME, LOCATION);

        // a "verification block"
        // One or more invocations to mocked types, causing expectations to be verified.
        new Verifications() {{
            // Verifies that zero or one invocations occurred, with the specified argument value:
            news.getTopStory(LOCATION); maxTimes = 1;
            weather.getTemperature(LOCATION); maxTimes = 1;
        }};

        // Additional verification code, if any, either here or before the verification block.
        final String EXPECTED = String.format("Hello %s!%n%s%nTemperature %d",
            NAME, NEWS_STORY, 22);
        assertEquals(EXPECTED, greeting);
    }

}
