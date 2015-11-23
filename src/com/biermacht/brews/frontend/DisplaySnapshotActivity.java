package com.biermacht.brews.frontend;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.biermacht.brews.R;
import com.biermacht.brews.database.DatabaseAPI;
import com.biermacht.brews.frontend.IngredientActivities.AddFermentableActivity;
import com.biermacht.brews.frontend.IngredientActivities.AddHopsActivity;
import com.biermacht.brews.frontend.IngredientActivities.AddMiscActivity;
import com.biermacht.brews.frontend.IngredientActivities.AddYeastActivity;
import com.biermacht.brews.frontend.IngredientActivities.EditRecipeActivity;
import com.biermacht.brews.frontend.adapters.DisplaySnapshotCollectionPagerAdapter;
import com.biermacht.brews.ingredient.Ingredient;
import com.biermacht.brews.recipe.Recipe;
import com.biermacht.brews.recipe.RecipeSnapshot;
import com.biermacht.brews.utils.AlertBuilder;
import com.biermacht.brews.utils.Constants;

public class DisplaySnapshotActivity extends AppCompatActivity {

  private RecipeSnapshot mSnapshot;
  private int currentItem;
  DisplaySnapshotCollectionPagerAdapter cpAdapter;
  ViewPager mViewPager;
  ViewPager.OnPageChangeListener pageListener;
  Menu menu;

  // Alert builder
  public AlertBuilder alertBuilder;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_display_snapshot);

    // Set icon as back button
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Get snapshot from intent
    mSnapshot = getIntent().getParcelableExtra(Constants.KEY_SNAPSHOT);

    // Create alert builder with no callback.
    alertBuilder = new AlertBuilder(this, null);

    // Set on page change listener
    pageListener = new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float offset, int offsetPixels) {
      }

      @Override
      public void onPageSelected(int position) {
        updateOptionsMenu();
      }

      @Override
      public void onPageScrollStateChanged(int state) {
      }
    };

    // Set to the first item.
    currentItem = 0;

    // Update user interface
    updateUI();
  }

  /**
   * TODO: This creates an entire new pager adapter adapter in order to update the UI.  It would be
   * nice if we could just update things in place, without having to create / destroy so many
   * objects.
   */
  public void updatePagerAdater() {
    cpAdapter = new DisplaySnapshotCollectionPagerAdapter(getSupportFragmentManager(), mSnapshot, getApplicationContext());

    // Set Adapter and onPageChangeListener.
    mViewPager = (ViewPager) findViewById(R.id.pager);
    mViewPager.setAdapter(cpAdapter);
    mViewPager.addOnPageChangeListener(pageListener);

    // Set the current item
    mViewPager.setCurrentItem(currentItem);
  }

  /**
   * Updates the UI after (potentially) changes have been made to the Snapshot being viewed.
   */
  private void updateUI() {
    // Update the PagerAdapter.
    updatePagerAdater();

    // Set title based on snapshot date
    setTitle(mSnapshot.getDescription());

    // Update which options menu is displayed.
    updateOptionsMenu();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    this.menu = menu;
    menu.removeItem(R.id.menu_new_snapshot);
    menu.removeItem(R.id.menu_add_ing);
    menu.removeItem(R.id.menu_edit_recipe);
    menu.removeItem(R.id.menu_timer);
    menu.removeItem(R.id.menu_profile_dropdown);

    switch (mViewPager.getCurrentItem()) {
      case 0:
        getMenuInflater().inflate(R.menu.fragment_snapshots_menu, menu);
        break;
      case 1:
        getMenuInflater().inflate(R.menu.fragment_ingredient_menu, menu);
        break;
      case 2:
        getMenuInflater().inflate(R.menu.fragment_instruction_menu, menu);
        break;
      case 3:
        getMenuInflater().inflate(R.menu.fragment_details_menu, menu);
        break;
      case 4:
        getMenuInflater().inflate(R.menu.fragment_profile_menu, menu);
        if (mSnapshot.getType().equals(Recipe.EXTRACT)) {
          menu.findItem(R.id.menu_edit_mash_profile).setVisible(false);
        }
        break;
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent i;

    // Pass the given event to the currently selected Fragment to handle.  If handled, return.
    if (cpAdapter.getItem(mViewPager.getCurrentItem()).onOptionsItemSelected(item)) {
      return true;
    }

    // Otherwise, switch on the item ID.
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;

      case R.id.add_fermentable:
        i = new Intent(this.getApplicationContext(), AddFermentableActivity.class);
        i.putExtra(Constants.KEY_RECIPE, mSnapshot);
        startActivity(i);
        return true;

      case R.id.add_hop:
        i = new Intent(getApplicationContext(), AddHopsActivity.class);
        i.putExtra(Constants.KEY_RECIPE, mSnapshot);
        startActivity(i);
        return true;

      case R.id.add_yeast:
        i = new Intent(getApplicationContext(), AddYeastActivity.class);
        i.putExtra(Constants.KEY_RECIPE, mSnapshot);
        startActivity(i);
        return true;

      case R.id.add_misc:
        i = new Intent(getApplicationContext(), AddMiscActivity.class);
        i.putExtra(Constants.KEY_RECIPE, mSnapshot);
        startActivity(i);
        return true;

      case R.id.menu_timer:
        // If we have no instructions, don't go to timer.
        if (mSnapshot.getInstructionList().size() == 0) {
          String msg = "Brew timer requires some instructions.  Add to your snapshot to generate instructions!";
          alertBuilder.notification("No Instructions", msg).show();
          return false;
        }

        // We have instructions, can go to timer.
        i = new Intent(getApplicationContext(), BrewTimerActivity.class);
        i.putExtra(Constants.KEY_RECIPE, mSnapshot);
        startActivity(i);
        return true;

      case R.id.menu_edit_recipe:
        i = new Intent(getApplicationContext(), EditRecipeActivity.class);
        i.putExtra(Constants.KEY_RECIPE, mSnapshot);
        startActivity(i);
        return true;

      case R.id.menu_edit_mash_profile:
        i = new Intent(getApplicationContext(), EditMashProfileActivity.class);
        i.putExtra(Constants.KEY_RECIPE, mSnapshot);
        i.putExtra(Constants.KEY_PROFILE_ID, mSnapshot.getMashProfile().getId());
        i.putExtra(Constants.KEY_PROFILE, mSnapshot.getMashProfile());
        startActivity(i);
        return true;

      case R.id.menu_edit_fermentation_profile:
        i = new Intent(getApplicationContext(), EditFermentationProfileActivity.class);
        i.putExtra(Constants.KEY_RECIPE, mSnapshot);
        startActivity(i);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  public void onResume() {
    super.onResume();
    Log.d("DisplaySnapshotActivity", "onResume: Getting snapshot from database");
    // Changes may have been made to this Snapshot in another activity - get the Snapshot
    // from the database and update the UI.
    try {
      mSnapshot = DatabaseAPI.getSnapshot(mSnapshot.getId());
    } catch (Exception e) {
      e.printStackTrace();
    }

    updateUI();
  }

  public void updateOptionsMenu() {
    if (menu != null) {
      onCreateOptionsMenu(menu);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    // Save the current page we're looking at
    this.currentItem = mViewPager.getCurrentItem();
  }

  public void onButtonClick(View v) {
    switch (v.getId()) {
       case R.id.date_picker:
         // The date picker button has been pressed - pass to AboutSnapshotFragment
         Log.d("DisplaySnapshotActivity", "Date-picker button selected");
         this.cpAdapter.aboutFragment.showDatePicker(v);
         return;
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }
}