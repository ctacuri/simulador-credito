package urp.arch.simuladorcredito.model;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ricardo on 6/23/2016.
 */
public class Calculadora {

    public SimulacionCredito simularCredito(ParametrosCredito p) {
        SimulacionCredito sim = new SimulacionCredito();

        sim.cuotaMensual = calcularCuota(p.tea,p.cuotas,p.monto, 30);

        Calendar c = Calendar.getInstance();
        c.setTime(p.fechaDesembolso); // inicio.

        Date inic = p.fechaDesembolso;
        Date venc = p.fechaVencimiento;
        float saldo = p.monto;
        Calendar month = Calendar.getInstance();
        for (int i = 0; i < p.cuotas; i++) {
            int dias = getDifferenceDays(inic, venc);

            SimulacionCuota cuota = new SimulacionCuota();
            cuota.numero = i+1;
            cuota.vencimiento = venc;
            cuota.dias = dias;
            cuota.interes = calcularInteresCuota(p.tea, saldo, dias);
            cuota.capital = sim.cuotaMensual - cuota.interes;

            if ((true == p.cuotadobleJulio && c.get(Calendar.MONTH) == 6) ||
                (true == p.cuotaDobleDiciembre && c.get(Calendar.MONTH) == 11)) {
                 cuota.capital += sim.cuotaMensual;
            }

            if (cuota.capital > saldo) {
                cuota.capital = saldo;
            }
            cuota.saldo = saldo - cuota.capital;
            cuota.cuota = cuota.interes + cuota.capital;

            //agrega la cuouta
            sim.cuotas.add(cuota);

            //avanza un mes
            saldo = cuota.saldo;
            inic = venc;
            c.setTime(inic);
            c.add(Calendar.MONTH, 1);
            venc = c.getTime();
        }

        return sim;
    }

    private float calcularTEM(float tea, int dias) {
        return (float) (Math.pow(1f + (tea / 100f), dias / 360f) - 1f) * 100f;
    }

    private float calcularCuota(float tea, float cuotas, float monto, int dias) {
        float tem = this.calcularTEM(tea, dias) / 100f;

        float x = (float) Math.pow(1f + tem, cuotas); //TODO: sumar cuotas dobles por año (+2 cada 12)
        return monto * ((x * tem) / (x - 1f));
    }

    private float calcularInteresCuota(float tea, float saldo, int dias) {
        //float tem = this.calcularTEM(tea, dias) / 100;
        float x = (float) ((Math.pow(1f + (tea/ 100f), dias / 360f) - 1f) * saldo);
        return x;
    }


    public static int getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }


}
