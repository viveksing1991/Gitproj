package com.example.root.testproject.ui.activities.base.navigationaction;

        import android.support.annotation.LayoutRes;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Gravity;
        import android.view.View;
        import android.widget.FrameLayout;
        import  com.example.root.testproject.R;

/**
 * Created by user on 13/4/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract void setListeners(); //method for set  the listeners

    protected abstract void setReferences(); // method for set the reference

    protected abstract void init();// method for initilization

}
