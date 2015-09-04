package org.nutritionfacts.dailydozen.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.nutritionfacts.dailydozen.R;
import org.nutritionfacts.dailydozen.data.DataManager;
import org.nutritionfacts.dailydozen.db.DBConsumption;
import org.nutritionfacts.dailydozen.food.FoodType;


public class FoodTypeDetailFragment extends DialogFragment {

    private static final String ARG_CONSUMPTION_ID = "FoodTypeDetailFragment.ARG_CONSUMPTION_ID";

    private OnConsumedServingChangedListener mListener;
    private DBConsumption consumption;
    private FoodType foodType;

    private boolean isDismissing;

    public static FoodTypeDetailFragment newInstance(long consumptionId) {
        FoodTypeDetailFragment fragment = new FoodTypeDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CONSUMPTION_ID, consumptionId);
        fragment.setArguments(args);
        return fragment;
    }

    public FoodTypeDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            long id = getArguments().getLong(ARG_CONSUMPTION_ID);
            consumption = DataManager.getInstance().getConsumption(id);
            foodType = consumption.foodType;
        }

        if (consumption == null || foodType == null) {
            isDismissing = true;
            dismiss();
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_food_type_detail, container, false);

        if (isDismissing) {
            return view;
        }

        TextView textView = (TextView) view.findViewById(R.id.title_text_view);
        textView.setText(foodType.name);

        ImageView imageView = (ImageView)view.findViewById(R.id.image_view);
        imageView.setImageDrawable(getResources().getDrawable(foodType.overviewImageResourceId));

        final EditText servingCountEditText = (EditText) view.findViewById(R.id.servings_edit_text);
        servingCountEditText.setText((String.valueOf(Math.round(consumption.getConsumedServingCount()))));
        servingCountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String inputtedString = servingCountEditText.getText().toString();

                int newServingCount = 0;
                try {
                    Double inputtedValue = Double.parseDouble(inputtedString);
                    newServingCount = inputtedValue.intValue();
                } catch (Exception e) {

                }

                DataManager.getInstance().setServingCount(consumption, newServingCount);
                mListener.onConsumedServingChanged(consumption.getId());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Spanned spanned;

        textView = (TextView) view.findViewById(R.id.recommended_servings_text_view);
        if (foodType.recommendedServingCount != null && foodType.recommendedServingCount >= 0.0) {

            spanned = Html.fromHtml(
                    "<b>" +
                            getResources().getString(R.string.recommendation) +
                            "</b> " +
                            getResources().getQuantityString(
                                    R.plurals.daily_serving,
                                    (int) Math.round(foodType.recommendedServingCount),
                                    (int) Math.round(foodType.recommendedServingCount)));
            textView.setText(spanned);
        } else {
            textView.setVisibility(View.GONE);
        }

        textView = (TextView) view.findViewById(R.id.serving_example_text_view);

        spanned = Html.fromHtml(
                "<b>" +
                        getResources().getString(R.string.one_serving_title) +
                        "</b><br />" + foodType.servingExample.replace("\n", "<br />"));
        textView.setText(spanned);


        textView = (TextView) view.findViewById(R.id.example_1_text_view);

        if (!foodType.exampleTitles.isEmpty()) {
            spanned = Html.fromHtml(
                    "<b>" +
                            foodType.exampleTitles.get(0) +
                            "</b> " + foodType.exampleBodies.get(0));
            textView.setText(spanned);
        } else {
            textView.setVisibility(View.GONE);
        }

        textView = (TextView) view.findViewById(R.id.example_2_text_view);

        if (foodType.exampleTitles.size() == 2) {

            spanned = Html.fromHtml(
                    "<b>" +
                            foodType.exampleTitles.get(1) +
                            "</b> " + foodType.exampleBodies.get(1));
            textView.setText(spanned);
        } else {
            textView.setVisibility(View.GONE);
        }

        textView = (TextView) view.findViewById(R.id.minus_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        textView = (TextView) view.findViewById(R.id.plus_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int servingCount = consumption.getConsumedServingCount() == null ? 0 : (int)Math.round(consumption.getConsumedServingCount());

                double newServingCount = servingCount+1;

                servingCountEditText.setText(String.valueOf((int)newServingCount));

                DataManager.getInstance().setServingCount(consumption, newServingCount);

                mListener.onConsumedServingChanged(consumption.getId());
            }
        });

        textView = (TextView) view.findViewById(R.id.minus_button);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int servingCount = consumption.getConsumedServingCount() == null ? 0 : (int)Math.round(consumption.getConsumedServingCount());

                double newServingCount = servingCount-1;

                if (newServingCount < 0.0) {
                    newServingCount = 0.0;
                }

                servingCountEditText.setText(String.valueOf((int)newServingCount));

                DataManager.getInstance().setServingCount(consumption, newServingCount);

                mListener.onConsumedServingChanged(consumption.getId());
            }
        });

        Button dismissButton = (Button) view.findViewById(R.id.dismiss_button);
        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
        /*
        Dialog dialog = new android.app.AlertDialog.Builder(getActivity())
                .setTitle(foodType.name)
                .setView(view)
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        return dialog;*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnConsumedServingChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnConsumedServingChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnConsumedServingChangedListener {
        void onConsumedServingChanged(long consumptionId);
    }

}
