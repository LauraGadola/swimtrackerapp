package cobaltix.internal_projects.swimtrackerapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

public class DailyGoalsActivity extends AppCompatActivity
{
    private ProgressBar pbMiles;
    private ProgressBar pbWeight;
    private ProgressBar pbLongest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_goals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pbMiles = (ProgressBar) findViewById(R.id.progressBarMiles);
        pbWeight = (ProgressBar) findViewById(R.id.progressBarWeight);
        pbLongest = (ProgressBar) findViewById(R.id.progressBarLongest);

        pbMiles.setProgress(25);
        pbWeight.setProgress(75);
        pbLongest.setProgress(40);
    }

}
