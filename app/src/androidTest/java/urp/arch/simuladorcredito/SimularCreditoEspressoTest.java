package urp.arch.simuladorcredito;

import android.support.test.espresso.ViewAssertion;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by Ricardo on 12/1/2016.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SimularCreditoEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void simulationShowsAfterCreditTypeSelection() {

        onView(withId(R.id.imageButton0))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .perform(click());

        onView(withId(R.id.toolbar))
            .check(matches(isEnabled()));
    }


    @Test
    public void simulationSimpleCreditValidateMonthlyAmmount() {

        onView(withId(R.id.imageButton0))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .perform(click());

        onView(withId(R.id.simulador_monto))
                .perform(typeText("300000"), closeSoftKeyboard());
        onView(withId(R.id.simulador_tea))
                .perform(typeText("12.0"), closeSoftKeyboard());
        onView(withId(R.id.simulador_cuotas))
                .perform(typeText("60"), closeSoftKeyboard());

        onView(withChild(withId(R.id.simulador_monto))).perform(click());

        onView(withId(R.id.simulador_TotalCuota))
                .check(matches(withText("S/. 6,580.70")));

    }

}
