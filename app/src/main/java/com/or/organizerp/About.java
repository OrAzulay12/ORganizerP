package com.or.organizerp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class About extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



    }



    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        if(!LoginActivity.isAdmin){
            menu.removeItem(R.id.menuManagerPage);

        }


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int itemid = menuitem.getItemId();
        if (itemid == R.id.menuAddEvent) {
            Intent goadmin = new Intent(About.this,calender.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuLogOut) {
            Intent goadmin = new Intent(About.this, MainPage.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuManagerPage) {
            Intent goadmin = new Intent(About.this, ManagerPage.class);
            startActivity(goadmin);
        }

        if (itemid == R.id.menuAbout) {
            Intent goadmin = new Intent(About.this, About.class);
            startActivity(goadmin);
        }
        if (itemid == R.id.menuManagerPage) {

            if(LoginActivity.isAdmin){
                Intent goadmin = new Intent(About.this, ManagerPage.class);
                startActivity(goadmin);

            }

        }

        return super.onOptionsItemSelected(menuitem);
    }



}