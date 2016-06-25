package urp.arch.simuladorcredito.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Ricardo on 6/23/2016.
 */
public class SimulacionCuota implements Serializable {
    public int numero;
    public int dias;
    public Date vencimiento;
    public float capital;
    public float interes;
    public float comision;
    public float desgravamen;
    public float seguro;
    public float cuota;
    public float saldo;
}
