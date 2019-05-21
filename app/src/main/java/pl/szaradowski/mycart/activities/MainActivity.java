package pl.szaradowski.mycart.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.fragments.ReceiptsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new ReceiptsFragment());
    }

    public void loadFragment(Fragment newFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.body_fragment_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
