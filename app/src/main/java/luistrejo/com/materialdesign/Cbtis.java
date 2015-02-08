package luistrejo.com.materialdesign;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Cbtis extends ActionBarActivity {

    Toolbar toolbar;
    TextView cbtis122, plataforma, horarios, alumnos, facebook, twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbtis);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setSubtitle("Informacion");
        cbtis122 = (TextView) findViewById(R.id.cbtis122);
        cbtis122.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        cbtis122.setOnClickListener(onClickListener);
        plataforma = (TextView) findViewById(R.id.plataforma);
        plataforma.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        plataforma.setOnClickListener(onClickListener);
        horarios = (TextView) findViewById(R.id.horarios);
        horarios.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        horarios.setOnClickListener(onClickListener);
        alumnos = (TextView) findViewById(R.id.alumnos);
        alumnos.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        alumnos.setOnClickListener(onClickListener);
        facebook = (TextView) findViewById(R.id.facebook);
        facebook.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        facebook.setOnClickListener(onClickListener);
        twitter = (TextView) findViewById(R.id.twitter);
        twitter.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        twitter.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cbtis122:
                    Intent cbtis = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URLCbtis)));
                    startActivity(cbtis);
                    break;
                case R.id.plataforma:
                    Intent plataforma = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URLPlataforma)));
                    startActivity(plataforma);
                    break;
                case R.id.horarios:
                    Intent horarios = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URLHorarios)));
                    startActivity(horarios);
                    break;
                case R.id.alumnos:
                    Intent alumnos = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URLAlumnos)));
                    startActivity(alumnos);
                    break;
                case R.id.facebook:
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URLFacebookAPP)));
                        startActivity(intent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URLFacebook))));
                    }

                    break;
                case R.id.twitter:
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.URLTwitterAPP)));
                        startActivity(intent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.URLTwitter))));
                    }
                    break;
            }
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
