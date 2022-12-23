package org.nutritionfacts.dailydozen.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.nutritionfacts.dailydozen.RDA;

import java.util.List;

import timber.log.Timber;

@Table(name = "tweaks")
public class Tweak extends TruncatableModel implements RDA {
    @Column(name = "name", index = true)
    private String name;

    @Column(name = "id_name", index = true)
    private String idName;

    @Column(name = "recommended_amount")
    private int recommendedAmount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public int getRecommendedAmount() {
        return recommendedAmount;
    }

    public Tweak() {
    }

    public Tweak(String name, String idName, int recommendedAmount) {
        this.name = name;
        this.idName = idName;
        this.recommendedAmount = recommendedAmount;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }

    public static void ensureAllTweaksExistInDatabase(final String[] tweakNames,
                                                      final String[] tweakIdNames,
                                                      final int[] recommendedAmounts) {
        ActiveAndroid.beginTransaction();

        try {
            for (int i = 0; i < tweakNames.length; i++) {
                createTweakIfDoesNotExist(tweakNames[i], tweakIdNames[i], recommendedAmounts[i]);
            }

            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static Tweak getById(long tweakId) {
        return new Select().from(Tweak.class).where("Id = ?", tweakId).executeSingle();
    }

    public static Tweak getByNameOrIdName(final String idName) {
        return new Select().from(Tweak.class)
                .where("id_name = ?", idName)
                .or("name = ?", idName)
                .executeSingle();
    }

    private static void createTweakIfDoesNotExist(final String tweakName,
                                                  final String idName,
                                                  final int recommendedAmount) {
        boolean needToSave = false;

        Tweak tweak = getByNameOrIdName(idName);

        if (tweak == null) {
            tweak = new Tweak(tweakName, idName, recommendedAmount);
            needToSave = true;
        } else if (TextUtils.isEmpty(tweak.getIdName())) {
            tweak.setIdName(idName);
            needToSave = true;
        }

        if (!tweak.getName().equalsIgnoreCase(tweakName)) {
            tweak.setName(tweakName);
            needToSave = true;
        }

        if (needToSave) {
            try {
                tweak.save();
            } catch (java.lang.SecurityException e) {
                Timber.e(e, "Caught SecurityException in createTweakIfDoesNotExist");
            }
        }
    }

    public static List<Tweak> getAllTweaks() {
        return new Select().from(Tweak.class).execute();
    }
}
