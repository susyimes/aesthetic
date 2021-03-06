package com.afollestad.aesthetic.views;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.TintHelper;

import rx.subscriptions.CompositeSubscription;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static com.afollestad.aesthetic.Rx.distinctToMainThread;
import static com.afollestad.aesthetic.Rx.onErrorLogAndRethrow;

@RestrictTo(LIBRARY_GROUP)
public class AestheticButton extends AppCompatButton {

  private int color;
  private boolean isDark;
  private CompositeSubscription subs;

  public AestheticButton(Context context) {
    super(context);
  }

  public AestheticButton(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public AestheticButton(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void invalidateColors(int color) {
    this.color = color;
    TintHelper.setTintAuto(this, color, false, isDark);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    subs = new CompositeSubscription();
    subs.add(
        Aesthetic.get()
            .accentColor()
            .compose(distinctToMainThread())
            .subscribe(this::invalidateColors, onErrorLogAndRethrow()));
    subs.add(
        Aesthetic.get()
            .isDark()
            .compose(distinctToMainThread())
            .subscribe(
                isDark -> {
                  this.isDark = isDark;
                  invalidateColors(color);
                }));
  }

  @Override
  protected void onDetachedFromWindow() {
    subs.unsubscribe();
    super.onDetachedFromWindow();
  }
}
