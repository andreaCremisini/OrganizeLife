package com.example.andrea.organizelife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by andrea on 23/04/2016.
 */
public class DetailActivity extends Activity implements View.OnClickListener {

  public static final String EXTRA_PARAM_ID = "place_id";
  public static final String NAV_BAR_VIEW_NAME = Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME;
  private ListView mList;
  private ImageView mImageView;
  private TextView mTitle;
  private LinearLayout mTitleHolder;
  private Palette mPalette;
  private ImageButton mAddButton;
  private Animatable mAnimatable;
  private LinearLayout mRevealView;
  private EditText mEditTextTodo;
  private boolean isEditTextVisible;
  private InputMethodManager mInputManager;
  private Category mCategory;
  private ArrayList<String> mTodoList;
  private ArrayAdapter mToDoAdapter;
  int defaultColorForRipple;
  private final static String MY_PREFERENCES = "MyPref";
  SharedPreferences prefs;
  SharedPreferences.Editor editor;
  TextView textInfo;
  Set<String> list_items = new HashSet<String>();


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
    mCategory = CategoryData.placeList().get(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

    mList = (ListView) findViewById(R.id.list);
    mImageView = (ImageView) findViewById(R.id.placeImage);
    mTitle = (TextView) findViewById(R.id.textView);
    mTitleHolder = (LinearLayout) findViewById(R.id.placeNameHolder);
    mAddButton = (ImageButton) findViewById(R.id.btn_add);
    mRevealView = (LinearLayout) findViewById(R.id.llEditTextHolder);
    mEditTextTodo = (EditText) findViewById(R.id.etTodo);
    mAddButton.setImageResource(R.drawable.icn_morph_reverse);
    mAddButton.setOnClickListener(this);
    defaultColorForRipple = getResources().getColor(R.color.primary_dark);
    mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    mRevealView.setVisibility(View.INVISIBLE);
    isEditTextVisible = false;
    textInfo = (TextView)findViewById(R.id.single);
    mTodoList = new ArrayList<String>();
    mTodoList.addAll(prefs.getStringSet(mCategory.name,new HashSet<String>()));
    mToDoAdapter = new ArrayAdapter(this, R.layout.row_todo, mTodoList);
    mList.setAdapter(mToDoAdapter);

    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view,
                              final int position, long id) {

        new AlertDialog.Builder(DetailActivity.this)
                .setTitle(mTodoList.get(position))
                .setMessage(mCategory.name)
                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mCategory.name + " note, from OrganizeLife");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mTodoList.get(position) + " - " + mCategory.name + " note, from OrganizeLife");
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));                }
                })
                .setNeutralButton("Done!",
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {

                    new AlertDialog.Builder(DetailActivity.this)
                            .setTitle("Task completed!")
                            .setMessage("You finish the task?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int which) {
                                mTodoList.remove(position);
                                Set<String> newList = new HashSet<String>();
                                newList.addAll(mTodoList);
                                prefs.edit().putStringSet(mCategory.name, newList).commit();
                                mToDoAdapter = new ArrayAdapter(DetailActivity.this, R.layout.row_todo, mTodoList);
                                mList.setAdapter(mToDoAdapter);
                                Toast.makeText(DetailActivity.this, "Task completed!", Toast.LENGTH_LONG).show();                              }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                              }
                            })
                            .setIcon(android.R.drawable.checkbox_on_background)
                            .show();
                  }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    new AlertDialog.Builder(DetailActivity.this)
                            .setTitle(mTodoList.get(position))
                            .setMessage(mCategory.name)
                            .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int which) {
                                setElement(position, mTodoList.get(position));
                              }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(DetailActivity.this)
                                        .setTitle("Delete item")
                                        .setMessage("Are you sure you want to delete this entry?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                          public void onClick(DialogInterface dialog, int which) {
                                            mTodoList.remove(position);
                                            Set<String> newList = new HashSet<String>();
                                            newList.addAll(mTodoList);
                                            prefs.edit().putStringSet(mCategory.name, newList).commit();
                                            mToDoAdapter = new ArrayAdapter(DetailActivity.this, R.layout.row_todo, mTodoList);
                                            mList.setAdapter(mToDoAdapter);
                                            Toast.makeText(DetailActivity.this, "Delete item", Toast.LENGTH_LONG).show();                              }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                          public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                          }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                              }
                            })
                            .show();
                  }
                })
                .show();
      }
    });

    loadPlace();
    windowTransition();
    getPhoto();
  }

  private void setElement(final int position, final String name){
    LayoutInflater inflater = LayoutInflater.from(DetailActivity.this);
    View subView = inflater.inflate(R.layout.dialog_layout, null);
    final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(name);
    builder.setMessage("How do you want to rename the item?");
    builder .setView(subView);
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        mTodoList.set(position,subEditText.getText().toString());
        Set<String> newList = new HashSet<String>();
        newList.addAll(mTodoList);
        prefs.edit().putStringSet(mCategory.name, newList).commit();
        mToDoAdapter = new ArrayAdapter(DetailActivity.this, R.layout.row_todo, mTodoList);
        mToDoAdapter.notifyDataSetChanged();
        mList.setAdapter(mToDoAdapter);
        Toast.makeText(DetailActivity.this, "Rename item", Toast.LENGTH_LONG).show();
      }
    });

    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(DetailActivity.this, "Cancel", Toast.LENGTH_LONG).show();
      }
    });

    builder.show();
  }

  private void loadPlace() {
    mTitle.setText(mCategory.name);
    mImageView.setImageResource(mCategory.getImageResourceId(this));
  }

  private void windowTransition() {
    getWindow().setEnterTransition(makeEnterTransition());
    getWindow().getEnterTransition().addListener(new CategoryAdapter() {
      @Override
      public void onTransitionEnd(Transition transition) {
        mAddButton.animate().alpha(1.0f);
        getWindow().getEnterTransition().removeListener(this);
      }
    });
  }

  public static Transition makeEnterTransition() {
    Transition fade = new Fade();
    fade.excludeTarget(android.R.id.navigationBarBackground, true);
    fade.excludeTarget(android.R.id.statusBarBackground, true);
    return fade;
  }

  private void addToDo(String todo) {
    mTodoList.add(todo);
  }

  private void getPhoto() {
    Bitmap photo = BitmapFactory.decodeResource(getResources(), mCategory.getImageResourceId(this));
    colorize(photo);
  }

  private void colorize(Bitmap photo) {
    mPalette = Palette.generate(photo);
    applyPalette();
  }

  private void applyPalette() {
    getWindow().setBackgroundDrawable(new ColorDrawable(mPalette.getDarkMutedColor(defaultColorForRipple)));
    mTitleHolder.setBackgroundColor(mPalette.getMutedColor(defaultColorForRipple));
    applyRippleColor(mPalette.getVibrantColor(defaultColorForRipple),
            mPalette.getDarkVibrantColor(defaultColorForRipple));
    mRevealView.setBackgroundColor(mPalette.getLightVibrantColor(defaultColorForRipple));
  }

  private void applyRippleColor(int bgColor, int tintColor) {
    colorRipple(mAddButton, bgColor, tintColor);
  }

  private void colorRipple(ImageButton id, int bgColor, int tintColor) {
    View buttonView = id;
    RippleDrawable ripple = (RippleDrawable) buttonView.getBackground();
    GradientDrawable rippleBackground = (GradientDrawable) ripple.getDrawable(0);
    rippleBackground.setColor(bgColor);
    ripple.setColor(ColorStateList.valueOf(tintColor));
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_add:
        if (!isEditTextVisible) {
          mEditTextTodo.setText("");
          revealEditText(mRevealView);
          mEditTextTodo.requestFocus();
          mInputManager.showSoftInput(mEditTextTodo, InputMethodManager.SHOW_IMPLICIT);
          mAddButton.setImageResource(R.drawable.icn_morp);
          mAnimatable = (Animatable) (mAddButton).getDrawable();
          mAnimatable.start();
          applyRippleColor(getResources().getColor(R.color.light_green), getResources().getColor(R.color.dark_green));

        } else {
          int i = mTodoList.size();
          addToDo(mEditTextTodo.getText().toString());
          mToDoAdapter.notifyDataSetChanged();
          mInputManager.hideSoftInputFromWindow(mEditTextTodo.getWindowToken(), 0);
          hideEditText(mRevealView);
          mAddButton.setImageResource(R.drawable.icn_morph_reverse);
          mAnimatable = (Animatable) (mAddButton).getDrawable();
          mAnimatable.start();
          applyRippleColor(mPalette.getVibrantColor(defaultColorForRipple),
                  mPalette.getDarkVibrantColor(defaultColorForRipple));
          editor = prefs.edit();
          list_items.add(mEditTextTodo.getText().toString());
          editor.putStringSet(mCategory.name, list_items);
          editor.commit();

        }
    }
  }

  private void revealEditText(LinearLayout view) {
    int cx = view.getRight() - 30;
    int cy = view.getBottom() - 60;
    int finalRadius = Math.max(view.getWidth(), view.getHeight());
    Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
    view.setVisibility(View.VISIBLE);
    isEditTextVisible = true;
    anim.start();
  }

  private void hideEditText(final LinearLayout view) {
    int cx = view.getRight() - 30;
    int cy = view.getBottom() - 60;
    int initialRadius = view.getWidth();
    Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
    anim.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        view.setVisibility(View.INVISIBLE);
      }
    });
    isEditTextVisible = false;
    anim.start();
  }

  @Override
  public void onBackPressed() {
    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
    alphaAnimation.setDuration(100);
    mAddButton.startAnimation(alphaAnimation);
    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        mAddButton.setVisibility(View.GONE);
        finishAfterTransition();
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
  }
}