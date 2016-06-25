package urp.arch.simuladorcredito.model;

import android.support.annotation.Nullable;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Ricardo on 6/23/2016.
 */
public class NumericHelper {

    NumberFormat ff;

    public NumericHelper(){
        ff = NumberFormat.getNumberInstance();
    }
    public int parseInt(String text) {
        try {
            Number n =  ff.parse(text);
            return n.intValue();
        } catch (ParseException e) {
            return 0;
        }
    }

    public float parseFloat(String text) {
        try {
            Number n = ff.parse(text);
            return n.floatValue();
        } catch (ParseException e) {
            return 0f;
        }
    }
}
