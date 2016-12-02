package urp.arch.simuladorcredito;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import urp.arch.simuladorcredito.model.Calculadora;
import urp.arch.simuladorcredito.model.NumericHelper;
import urp.arch.simuladorcredito.model.ParametrosCredito;
import urp.arch.simuladorcredito.model.SimulacionCredito;

public class SimuladorActivity extends AppCompatActivity {

    public final static String SIMULACION = "urp.arch.simuladorcredito.SIMULACION";

    EditText _txtMonto, _txtCuotas, _txtTEA;
    TextView _txtTotCuota, _txtTotInteres, _txtTotPagar, _txtDesembolso;
    Calendar _fecDesembolso = Calendar.getInstance();
    CheckBox _chkDobleJ, _chkDobleD;
    DateFormat dateFormatter = SimpleDateFormat.getDateInstance(); // new SimpleDateFormat( "dd-MM-yyyy", Locale.getDefault());
    //To pass:
    //intent.putExtra("MyClass", obj);

    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);

        configureFields();

        //----
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TIPO_CREDITO);
        Log.i("SIMULADOR", message);
    }




    private DatePickerDialog fromDatePickerDialog;
    void configureFields() {

        _txtCuotas = (EditText)findViewById(R.id.simulador_cuotas);
        _txtMonto = (EditText)findViewById(R.id.simulador_monto);
        _txtTEA  = (EditText)findViewById(R.id.simulador_tea);
        _chkDobleJ = (CheckBox) findViewById(R.id.simulador_doble_julio);
        _chkDobleD = (CheckBox) findViewById(R.id.simulador_doble_diciembre);
        _txtTotCuota  = (TextView) findViewById(R.id.simulador_TotalCuota);
        _txtDesembolso = (TextView) findViewById(R.id.simulador_desembolso);

        findViewById(R.id.layout).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    hideKeyboard(v);
                }
            }
        });

        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText ctrl = (EditText) v;
                if (!hasFocus) {
                    if (ctrl.getText().length() == 0) {
                        ctrl.setText("0"); //TODO: Default view format
                    }
                    updateTotal();
                }
            }
        };

        //
        _txtMonto.setSelectAllOnFocus(true);
        _txtMonto.setOnFocusChangeListener(focusListener);

        _txtCuotas.setSelectAllOnFocus(true);
        _txtCuotas.setOnFocusChangeListener(focusListener);

        _txtTEA.setSelectAllOnFocus(true);
        _txtTEA.setOnFocusChangeListener(focusListener);

        //
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getWindow().getDecorView().clearFocus();
                updateTotal();
            }
        };
        _chkDobleJ.setOnCheckedChangeListener(onCheckedChangeListener);
        _chkDobleD.setOnCheckedChangeListener(onCheckedChangeListener);

        //
        _txtDesembolso.setText(dateFormatter.format(_fecDesembolso.getTime())); //today
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                _fecDesembolso.set(year, monthOfYear, dayOfMonth);
                _txtDesembolso.setText(dateFormatter.format(_fecDesembolso.getTime()));
                getWindow().getDecorView().clearFocus();
                updateTotal();
            }
        }, _fecDesembolso.get(Calendar.YEAR), _fecDesembolso.get(Calendar.MONTH), _fecDesembolso.get(Calendar.DAY_OF_MONTH));
        _txtDesembolso.setInputType(InputType.TYPE_NULL);
        _txtDesembolso.setKeyListener(null);
        _txtDesembolso.setFocusable(false);
        _txtDesembolso.setClickable(true);
        _txtDesembolso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        });

        //
        updateTotal();
    }
    SimulacionCredito simulacion;
    void updateTotal(){
        ParametrosCredito p = readParameters();

        Answers.getInstance().logCustom(new CustomEvent("Simulacion Realizada")
                .putCustomAttribute("NumeroCuotas", p.cuotas)
                .putCustomAttribute("MontoCredito", p.monto));


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
        (( TextView )findViewById(R.id.simulador_TotalInteres)).setText(String.format("%s %,.2f", "S/.", sim.totalIntereses));
        (( TextView )findViewById(R.id.simulador_TotalPagar)).setText(String.format("%s %,.2f", "S/.", sim.totalPagar));

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
        p.fechaDesembolso = _fecDesembolso.getTime();
        Calendar venc = Calendar.getInstance();
        venc.setTime(_fecDesembolso.getTime());
        venc.add(Calendar.DATE, 30); //FIX-it
        p.fechaVencimiento = venc.getTime();

        return  p;
    }

    void gotoCuotas(View view){
        Intent intent = new Intent(this, SimulacionCuotasActivity.class);
        intent.putExtra(SIMULACION, simulacion);
        this.startActivity(intent);
    }

    public void hideKeyboard(View view) {
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    void layoutOnClick(final View view) {

    }
}
