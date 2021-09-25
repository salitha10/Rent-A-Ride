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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.astraride.MainActivity;
import com.example.astraride.R;
import com.example.astraride.models.Order;
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


    @RequiresApi(api = Build.VERSION_CODES.O)
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
        totalCost.setText("Total cost: " + order.getCost());
        itemID = order.getItemID();

        rental = order.getCost();

        try {
            pickDate = new SimpleDateFormat("dd/MM/yyyy").parse(order.getPickupDate());
            retDate = new SimpleDateFormat("dd/MM/yyyy").parse(order.getReturnDate());
        }   catch (Exception e){e.printStackTrace();}

        calc_item_Cost();

        //Disable keyboard
        pickupDate.setShowSoftInputOnFocus(false);
        pickupDate.setFocusable(false);
        returnDate.setShowSoftInputOnFocus(false);
        returnDate.setFocusable(false);

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

                                //Calculate cost
                                if(!TextUtils.isEmpty(returnDate.getText())){
                                    calculate_cost();
                                }
                            }
                        }, year, month, day);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
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

                                returnDate.setText(rDate);

                                if(!TextUtils.isEmpty(pickupDate.getText())){
                                    calculate_cost();
                                }

                            }
                        }, year, month, day);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
            }
        });

        //When delete button pressed
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    save();
                    finish();
                } else {
                    Toast.makeText(EditOrder.this, "Please fill details", Toast.LENGTH_SHORT).show();
                }
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

    //Calculate cost
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void calculate_cost() {

        LocalDate date1 = pickDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = retDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Period diff = Period.between(date1, date2);
        int hrs = diff.getDays() * 24;

        if(hrs == 0){hrs = 1;};

        totalPrice = Long.toString(hrs * Long.parseLong(rental)); //Calculate cost
        totalCost.setText("Total Cost: Rs." + totalPrice);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void calc_item_Cost(){

        LocalDate date1 = pickDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = retDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Period diff = Period.between(date1, date2);
        int hrs = diff.getDays() * 24;

        if(hrs == 0){hrs = 1;};

        rental = Long.toString(Long.parseLong(rental) / hrs); //Calculate cost
    }

    public void delete(View view){
        //Confirm delete
        AlertDialog.Builder alert = new AlertDialog.Builder(EditOrder.this);
        alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to cancel?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                try {
                    dbf = FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUser).child(orderID);
                    dbf.removeValue(); //Delete

                    finish();

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

    //Validation
    public boolean validate(){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(name.getText())) {
            name.setError("Please Enter a name!");
        }
        else if (TextUtils.isEmpty(email.getText())) {
            email.setError("Please Enter a email!");
        }
        else if(!email.getText().toString().matches(emailPattern)){
            email.setError("Please Enter valid email!");
        }
        else if (TextUtils.isEmpty(phoneNo.getText())) {
            phoneNo.setError("Please Enter a phoneNo!");
        }
        else if(phoneNo.getText().toString().length() < 10 || phoneNo.getText().toString().length() > 10){
            phoneNo.setError("Please Enter a valid phoneNo!");
        }
        else if (TextUtils.isEmpty(address.getText())) {
            address.setError("Please Enter a address!");
        }
        else if (TextUtils.isEmpty(pickupDate.getText())) {
            pickupDate.setError("Please Enter a pickup date!");
        }
        else if (TextUtils.isEmpty(returnDate.getText())) {
            returnDate.setError("Please Enter a return date!");
        }
        else{
            return true;
        }
        Toast.makeText(getApplicationContext(), "Fields can't be empty", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}