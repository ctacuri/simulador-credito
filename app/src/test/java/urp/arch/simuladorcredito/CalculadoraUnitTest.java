package urp.arch.simuladorcredito;

import android.util.Log;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import urp.arch.simuladorcredito.model.Calculadora;
import urp.arch.simuladorcredito.model.ParametrosCredito;
import urp.arch.simuladorcredito.model.SimulacionCredito;


import static org.junit.Assert.*;


/**
 * Created by Ricardo on 11/28/2016.
 */

public class CalculadoraUnitTest {

    private ParametrosCredito createTestScenarioForCredit() {

        ParametrosCredito pc = new ParametrosCredito();
        pc.cuotas = 24;
        pc.monto= 300000;
        pc.tea=12.0f;

        Calendar cal = Calendar.getInstance();
        cal.set(2016,1,31,0,0,0);
        pc.fechaDesembolso = cal.getTime();
        cal.add(Calendar.DATE, 30); //FIX-it
        pc.fechaVencimiento = cal.getTime();

        return pc;
    }

    @Test
    public void Calculadora_missingParametersThrowsException() {
        Calculadora calculadora = new Calculadora();
        ParametrosCredito pc = new ParametrosCredito();

        try {
            SimulacionCredito sim = calculadora.simularCredito(pc);
            fail();
        }
        catch(Exception ex)
        {}
    }

    @Test
    public void Calculadora_standardCreditCalculation() {
        Calculadora calculadora = new Calculadora();

        ParametrosCredito pc = createTestScenarioForCredit();
        SimulacionCredito sim = calculadora.simularCredito(pc);

        assertEquals(24,sim.cuotas.size());
        assertEquals(336869.21, sim.totalPagar,0.01); //manually calculated
    }


    @Test
    public void Calculadora_standardCreditCalculationUsingDoublePayments() {
        Calculadora calculadora = new Calculadora();

        ParametrosCredito pc = createTestScenarioForCredit();
        SimulacionCredito sim = calculadora.simularCredito(pc);
        pc.cuotaDobleDiciembre=true;
        pc.cuotadobleJulio=true;

        assertEquals(24,sim.cuotas.size());
        assertEquals(336869.21, sim.totalPagar,0.01); //manually calculated
    }


    @Test
    public void Calculadora_simulationWithoutRemainder() {
        Calculadora calculadora = new Calculadora();

        ParametrosCredito pc = createTestScenarioForCredit();
        SimulacionCredito sim = calculadora.simularCredito(pc);
        pc.cuotaDobleDiciembre=true;
        pc.cuotadobleJulio=true;

        assertEquals(0, sim.cuotas.get(sim.cuotas.size()-1).saldo,0.001f);
        assertEquals(131.94,sim.cuotas.get(sim.cuotas.size()-1).interes,0.01f);
    }

    @Test
    public void Calculadora_standardCreditCalculationWithoutNegativeValues() {
        Calculadora calculadora = new Calculadora();

        ParametrosCredito pc = createTestScenarioForCredit();
        SimulacionCredito sim = calculadora.simularCredito(pc);
        pc.cuotaDobleDiciembre=true;
        pc.cuotadobleJulio=true;

        assertEquals(24,sim.cuotas.size());

        assertTrue(sim.cuotas.get(sim.cuotas.size()-1).capital > 0);
        assertTrue(sim.cuotas.get(sim.cuotas.size()-1).saldo > 0);
        assertTrue(sim.cuotas.get(sim.cuotas.size()-1).interes > 0);
        assertTrue(sim.cuotas.get(sim.cuotas.size()-1).cuota > 0);
    }
}
