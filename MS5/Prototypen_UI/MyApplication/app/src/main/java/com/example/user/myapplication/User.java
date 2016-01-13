package com.example.user.myapplication;

public class User
{
    private String id;

    private String nummernschild;

    private String rolle;

    private String passwort;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getNummernschild ()
    {
        return nummernschild;
    }

    public void setNummernschild (String nummernschild)
    {
        this.nummernschild = nummernschild;
    }

    public String getRolle ()
    {
        return rolle;
    }

    public void setRolle (String rolle)
    {
        this.rolle = rolle;
    }

    public String getPasswort ()
    {
        return passwort;
    }

    public void setPasswort (String passwort)
    {
        this.passwort = passwort;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", nummernschild = "+nummernschild+", rolle = "+rolle+", passwort = "+passwort+"]";
    }
}