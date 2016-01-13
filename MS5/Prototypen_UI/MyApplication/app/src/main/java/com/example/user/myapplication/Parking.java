package com.example.user.myapplication;

public class Parking
{
    private String id;

    private String addresse;

    private String kapazität;

    private String typ;

    private String tendenz;

    private String belegung;

    private Geometry geometry;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getAddresse ()
    {
        return addresse;
    }

    public void setAddresse (String addresse)
    {
        this.addresse = addresse;
    }

    public String getKapazität ()
    {
        return kapazität;
    }

    public void setKapazität (String kapazität)
    {
        this.kapazität = kapazität;
    }

    public String getTyp ()
    {
        return typ;
    }

    public void setTyp (String typ)
    {
        this.typ = typ;
    }

    public String getTendenz ()
    {
        return tendenz;
    }

    public void setTendenz (String tendenz)
    {
        this.tendenz = tendenz;
    }

    public String getBelegung ()
    {
        return belegung;
    }

    public void setBelegung (String belegung)
    {
        this.belegung = belegung;
    }

    public Geometry getGeometry ()
    {
        return geometry;
    }

    public void setGeometry (Geometry geometry)
    {
        this.geometry = geometry;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", addresse = "+addresse+", kapazität = "+kapazität+", typ = "+typ+", tendenz = "+tendenz+", belegung = "+belegung+", geometry = "+geometry+"]";
    }
}