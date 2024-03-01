package com.abdulhameed.foodieplan.home.setting;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.FragmentSettingsBinding;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public class SettingsFragment extends BottomSheetDialogFragment {
    private FragmentSettingsBinding binding;
    private SharedPreferencesManager preferencesManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = SharedPreferencesManager.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean currentDarkMode = preferencesManager.getDarkMode();
        String language = preferencesManager.getLanguage();

        if (currentDarkMode)
            binding.cDark.setChecked(true);
        else
            binding.cLight.setChecked(true);

        if (language.equals("arabic"))
            binding.cArabic.setChecked(true);
        else
            binding.cEnglish.setChecked(true);

        binding.applyBtn.setOnClickListener(v -> applySettings());

    }
    private void applySettings() {
        boolean languageChanged = setLanguage();
        boolean displayModeChanged = setDisplayingMode();

        if (languageChanged || displayModeChanged) {
            requireActivity().recreate();
        }
        dismiss();
    }

    private boolean setLanguage() {
        int checkedLanguageChipId = binding.cgLang.getCheckedChipId();

        String selectedLanguage = checkedLanguageChipId == R.id.c_english ? "english" : "arabic";

        String currentLanguage = preferencesManager.getLanguage();

        if (selectedLanguage.equals(currentLanguage)) {
            Toast.makeText(requireContext(), "This Language Is Already Applied !", Toast.LENGTH_SHORT).show();
            return false;
        }

        preferencesManager.saveLanguage(selectedLanguage);

        String selectedCountryCode = selectedLanguage.substring(0, 2);
        Locale locale = new Locale(selectedCountryCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration(getResources().getConfiguration());
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        return true;
    }

    private boolean setDisplayingMode() {
        int checkedDisplayChipId = binding.cgDisplay.getCheckedChipId();
        boolean isDarkMode = checkedDisplayChipId == R.id.c_dark;
        boolean currentDarkMode = preferencesManager.getDarkMode();

        if (currentDarkMode == isDarkMode) {
            Toast.makeText(requireContext(), "This Theme Is Already Applied !", Toast.LENGTH_SHORT).show();
            return false;
        }

        preferencesManager.saveDarkMode(isDarkMode);

        if (isDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        return true;
    }
}
