package urp.arch.simuladorcredito.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Ricardo on 6/23/2016.
 */
public class SimulacionCredito implements Serializable {
    public float cuotaMensual;
    public float totalIntereses;
    public float totalPagar;

    public List<SimulacionCuota> cuotas = new ArrayList<SimulacionCuota>();
}
