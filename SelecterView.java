package com.careers360.mrl.customviews;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.careers360.mrl.R;
import com.careers360.mrl.utils.LogUtils;

import static android.view.MotionEvent.INVALID_POINTER_ID;


public class SelecterView extends RelativeLayout implements GestureDetector.OnGestureListener, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

  public ImageView slidingButton, leftImage, rightImage;

  private int _xDelta;
  private Drawable leftDrawable;
  private Drawable rightDrawable;
  private Drawable centerDrawable;
  public static final String TAG = SelecterView.class.getCanonicalName();
  boolean buttonFLagCenter = false;
  private int startX, endX, centerX;
  private Context context;
  private boolean isIconChaned = false;
  private ActionPerform actionPerform;
  private boolean enable = true;

  private float mLastTouchX;
  private float mLastTouchY;
  private float mPosX, mPosY;


  private GestureDetectorCompat mDetector;
  private ValueAnimator positionAnimator;


  public SelecterView(Context context) {
    super(context);
    init(context, null, -1, -1);
  }

  public SelecterView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, -1, -1);
  }

  public SelecterView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr, -1);


  }


  private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    this.context = context;
    mDetector = new GestureDetectorCompat(context, this);

    addViews();

  }

  private void initAnimator(float start, float end) {
     positionAnimator =
      ValueAnimator.ofFloat(start, end);

    positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

    positionAnimator.addUpdateListener(this);
    positionAnimator.addListener(this);
    positionAnimator.setDuration(300);
    positionAnimator.start();
  }


  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    Log.d(TAG, "onLayout: ");
    super.onLayout(changed, l, t, r, b);
    if (!buttonFLagCenter) {
      initVars();
      setButtonInCenter();
    }
    mPosX = slidingButton.getX();
  }


  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    Log.d(TAG, "onMeasure: ");
  }

  public void addViews() {
    setCapsule();
    setCenterCircle();
    setLeftRightIcons();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Log.d(TAG, "onDraw: ");
  }

  private void setCapsule() {

    RelativeLayout background = new RelativeLayout(context);

    LayoutParams layoutParamsView = new LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.WRAP_CONTENT);

    layoutParamsView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

    background.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_filled_circle_purple));

    addView(background, layoutParamsView);
  }

  public void setCenterCircle() {

    final ImageView swipeButton = new ImageView(context);
    this.slidingButton = swipeButton;

    leftDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_keyboard);
    rightDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_send);
    centerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_drag_variant);

    slidingButton.setImageDrawable(centerDrawable);
    slidingButton.setPadding(60, 60, 60, 60);

    LayoutParams layoutParamsButton = new LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT);


    swipeButton.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_filled_circle_red));
    swipeButton.setImageDrawable(centerDrawable);
    addView(slidingButton, layoutParamsButton);

    slidingButton.setOnTouchListener(getButtonTouchListener());


  }

  private void setLeftRightIcons() {
    ImageView leftIcon = new ImageView(context);
    LayoutParams l1 = new LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT);


    l1.addRule(ALIGN_PARENT_START, RelativeLayout.TRUE);
    l1.addRule(CENTER_VERTICAL, RelativeLayout.TRUE);
    l1.setMarginStart(50);
    leftIcon.setImageDrawable(leftDrawable);
    leftImage = leftIcon;
    addView(leftIcon, l1);

    ImageView rightIcon = new ImageView(context);
    LayoutParams l2 = new LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT);

    l2.addRule(ALIGN_PARENT_END, RelativeLayout.TRUE);
    l2.addRule(CENTER_VERTICAL, RelativeLayout.TRUE);
    l2.setMarginEnd(50);
    rightIcon.setImageDrawable(rightDrawable);
    rightImage = rightIcon;
    addView(rightIcon, l2);


  }

  private void initVars() {


    if (getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
      ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) getLayoutParams();
      startX = layoutParams.getMarginStart();
    }
    if (getLayoutParams() instanceof RelativeLayout.LayoutParams) {
      RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
      startX = layoutParams.getMarginStart();
    }
    if (getLayoutParams() instanceof LinearLayout.LayoutParams) {
      LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
      startX = layoutParams.getMarginStart();
    }
    endX = getRootView().getWidth() - startX;
    centerX = (startX + endX) / 2;


  }

  private void setButtonInCenter() {

    buttonFLagCenter = true;
    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) slidingButton.getLayoutParams();
    layoutParams.leftMargin = centerX - slidingButton.getWidth() / 2 - startX;
    slidingButton.setLayoutParams(layoutParams);

    invalidate();
  }

  private OnTouchListener getButtonTouchListener() {
    return new OnTouchListener() {
      @Override
      public boolean onTouch(View v, final MotionEvent event) {

        if (mDetector.onTouchEvent(event)) {
          return true;
        }

        final int X = (int) event.getRawX();


        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN: {
            final int pointerIndex = MotionEventCompat.getActionIndex(event);
            final float x = MotionEventCompat.getX(event, pointerIndex);
            final float y = MotionEventCompat.getY(event, pointerIndex);
            RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
            _xDelta = X - lParams.leftMargin;

            // Remember where we started (for dragging)
            mLastTouchX = x;
            mLastTouchY = y;
            // Save the ID of this pointer (for dragging)

          }


          return true;
          case MotionEvent.ACTION_MOVE:

//            if (enable) {
//              if (slidingButton.getX() + slidingButton.getWidth() + startX < getRootView().getWidth() - startX && slidingButton.getX() > 0) {
//
//                final int pointerIndex =
//                  MotionEventCompat.findPointerIndex(event, mActivePointerId);
//
//                final float x = MotionEventCompat.getX(event, pointerIndex);
//                final float y = MotionEventCompat.getY(event, pointerIndex);
//
//                // Calculate the distance moved
//                final float dx = x - mLastTouchX;
//                final float dy = y - mLastTouchY;
//
//                mPosX += dx;
//                mPosY += dy;
//
////                v.setX(mPosX);
//
//                invalidate();
//
//                // Remember this touch position for the next move event
//                mLastTouchX = x;
//                mLastTouchY = y;
////                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
////                layoutParams.leftMargin = X - _xDelta;
////                v.setLayoutParams(layoutParams);
//
//              }
//            }


            return true;
          case MotionEvent.ACTION_UP:
            _xDelta = 0;
//            buttonAnimate();
            return true;
        }

        return true;
      }
    };
  }

  private void buttonAnimate() {
    if (slidingButton.getX() <= 0) {
      moveButtonLeft();
    } else if (slidingButton.getX() + slidingButton.getWidth() + startX >= getRootView().getWidth() - startX) {
      moveButtonRight();
    } else moveButtonCenter();

  }

  public void moveButtonCenter() {

    if (isIconChaned) {
      isIconChaned = false;
      leftImage.setVisibility(VISIBLE);
      rightImage.setVisibility(VISIBLE);
      slidingButton.setImageDrawable(centerDrawable);
    }

    initAnimator(slidingButton.getX(), centerX - slidingButton.getWidth() / 2 - startX);

//    final ValueAnimator positionAnimator =
//      ValueAnimator.ofFloat(slidingButton.getX(), centerX - slidingButton.getWidth() / 2 - startX);
//
//    positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//
//    positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//      @Override
//      public void onAnimationUpdate(ValueAnimator animation) {
//        float x = (Float) positionAnimator.getAnimatedValue();
//        slidingButton.setX(x);
//        invalidate();
//      }
//    });
//
//
//
//    positionAnimator.setDuration(200);
//    positionAnimator.start();


//    removeView(slidingButton);
//    setCenterCircle();
  }

  public void moveButtonRight() {
    isIconChaned = true;
    slidingButton.setImageDrawable(rightDrawable);
    rightImage.setVisibility(GONE);

    initAnimator(slidingButton.getX(), endX - slidingButton.getWidth() - startX);

//    slidingButton.setX(endX - slidingButton.getWidth() - startX);

//    final ValueAnimator positionAnimator =
//      ValueAnimator.ofFloat(slidingButton.getX(), endX - slidingButton.getWidth() - startX);
//
//
//    positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//
//    positionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//      @Override
//      public void onAnimationUpdate(ValueAnimator animation) {
//        float x = (Float) positionAnimator.getAnimatedValue();
//        slidingButton.setX(x);
//        LogUtils.println(x + "");
//        invalidate();
//
//      }
//    });
//    positionAnimator.setDuration(200);
//    positionAnimator.start();
//
//    if (actionPerform != null) {
//      actionPerform.onSend();
//    }

  }

  public void moveButtonLeft() {
    isIconChaned = true;
//    slidingButton.setX(0);
    slidingButton.setImageDrawable(leftDrawable);
    leftImage.setVisibility(GONE);

    initAnimator(slidingButton.getX(), 0);

  }

  public void setIcon(Drawable leftDrawable, Drawable rightDrawable, Drawable centerDrawable) {

    this.leftDrawable = leftDrawable;
    this.rightDrawable = rightDrawable;
    this.centerDrawable = centerDrawable;
//    setLeftRightIcons();
    leftImage.setImageDrawable(leftDrawable);
    rightImage.setImageDrawable(rightDrawable);
  }

  public boolean isEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  public void setActionPerform(ActionPerform actionPerform) {
    this.actionPerform = actionPerform;
  }

  @Override
  public boolean onDown(MotionEvent e) {
    Log.d(TAG, "onDown: ");
    return false;
  }

  @Override
  public void onShowPress(MotionEvent e) {
    Log.d(TAG, "onShowPress: ");
  }

  @Override
  public boolean onSingleTapUp(MotionEvent e) {
    Log.d(TAG, "onSingleTapUp: ");
    return false;
  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    Log.d(TAG, "onScroll: ");
    return false;
  }

  @Override
  public void onLongPress(MotionEvent e) {
    Log.d(TAG, "onLongPress: ");
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    Log.d(TAG, "onFling: ");
    if (e1.getX() - e2.getX() < 0)
      moveButtonRight();
    else moveButtonLeft();
    return false;
  }

  @Override
  public void onAnimationUpdate(ValueAnimator animation) {
    float x = (Float) animation.getAnimatedValue();
    slidingButton.setX(x);
    invalidate();
    LogUtils.println(x + "");
  }

  @Override
  public void onAnimationStart(Animator animation) {

  }

  @Override
  public void onAnimationEnd(Animator animation) {
    if (slidingButton.getX() == 0) {
      if (actionPerform != null) {
        LogUtils.logResponse("TAG", "moveButtonLeft: ");
        actionPerform.onKeyboardOpen();
      }
    } else if (slidingButton.getX() == endX - slidingButton.getWidth() - startX) {
      if (actionPerform != null) {
        LogUtils.logResponse("TAG", "moveButtonLeft: ");
        actionPerform.onSend();
      }
    }
  }

  @Override
  public void onAnimationCancel(Animator animation) {

  }

  @Override
  public void onAnimationRepeat(Animator animation) {

  }

  public interface ActionPerform {
    void onKeyboardOpen();

    void onSend();
  }


}
