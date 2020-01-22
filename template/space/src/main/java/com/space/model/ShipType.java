package com.space.model;

public enum ShipType
{
    TRANSPORT,
    MILITARY,
    MERCHANT;

    public static boolean isElementOfEnum(String string)
    {
        for (ShipType shipType : ShipType.values())
        {
            if (shipType.equals(string))
            {
                return true;
            }
        }
        return false;
    }
}