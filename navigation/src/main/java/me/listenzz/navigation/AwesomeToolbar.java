package me.listenzz.navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Listen on 2017/11/22.
 */

public class AwesomeToolbar extends Toolbar {

    private TextView titleView;
    private TextView leftButton;
    private TextView rightButton;
    private Drawable divider;
    private int dividerAlpha = 255;
    private int contentInset;

    private int backgroundColor;
    private int buttonTintColor;
    private int buttonTextSize;
    private int titleGravity;
    private int titleTextColor;
    private int titleTextSize;

    private List<TextView> leftButtons;
    private List<TextView> rightButtons;

    private ViewOutlineProvider outlineProvider;

    public AwesomeToolbar(Context context) {
        super(context);
        init();
    }

    public AwesomeToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AwesomeToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        contentInset = getContentInsetStart();
        setContentInsetStartWithNavigation(getContentInsetStartWithNavigation() - contentInset);
        setContentInsetsRelative(0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (divider != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            int height = (int) getContext().getResources().getDisplayMetrics().density;
            divider.setBounds(0, getHeight() - height, getWidth(), getHeight());
            divider.setAlpha(dividerAlpha);
            divider.draw(canvas);
            divider.setAlpha(255);
        }
    }

    public void setButtonTintColor(int color) {
        this.buttonTintColor = color;
    }

