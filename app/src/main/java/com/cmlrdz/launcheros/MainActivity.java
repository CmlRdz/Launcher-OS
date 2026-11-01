package com.cmlrdz.launcheros;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.TextWatcher;
import android.text.Editable;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {

    LinearLayout appContainer;
    EditText search;
    ArrayList<ResolveInfo> apps = new ArrayList<>();

    int dark = Color.rgb(35, 43, 65);

    int dp(int n) {
        return (int)(n * getResources().getDisplayMetrics().density);
    }

    GradientDrawable rounded(int color, int radius) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(radius));
        return bg;
    }

    TextView label(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);

        getWindow().setStatusBarColor(Color.rgb(235, 239, 249));
        getWindow().setNavigationBarColor(Color.rgb(235, 239, 249));
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(25), dp(22), dp(15));

        GradientDrawable background = new GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            new int[]{
                Color.rgb(226, 235, 250),
                Color.rgb(245, 238, 249),
                Color.rgb(229, 242, 244)
            }
        );
        root.setBackground(background);

        TextView greeting = label("LAUNCHER OS", 12,
            Color.rgb(105, 116, 139), true);
        greeting.setLetterSpacing(0.2f);
        root.addView(greeting);

        TextView clock = label(
            new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date()),
            58, dark, false
        );
        root.addView(clock);

        TextView date = label(
            new SimpleDateFormat("EEEE, d 'de' MMMM",
                new Locale("es", "CO")).format(new Date()),
            15, Color.rgb(100, 110, 130), false
        );
        date.setPadding(0, 0, 0, dp(25));
        root.addView(date);

        search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(15);
        search.setHint("⌕   Buscar aplicaciones");
        search.setPadding(dp(18), 0, dp(18), 0);
        search.setBackground(rounded(Color.WHITE, 25));

        root.addView(search, new LinearLayout.LayoutParams(
            -1, dp(55)
        ));

        TextView title = label("Tus aplicaciones", 20, dark, true);
        title.setPadding(0, dp(25), 0, dp(15));
        root.addView(title);

        ScrollView scroll = new ScrollView(this);
        appContainer = new LinearLayout(this);
        appContainer.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(appContainer);
        root.addView(scroll, new LinearLayout.LayoutParams(
            -1, 0, 1
        ));

        setContentView(root);

        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(
                CharSequence s, int start, int count, int after) {}
            public void onTextChanged(
                CharSequence s, int start, int before, int count) {
                showApps(s.toString());
            }
            public void afterTextChanged(Editable e) {}
        });

        loadApps();
    }

    void loadApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        apps.clear();
        apps.addAll(pm.queryIntentActivities(intent, 0));

        Collections.sort(apps, (a, b) ->
            a.loadLabel(pm).toString().compareToIgnoreCase(
                b.loadLabel(pm).toString()
            )
        );

        showApps("");
    }

    void showApps(String filter) {
        appContainer.removeAllViews();

        PackageManager pm = getPackageManager();
        LinearLayout row = null;
        int column = 0;

        for (ResolveInfo app : apps) {
            String name = app.loadLabel(pm).toString();

            if (!name.toLowerCase().contains(
                filter.toLowerCase().trim())) continue;

            if (column == 0) {
                row = new LinearLayout(this);
                row.setGravity(Gravity.CENTER);
                appContainer.addView(row);
            }

            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(5), dp(10), dp(5), dp(12));

            ImageView icon = new ImageView(this);
            icon.setImageDrawable(app.loadIcon(pm));
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            item.addView(icon, new LinearLayout.LayoutParams(
                dp(55), dp(55)
            ));

            TextView title = label(name, 11, dark, false);
            title.setGravity(Gravity.CENTER);
            title.setMaxLines(2);
            title.setPadding(0, dp(6), 0, 0);

            item.addView(title);

            item.setOnClickListener(v -> {
                Intent launch = pm.getLaunchIntentForPackage(
                    app.activityInfo.packageName
                );
                if (launch != null) startActivity(launch);
            });

            row.addView(item, new LinearLayout.LayoutParams(
                0, -2, 1
            ));

            column++;
            if (column == 4) column = 0;
        }
    }
                        }
