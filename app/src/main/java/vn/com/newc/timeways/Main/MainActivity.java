package vn.com.newc.timeways.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import vn.com.newc.timeways.R;

public class MainActivity extends AppCompatActivity {

    private Toolbar tlbarMain;
    private TabLayout tabLayoutMain;
    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        handlePageChange();
    }

    private void handlePageChange() {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutMain));
        tabLayoutMain.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void init() {
        tlbarMain=(Toolbar) findViewById(R.id.toolbarMain);
        tabLayoutMain=(TabLayout) findViewById(R.id.tabLayoutMain);
        viewPager=(ViewPager) findViewById(R.id.viewPagerMain);
        setSupportActionBar(tlbarMain);
        tabLayoutMain.addTab(tabLayoutMain.newTab().setText("To do list"));
        tabLayoutMain.addTab(tabLayoutMain.newTab().setText("Pomodoro"));
        tabLayoutMain.addTab(tabLayoutMain.newTab().setText("Eisenhower"));
        tabLayoutMain.addTab(tabLayoutMain.newTab().setText("40-30-20-10"));
        tabLayoutMain.setTabGravity(TabLayout.GRAVITY_FILL);
        tabsAdapter=new TabsAdapter(getSupportFragmentManager(),tabLayoutMain.getTabCount(), this);
        viewPager.setAdapter(tabsAdapter);
    }
}
