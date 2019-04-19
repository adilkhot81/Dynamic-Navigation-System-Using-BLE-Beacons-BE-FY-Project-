package com.example.mohdadil.visit21;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;



public class chat extends AppCompatActivity {

    // test tt;
  //  private AutoCompleteTextView myview;
    private int flag=0;
    private static final String[] COUNTRIES = new String[]{
            "Afghanistan", "Albania", "Algeria", "Andorra", "Angola"
    };

    List<ChatModel> lstChat = new ArrayList<>();

    ArrayList<String> chatList= new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if(!chatList.isEmpty()){
            chatList.clear();
        }
        chatList =intent.getStringArrayListExtra("list");



        setUpMessage();
        ListView lstView = (ListView) findViewById(R.id.listView);
        CustomAdapter adapter = new CustomAdapter(lstChat, this);
        lstView.setAdapter(adapter);
        String[] countries = getResources().getStringArray(R.array.countries);

        final AutoCompleteTextView editText = findViewById(R.id.actv);
        ImageView bt = findViewById(R.id.button3);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                R.layout.custom_list_item, R.id.text_view_list_item, chatList);
        editText.setAdapter(adapter1);

editText.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        editText.showDropDown();
        return false;
    }
});



        //editText.setThreshold(1);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = editText.getText().toString();
                int pos=0;
                for(int i=0;i<chatList.size();i++){
                    if(chatList.get(i).equals(input)){
                        pos=i;
                    }
                }
                Intent mainIntent = new Intent(chat.this,MainActivity.class);
                mainIntent.putExtra("pos",pos);
                startActivity(mainIntent);
            }
        });}


    private void setUpMessage() {
        lstChat.add(new ChatModel("Hey there!! Welcome to FCRIT", true));
        lstChat.add(new ChatModel("Hope you are having a great day!", true));
        lstChat.add(new ChatModel("We can help you to navigate to places indoors", true));
        lstChat.add(new ChatModel("Search for the place you want to navigate to..", true));
        // lstChat.add(new ChatModel("Search Places",false));
    }

}
