package com.example.luis.adminpersonal.Utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by luis_ on 16/10/2018.
 */

public class LabelsFormatter implements IAxisValueFormatter {
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
     return String.valueOf(value);
    }
}
