package vn.com.newc.timeways.Main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import vn.com.newc.timeways.Eisenhower.FragmentEisenhower;
import vn.com.newc.timeways.Pomodoro.FragmentPomodoro;
import vn.com.newc.timeways.ToDoList.FragmentToDoList;
import vn.com.newc.timeways._40302010.Fragment40302010;

public class TabsAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;
    private Context context;

    public TabsAdapter(@NonNull FragmentManager fm, int numOfTabs, Context context) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FragmentToDoList fragmentToDoList=new FragmentToDoList();
                return fragmentToDoList;
            case 1:
                FragmentPomodoro fragmentPomodoro=new FragmentPomodoro();
                return fragmentPomodoro;
            case 2:
                FragmentEisenhower fragmentEisenhower=new FragmentEisenhower();
                return fragmentEisenhower;
            case 3:
                Fragment40302010 fragment40302010=new Fragment40302010();
                return fragment40302010;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }


}
