package urp.arch.simuladorcredito;

import android.content.Intent;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public final static String TIPO_CREDITO = "urp.arch.simuladorcredito.TIPO_CREDITO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void selectCreditType(View view) {
        Object creditType = view.getTag();
        if (null != creditType) {

            Log.i("HOME", creditType.toString() + " selected");

            Intent intent = new Intent(this, Simulador.class);
            intent.putExtra(TIPO_CREDITO, creditType.toString());
            this.startActivity(intent);
        }


    }
}
