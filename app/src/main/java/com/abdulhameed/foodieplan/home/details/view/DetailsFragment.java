package com.abdulhameed.foodieplan.home.details.view;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.abdulhameed.foodieplan.R;
import com.abdulhameed.foodieplan.databinding.DialogSelectDayBinding;
import com.abdulhameed.foodieplan.databinding.FragmentDetailsBinding;
import com.abdulhameed.foodieplan.home.details.DetailsContract;
import com.abdulhameed.foodieplan.home.details.presenter.DetailsPresenter;
import com.abdulhameed.foodieplan.model.Meal;
import com.abdulhameed.foodieplan.model.SharedPreferencesManager;
import com.abdulhameed.foodieplan.model.data.WatchedMeal;
import com.abdulhameed.foodieplan.model.local.MealsLocalDataSource;
import com.abdulhameed.foodieplan.model.remote.MealsRemoteDataSource;
import com.abdulhameed.foodieplan.model.repository.MealRepository;
import com.abdulhameed.foodieplan.utils.MyCalender;
import com.abdulhameed.foodieplan.utils.NetworkManager;
import com.google.android.material.chip.Chip;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.squareup.picasso.Picasso;


public class DetailsFragment extends Fragment implements DetailsContract.View {
    private static final long MINIMUM_TIME_SPENT_MILLISECONDS = 10000;
    private static final String TAG = "DetailsFragment";
    private DetailsContract.Presenter presenter;
    private FragmentDetailsBinding binding;
    private IngredientAdapter ingredientAdapter;
    private InstructionAdapter instructionAdapter;
    private boolean mealLiked = false;
    private CountDownTimer countDownTimer;
    private Meal meal;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new DetailsPresenter(this,
                MealRepository.getInstance(
                        MealsRemoteDataSource.getInstance(),
                        MealsLocalDataSource.getInstance(getContext())),
                SharedPreferencesManager.getInstance(requireContext()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);

        initRecyclerViews();
        getLifecycle().addObserver(binding.ypMealDetailsVideo);
        if(NetworkManager.isOnline(requireContext())) {
            getArgs();
            binding.avNoInternet.setVisibility(View.GONE);
            binding.cvMealImg.setVisibility(View.VISIBLE);
            binding.nestedScrollView.setVisibility(View.VISIBLE);
        } else {
            binding.avNoInternet.setVisibility(View.VISIBLE);
            binding.cvMealImg.setVisibility(View.GONE);
            binding.nestedScrollView.setVisibility(View.GONE);
        }

        setListeners();

        startTimer();

        return binding.getRoot();
    }

    private void setListeners() {
        binding.ibFavourite.setOnClickListener(view -> {
            presenter.addToFavourite(meal);
        });

        binding.ibPlan.setOnClickListener(view -> {
            showDaySelectionDialog();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(MINIMUM_TIME_SPENT_MILLISECONDS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                saveWatchedMeal();
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void saveWatchedMeal() {
        if (meal != null && mealLiked) {
            presenter.saveWatchedMeal(new WatchedMeal(meal.getId(), meal.getName(),
                    meal.getCategory(), meal.getCountry(), meal.getThumb()));
        }
    }

    private void initRecyclerViews() {
        ingredientAdapter = new IngredientAdapter();
        binding.rvMealDetailsIngredient.setAdapter(ingredientAdapter);

        instructionAdapter = new InstructionAdapter();
        binding.rvSteps.setAdapter(instructionAdapter);
    }

    private void getArgs() {
        Bundle args = getArguments();
        if (args != null) {
            String mealId = args.getString("meal_id");
            presenter.getMealById(mealId);
        }
    }

    @Override
    public void showMeal(Meal meal) {
        this.meal = meal;
        Toast.makeText(requireContext(), meal.getName(), Toast.LENGTH_SHORT).show();
        Picasso.get().load(meal.getThumb()).placeholder(R.drawable.cooking).into(binding.ivMealImg);

        binding.tvMealDetailsName.setText(meal.getName());
        binding.tvMealDetailsCategory.setText(meal.getCategory());
        binding.tvMealDetailsCountry.setText(meal.getCountry());

        binding.ypMealDetailsVideo.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                mealLiked = true;
                String videoId = getVideoId(meal.getVideoUrl());
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        ingredientAdapter.setList(meal.getIngredients());
        instructionAdapter.setList(InstructionsConverter.convertStringToInstructions(meal.getInstructions()));
    }

    public String getVideoId(String videoUrl) {
        if (videoUrl != null && videoUrl.split("\\?v=").length > 1)
            return videoUrl.split("\\?v=")[1];
        else return "";
    }

    private void showDaySelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        DialogSelectDayBinding dialogBinding = DialogSelectDayBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();

        String today = MyCalender.getDayName(MyCalender.getDayOfWeek());

        for (String day : MyCalender.getDaysOfWeek()) {
                Chip chip = new Chip(requireContext());
                chip.setText(day);
                chip.setCheckable(true);
                dialogBinding.chipGroup.addView(chip);
        }

        disableChipsBeforeToday(dialogBinding, today);

        dialogBinding.btnSelectDay.setOnClickListener(view -> {
            setSelectDays(dialogBinding);
            dialog.dismiss();
            Toast.makeText(requireContext(), "Meals ", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void setSelectDays(DialogSelectDayBinding dialogBinding) {
        for (int i = 0; i < dialogBinding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) dialogBinding.chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                String day = chip.getText().toString();
                presenter.savePlannedMeal(day, meal.getId());
                Toast.makeText(requireContext(),
                        meal.getName() + " Stored Successfully To "+ day
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static void disableChipsBeforeToday(DialogSelectDayBinding dialogBinding, String today) {
        for (int i = 0; i < dialogBinding.chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) dialogBinding.chipGroup.getChildAt(i);
            if(chip.getText().toString().equals(today))
                break;
            chip.setEnabled(false);
        }
    }

    @Override
    public void showErrorMsg(String error) {
        Toast.makeText(getContext(),  error, Toast.LENGTH_SHORT).show();
    }
}