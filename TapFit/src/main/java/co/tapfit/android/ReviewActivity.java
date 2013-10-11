package co.tapfit.android;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import co.tapfit.android.model.Place;
import co.tapfit.android.request.WorkoutRequest;

public class ReviewActivity extends BaseActivity {

    ImageView heart_1;
    ImageView heart_2;
    ImageView heart_3;
    ImageView heart_4;
    ImageView heart_5;

    EditText review_text;
    Button add_review;
    Place mPlace;
    TextView question_text;

    Integer rating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mPlace = dbWrapper.getPlace(getIntent().getIntExtra(PlaceInfoActivity.PLACE_ID, -1));

        setUpViews();
    }

    private void setUpViews() {

        question_text = (TextView) findViewById(R.id.question_text);
        question_text.setText("How was your workout at " + mPlace.name + "?");
        review_text = (EditText) findViewById(R.id.review_text);
        add_review = (Button) findViewById(R.id.add_review_button);
        add_review.setOnClickListener(addReview);

        heart_1 = (ImageView) findViewById(R.id.heart_1);
        heart_2 = (ImageView) findViewById(R.id.heart_2);
        heart_3 = (ImageView) findViewById(R.id.heart_3);
        heart_4 = (ImageView) findViewById(R.id.heart_4);
        heart_5 = (ImageView) findViewById(R.id.heart_5);

        heart_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heart_1.setImageResource(R.drawable.heart_red);
                heart_2.setImageResource(R.drawable.heart_clicked);
                heart_3.setImageResource(R.drawable.heart_clicked);
                heart_4.setImageResource(R.drawable.heart_clicked);
                heart_5.setImageResource(R.drawable.heart_clicked);
                rating = 1;
            }
        });

        heart_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heart_1.setImageResource(R.drawable.heart_red);
                heart_2.setImageResource(R.drawable.heart_red);
                heart_3.setImageResource(R.drawable.heart_clicked);
                heart_4.setImageResource(R.drawable.heart_clicked);
                heart_5.setImageResource(R.drawable.heart_clicked);
                rating = 2;
            }
        });

        heart_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heart_1.setImageResource(R.drawable.heart_red);
                heart_2.setImageResource(R.drawable.heart_red);
                heart_3.setImageResource(R.drawable.heart_red);
                heart_4.setImageResource(R.drawable.heart_clicked);
                heart_5.setImageResource(R.drawable.heart_clicked);
                rating = 3;
            }
        });

        heart_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heart_1.setImageResource(R.drawable.heart_red);
                heart_2.setImageResource(R.drawable.heart_red);
                heart_3.setImageResource(R.drawable.heart_red);
                heart_4.setImageResource(R.drawable.heart_red);
                heart_5.setImageResource(R.drawable.heart_clicked);
                rating = 4;
            }
        });

        heart_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heart_1.setImageResource(R.drawable.heart_red);
                heart_2.setImageResource(R.drawable.heart_red);
                heart_3.setImageResource(R.drawable.heart_red);
                heart_4.setImageResource(R.drawable.heart_red);
                heart_5.setImageResource(R.drawable.heart_red);
                rating = 5;
            }
        });
    }

    View.OnClickListener addReview = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Bundle args = new Bundle();
            args.putString(WorkoutRequest.REVIEW, review_text.getText().toString());
            args.putInt(WorkoutRequest.RATING, rating);
            WorkoutRequest.addReview(ReviewActivity.this, mPlace, args);
            finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.review, menu);
        return true;
    }
    
}
