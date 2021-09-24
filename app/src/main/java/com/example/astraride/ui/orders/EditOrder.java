package com.example.astraride.ui.orders;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.astraride.R;
import com.example.astraride.models.Order;
import com.example.astraride.ui.products.EditItem;
import com.example.astraride.ui.products.Inventory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditOrder extends AppCompatActivity {

    EditText name, email, phoneNo, address, pickupDate, returnDate;
    TextView totalCost;
    Button update, delete;
    String rental, currentUser, itemID, pDate, rDate, totalPrice, orderID;
    Order order;
    RadioButton cash, card;
    DatabaseReference dbf;
    DatePickerDialog picker;
    long duration;
    Date pickDate, retDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        order = (Order)intent.getSerializableExtra("order");
        orderID = order.getOrderId();

        //Initialize values
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        phoneNo = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        pickupDate = findViewById(R.id.PickupDate);
        returnDate = findViewById(R.id.ReturnDate);
        update = findViewById(R.id.btnSave);
        delete = findViewById(R.id.btnCancelOrder);
        cash = findViewById(R.id.radioCash);
        card = findViewById(R.id.radioCard);
        totalCost = findViewById(R.id.TotalCost);

        //Set values
        name.setText(order.getName());
        email.setText(order.getEmail());
        phoneNo.setText(order.getPhoneNo());
        address.setText(order.getAddress());
        pickupDate.setText(order.getPickupDate());
        returnDate.setText(order.getReturnDate());


        //Setup Date picker
        pickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                // date picker dialog
                picker = new DatePickerDialog(EditOrder.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                pDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                                try {
                                    pickDate = new SimpleDateFormat("dd/MM/yyyy").parse(pDate);
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                pickupDate.setText(pDate);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(EditOrder.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                rDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                //Calculate total cost
                                try {
                                    retDate = new SimpleDateFormat("dd/MM/yyyy").parse(rDate);
                                }catch (Exception e){e.printStackTrace();}

                                calculate_cost();
                                returnDate.setText(rDate);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        //When delete button pressed
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }


    //Save values
    public void save(){

        ProgressDialog pd = new ProgressDialog(EditOrder.this);
        pd.setMessage("Uploading...");
        pd.show();

        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());

        order = new Order();

        //Save data in model object
        order.setName(name.getText().toString().trim());
        order.setEmail(email.getText().toString().trim());
        order.setPhoneNo(phoneNo.getText().toString().trim());
        order.setAddress(address.getText().toString().trim());
        order.setPickupDate(pickupDate.getText().toString().trim());
        order.setReturnDate(returnDate.getText().toString().trim());
        order.setOrderId(orderID);
        order.setBuyerId(currentUser);
        order.setItemID(itemID);
        order.setOrderDate(date);
        order.setCost(totalPrice);
        order.setItemID(itemID);

        //Save to database
        try {
            dbf = FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUser).child(orderID);
            dbf.setValue(order);
            Toast.makeText(EditOrder.this, "Order Updated", Toast.LENGTH_SHORT).show();
        }
        catch(DatabaseException error){
            Log.e("DBError", error.getMessage());
        }
        pd.cancel();
    }

    //Convert string to data
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void calculate_cost() {

        LocalDate date1 = pickDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = retDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Period diff = Period.between(date1, date2);
        int hrs = diff.getDays() * 24;

        totalPrice = Long.toString(hrs * Long.parseLong(rental)); //Calculate cost
        totalCost.setText("Total Cost: Rs." + totalPrice);

    }

    public void delete(View view){
        //Confirm delete
        AlertDialog.Builder alert = new AlertDialog.Builder(EditOrder.this);
        alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to delete?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                try {
                    dbf = FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUser).child(orderID);
                    dbf.removeValue(); //Delete

                    Intent intent = new Intent(EditOrder.this, Inventory.class);
                    startActivity(intent);

                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }
}