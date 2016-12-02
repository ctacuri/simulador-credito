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
        Date inic = p.fechaDesembolso;
        Date venc = p.fechaVencimiento;
        float saldo = p.monto;
        Calendar month = Calendar.getInstance();

        Calendar c = Calendar.getInstance();

        c.setTime(p.fechaDesembolso); // inicio.

        int cuotasFix = p.cuotas;
        for (int i = 0; i < p.cuotas; i++) {
            if ((p.cuotadobleJulio && c.get(Calendar.MONTH) == Calendar.JULY) ||
                    (p.cuotaDobleDiciembre && c.get(Calendar.MONTH) == Calendar.DECEMBER)) {
                //cuotasFix--;
            }
            c.add(Calendar.MONTH, 1);
        }

        sim.cuotaMensual = calcularCuota(p.tea,cuotasFix,p.monto);

        c.setTime(p.fechaDesembolso); // inicio.
        for (int i = 0; i < p.cuotas; i++) {
            //int dias = getDifferenceDays(inic, venc);
            int dias = 30;
            SimulacionCuota cuota = new SimulacionCuota();
            cuota.numero = i + 1;
            cuota.vencimiento = venc;
            cuota.dias = dias;

            cuota.interes = calcularInteresCuota(p.tea, saldo, dias);

            cuota.capital = sim.cuotaMensual - cuota.interes;

            if ((p.cuotadobleJulio && c.get(Calendar.MONTH) == Calendar.JULY) ||
                    (p.cuotaDobleDiciembre && c.get(Calendar.MONTH) == Calendar.DECEMBER)) {

                cuota.capital += sim.cuotaMensual;
            }

            cuota.saldo = saldo - cuota.capital;

            cuota.cuota = cuota.interes + cuota.capital;

            //agrega la cuouta
            sim.cuotas.add(cuota);

            sim.totalIntereses += cuota.interes;
            sim.totalPagar += cuota.interes + cuota.capital + cuota.comision + cuota.desgravamen + cuota.seguro;

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

    private float calcularCuota(float tea, float cuotas, float monto) {
        float tem = this.calcularTEM(tea, 30) / 100f;

        float x = (float) Math.pow(1f + tem, cuotas);
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
