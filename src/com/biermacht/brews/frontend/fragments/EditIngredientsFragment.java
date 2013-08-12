package com.biermacht.brews.frontend.fragments;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.biermacht.brews.R;
import com.biermacht.brews.frontend.IngredientActivities.EditCustomFermentableActivity;
import com.biermacht.brews.frontend.IngredientActivities.EditHopActivity;
import com.biermacht.brews.frontend.IngredientActivities.EditMiscActivity;
import com.biermacht.brews.frontend.IngredientActivities.EditYeastActivity;
import com.biermacht.brews.frontend.adapters.CustomIngredientArrayAdapter;
import com.biermacht.brews.ingredient.Ingredient;
import com.biermacht.brews.utils.Constants;
import com.biermacht.brews.utils.Database;

import android.content.*;

import com.biermacht.brews.utils.comparators.IngredientComparator;

public class EditIngredientsFragment extends Fragment {

    private static int resource = R.layout.fragment_ingredient_view;;
    private OnItemClickListener mClickListener;
    private ListView ingredientListView;
    private ArrayList<Ingredient> ingredientList;
    View pageView;
    Context c;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        pageView = inflater.inflate(resource, container, false);

        setHasOptionsMenu(true);

        // Context
        c = getActivity();

        // Get ingredient list
        ingredientList = Database.getIngredientsFromVirtualDatabase(Constants.INGREDIENT_DB_CUSTOM);
        Collections.sort(ingredientList, new IngredientComparator());

        // Initialize important junk
        ingredientListView = (ListView) pageView.findViewById(R.id.ingredient_list);

        // Set up the onClickListener
        mClickListener = new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parentView, View childView, int pos, long id)
            {
                Ingredient ing = ingredientList.get(pos);

                // Grain pressed
                if (ing.getType().equals(Ingredient.FERMENTABLE))
                {
                    Intent editGrainIntent = new Intent(c, EditCustomFermentableActivity.class);
                    editGrainIntent.putExtra(Constants.INTENT_RECIPE_ID, Constants.MASTER_RECIPE_ID);
                    editGrainIntent.putExtra(Constants.INTENT_INGREDIENT_ID, ing.getId());
                    startActivity(editGrainIntent);
                }

                // Hop Pressed
                if (ing.getType().equals(Ingredient.HOP))
                {
                    Intent editHopIntent = new Intent(c, EditHopActivity.class);
                    editHopIntent.putExtra(Constants.INTENT_RECIPE_ID, Constants.MASTER_RECIPE_ID);
                    editHopIntent.putExtra(Constants.INTENT_INGREDIENT_ID, ing.getId());
                    editHopIntent.putExtra(Constants.INTENT_INGREDIENT, ing);
                    startActivity(editHopIntent);
                }

                // Yeast Pressed
                if (ing.getType().equals(Ingredient.YEAST))
                {
                    Intent editYeastIntent = new Intent(c, EditYeastActivity.class);
                    editYeastIntent.putExtra(Constants.INTENT_RECIPE_ID, Constants.MASTER_RECIPE_ID);
                    editYeastIntent.putExtra(Constants.INTENT_INGREDIENT_ID, ing.getId());
                    startActivity(editYeastIntent);
                }

                // Misc Pressed
                if (ing.getType().equals(Ingredient.MISC))
                {
                    Intent editMiscIntent = new Intent(c, EditMiscActivity.class);
                    editMiscIntent.putExtra(Constants.INTENT_RECIPE_ID, Constants.MASTER_RECIPE_ID);
                    editMiscIntent.putExtra(Constants.INTENT_INGREDIENT_ID, ing.getId());
                    editMiscIntent.putExtra(Constants.INTENT_INGREDIENT, ing);
                    startActivity(editMiscIntent);
                }
            }
        };

        // Set whether or not we show the list view
        if (ingredientList.size() > 0)
        {
            CustomIngredientArrayAdapter ingredientArrayAdapter = new CustomIngredientArrayAdapter(c, ingredientList);
            ingredientListView.setVisibility(View.VISIBLE);
            ingredientListView.setAdapter(ingredientArrayAdapter);
            registerForContextMenu(ingredientListView);
            ingredientListView.setOnItemClickListener(mClickListener);
        }
        else
        {
            TextView noIngredientsView = (TextView) pageView.findViewById(R.id.no_ingredients_view);
            noIngredientsView.setVisibility(View.VISIBLE);
        }

        return pageView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_ingredient_menu, menu);
    }

}