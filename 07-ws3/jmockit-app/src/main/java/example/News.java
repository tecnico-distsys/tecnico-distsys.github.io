package example;

/**
 *  This interface should be implemented by News services.
 */
public interface News {

    public String getTopStory(String location);

    public String getLatestStory(String location);

}
