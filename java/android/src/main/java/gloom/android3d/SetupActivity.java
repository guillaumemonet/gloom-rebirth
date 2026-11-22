package gloom.android3d;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import java.io.File;

import gloom.android.AssetSetup;

/**
 * Point d'entrée de l'APK Gloom Rebirth.
 *
 * Dans l'APK commun d'avant la séparation, c'était le launcher 2D ({@code GameView}) qui récupérait
 * les assets avant d'ouvrir {@link RebirthActivity}. Ici il n'y a plus de 2D : cette activité fait
 * seulement ce travail-là — vérifier/télécharger les assets ({@link AssetSetup}, partagé avec l'APK
 * 2D) dans le même dossier privé que celui lu par {@link RebirthActivity} — puis passe la main.
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
        setContentView(status);

        File root = new File(getFilesDir(), "GloomAmiga");
        if (AssetSetup.ready(root)) {
            launch();
            return;
        }
        show("Premier lancement\nRecuperation des assets\n" + AssetSetup.sourceLabel());
        new Thread(() -> {
            try {
                AssetSetup.install(root, this::show);
                runOnUiThread(this::launch);
            } catch (Exception e) {
                show("Assets indisponibles\n" + e.getMessage() + "\n\nVerifie ta connexion et relance.");
            }
        }, "gloom-assets").start();
    }

    private void show(String msg) {
        runOnUiThread(() -> status.setText(msg));
    }

    private void launch() {
        startActivity(new Intent(this, RebirthActivity.class));
        finish();
    }
}
