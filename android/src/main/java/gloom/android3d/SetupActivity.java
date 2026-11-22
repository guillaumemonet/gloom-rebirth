package gloom.android3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import java.io.File;

import gloom.android.AndroidAsk;
import gloom.boot.Boot;

/**
 * Point d'entrée de l'APK Gloom Rebirth : s'assure que les disquettes Gloom Deluxe sont là (sinon
 * propose de les télécharger, rien sans un oui), puis ouvre la vue 3D ({@link RebirthActivity}).
 */
public final class SetupActivity extends Activity {

    private TextView status;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setBackgroundColor(Color.BLACK);
        status.setGravity(Gravity.CENTER);
        status.setTextSize(18f);
        status.setText("Disquettes Gloom Deluxe...");
        setContentView(status);

        File adf = new File(getFilesDir(), "adf");
        System.setProperty("gloom.adf", adf.getAbsolutePath());
        AndroidAsk ask = new AndroidAsk(this);
        new Thread(() -> {                       // jamais sur le fil principal (AndroidAsk attend)
            if (Boot.ensure(ask)) {
                runOnUiThread(this::launch);
            } else {
                runOnUiThread(() -> status.setText("Disquettes absentes\n\nCopie les deux .adf de Gloom Deluxe dans\n"
                        + adf.getAbsolutePath() + "\nou relance pour les telecharger."));
            }
        }, "gloom-disks").start();
    }

    private void launch() {
        startActivity(new Intent(this, RebirthActivity.class));
        finish();
    }
}
