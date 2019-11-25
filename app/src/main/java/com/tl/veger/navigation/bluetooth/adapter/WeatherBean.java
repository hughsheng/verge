package com.tl.veger.navigation.bluetooth.adapter;

import java.util.List;

public class WeatherBean {

    /**
     * coord : {"lon":114.03,"lat":22.62}
     * weather : [{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}]
     * base : stations
     * main : {"temp":27.31,"pressure":1015,"humidity":61,"temp_min":27,"temp_max":28}
     * visibility : 10000
     * wind : {"speed":4.1,"deg":100}
     * clouds : {"all":40}
     * dt : 1572076538
     * sys : {"type":1,"id":9154,"country":"CN","sunrise":1572042290,"sunset":1572083445}
     * timezone : 28800
     * id : 1802620
     * name : Longhua
     * cod : 200
     */

    private CoordBean coord;
    private String base;
    private MainBean main;
    private int visibility;
    private WindBean wind;
    private CloudsBean clouds;
    private int dt;
    private SysBean sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;
    private List<WeatherEntity> weather;

    public CoordBean getCoord() {
        return coord;
    }

    public void setCoord(CoordBean coord) {
        this.coord = coord;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public MainBean getMain() {
        return main;
    }

    public void setMain(MainBean main) {
        this.main = main;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public WindBean getWind() {
        return wind;
    }

    public void setWind(WindBean wind) {
        this.wind = wind;
    }

    public CloudsBean getClouds() {
        return clouds;
    }

    public void setClouds(CloudsBean clouds) {
        this.clouds = clouds;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public SysBean getSys() {
        return sys;
    }

    public void setSys(SysBean sys) {
        this.sys = sys;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public List<WeatherEntity> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherEntity> weather) {
        this.weather = weather;
    }

    public static class CoordBean {
        /**
         * lon : 114.03
         * lat : 22.62
         */

        private double lon;
        private double lat;

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }
    }

    public static class MainBean {
        /**
         * temp : 27.31
         * pressure : 1015
         * humidity : 61
         * temp_min : 27
         * temp_max : 28
         */

        private double temp;
        private int pressure;
        private int humidity;
        private double temp_min;
        private double temp_max;

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public int getPressure() {
            return pressure;
        }

        public void setPressure(int pressure) {
            this.pressure = pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public double getTemp_min() {
            return temp_min;
        }

        public void setTemp_min(double temp_min) {
            this.temp_min = temp_min;
        }

        public double getTemp_max() {
            return temp_max;
        }

        public void setTemp_max(double temp_max) {
            this.temp_max = temp_max;
        }
    }

    public static class WindBean {
        /**
         * speed : 4.1
         * deg : 100
         */

        private double speed;
        private int deg;

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public int getDeg() {
            return deg;
        }

        public void setDeg(int deg) {
            this.deg = deg;
        }
    }

    public static class CloudsBean {
        /**
         * all : 40
         */

        private int all;

        public int getAll() {
            return all;
        }

        public void setAll(int all) {
            this.all = all;
        }
    }

    public static class SysBean {
        /**
         * type : 1
         * id : 9154
         * country : CN
         * sunrise : 1572042290
         * sunset : 1572083445
         */

        private int type;
        private int id;
        private String country;
        private int sunrise;
        private int sunset;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getSunrise() {
            return sunrise;
        }

        public void setSunrise(int sunrise) {
            this.sunrise = sunrise;
        }

        public int getSunset() {
            return sunset;
        }

        public void setSunset(int sunset) {
            this.sunset = sunset;
        }
    }

    public static class WeatherEntity {
        /**
         * id : 802
         * main : Clouds
         * description : scattered clouds
         * icon : 03d
         */

        private int id;
        private String main;
        private String description;
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
