package com.springvuegradle.seng302team600.model;

/**
 * Possible activity types for an activity
 */
public enum ActivityTypes {
    //
    //Aerial
    //
    AEROBATICS("Aeroplane flying and Aerobatics"),
    GLIDING("Gliding"),
    HANG_GLIDING("Hang Gliding"),
    HELICOPTER_FLIGHTS("Helicopter Flights"),
    BALLOONING("Hot Air Balloon Rides"),
    INDOOR_SKYDIVING("Indoor Skydiving"),
    KITING("Kites and Power Kites"),
    MODEL_FLYING("Model aircraft flying"),
    PARAGLIDING("Paragliding and Paramotoring"),
    SKYDIVING("Skydiving"),
    //
    //Marine
    //
    SAILING("Sailing"),
    WATER_SKIING("Water Skiing"),

    //
    //Land Sports
    //
    ARCHERY("Archery"),
    AIRSOFT("Airsoft"),
    ATHLETICS("Athletics"),
    BADMINTON("Badminton"),
    BASEBALL("Baseball and Softball"),
    BASKETBALL("Basketball"),
    BOULDERING("Bouldering"),
    BOWLS("Bowls"),
    CHEERLEADING("Cheerleading"),
    CRICKET("Cricket"),
    CYCLING("Cycling"),
    DANCING("Dance"),
    EQUESTRIAN("Equestrian Activities"),
    FOOTBALL("Football and Soccer"),
    FUTSAL("Futsal"),
    FREE_RUNNING("Free Running"),
    GOLF("Golf"),
    GYMNASTICS("Gymnastics"),
    HIKING("Hiking"),
    HOCKEY("Hockey"),
    MARATHON("Marathon"),
    MULTISPORT("Multisport Racing"),
    MARTIALS_ARTS("Martial Arts"),
    MOTOR_SPORTS("Motor Sports"),
    MOUNTAIN_BIKING("Mountain Riking"),
    ORIENTEERING("Orienteering"),
    RUGBY("Rugby"),
    SKIING("Skiing and Snowboarding"),

    //
    //Misc
    //
    ASTRONOMY("Astronomy"),
    BASE_JUMPING("BASE Jumping"),
    BUNGEE_JUMPING("Bungee Jumping"),
    FOUR_WHEEL_DRIVING("4×4 Driving Experience"),
    WILDLIFE("Animal Parks, Wildlife Parks and Zoos"),
    CAMPING("Camping"),
    CAVING("Caving"),
    CARNIVAL("Carnivals"),
    CONCERT("Concerts, Festivals and Gigs"),
    LARPING("Live Action Role Playing Games"),
    OTHER_SPORTS("Other sports"),
    YOGA("Yoga")

    private final String humanReadable;

    ActivityTypes(String humanReadable) {this.humanReadable = humanReadable;}
    String getHumanReadable() {return this.humanReadable;}
}