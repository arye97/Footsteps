package com.springvuegradle.seng302team600.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.seng302team600.enumeration.UnitType;

import javax.persistence.*;

@Entity
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id", nullable = false)
    @JsonProperty("unit_id")
    private Long id;

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @Column(name = "unit_type", nullable = false)
    @JsonProperty("unit_type")
    private UnitType unitType;

    @Column(name = "measurement_unit")
    @JsonProperty("measurement_unit")
    private String measurementUnit;

    public Unit() {}

    public Unit(String name, UnitType unitType, String measurementUnit) {
        this.name = name;
        this.unitType = unitType;
        this.measurementUnit = measurementUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }
}

