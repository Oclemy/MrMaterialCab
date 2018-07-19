package com.devosha.mrmaterialcab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.afollestad.materialcab.MaterialCab;
import java.lang.reflect.Field;

/*
MainActivity class. Is implementing two Callback interfaces from both MainAdapter and MaterialAdapter
 */
public class MainActivity extends AppCompatActivity implements RecyclerAdapter.Callback, MaterialCab.Callback {

  // Fields
  private RecyclerAdapter adapter;
  private MaterialCab cab;
  private Toast toast;

  /*
  Our onCreate method
    1. Reference RecyclerView then Initialize RecyclerAdapter
    2. Set RecyclerView LayoutManager.
    3. Set toolbar
    4. If MaterialCab instance state is not null then restore it
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    adapter = new RecyclerAdapter(this);
    RecyclerView list = (RecyclerView) findViewById(R.id.list);
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(adapter);
    Toolbar toolbar=findViewById(R.id.main_toolbar);
    toolbar.setBackgroundColor(Color.parseColor("#f39c12"));

    setSupportActionBar(toolbar);

    if (savedInstanceState != null) {
      cab = MaterialCab.restoreState(savedInstanceState, this, this);
      adapter.restoreState(savedInstanceState);
    } else {
      String[] spacecrafts =("Atlantis Casini Spitzer Chandra Galileo Kepler Wise Apollo Saturn-5 Hubble Challenger" +
              " James-Web Huygens Enterprise New-Horizon Opportunity Pioneer Curiosity Spirit Orion Mars-Explorer WMAP Columbia Voyager Juno").split(" ");
      for (String spacecraft: spacecrafts) {
        adapter.add(spacecraft);
      }
    }
  }

  /*
  Save MaterialCab instance state if it's not null.
   */
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (cab != null) {
      cab.saveState(outState);
    }
    if (adapter != null) {
      adapter.saveState(outState);
    }
  }

  /*
  Show Toast message.
   */
  private void showToast(String text) {
    if (toast != null) {
      toast.cancel();
    }
    toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
    toast.show();
  }

  /*
  Determine Clicks
   */
  @Override
  public void onItemClicked(int index, boolean longClick) {
    if (longClick || (cab != null && cab.isActive())) {
      onIconClicked(index);
      return;
    }
    showToast(adapter.getItem(index));
  }

  /*
  Menu icon clicked
   */
  @Override
  public void onIconClicked(int index) {
    adapter.toggleSelected(index);
    if (adapter.getSelectedCount() == 0) {
      cab.finish();
      return;
    }
    if (cab == null) {
      cab = new MaterialCab(this, R.id.cab_stub).start(this);
	  cab.getToolbar().setTitleTextColor(Color.BLACK);
	  cab.getToolbar().setBackgroundColor(Color.parseColor("#f39c12"));
    } else if (!cab.isActive()) {
      cab.reset().start(this);
	  cab.getToolbar().setTitleTextColor(Color.BLACK);
	  cab.getToolbar().setBackgroundColor(Color.parseColor("#f39c12"));
    }

    cab.setTitle(getString(R.string.x_selected,adapter.getSelectedCount()));
  }

  /*
  When MaterialCab has just been created.
   */
  @Override
  public boolean onCabCreated(@NonNull MaterialCab cab, Menu menu) {
    // Makes the icons in the overflow menu visible
    if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
      try {
        Field field = menu.getClass().getDeclaredField("mOptionalIconsVisible");
        field.setAccessible(true);
        field.setBoolean(menu, true);
      } catch (Exception ignored) {
        ignored.printStackTrace();
      }
    }
    return true; // allow creation
  }

  /*
  When a Material Cab is clicked show a Toast message.
   */
  @Override
  public boolean onCabItemClicked(@NonNull MenuItem item) {
    showToast((String) item.getTitle());
    return true;
  }

  /*
  Cleared adapter when Contextual ActionBar is being destroyed
   */
  @Override
  public boolean onCabFinished(@NonNull MaterialCab cab) {
    adapter.clearSelected();
    return true; // allow destruction
  }

  /*
  When user clicks Back button finish Contextual ActionBar
   */
  @Override
  public void onBackPressed() {
    if (cab != null && cab.isActive()) {
      cab.finish();
      cab = null;
    } else {
      super.onBackPressed();
    }
  }
}
