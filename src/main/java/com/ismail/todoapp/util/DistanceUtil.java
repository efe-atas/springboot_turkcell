package com.ismail.todoapp.util;

public final class DistanceUtil {

    private static final double EARTH_RADIUS_METERS = 6_371_000; // Dünya yarıçapı (metre)

    private DistanceUtil() {
        // utility class
    }

    /**
     * Iki nokta arasindaki yaklasik mesafeyi Haversine formulu ile metre cinsinden hesaplar.
     *
     * @param lat1 birinci noktanin enlemi (derece)
     * @param lon1 birinci noktanin boylami (derece)
     * @param lat2 ikinci noktanin enlemi (derece)
     * @param lon2 ikinci noktanin boylami (derece)
     * @return mesafe (metre)
     */
    public static double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(latRad1) * Math.cos(latRad2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}

