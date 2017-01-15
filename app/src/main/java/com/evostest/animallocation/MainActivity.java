package com.evostest.animallocation;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.evostest.animallocation.model.Animal;
import com.evostest.animallocation.model.Bird;
import com.evostest.animallocation.model.Mammal;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static int smallestWidth;

    FloatingActionButton fab;

    RecyclerView rvAnimals;
    AnimalsRVAdapter adapter;

    ArrayList<Animal> animals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_main);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AnimalsRVAdapter.longClick) {
                    fab.setImageBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.ic_add_white_24dp));
                    adapter.animateBack();
                } else {
                    Intent intent = new Intent(getApplicationContext(), NewAnimalActivity.class);
                    intent.putExtra(API.KEY_LIST_ANIMALS, animals);
                    startActivityForResult(intent, 1);
                }
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        smallestWidth = dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels;

        if (savedInstanceState != null && savedInstanceState.containsKey(API.KEY_LIST_ANIMALS)) {
            animals = savedInstanceState.getParcelableArrayList(API.KEY_LIST_ANIMALS);
        } else {
            animals = new ArrayList<>();
            selectAnimals();
        }

        rvAnimals = (RecyclerView) findViewById(R.id.rvAnimals);
        adapter = new AnimalsRVAdapter(this, fab, animals, new AnimalsRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean location) {
                if (!location) {
                    removeAnimal(animals.get(position).getId());
                    animals.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyDataSetChanged();
                    if (animals.size() == 0) {
                        fab.setImageBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                R.drawable.ic_add_white_24dp));
                        adapter.animateBack();
                    }
                } else {
                    ArrayList<Animal> animal = new ArrayList<>();
                    animal.add(animals.get(position));
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra(API.KEY_LIST_ANIMALS, animal);
                    startActivity(intent);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        rvAnimals.setAdapter(adapter);
        rvAnimals.setLayoutManager(layoutManager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Animal newAnimal = null;
        if (data.hasExtra(API.KEY_NEW_ANIMAL)) {
            newAnimal = data.getParcelableExtra(API.KEY_NEW_ANIMAL);
        }
        addAnimal(newAnimal);
    }

    public void addAnimal(Animal newAnimal) {
        animals.add(newAnimal);
        adapter.notifyDataSetChanged();
        switch (newAnimal.getType()) {
            case MAMMAL:
                int hasMilk = 0;
                if (((Mammal) newAnimal).hasMilk) {
                    hasMilk = 1;
                }
                insertAnimal(newAnimal.getName(), getResources().getString(R.string.db_type_mammal), newAnimal.getLocation().latitude,
                        newAnimal.getLocation().longitude, hasMilk, 0);
                break;
            case BIRD:
                int canFly = 0;
                if (((Bird) newAnimal).canFly) {
                    canFly = 1;
                }
                insertAnimal(newAnimal.getName(), getResources().getString(R.string.db_type_bird), newAnimal.getLocation().latitude,
                        newAnimal.getLocation().longitude, 0, canFly);
                break;
        }
    }


    private void insertAnimal(String name, String type, double latitude, double longitude, int hasMilk, int canFly) {
        final String insertAnimal = "INSERT INTO animals " + "(" +
                "name, type, latitude, longitude, has_milk, can_fly) " +
                "VALUES ('" + name + "', '" + type + "', " + latitude + ", " + longitude
                + ", " + hasMilk + ", " + canFly + ");";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyDBHelper.getInstance(getApplicationContext()).openDatabase().execSQL(insertAnimal);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    MyDBHelper.getInstance(getApplicationContext()).closeDatabase();
                }
            }
        }).start();
    }

    private void removeAnimal(int id) {
        final String removeAnimal = "DELETE FROM animals WHERE id = " + id + ";";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MyDBHelper.getInstance(getApplicationContext()).openDatabase().execSQL(removeAnimal);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    MyDBHelper.getInstance(getApplicationContext()).closeDatabase();
                }
            }
        }).start();
    }

    private void selectAnimals() {
        final String selectStmt = "SELECT * FROM animals ORDER BY id;";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor c = MyDBHelper.getInstance(getApplicationContext()).openDatabase().rawQuery(selectStmt, null);
                    int columns = c.getColumnCount();

                    if (!c.moveToFirst()) {
                        return;
                    }
                    while (!c.isAfterLast()) {
                        for (int i = 0; i < columns; i++) {
                            int id = c.getInt(c.getColumnIndex("id"));
                            String name = c.getString(c.getColumnIndex("name"));
                            String type = c.getString(c.getColumnIndex("type"));
                            double latitude = c.getDouble(c.getColumnIndex("latitude"));
                            double longitude = c.getDouble(c.getColumnIndex("longitude"));
                            boolean canFly = c.getInt(c.getColumnIndex("can_fly")) > 0;
                            boolean hasMilk = c.getInt(c.getColumnIndex("has_milk")) > 0;

                            Animal animal = null;
                            if (type.equals(getResources().getString(R.string.db_type_bird))) {
                                animal = new Bird(id, name, canFly);
                                animal.setLocation(latitude, longitude);
                            } else if (type.equals(getResources().getString(R.string.db_type_mammal))) {
                                animal = new Mammal(id, name, hasMilk);
                                animal.setLocation(latitude, longitude);
                            }

                            if (animal != null) {
                                animals.add(animal);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }

                            c.moveToNext();
                        }
                        c.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    MyDBHelper.getInstance(getApplicationContext()).closeDatabase();
                }
            }
        }).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (animals != null) {
            outState.putParcelableArrayList(API.KEY_LIST_ANIMALS, animals);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_locations:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra(API.KEY_LIST_ANIMALS, animals);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
