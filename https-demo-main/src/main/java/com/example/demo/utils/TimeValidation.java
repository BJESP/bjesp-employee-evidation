package com.example.demo.utils;

import java.time.LocalDate;

public class TimeValidation {

    public TimeValidation() {
    }


    public boolean ThrowIfEndBeforeStart(LocalDate start, LocalDate end)
    {
        if (start.compareTo(end) >= 0)
        {
            return false;
        }
        return true;
    }

    private boolean ThrowIfInPast(LocalDate start)
    {
        if (start.compareTo(LocalDate.now().plusDays(1)) < 0)
        {
            return false;
        }
        return true;
    }
}
