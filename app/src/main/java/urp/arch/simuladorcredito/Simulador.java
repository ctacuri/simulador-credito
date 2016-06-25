package urp.arch.simuladorcredito;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import urp.arch.simuladorcredito.model.Calculadora;
import urp.arch.simuladorcredito.model.NumericHelper;
import urp.arch.simuladorcredito.model.ParametrosCredito;
import urp.arch.simuladorcredito.model.SimulacionCredito;

public class Simulador extends AppCompatActivity {

    public final static String SIMULACION = "urp.arch.simuladorcredito.SIMULACION";

    EditText _txtMonto, _txtCuotas, _txtTEA;
    TextView _txtTotCuota, _txtTotInteres, _txtTotPagar;
    CheckBox _chkDobleJ, _chkDobleD;

    //To pass:
    //intent.putExtra("MyClass", obj);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _txtCuotas = (EditText)findViewById(R.id.simulador_cuotas);
        _txtMonto = (EditText)findViewById(R.id.simulador_monto);
        _txtTEA  = (EditText)findViewById(R.id.simulador_tea);
        _chkDobleJ = (CheckBox) findViewById(R.id.simulador_doble_julio);
        _chkDobleD = (CheckBox) findViewById(R.id.simulador_doble_diciembre);
        _txtTotCuota  = (TextView) findViewById(R.id.simulador_TotalCuota);

        configureFields();

        //----
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TIPO_CREDITO);
        Log.i("SIMULADOR", message);
    }

    void configureFields() {

        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText ctrl = (EditText)v;
                if (!hasFocus) {
                    if(ctrl.getText().length() == 0) {
                        ctrl.setText("0"); //TODO: Default view format
                    }
                    updateTotal();
                }
            }
        };

        _txtMonto.setSelectAllOnFocus(true);
        _txtMonto.setOnFocusChangeListener(focusListener);

        _txtCuotas.setSelectAllOnFocus(true);
        _txtCuotas.setOnFocusChangeListener(focusListener);

        _txtTEA.setSelectAllOnFocus(true);
        _txtTEA.setOnFocusChangeListener(focusListener);


    }
SimulacionCredito simulacion;
    void updateTotal(){
        ParametrosCredito p = readParameters();

        //Calcular credito
        Calculadora calculadora = new Calculadora();
        SimulacionCredito sim = calculadora.simularCredito(p);
        Log.i("Simulacion", "cuotas:" + String.valueOf(sim.cuotas.size()) + " interes  capital   saldo   cuota");
        for (int i=0; i < p.cuotas;i++) {
            Log.i("Simulacion", "cuota[" + String.valueOf(i+1) + "," + String.valueOf(sim.cuotas.get(i).dias)
                    + "=" + String.format("%.2f %.2f %.2f %.2f",
                        sim.cuotas.get(i).interes,
                        sim.cuotas.get(i).capital,
                        sim.cuotas.get(i).saldo,
                        sim.cuotas.get(i).cuota
            ));
        }
        //Presentar resultados
        (( TextView )findViewById(R.id.simulador_TotalCuota)).setText(String.format("%s %,.2f", "S/.", sim.cuotaMensual));

        simulacion = sim;
    }

    ParametrosCredito readParameters(){
        ParametrosCredito p = new ParametrosCredito();

        NumericHelper nh = new NumericHelper();

        p.monto = nh.parseFloat(_txtMonto.getText().toString());
        p.cuotas = nh.parseInt(_txtCuotas.getText().toString());
        p.tea  = nh.parseFloat(_txtTEA.getText().toString());

        p.cuotadobleJulio = _chkDobleJ.isChecked();
        p.cuotaDobleDiciembre = _chkDobleD.isChecked();

        // debug
        Calendar cal = Calendar.getInstance();
        cal.set(2014,2,28);
        p.fechaDesembolso =cal.getTime();
        cal.add(Calendar.DATE, 30);
        p.fechaVencimiento =cal.getTime();

        return  p;
    }

    void gotoCuotas(View view){
        Intent intent = new Intent(this, SimulacionCuotasActivity.class);
        intent.putExtra(SIMULACION, simulacion);
        this.startActivity(intent);
    }
}
