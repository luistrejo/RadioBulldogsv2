package luistrejo.com.materialdesign;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import luistrejo.com.materialdesign.Calendarioadapter;
import luistrejo.com.materialdesign.Calendarioadapter.PinnedSectionListAdapter;

public class Calendario extends ListFragment implements View.OnClickListener {

    static class SimpleAdapter extends ArrayAdapter<Item> implements PinnedSectionListAdapter {

        private static final int[] COLORS = new int[]{
                R.color.calendario};

        public SimpleAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);

            final int sectionsNumber = 6;
            final String[] mes = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio"};
            final String[][] fechas = {
                    {"7 - 8              Solicitud de papeletas (Pago de recurso)",
                            "9               Inscripción a Recursos",
                            "12 - 25     Curso de Capacitacion CENALTEC (Ensamble de aéro-estructuras)     ",
                            "12 - 23     Recursos",
                            "12 - 13     Reunion Estatal Construye-T",
                            "14 - 16     Reunion de Academia Estatal en Cd. Chihuahua",
                            "19 - 21     Curso para Orientadores y Fomento a la Salud",
                            "20             Curso Taller - Construye-T - Tutorias (UPN)",
                            "21 - 23     Reinscripción de Alumnos Regulares: 2°, 4° y 6°",
                            "23 - 27     Captura de Calificaciones de Recurso",
                            "26 - 30     Asesoria Ceneval - Enlace para 6to Semestre",
                            "26 - 30     Planeacion Curricular",
                            "26 - 30     Trabajos de Academias Locales",
                            "30             Colegiado Académico"},
                    {"2               Suspensión de Labores",
                            "3               Inicio de clases - Ceremonia Civica(T.M. 9:00 hrs, T.V. 16:45 hrs)",
                            "3 - 6              Reinscripción de Alumnos Irregulares: 2°, 4° y 6°",
                            "17 - 20     Entrega de Planeación Académica",
                            "17 - 20     Concurso Estatal de Prototipos",
                            "17 - 20     XV Festival Académico Local 2015"},
                    {"2 - 6              1° Evaluacion Parcial",
                            "3 - 6              Festival Académico Etapa Estatal",
                            "9 - 11       Captura calificaciones 1° Evaluacion Parcial",
                            "11 - 13     1° Academia Local",
                            "16              Suspensión de Labores",
                            "17              Entrega de boletas",
                            "18 - 20     Aplicacion Prueba Enlace 2015",
                            "18 - 20     Promocion del Plantel",
                            "23              Ceremonia Civica T.V. 16:45 hrs",
                            "23 - 27     Supervisión en el Aula",
                            "24 - 27     Festival Académico Etapa Nacional",
                            "30 - 31     Periodo Vacional"},
                    {"1 - 2              Periodo Vacacional",
                            "20 - 24     2° Evaluación Parcial",
                            "27 - 29     Captura de la 2° Evaluación Parcial",
                            "27 - 30     Semana Nacional de Vinculación",
                            "27 - 30     2° Academia Local",
                            "4 y 6              2°"},
                    {"1               Fecha",
                            "2               Fecha",
                            "3               Fecha"},
                    {"1               Fecha",
                            "2               Fecha",
                            "3               Fecha"}};

            prepareSections(sectionsNumber);

            int sectionPosition = 0, listPosition = 0;

            for (char i = 0; i < sectionsNumber; i++) {
                Item section = new Item(Item.SECTION, mes[i]);
                section.sectionPosition = sectionPosition;
                section.listPosition = listPosition++;
                onSectionAdded(section, sectionPosition);
                add(section);

                final int itemsNumber = fechas[i].length;

                for (int j = 0; j < itemsNumber; j++) {
                    Item item = new Item(Item.ITEM, fechas[i][j]);
                    item.sectionPosition = sectionPosition;
                    item.listPosition = listPosition++;

                    add(item);
                }

                sectionPosition++;

            }
        }

        protected void prepareSections(int sectionsNumber) {
        }

        protected void onSectionAdded(Item section, int sectionPosition) {
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTextColor(Color.DKGRAY);
            view.setTag("" + position);
            Item item = getItem(position);
            if (item.type == Item.SECTION) {
                //view.setOnClickListener(PinnedSectionListActivity.this);
                view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
            }
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).type;
        }

        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == Item.SECTION;
        }

    }

    static class FastScrollAdapter extends SimpleAdapter implements SectionIndexer {

        private Item[] sections;

        public FastScrollAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        @Override
        protected void prepareSections(int sectionsNumber) {
            sections = new Item[sectionsNumber];
        }

        @Override
        protected void onSectionAdded(Item section, int sectionPosition) {
            sections[sectionPosition] = section;
        }

        @Override
        public Item[] getSections() {
            return sections;
        }

        @Override
        public int getPositionForSection(int section) {
            if (section >= sections.length) {
                section = sections.length - 1;
            }
            return sections[section].listPosition;
        }

        @Override
        public int getSectionForPosition(int position) {
            if (position >= getCount()) {
                position = getCount() - 1;
            }
            return getItem(position).sectionPosition;
        }

    }

    static class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final String text;

        public int sectionPosition;
        public int listPosition;

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

    }

    private boolean hasHeaderAndFooter;
    private boolean isFastScroll;
    private boolean addPadding;
    private boolean isShadowVisible = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendario, container, false);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            isFastScroll = savedInstanceState.getBoolean("isFastScroll");
            addPadding = savedInstanceState.getBoolean("addPadding");
            isShadowVisible = savedInstanceState.getBoolean("isShadowVisible");
            hasHeaderAndFooter = savedInstanceState.getBoolean("hasHeaderAndFooter");
        }
        initializeHeaderAndFooter();
        initializeAdapter();
        initializePadding();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFastScroll", isFastScroll);
        outState.putBoolean("addPadding", addPadding);
        outState.putBoolean("isShadowVisible", isShadowVisible);
        outState.putBoolean("hasHeaderAndFooter", hasHeaderAndFooter);
    }



    private void initializePadding() {
        float density = getResources().getDisplayMetrics().density;
        int padding = addPadding ? (int) (16 * density) : 0;
        getListView().setPadding(padding, padding, padding, padding);
    }

    private void initializeHeaderAndFooter() {
        setListAdapter(null);
        if (hasHeaderAndFooter) {
            ListView list = getListView();

            LayoutInflater inflater = LayoutInflater.from(this.getActivity());
            TextView header1 = (TextView) inflater.inflate(R.layout.item_calendario, list, false);
            header1.setText("First header");
            list.addHeaderView(header1);

            TextView header2 = (TextView) inflater.inflate(R.layout.item_calendario, list, false);
            header2.setText("Second header");
            list.addHeaderView(header2);

            TextView footer = (TextView) inflater.inflate(R.layout.item_calendario, list, false);
            footer.setText("Single footer");
            list.addFooterView(footer);
        }
        initializeAdapter();
    }

    private void initializeAdapter() {
        getListView().setFastScrollEnabled(isFastScroll);
        if (isFastScroll) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                getListView().setFastScrollAlwaysVisible(true);
            }
            setListAdapter(new FastScrollAdapter(this.getActivity(), R.layout.item_calendario, R.id.text1));
        } else {
            setListAdapter(new SimpleAdapter(this.getActivity(), R.layout.item_calendario, R.id.text1));
        }
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this.getActivity(), "Item: " + v.getTag(), Toast.LENGTH_SHORT).show();
    }


}