package luistrejo.com.materialdesign;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.gc.materialdesign.widgets.Dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends ActionBarActivity {

    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see

    String TITLES[] = {"Radio", "Comentarios", "Horarios", "Calendario de actividades", "Sugerencias", "Acerca de", "Salir"};
    int ICONS[] = {
            R.drawable.ic_radio,
            R.drawable.ic_comentarios,
            R.drawable.ic_horarios,
            R.drawable.ic_calendario,
            R.drawable.ic_sugerencia,
            R.drawable.ic_acercade,
            R.drawable.ic_salir};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String EMAIL = "Cbtis 122";
    int PROFILE = R.drawable.ic_sincaratula;

    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle
    String title = null;
    FragmentManager fragmentManager = getFragmentManager();
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private static final String LIST_FRAGMENT_TAG = "list_fragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Consultamos valor usuario del shared preferences
        SharedPreferences settings = MainActivity.this.getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String NAME = settings.getString("usuario", "?");
        settings = getSharedPreferences("usuario", MODE_PRIVATE);
        editor = settings.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fragmentManager.beginTransaction().replace(R.id.frame_container, new Radio()).commit();

    /* Assinging the toolbar object ot the view
    and setting the the Action bar to our toolbar
     */
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new MyAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE, this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(final RecyclerView recyclerView, MotionEvent motionEvent) {
                final View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                    Drawer.closeDrawers();
                    Fragment fragment = null;

                    switch (recyclerView.getChildPosition(child)) {
                        case 1: {
                            fragment = new Radio();
                            title = "Radio Bulldog";
                            break;
                        }
                        case 2: {
                            fragment = new Chat();
                            title = "Comentarios";
                            break;
                        }
                        case 3: {
                            copyAssets();
                            break;
                        }
                        case 4: {
                            fragment = new Calendario();
                            title = "Calendario de Actividades";
                            break;
                        }
                        case 5: {
                            fragment = new Sugerencias();
                            title = "Sugerencias";
                            break;
                        }
                        case 6: {
                            fragment = new Acerca();
                            title = "Acerca de";
                            break;
                        }
                        case 7: {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                            builder1.setMessage("Estas a punto de cerrar sesión");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton("Entendido",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent logout = new Intent(MainActivity.this, Login.class);
                                            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(logout);
                                            // guardar true para login para no mostrar esta activity de nuevo
                                            editor.putBoolean("login", false);
                                            editor.commit();

                                            //si esta activo el servicio de musica lo cerramos
                                            MainActivity.this.stopService(new Intent(MainActivity.this, Servicio.class));
                                        }
                                    });
                            builder1.setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog salir = builder1.create();
                            salir.show();

                            break;
                        }


                        default:
                            break;
                    }
                    if (fragment != null) {
                        try {
                            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                            setActionBarTitle(title);
                        } catch (IllegalStateException e) {
                            Log.d("Fragment manager", "IlegalStateException");
                        } catch (Exception e) {
                            Log.d("Fragment manager", "Error añadiendo fragment");

                        }
                    } else {

                        Log.d("Main Activity", "Error creando fragments");
                    }
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                title = "Radio Bulldog";
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().replace(R.id.frame_container, new Radio()).commit();
            setActionBarTitle("Radio Bulldog");
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Cbtis) {
            Intent cbtis = new Intent(MainActivity.this, Cbtis.class);
            cbtis.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(cbtis);
            return true;
        }
        if (id == R.id.cancionesant) {
            Fragment f = getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
            if (f != null) {
                getFragmentManager().popBackStack();
            } else {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_up,
                                R.animator.slide_down,
                                R.animator.slide_up,
                                R.animator.slide_down)
                        .add(R.id.list_fragment_container, SlidingListFragmentCanciones
                                        .instantiate(this, SlidingListFragmentCanciones.class.getName()),
                                LIST_FRAGMENT_TAG
                        ).addToBackStack(null).commit();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //evitar que vuelva a la actividad de login
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //PDF
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Fallo al copiar archivo pdf", e);
        }
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch (IOException e) {
                Log.e("tag", "Fallo al copiar archivo pdf: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + getExternalFilesDir(null) + "/grupos.pdf"), "application/pdf");
        startActivity(intent);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}

