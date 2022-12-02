//Kieran Cosgrove
//Student Number: 20226841
//Note to Ta: app will run on my roommates computer but not mine,
//I think there is an issue with my HAXM installation
package com.example.guessmaster;

import java.util.Random;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.*;
import android.content.DialogInterface;
import android.view.View;
import androidx.annotation.NonNull;
import android.os.Bundle;

public class GuessMasterActivity extends AppCompatActivity {
    //Defining view components
    private TextView entityName;
    private TextView ticketsum;
    private Button guessButton;
    private EditText userIn;
    private Button btnclear;
    private String user_input;
    private ImageView entityImage;
    private String answer;

    //Instance information
    private int entityid;
    private Entity currentEntity;
    private String entName;

    //Will store ticket and entity data
    private final Entity[] entities;
    private int numOfEntities;
    private int totaltickets = 0;
    private int currentTicketsWon = 0;

    protected void onCreate(Bundle instanceState) {
        super.onCreate(instanceState);
        setContentView(R.layout.activity_guess_activity);
        //Establishing more thorough access of the layout
        guessButton = (Button) findViewById(R.id.btnGuess);
        userIn = (EditText) findViewById(R.id.guessinput);
        ticketsum = (TextView) findViewById(R.id.ticket);
        entityName = (TextView) findViewById(R.id.entityName);
        btnclear = (Button) findViewById(R.id.btnClear);
        entityImage = (ImageView) findViewById(R.id.entityImage);

        //Code to create objects
        Country usa = new Country("United States", new Date("July", 4, 1776), "Washinton D.C.", 0.1);
        Person myCreator = new Person("My Creator", new Date("September", 1, 2000), "Female", 1);
        Politician jTrudeau = new Politician("Justin Trudeau", new Date("December", 25, 1971), "Male", "Liberal", 0.25);
        Singer cDion = new Singer("Celine Dion", new Date("March", 30, 1961), "Female", "La voix du bon Dieu", new Date("November", 6, 1981), 0.5);

        //Integrate the main activity into the games
        new GuessMasterActivity();
        addEntity(usa);
        addEntity(myCreator);
        addEntity(jTrudeau);
        addEntity(cDion);

        //Selecting an entity and displaying starting message
        changeEntity();
        welcomeToGame(currentEntity);

        //OnClick Listener action for clear button
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEntity();
            }
        });

        //OnClick Listener action for submit button
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playing game
                playGame(currentEntity);
            }
        });
    }

    //Changes current entity
    public void changeEntity() {
        userIn.getText().clear();
        entityid = genRandomentityid();
        Entity entity = entities[entityid];
        entName = entity.getName();
        entityName.setText(entName);
        ImageSetter();
        currentEntity = entity;
    }

    //Match the appropriate image with the current entity
    public void ImageSetter() {
        String name = entName;
        switch (name) {
            case "United States":
                entityImage.setImageResource(R.drawable.usaflag);
                break;
            case "My Creator":
                entityImage.setImageResource(R.drawable.checkmark);
                break;
            case "Justin Trudeau":
                entityImage.setImageResource(R.drawable.justint);
                break;
            case "Celine Dion":
                entityImage.setImageResource(R.drawable.celidion);
                break;
        }
    }

    //Displays the starting message for the game
    public void welcomeToGame(@NonNull Entity entity) {
        AlertDialog.Builder welcomealert = new AlertDialog.Builder(GuessMasterActivity.this);
        welcomealert.setTitle("GuessMaster_Game_v3");
        welcomealert.setMessage(entity.welcomeMessage());
        welcomealert.setCancelable(false);

        welcomealert.setNegativeButton("START_GAME", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Game_is_Starting...Enjoy", Toast.LENGTH_SHORT).show();
            }
        });
        //Show Dialog
        AlertDialog dialog = welcomealert.create();
        dialog.show();
    }
    //Sets up initial variables when starting game
    public GuessMasterActivity() {
        numOfEntities = 0;
        entities = new Entity[10];
        totaltickets = 0;
    }
    //Adds entities to entities array
    public void addEntity(Entity entity) {
        entities[numOfEntities++] = entity.clone();
    }

    //Calls playgame if only entitId passed
    public void playGame(int entityid) {
        Entity entity = entities[entityid];
        playGame(entity);
    }
    //Main play game code
    public void playGame(Entity entity) {
        //Name of the entity to be guessed in the entityName textview
        entityName.setText(entity.getName());
        //Get Input from the EdiText
        answer = userIn.getText().toString();
        answer = answer.replace("\n", "").replace("\r","");
        Date date = new Date(answer);
        //Check user date input
        if (date.precedes(entity.getBorn())) {
            //Alert Dialog for an early guess
            AlertDialog.Builder alert = new AlertDialog.Builder(GuessMasterActivity.this);
            alert.setTitle("Incorrect");
            alert.setMessage("Try a later date.");
            alert.setNegativeButton("Ok", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alert.show();
        } else if (entity.getBorn().precedes(date)) {
            //Alert Dialog for a late guess
            AlertDialog.Builder alert = new AlertDialog.Builder(GuessMasterActivity.this);
            alert.setTitle("Incorrect");
            alert.setMessage("Try an earlier date.");
            alert.setNegativeButton("Ok", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alert.show();
        } else { //When the guess is correct this code executes
            currentTicketsWon = entity.getAwardedTicketNumber();
            totaltickets += currentTicketsWon;
            String total = (new Integer(totaltickets)).toString();

            //AlertDialog to inform users that they have won
            AlertDialog.Builder alert = new AlertDialog.Builder(GuessMasterActivity.this);
            alert.setTitle("You won");
            alert.setMessage("BINGO! \n" + entity.closingMessage());
            alert.setCancelable(false);
            alert.setNegativeButton("Continue", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getBaseContext(), "You got" + total, Toast.LENGTH_SHORT).show();
                    continueGame(); //Changes entity
                }
            });
            ticketsum.setText("Total Tickets" + total);
            alert.show();
        }
    }

    //Clears userIn and changes the current entity
    public void continueGame(){
        changeEntity();
        //clear previous entry
        userIn.getText().clear();
    }

    // Picks random entity and then calls main playgame method
    public void playGame() {
        int entityid = genRandomentityid();
        playGame(entityid);
    }

    //Returns Id of entity in arr not out of bounds
    public int genRandomentityid() {
        Random randomNumber = new Random();
        return randomNumber.nextInt(numOfEntities);
    }

}
