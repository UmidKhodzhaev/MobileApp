package com.example.notes.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.CONSTANTS;
import com.example.notes.R;
import com.example.notes.adapter.CategoryAdapter;
import com.example.notes.StandardDatabaseHelper;
import com.example.notes.model.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ListCategory extends AppCompatActivity {

    private StandardDatabaseHelper dbh;
    RecyclerView recycler;
    FloatingActionButton add_item;
    private TextView weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_category);

        recycler = findViewById(R.id.categoryRecycler);
        add_item = findViewById(R.id.add_category);
        weather = findViewById(R.id.weather);

        dbh = new StandardDatabaseHelper(ListCategory.this);

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_add_dialog();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        check_permissions();
        load_adapter();
        weather.setText("Загрузка..");

        new GetURLData().execute(CONSTANTS.WEATHER.getURL(56, 92.89));

        super.onResume();
    }


    private class GetURLData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String all_weather = jsonObject.getString("name");
                double temp = jsonObject.getJSONObject("main").getDouble("temp");
                if (temp > 0) {
                    all_weather += " +" + String.valueOf(temp);
                } else {
                    all_weather += "  " + String.valueOf(temp);
                }
                all_weather += " " + jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                all_weather += " " +  jsonObject.getJSONObject("wind").getString("speed") + "м/с";

                weather.setText(all_weather);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void check_permissions(){
        int PERMISSION_ALL = 1;
        if (!hasPermissions(this, CONSTANTS.PERMISSIONS)) {
            this.requestPermissions(CONSTANTS.PERMISSIONS, PERMISSION_ALL);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all_category) {
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void load_adapter() {
        List<Category> items = dbh.getAllCategory();
        CategoryAdapter adapter = new CategoryAdapter(ListCategory.this, this, items);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(ListCategory.this));
    }

    public void show_add_dialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        EditText edit = new EditText(this);
        edit.setText("");
        edit.setSelection(edit.length());

        dialog.setTitle("Название категории");
        dialog.setView(edit);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = edit.getText().toString().trim();
                if (text.length() == 0) {
                    Toast.makeText(ListCategory.this, "Введите название!", Toast.LENGTH_SHORT).show();
                } else {
                    dbh.addCategory(text);
                    load_adapter();
                }

            }
        });
        dialog.show();
    }

    public void show_update_dialog(Category item) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        EditText edit = new EditText(this);
        edit.setText(item.getName());
        edit.setSelection(edit.length());

        dialog.setTitle("Название категории");
        dialog.setView(edit);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = edit.getText().toString().trim();
                if (text.length() == 0) {
                    Toast.makeText(ListCategory.this, "Введите название!", Toast.LENGTH_SHORT).show();
                } else {
                    dbh.updateCategory(String.valueOf(item.getId()), text);
                    load_adapter();
                }
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbh.deleteOneCategory(String.valueOf(item.getId()));
                load_adapter();
            }
        });
        dialog.show();
    }

    public void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить все?");
        builder.setMessage("Вы уверены, что хотите удалить все данные?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StandardDatabaseHelper dbh = new StandardDatabaseHelper(ListCategory.this);
                dbh.deleteAllCategory();
                //Refresh Activity
                Intent intent = new Intent(ListCategory.this, ListCategory.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}
