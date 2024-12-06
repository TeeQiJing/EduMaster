package com.practical.edumasters.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.practical.edumasters.R;
import com.practical.edumasters.models.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFragment extends Fragment {
    private static final String TAG = "QuizFragment";
    private FirebaseFirestore db;
    private TextView questionTitle;
    private ImageView questionImage;
    private Button optionA, optionB, optionC, optionD, nextButton, previousButton;
    private String quizId;
    private String userId;
    private List<Question> questions = new ArrayList<>();
    private List<String> questionIds = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private boolean isAnswered = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Bind views
        questionTitle = view.findViewById(R.id.questionTitle);
        questionImage = view.findViewById(R.id.questionImage);
        optionA = view.findViewById(R.id.optionA);
        optionB = view.findViewById(R.id.optionB);
        optionC = view.findViewById(R.id.optionC);
        optionD = view.findViewById(R.id.optionD);
        nextButton = view.findViewById(R.id.nextButton);
        previousButton = view.findViewById(R.id.previousButton);

        // Get quizId and userId from arguments
        quizId = getArguments() != null ? getArguments().getString("quizId") : null;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "Received quizId: " + quizId);
        Log.d(TAG, "Received userId: " + userId);

        if (quizId != null && userId != null) {
            loadQuizData(quizId);
        } else {
            Log.e(TAG, "Quiz ID or User ID is missing");
            Toast.makeText(getContext(), "Quiz ID or User ID is missing", Toast.LENGTH_SHORT).show();
        }

        // Handle navigation buttons
        nextButton.setOnClickListener(v -> {
            if (currentQuestionIndex < questions.size() - 1) {
                currentQuestionIndex++;
                displayQuestion(questions.get(currentQuestionIndex));
            }
        });

        previousButton.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayQuestion(questions.get(currentQuestionIndex));
            }
        });

        return view;
    }

    private void loadQuizData(String quizId) {
        Log.d(TAG, "Loading quiz data for quizId: " + quizId);

        DocumentReference quizReference = db.collection("quiz").document(quizId);

        db.collection("questions")
                .whereEqualTo("ref", quizReference)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "Successfully loaded quiz data");
                        for (DocumentSnapshot document : task.getResult()) {
                            Question question = document.toObject(Question.class);
                            if (question != null) {
                                questions.add(question);
                                questionIds.add(document.getId()); // Store the question ID
                                Log.d(TAG, "Loaded question: " + question.getTitle() + " with ID: " + document.getId());
                            }
                        }

                        if (!questions.isEmpty()) {
                            displayQuestion(questions.get(0)); // Display the first question
                        } else {
                            Log.e(TAG, "No questions found for this quiz");
                            Toast.makeText(getContext(), "No questions found for this quiz.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Failed to load quiz data", task.getException());
                        Toast.makeText(getContext(), "Failed to load quiz data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayQuestion(Question question) {
        Log.d(TAG, "Displaying question: " + question.getTitle());

        // Reset state for new question
        isAnswered = false;
        resetButtonColors();

        // Update visibility of navigation buttons
        nextButton.setVisibility(currentQuestionIndex < questions.size() - 1 ? View.VISIBLE : View.GONE);
        previousButton.setVisibility(currentQuestionIndex > 0 ? View.VISIBLE : View.GONE);

        // Set question title
        questionTitle.setText("Q" + (currentQuestionIndex + 1) + ". " + question.getTitle());

        // Load image if available
        String imageUrl = question.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            questionImage.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(imageUrl).into(questionImage);
        } else {
            questionImage.setVisibility(View.GONE);
        }

        // Set options
        List<String> options = question.getOptions();
        if (options.size() >= 4) {
            optionA.setText(options.get(0));
            optionB.setText(options.get(1));
            optionC.setText(options.get(2));
            optionD.setText(options.get(3));

            optionA.setOnClickListener(v -> processAnswer(question, options.get(0), optionA));
            optionB.setOnClickListener(v -> processAnswer(question, options.get(1), optionB));
            optionC.setOnClickListener(v -> processAnswer(question, options.get(2), optionC));
            optionD.setOnClickListener(v -> processAnswer(question, options.get(3), optionD));
        }

        // Restore answer state if already answered
        restoreAnswerState(question);
    }


    private void processAnswer(Question question, String selectedAnswer, Button selectedButton) {
        if (isAnswered) return;

        isAnswered = true;
        resetButtonColors();

        saveUserAnswer(question, selectedAnswer);

        if (selectedAnswer.equals(question.getAnswer())) {
            selectedButton.setBackgroundColor(Color.GREEN);
        } else {
            selectedButton.setBackgroundColor(Color.RED);
            highlightCorrectAnswer(question);
        }

        nextButton.setVisibility(View.VISIBLE);
    }

    private void restoreAnswerState(Question question) {
        String questionId = questionIds.get(currentQuestionIndex);
        Log.d(TAG, "Restoring progress for question ID: " + questionId);

        db.collection("user_question")
                .whereEqualTo("userIdRef", db.collection("users").document(userId))
                .whereEqualTo("questionIdRef", db.collection("questions").document(questionId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        String selectedAnswer = document.getString("selectedAnswer");
                        Boolean isCorrect = document.getBoolean("isCorrect");

                        if (selectedAnswer != null && isCorrect != null) {
                            highlightAnswer(question, selectedAnswer, isCorrect);
                            disableOptions();
                        }
                    }
                });
    }

    private void saveUserAnswer(Question question, String selectedAnswer) {
        String questionId = questionIds.get(currentQuestionIndex);
        DocumentReference userRef = db.collection("users").document(userId);
        DocumentReference questionRef = db.collection("questions").document(questionId);

        Map<String, Object> userAnswerData = new HashMap<>();
        userAnswerData.put("userIdRef", userRef);
        userAnswerData.put("questionIdRef", questionRef);
        userAnswerData.put("selectedAnswer", selectedAnswer);
        userAnswerData.put("isCorrect", selectedAnswer.equals(question.getAnswer()));

        db.collection("user_question")
                .add(userAnswerData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Answer saved successfully.");
                    } else {
                        Log.e(TAG, "Failed to save answer", task.getException());
                    }
                });
    }

    private void highlightAnswer(Question question, String selectedAnswer, boolean isCorrect) {
        if (optionA.getText().equals(selectedAnswer)) optionA.setBackgroundColor(isCorrect ? Color.GREEN : Color.RED);
        if (optionB.getText().equals(selectedAnswer)) optionB.setBackgroundColor(isCorrect ? Color.GREEN : Color.RED);
        if (optionC.getText().equals(selectedAnswer)) optionC.setBackgroundColor(isCorrect ? Color.GREEN : Color.RED);
        if (optionD.getText().equals(selectedAnswer)) optionD.setBackgroundColor(isCorrect ? Color.GREEN : Color.RED);
        highlightCorrectAnswer(question);
    }

    private void highlightCorrectAnswer(Question question) {
        if (optionA.getText().equals(question.getAnswer())) optionA.setBackgroundColor(Color.GREEN);
        if (optionB.getText().equals(question.getAnswer())) optionB.setBackgroundColor(Color.GREEN);
        if (optionC.getText().equals(question.getAnswer())) optionC.setBackgroundColor(Color.GREEN);
        if (optionD.getText().equals(question.getAnswer())) optionD.setBackgroundColor(Color.GREEN);
    }

    private void resetButtonColors() {
        int purpleColor = ContextCompat.getColor(getContext(), R.color.purple_500);

        // Reset the background color of each option button
        optionA.setBackgroundColor(purpleColor);
        optionB.setBackgroundColor(purpleColor);
        optionC.setBackgroundColor(purpleColor);
        optionD.setBackgroundColor(purpleColor);

        // Ensure text color is consistent (optional, if needed)
        optionA.setTextColor(Color.WHITE);
        optionB.setTextColor(Color.WHITE);
        optionC.setTextColor(Color.WHITE);
        optionD.setTextColor(Color.WHITE);
    }

    private void disableOptions() {
        optionA.setEnabled(false);
        optionB.setEnabled(false);
        optionC.setEnabled(false);
        optionD.setEnabled(false);
    }
}
