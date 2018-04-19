package flying.grub.tamtime.data.weather;

public class Weather {
    private int temperature;
    private String weatherString;
    private String humidityString;
    private double wind;

    public Weather(int temperature, String weatherString, String humidityString, double wind)
    {
        this.temperature = temperature;
        this.weatherString = weatherString;
        this.humidityString = humidityString;
        this.wind = wind;
    }

    public int getTemperature()
    {
        return temperature;
    }

    public String getWeatherString()
    {
        return weatherString;
    }

    public String getHumidityString()
    {
        return humidityString;
    }

    public double getWind()
    {
        return wind;
    }
}
