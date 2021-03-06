package com.vj.makosocial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import java.util.ArrayList;

import adapters.ListViewMakoAdapter;
import async.AsyncGetMakoEvents;
import dbObjects.MakoEvent;



public class NavDrawerActivity extends ActionBarActivity {
    private static String POS_TAG = "position";
    private Drawer.Result drawer_res;
    private Toolbar toolbar;
    private ShareActionProvider mShareActionProvider;
    private ListView lvMakoEvents;
    ArrayList<MakoEvent> mEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        parseInit();

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawer_res = getDrawerResult();

        //list view
        lvMakoEvents = (ListView) findViewById(R.id.lvMakoEvents);
        //set adapter
        ListViewMakoAdapter adapter = new ListViewMakoAdapter( this);
        lvMakoEvents.setAdapter(adapter);
        // download entities from Parse (asynctask)
        mEventList = new ArrayList<MakoEvent>();
        AsyncGetMakoEvents async = new AsyncGetMakoEvents(NavDrawerActivity.this,mEventList,adapter);
        async.execute();

        lvMakoEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NavDrawerActivity.this, DetailedViewActivity.class);
                intent.putExtra(POS_TAG, position);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_nav_drawer, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem share = menu.findItem(R.id.menu_action_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);
        setShareIntent(getShareIntent());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
           // return true;
        //}


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer_res.isDrawerOpen()) {
            drawer_res.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private Intent getShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    private void parseInit(){
        // test parse auth
        // Enable Local Datastore.
       // Parse.enableLocalDatastore(this);
        // init Parse session
        Parse.initialize(this, "vTOFv5b5IhCPhTrl0uqqCCXDiZSojjwt7FtzSMsU", "YAL4h7JMBz2gPClEnuQHXTyZv4R3YAnYV4Lt74JK");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }

    private Drawer.Result getDrawerResult(){
        /* Using Mike Penz material design library:
        * https://github.com/mikepenz/MaterialDrawer
        */

        Drawer.Result dr = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.nav_drawer_header)
                .addDrawerItems(
                        //"List view"
                        new PrimaryDrawerItem().withName(R.string.drawer_item_list_view)
                                .withIcon(FontAwesome.Icon.faw_list)
                                .withIdentifier(1),
                        //"Full view"
                        new PrimaryDrawerItem().withName(R.string.drawer_item_full_view)
                                .withIcon(FontAwesome.Icon.faw_square_o)
                                .withIdentifier(2),

                        //Divider line
                        new DividerDrawerItem(),

                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom1)
                                .withIcon(FontAwesome.Icon.faw_arrow_circle_right)
                                .withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom2)
                                .withIcon(FontAwesome.Icon.faw_arrow_circle_left)
                                .withIdentifier(4),
                        //new SectionDrawerItem()
                        //.withName(R.string.drawer_item_settings),

                        //Divider line
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName(R.string.drawer_item_help)
                                .withIcon(FontAwesome.Icon.faw_question_circle)
                                .withIdentifier(4)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // click handle
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            // Action on click
                            Toast.makeText(NavDrawerActivity.this, NavDrawerActivity.this.getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build();
        return dr;
    }
}
