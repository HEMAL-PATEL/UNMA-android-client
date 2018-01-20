package com.paperplanes.unma.common;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by abdularis on 20/01/18.
 */

public class DateUtilTest {

    @Test
    public void isSameDay_testCorrect() {
        Date d1 = new Date();
        Date d2 = new Date();

        assertTrue(DateUtil.isSameDay(d1, d2));
    }

    @Test
    public void isSameDay_testWrong() {
        Date d1 = new Date();
        // dikurangi satu hari
        Date d2 = new Date(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
        assertFalse(DateUtil.isSameDay(d1, d2));
    }
}