    @Override
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        setBackground(new ColorDrawable(color));
        if (color == Color.TRANSPARENT) {
            hideShadow();
        }
    }

    public void setButtonTextSize(int size) {
        buttonTextSize = size;
    }

    @Override
    public void setAlpha(float alpha) {
        Drawable drawable = getBackground();
        drawable.setAlpha((int) (alpha * 255 + 0.5));
        setBackground(drawable);
        if (divider != null) {
            dividerAlpha = (int) (alpha * 255 + 0.5);
            invalidate();
        }
    }

    private void setShadow(@Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            divider = drawable;
            postInvalidate();
        }
    }

    public void hideShadow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (outlineProvider == null) {
                outlineProvider = getOutlineProvider();
            }
            setOutlineProvider(new DefaultOutlineProvider());
        } else {
            setShadow(null);
        }
    }

    public void showShadow(@NonNull Drawable shadowImage, float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (outlineProvider != null) {
                setOutlineProvider(outlineProvider);
            }
            setElevation(elevation);
        } else {
            setShadow(shadowImage);
        }
    }

    public void setTitleGravity(int gravity) {
        titleGravity = gravity;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
        if (titleView != null) {
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleTextSize);
        }
    }

    @Override
    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        super.setTitleTextColor(titleTextColor);
        if (titleView != null) {
            titleView.setTextColor(titleTextColor);
        }
    }

    public void setAwesomeTitle(int resId) {
        setAwesomeTitle(getContext().getText(resId));
    }

    public void setAwesomeTitle(CharSequence title) {
        TextView titleView = getTitleView();
        titleView.setText(title);
    }

    public TextView getTitleView() {
        if (titleView == null) {
            titleView = new TextView(getContext());
            LayoutParams layoutParams = new LayoutParams(-2, -2, Gravity.CENTER_VERTICAL | titleGravity);
            if (titleGravity == Gravity.START) {
                layoutParams.leftMargin = getContentInset();
            }
            titleView.setMaxLines(1);
            titleView.setEllipsize(TextUtils.TruncateAt.END);
            titleView.setTextColor(titleTextColor);
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleTextSize);
            addView(titleView, layoutParams);
        }
        return titleView;
    }

    protected TextView getLeftButton() {
        if (leftButton == null) {
            leftButton = new TextView(getContext());
            leftButton.setGravity(Gravity.CENTER);
            LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, MATCH_PARENT, Gravity.CENTER_VERTICAL | Gravity.START);
            layoutParams.leftMargin = AppUtils.dp2px(getContext(), 8);
            addView(leftButton, layoutParams);
        }
        return leftButton;
    }

    protected TextView getRightButton() {
        if (rightButton == null) {
            rightButton = new TextView(getContext());
            rightButton.setGravity(Gravity.CENTER);
            LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, MATCH_PARENT, Gravity.CENTER_VERTICAL | Gravity.END);
            layoutParams.rightMargin = AppUtils.dp2px(getContext(), 8);
            addView(rightButton, layoutParams);
        }
        return rightButton;
    }

    protected int getContentInset() {
        return this.contentInset;
    }

    public void clearLeftButton() {
        if (leftButton != null) {
            removeView(leftButton);
            leftButton = null;
        }
    }

    public void clearLeftButtons() {
        clearLeftButton();
        if (leftButtons != null) {
            for (TextView button : leftButtons) {
                removeView(button);
            }
            leftButtons.clear();
        }
        setNavigationIcon(null);
        setNavigationOnClickListener(null);
    }

    public void clearRightButton() {
        if (rightButton != null) {
            removeView(rightButton);
            rightButton = null;
        }
    }

    public void clearRightButtons() {
        clearLeftButton();
        if (rightButtons != null) {
            for (TextView button : rightButtons) {
                removeView(button);
            }
            rightButtons.clear();
        }
        Menu menu = getMenu();
        menu.clear();
    }

    public void addLeftButton(ToolbarButtonItem buttonItem) {
        if (leftButton != null) {
            removeView(leftButton);
        }
        if (leftButtons == null) {
            leftButtons = new ArrayList<>();
        }
        TextView button = new TextView(getContext());
        button.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.START);
        if (leftButtons.size() == 0) {
            layoutParams.leftMargin = AppUtils.dp2px(getContext(), 8);
        }
        addView(button, layoutParams);
        setButton(button, buttonItem);
        leftButtons.add(button);
        bringTitleViewToFront();
    }

    public void addRightButton(ToolbarButtonItem buttonItem) {
        if (rightButton != null) {
            removeView(rightButton);
        }
        if (rightButtons == null) {
            rightButtons = new ArrayList<>();
        }

        TextView button = new TextView(getContext());
        button.setGravity(Gravity.CENTER);
        LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.END);
        if (rightButtons.size() == 0) {
            layoutParams.rightMargin = AppUtils.dp2px(getContext(), 8);
        }
        addView(button, layoutParams);
        setButton(button, buttonItem);
        rightButtons.add(button);
        bringTitleViewToFront();
    }

    public void setLeftButton(ToolbarButtonItem buttonItem) {
        if (leftButtons != null && leftButtons.size() > 0) {
            return;
        }
        setNavigationIcon(null);
        setNavigationOnClickListener(null);
        TextView leftButton = getLeftButton();
        setButton(leftButton, buttonItem);
        bringTitleViewToFront();
    }

    public void setRightButton(ToolbarButtonItem buttonItem) {
        if (rightButtons != null && rightButtons.size() > 0) {
            return;
        }
        TextView rightButton = getRightButton();
        setButton(rightButton, buttonItem);
        bringTitleViewToFront();
    }

    private void bringTitleViewToFront() {
        if (titleView != null) {
            bringChildToFront(titleView);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                requestLayout();
                invalidate();
            }
        }
    }

    @Override
    public void setNavigationIcon(@Nullable Drawable icon) {
        super.setNavigationIcon(icon);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child instanceof AppCompatImageButton) {
                    AppCompatImageButton button = (AppCompatImageButton) child;
                    button.setBackgroundResource(R.drawable.nav_toolbar_button_item_background);
                }
            }
        }
    }

    private void setButton(TextView button, ToolbarButtonItem buttonItem) {
        button.setOnClickListener(buttonItem.onClickListener);
        button.setText(null);
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        button.setMaxWidth(Integer.MAX_VALUE);
        button.setVisibility(View.VISIBLE);

        int color = buttonItem.tintColor != 0 ? buttonItem.tintColor : buttonTintColor;
        int disableColor = ColorUtils.blendARGB(AppUtils.toGrey(color), backgroundColor, 0.75f);

        Drawable icon = drawableFromBarButtonItem(buttonItem);

        if (icon != null) {
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_enabled}, // enabled
                    new int[]{-android.R.attr.state_enabled}, // disabled
            };

            int[] colors = new int[]{
                    color,
                    disableColor,
            };

            ColorStateList colorStateList = new ColorStateList(states, colors);

            if (!buttonItem.renderOriginal) {
                DrawableCompat.setTintList(icon, colorStateList);
            }

            button.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            int size = AppUtils.dp2px(getContext(), 40);
            int padding = (size - icon.getIntrinsicWidth()) / 2;
            button.setWidth(size);
            button.setHeight(size);
            button.setPaddingRelative(padding, 0, padding, 0);
            TypedValue typedValue = new TypedValue();
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
                button.setBackgroundResource(R.drawable.nav_toolbar_button_item_background);
            } else {
                if (getContext().getTheme().resolveAttribute(R.attr.actionBarItemBackground, typedValue, true)) {
                    button.setBackgroundResource(typedValue.resourceId);
                }
            }
        } else {
            int pressedColor = ColorUtils.blendARGB(ColorUtils.setAlphaComponent(color, 51), backgroundColor, .75f);
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_pressed},  // pressed
                    new int[]{android.R.attr.state_enabled},  // enabled
                    new int[]{-android.R.attr.state_enabled}, // disabled
            };

            int[] colors = new int[]{
                    pressedColor,
                    color,
                    disableColor,
            };

            ColorStateList colorStateList = new ColorStateList(states, colors);

            int padding = AppUtils.dp2px(getContext(), 8);
            button.setPaddingRelative(padding, 0, padding, 0);
            button.setText(buttonItem.title);
            button.setTextColor(colorStateList);
            button.setTextSize(buttonTextSize);
            button.setBackground(null);
        }

        button.setEnabled(buttonItem.enabled);
    }

    private Drawable drawableFromBarButtonItem(ToolbarButtonItem barButtonItem) {
        if (getContext() == null) {
            return null;
        }
        Drawable drawable = null;
        if (barButtonItem.iconUri != null) {
            drawable = DrawableUtils.fromUri(getContext(), barButtonItem.iconUri);
        } else if (barButtonItem.iconRes != 0) {
            drawable = ContextCompat.getDrawable(getContext(), barButtonItem.iconRes);
        }
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable.mutate());
        }
        return drawable;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static final class DefaultOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {

        }
    }

}
