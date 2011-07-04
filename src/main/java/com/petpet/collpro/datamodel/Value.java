package com.petpet.collpro.datamodel;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Value<T> {
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    private String jsonValue;
    
    private long measuredAt;
    
    private int reliability;

    @ManyToOne
    private Property property;
    
    @ManyToOne
    private ValueSource source;
    
    @Transient
    private transient T value;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setMeasuredAt(long measuredAt) {
        this.measuredAt = measuredAt;
    }

    public long getMeasuredAt() {
        return measuredAt;
    }

    public void setReliability(int reliability) {
        this.reliability = reliability;
    }

    public int getReliability() {
        return reliability;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    public void setSource(ValueSource source) {
        this.source = source;
    }

    public ValueSource getSource() {
        return source;
    }

    public void setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}