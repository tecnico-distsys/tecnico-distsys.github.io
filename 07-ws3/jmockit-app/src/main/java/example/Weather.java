package example;

/**
 *  This interface should be implemented by Weather data services.
 */
public interface Weather {

    public int getTemperature(String location);

    public float getChanceOfRain(String location);

}
