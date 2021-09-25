package com.example.astraride.ui.orders;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.astraride.MainActivity;
import com.example.astraride.R;
import com.example.astraride.models.Order;
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

public class Checkout extends AppCompatActivity {

    EditText name, email, phoneNo, address, pickupDate, returnDate;
    TextView totalCost;
    Button checkout;
    String rental, currentUser, itemID, pDate, rDate, totalPrice;

    Order order;
    RadioButton cash, card;
    DatabaseReference dbf;
    DatePickerDialog picker;
    long duration;
    Date pickDate, retDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent = getIntent();
        itemID = intent.getStringExtra("itemId");
        rental = intent.getStringExtra("rental");
        Log.d("rental", rental);

        //Initialize values
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        phoneNo = findViewById(R.id.editTextPhone);
        address = findViewById(R.id.editTextAddress);
        pickupDate = findViewById(R.id.PickupDate);
        returnDate = findViewById(R.id.ReturnDate);
        checkout = findViewById(R.id.btnPay);
        cash = findViewById(R.id.radioCash);
        card = findViewById(R.id.radioCard);
        totalCost = findViewById(R.id.TotalCost);

        //Disable keyboard
        pickupDate.setShowSoftInputOnFocus(false);
        pickupDate.setFocusable(false);
        returnDate.setShowSoftInputOnFocus(false);
        returnDate.setFocusable(false);

        //Setup Date picker
        pickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // date picker dialog
                picker = new DatePickerDialog(Checkout.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
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

                                if(!TextUtils.isEmpty(returnDate.getText())){
                                    calculate_cost();
                                }
                                pickupDate.setText(pDate);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(Checkout.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                rDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                //Calculate total cost
                                try {
                                    retDate = new SimpleDateFormat("dd/MM/yyyy").parse(rDate);
                                }catch (Exception e){e.printStackTrace();}

                                //Calculate cost
                                if(!TextUtils.isEmpty(pickupDate.getText())){
                                    calculate_cost();
                                }
                                returnDate.setText(rDate);
                            }
                        }, year, month, day);

                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
            }
        });

        //When checkout button pressed
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    //Check radio button click
                    if (cash.isChecked()) {
                        save();
                        Intent intent = new Intent(Checkout.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        showPayment();
                    }
                }
                else{
                    Toast.makeText(Checkout.this, "Please fill details", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    //Show payment screen
    private void showPayment() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.activity_payment);

        LinearLayout layout = bottomSheetDialog.findViewById(R.id.bottom_sheet_payment);
        Button pay = bottomSheetDialog.findViewById(R.id.btnPay);
        TextView amount = bottomSheetDialog.findViewById(R.id.payment_amount);
        amount.setText("LKR." + totalPrice);

        //When pay button is clicked
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
                bottomSheetDialog.cancel();
                Intent intent = new Intent(Checkout.this, MainActivity.class);
                startActivity(intent);

            }
        });

        bottomSheetDialog.show();
    }

    //Save values
    public void save(){

        ProgressDialog pd = new ProgressDialog(Checkout.this);
        pd.setMessage("Placing order...");
        pd.show();
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String time = Long.toString(System.currentTimeMillis());
        order = new Order();
        dbf = FirebaseDatabase.getInstance().getReference().child("Orders").child(currentUser);
        String id = dbf.push().getKey();

        //Save data in model object
        order.setName(name.getText().toString().trim());
        order.setEmail(email.getText().toString().trim());
        order.setPhoneNo(phoneNo.getText().toString().trim());
        order.setAddress(address.getText().toString().trim());
        order.setPickupDate(pickupDate.getText().toString().trim());
        order.setReturnDate(returnDate.getText().toString().trim());
        order.setOrderId(id);
        order.setBuyerId(currentUser);
        order.setItemID(itemID);
        order.setOrderDate(date);
        order.setCost(totalPrice);

        //Save to database
        try {
            dbf.child(id).setValue(order);
            Toast.makeText(Checkout.this, "Order Placed", Toast.LENGTH_SHORT).show();
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

    //Validation
    public boolean validate(){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String phonePattern = "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$";

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
        else if(phoneNo.getText().toString().length() < 10 || phoneNo.getText().toString().length() > 10 ){
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