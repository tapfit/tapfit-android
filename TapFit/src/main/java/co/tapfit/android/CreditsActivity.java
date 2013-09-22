package co.tapfit.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import co.tapfit.android.model.User;
import co.tapfit.android.request.ResponseCallback;
import co.tapfit.android.request.UserRequest;

public class CreditsActivity extends BaseActivity {

    private TextView mCreditAmount;
    private Button mApplyCodeButton;
    private EditText mPromoCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        User user = dbWrapper.getCurrentUser();
        mCreditAmount = (TextView) findViewById(R.id.credit_amount);
        mCreditAmount.setText("$" + Math.round(user.credit_amount));

        mApplyCodeButton = (Button) findViewById(R.id.button_apply_code);
        mApplyCodeButton.setOnClickListener(addCodeToUser);

        mPromoCodeText = (EditText) findViewById(R.id.promo_code);
    }

    private View.OnClickListener addCodeToUser = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ProgressDialog progressDialog = new ProgressDialog(CreditsActivity.this);
            progressDialog.setMessage("Adding code....");
            progressDialog.show();

            UserRequest.redeemPromoCode(getApplicationContext(), mPromoCodeText.getText().toString(), new ResponseCallback() {
                @Override
                public void sendCallback(Object responseObject, String message) {
                    progressDialog.cancel();
                    AlertDialog alertDialog = new AlertDialog.Builder(CreditsActivity.this)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            })
                            .create();

                    if (responseObject == null) {
                        alertDialog.setTitle("Error Redeeming Code");
                        alertDialog.setMessage(message);
                        alertDialog.show();
                    }
                    else
                    {
                        User user = dbWrapper.getCurrentUser();
                        mCreditAmount.setText("$" + Math.round(user.credit_amount));
                        alertDialog.setTitle("Success");
                        alertDialog.setMessage("You now have $" + Math.round(user.credit_amount) + " in credits.");
                        alertDialog.show();
                    }
                }
            });
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.credits, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
