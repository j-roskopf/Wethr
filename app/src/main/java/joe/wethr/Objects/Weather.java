package joe.wethr.Objects;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Weather implements Parcelable {

    private Weather(Parcel in) {
        dt = in.readString();
        tempMin = in.readString();
        tempMax = in.readString();
        pressure = in.readString();
        humidity = in.readString();
        weatherCondition = in.readString();
        weatherConditionDescription = in.readString();
        windDirection = in.readString();
        windSpeed = in.readString();
        icon = in.readString();
        temp = in.readString();
        id = in.readString();
        main = in.readString();


    }

    public Weather() {
    }


    String dt;
    String tempMin;
    String tempMax;
    String pressure;
    String humidity;
    String weatherCondition;
    String weatherConditionDescription;
    String windDirection;
    String windSpeed;
    String icon;
    String id;
    String main;


    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }


    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }


    Drawable image;
    String temp;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getDt() {
        return dt;
    }
    public void setDt(String dt) {
        this.dt = dt;
    }
    public String getWindDirection() {
        return windDirection;
    }
    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }
    public String getWindSpeed() {
        return windSpeed;
    }
    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
    public String getTempMin() {
        return tempMin;
    }
    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }
    public String getTempMax() {
        return tempMax;
    }
    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }
    public String getPressure() {
        return pressure;
    }
    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
    public String getHumidity() {
        return humidity;
    }
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
    public String getWeatherCondition() {
        return weatherCondition;
    }
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }
    public String getWeatherConditionDescription() {
        return weatherConditionDescription;
    }
    public void setWeatherConditionDescription(String weatherConditionDescription) {
        this.weatherConditionDescription = weatherConditionDescription;
    }
    public Drawable getImage() {
        return image;
    }
    public void setImage(Drawable image) {
        this.image = image;
    }

    public static final Parcelable.Creator<Weather> CREATOR = new Parcelable.Creator<Weather>() {
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dt);
        dest.writeString(tempMin);
        dest.writeString(tempMax);
        dest.writeString(pressure);
        dest.writeString(humidity);
        dest.writeString(weatherCondition);
        dest.writeString(weatherConditionDescription);
        dest.writeString(windDirection);
        dest.writeString(windSpeed);
        dest.writeString(icon);
        dest.writeString(temp);
        dest.writeString(id);
        dest.writeString(main);


    }




}

