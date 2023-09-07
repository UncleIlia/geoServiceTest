package ru.netology.entity;

public class Location {

    private final String city;

    private final Country country;

    private final String street;

    private final int builing;

    public Location(String city, Country country, String street, int builing) {
        this.city = city;
        this.country = country;
        this.street = street;
        this.builing = builing;
    }

    public String getCity() {
        return city;
    }

    public Country getCountry() {
        return country;
    }

    public String getStreet() {
        return street;
    }

    public int getBuiling() {
        return builing;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!(obj instanceof Location))
            return false;

        Location location = (Location) obj;


        return (this.city == null ? location.city == null : this.city.equals(location.city)) && this.country == location.country
                && (this.street == null ? location.street == null : this.street.equals(location.street)) && this.builing == location.builing;
    }
}
