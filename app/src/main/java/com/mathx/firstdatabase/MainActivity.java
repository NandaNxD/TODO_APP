package com.mathx.firstdatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText name;
    Button add;
    Button clears;
    SQLiteDatabase database;
    ArrayList<String> array;
    Cursor cursor;
    ConstraintLayout constraintLayout;
    ArrayAdapter<String> arrayAdapter;
    String s;

    ListView listView;
    String bo="(\"";
    String space="     ";
    String bc="\")";

    public void setAlpha(View view){
        //constraintLayout.setActivated(false);
        constraintLayout.setAlpha(0.7f);
    }
    public void addPrev(){
        if(cursor!=null && cursor.getCount()>0){
            int row=1;
            Log.i("While","Loop");
            while(cursor.moveToNext()){
                if(row==1){
                    cursor.moveToFirst();
                }
                s=cursor.getString(0);
                array.add(s);
                row++;
            }
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void clearAll(View view){
        name.setText("");
        if(array.isEmpty()){
            Toast.makeText(this,"Nothing to Clear!!",Toast.LENGTH_SHORT).show();
            return;
        }
        database.execSQL("Drop table todo");
        array.clear();
        arrayAdapter.notifyDataSetChanged();
        database.execSQL("CREATE TABLE IF NOT EXISTS todo(sl_no integer primary key autoincrement,name text)");
    }

    public void addName(String nam){
        String ins="insert into todo (name) values"+bo+nam+bc;
        Log.i("mess",ins);
        database.execSQL(ins);
    }

    public void deleteName(View view){
        String del="delete from todo where sl_no="+String.valueOf(1);
        Log.i("crud",del);
        if(array.isEmpty()){
            Toast.makeText(this,"Nothing to Delete!!",Toast.LENGTH_SHORT).show();
            return;
        }
        array.remove(0);
        //database.execSQL(del);
        arrayAdapter.notifyDataSetChanged();
    }
    public void onClick(View view){
        String data=name.getText().toString();
        if(data.isEmpty()){
            Toast.makeText(this,"Todo empty!!",Toast.LENGTH_SHORT).show();
            return;
        }
        data="";
        data=space+name.getText().toString();
        addName(data);
        Log.i("messy","Logiu");
        array.add(data);
        arrayAdapter.notifyDataSetChanged();
        cursor=database.rawQuery("select * from todo",null);
        Log.i("messy","Logx");
        cursor.moveToLast();
        s=cursor.getString(0);
        Log.i("Insert",s);
        name.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = openOrCreateDatabase("test.sqlite",MODE_PRIVATE,null);
        name=findViewById(R.id.name);
        constraintLayout=findViewById(R.id.constraintLayout);
        add=findViewById(R.id.add);
        listView=findViewById(R.id.listView);
        array=new ArrayList<>();
        clears=findViewById(R.id.clearAll);
        arrayAdapter=new ArrayAdapter<>(this,R.layout.white_text,array);
        //database.execSQL("Drop table todo");
        database.execSQL("CREATE TABLE IF NOT EXISTS todo(name text)");
        cursor=database.rawQuery("select name from todo",null);

        listView.setAdapter(arrayAdapter);
        addPrev();          // Add Previously saved values
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                return;
//            }
//        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(!array.isEmpty())
                array.remove(position);
                String del="delete from todo where rowid="+String.valueOf(position+1);
                database.execSQL(del);
                database.execSQL("vacuum");
                arrayAdapter.notifyDataSetChanged();
                return true;
            }
        });
        clears.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Nothing to Clear!!",Toast.LENGTH_SHORT).show();
                    return;
                }
                name.setText("");
            }
        });
        clears.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                name.setText("");
                if(array.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Nothing to Clear!!",Toast.LENGTH_SHORT).show();
                    return true;
                }
                database.execSQL("Drop table todo");
                array.clear();
                arrayAdapter.notifyDataSetChanged();
                database.execSQL("CREATE TABLE IF NOT EXISTS todo(sl_no integer primary key autoincrement,name text)");
                return true;
            }
        });

    }
}