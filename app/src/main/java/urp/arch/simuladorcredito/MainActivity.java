package urp.arch.simuladorcredito;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public final static String TIPO_CREDITO = "urp.arch.simuladorcredito.TIPO_CREDITO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
    }

    void selectCreditType(View view) {
        Object creditType = view.getTag();
        if (null != creditType) {

            Log.i("HOME", creditType.toString() + " selected");

            Intent intent = new Intent(this, SimuladorActivity.class);
            intent.putExtra(TIPO_CREDITO, creditType.toString());
            this.startActivity(intent);
        }


    }
}
