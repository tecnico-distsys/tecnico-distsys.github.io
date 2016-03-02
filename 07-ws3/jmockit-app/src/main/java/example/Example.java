package example;

/**
 *  This example class has information to present to a user.
 *  It has two dependencies: Weather and News.
 */
public class Example {

    // members

    private String greeting = "Hello";
    private News news;
    private Weather weather;

    // constructor

    public Example(News news, Weather weather) {
        this.news = news;
        this.weather = weather;
    }

    // methods

    public String greet(String name, String location) {
        if (name == null)
            throw new IllegalArgumentException();

        final String EOL = String.format("%n");
        StringBuilder sb = new StringBuilder();

        sb.append(greeting);
        if (name.length() > 0) {
            sb.append(" ");
            sb.append(name);
        }
        sb.append("!");

        sb.append(EOL);

        String topStory = news.getTopStory(location);
        sb.append(topStory);

        sb.append(EOL);

        int temperature = weather.getTemperature(location);
        String weatherMessage = String.format("Temperature %d", temperature);
        sb.append(weatherMessage);

        return sb.toString();
    }

}
