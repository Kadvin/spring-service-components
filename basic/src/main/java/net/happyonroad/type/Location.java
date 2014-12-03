/**
 * Developer: Kadvin Date: 14-5-6 下午8:20
 */
package net.happyonroad.type;

/**
 * 地址
 */
public class Location {
    private float latitude, longitude;

    public Location(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
