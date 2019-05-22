/*
 * Created by Dominik Szaradowski on 22.05.19 11:20
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.activities.MainActivity;

public class Screen {
    private static String actualScreen = null;
    private static ArrayList<Screen> screens = new ArrayList<>();

    private Context ctx;
    private Fragment fragment;
    private String tag;
    private Screen back;

    public Screen(Context ct, Fragment fr, String tg, Screen bck){
        ctx = ct;
        fragment = fr;
        tag = tg;
        back = bck;

        screens.add(this);
    }

    public void Load(){
        if(actualScreen == null || !actualScreen.equals(tag)){
            FragmentManager manager = ((MainActivity) ctx).getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.body_fragment_container, fragment, tag);

            transaction.disallowAddToBackStack();

            transaction.commit();
            actualScreen = tag;
        }
    }

    public String getTag(){
        return tag;
    }

    public Screen getBackScreen(){
        return back;
    }

    public static void onBack(Context ctx){
        for (Screen s : screens) {
            if(s.getTag().equals(actualScreen)){
                Screen bck = s.getBackScreen();

                if(bck != null){
                    bck.Load();
                }else{
                    ((MainActivity) ctx).moveTaskToBack(true);
                }
            }
        }
    }
}
