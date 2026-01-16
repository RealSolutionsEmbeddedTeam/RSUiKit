package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * RSMenu - Menü container bileşeni.
 * RSMenuItem öğelerini dikey olarak listeler.
 *
 * Figma Tasarım Özellikleri:
 * - Width: 252dp (min)
 * - Padding: 16dp
 * - Border-radius: 24dp
 * - Background: rs-bg-primary
 * - Shadow: elevation 4dp
 * - Gap: 2dp (item'lar arası)
 * - Direction: column (vertical)
 *
 * Kullanım (XML):
 *
 * <pre>
 * &lt;com.realsolutions.uikit.RSMenu
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"&gt;
 *
 *     &lt;com.realsolutions.uikit.RSMenuItem
 *         app:rsMenuItemLabel="Item 1" /&gt;
 *
 *     &lt;com.realsolutions.uikit.RSMenuItem
 *         app:rsMenuItemLabel="Item 2"
 *         app:rsMenuItemIconVisible="true"
 *         app:rsMenuItemIcon="@drawable/rs_ic_circle_dashed" /&gt;
 *
 * &lt;/com.realsolutions.uikit.RSMenu&gt;
 * </pre>
 *
 * Kullanım (Java):
 *
 * <pre>
 * RSMenu menu = new RSMenu(context);
 * menu.addItem("Item 1");
 * menu.addItem("Item 2", R.drawable.rs_ic_circle_dashed);
 * menu.setOnItemClickListener((item, position) -> {
 *     // Handle click
 * });
 * </pre>
 */
public class RSMenu extends FrameLayout {

    // =====================
    // Listener Interface
    // =====================

    /**
     * Menü öğesi tıklama dinleyicisi.
     */
    public interface OnItemClickListener {
        /**
         * Bir menü öğesine tıklandığında çağrılır.
         *
         * @param item     Tıklanan menü öğesi
         * @param position Öğenin pozisyonu (0-indexed)
         */
        void onItemClick(RSMenuItem item, int position);
    }

    // =====================
    // Views
    // =====================

    private LinearLayout container;

    // =====================
    // State
    // =====================

    private final List<RSMenuItem> items = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private int itemGap;

    // =====================
    // Constructors
    // =====================

    public RSMenu(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RSMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // =====================
    // Initialization
    // =====================

    private void init(Context context, AttributeSet attrs) {
        // Layout inflate
        LayoutInflater.from(context).inflate(R.layout.rs_menu, this, true);

        // View binding
        container = findViewById(R.id.rs_menu_container);

        // Item gap değerini al
        itemGap = getResources().getDimensionPixelSize(R.dimen.rs_menu_item_gap);

        // XML attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSMenu);
            try {
                // Custom width
                int menuWidth = a.getDimensionPixelSize(R.styleable.RSMenu_rsMenuWidth, -1);
                if (menuWidth > 0 && container != null) {
                    ViewGroup.LayoutParams params = container.getLayoutParams();
                    params.width = menuWidth;
                    container.setLayoutParams(params);
                }
            } finally {
                a.recycle();
            }
        }
    }

    // =====================
    // Child Handling (XML'den eklenen child'lar için)
    // =====================

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof RSMenuItem) {
            // RSMenuItem ise container'a ekle
            addMenuItem((RSMenuItem) child);
        } else if (container == null) {
            // Container henüz oluşturulmadıysa (init sırasında)
            super.addView(child, index, params);
        } else {
            // Diğer view'ları container'a ekle
            super.addView(child, index, params);
        }
    }

    // =====================
    // Public API - Item Management
    // =====================

    /**
     * Menüye yeni bir öğe ekler.
     *
     * @param item Eklenecek menü öğesi
     */
    public void addMenuItem(RSMenuItem item) {
        if (container == null || item == null)
            return;

        // Gap margin ekle (ilk item hariç)
        if (!items.isEmpty()) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = itemGap;
            item.setLayoutParams(params);
        }

        // Click listener ekle
        final int position = items.size();
        item.setOnMenuItemClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(item, position);
            }
        });

        items.add(item);
        container.addView(item);
    }

    /**
     * Menüye sadece label ile yeni bir öğe ekler.
     *
     * @param label Öğe metni
     * @return Oluşturulan menü öğesi
     */
    public RSMenuItem addItem(String label) {
        RSMenuItem item = new RSMenuItem(getContext());
        item.setLabel(label);
        addMenuItem(item);
        return item;
    }

    /**
     * Menüye ikon ve label ile yeni bir öğe ekler.
     *
     * @param label   Öğe metni
     * @param iconRes Drawable resource ID
     * @return Oluşturulan menü öğesi
     */
    public RSMenuItem addItem(String label, int iconRes) {
        RSMenuItem item = new RSMenuItem(getContext());
        item.setLabel(label);
        item.setIcon(iconRes);
        item.setIconVisible(true);
        addMenuItem(item);
        return item;
    }

    /**
     * Belirtilen pozisyondaki öğeyi kaldırır.
     *
     * @param position Kaldırılacak öğenin pozisyonu
     */
    public void removeItem(int position) {
        if (position < 0 || position >= items.size())
            return;

        RSMenuItem item = items.remove(position);
        if (container != null && item != null) {
            container.removeView(item);
        }

        // Click listener pozisyonlarını güncelle
        updateClickListeners();
    }

    /**
     * Tüm menü öğelerini kaldırır.
     */
    public void clearItems() {
        items.clear();
        if (container != null) {
            container.removeAllViews();
        }
    }

    /**
     * Belirtilen pozisyondaki öğeyi döndürür.
     *
     * @param position Öğe pozisyonu
     * @return Menü öğesi veya null
     */
    @Nullable
    public RSMenuItem getItem(int position) {
        if (position < 0 || position >= items.size()) {
            return null;
        }
        return items.get(position);
    }

    /**
     * Menüdeki öğe sayısını döndürür.
     *
     * @return Öğe sayısı
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Tüm menü öğelerini liste olarak döndürür.
     *
     * @return Menü öğeleri listesi
     */
    public List<RSMenuItem> getItems() {
        return new ArrayList<>(items);
    }

    private void updateClickListeners() {
        for (int i = 0; i < items.size(); i++) {
            final int position = i;
            final RSMenuItem item = items.get(i);
            item.setOnMenuItemClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(item, position);
                }
            });
        }
    }

    // =====================
    // Public API - Selection
    // =====================

    /**
     * Belirtilen pozisyondaki öğeyi seçili yapar.
     * Diğer öğelerin seçimi kaldırılır.
     *
     * @param position Seçilecek öğenin pozisyonu
     */
    public void setSelectedPosition(int position) {
        for (int i = 0; i < items.size(); i++) {
            RSMenuItem item = items.get(i);
            if (i == position) {
                item.setState(RSMenuItem.STATE_SELECTED);
            } else if (item.getState() == RSMenuItem.STATE_SELECTED) {
                item.setState(RSMenuItem.STATE_DEFAULT);
            }
        }
    }

    /**
     * Seçili öğenin pozisyonunu döndürür.
     *
     * @return Seçili öğe pozisyonu veya -1 (seçili öğe yoksa)
     */
    public int getSelectedPosition() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getState() == RSMenuItem.STATE_SELECTED) {
                return i;
            }
        }
        return -1;
    }

    // =====================
    // Public API - Listener
    // =====================

    /**
     * Menü öğesi tıklama dinleyicisini ayarlar.
     *
     * @param listener Dinleyici
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
}
