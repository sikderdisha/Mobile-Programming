package edu.disha.calculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView display;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnPlus, btnMinus, btnMul, btnDiv, btnEqual, btnPoint, btnDelete;
    private ImageButton btnHistory;
    private LinearLayout calculatorLayout, historyLayout;
    private ListView historyListView;
    private Button btnBackToCalc, btnClearHistory;

    private static final String PREFS_NAME = "CalculatorPrefs";
    private static final String HISTORY_KEY = "calculationHistory";
    private static final int MAX_HISTORY = 20;

    private ArrayList<String> historyList = new ArrayList<>();
    private ArrayAdapter<String> historyAdapter;
    private StringBuilder currentInput = new StringBuilder();
    private boolean isOperatorLastInput = false;
    private boolean isPointLastInput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();

        // Set click listeners
        setClickListeners();

        // Load history from SharedPreferences
        loadHistory();

        // Set up history adapter with custom layout for white text
        historyAdapter = new ArrayAdapter<>(this, R.layout.history_list_item, R.id.text1, historyList);
        historyListView.setAdapter(historyAdapter);

        // Show calculator layout initially
        showCalculatorLayout();
    }

    private void initializeViews() {
        display = findViewById(R.id.display);

        btn0 = findViewById(R.id.btn_0);
        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        btn3 = findViewById(R.id.btn_3);
        btn4 = findViewById(R.id.btn_4);
        btn5 = findViewById(R.id.btn_5);
        btn6 = findViewById(R.id.btn_6);
        btn7 = findViewById(R.id.btn_7);
        btn8 = findViewById(R.id.btn_8);
        btn9 = findViewById(R.id.btn_9);

        btnPlus = findViewById(R.id.btn_plus);
        btnMinus = findViewById(R.id.btn_minus);
        btnMul = findViewById(R.id.btn_mul);
        btnDiv = findViewById(R.id.btn_division);
        btnEqual = findViewById(R.id.equal);
        btnPoint = findViewById(R.id.btn_point);
        btnDelete = findViewById(R.id.delete);
        btnHistory = findViewById(R.id.btn_history);

        calculatorLayout = findViewById(R.id.calculator_layout);
        historyLayout = findViewById(R.id.history_layout);
        historyListView = findViewById(R.id.history_list);
        btnBackToCalc = findViewById(R.id.btn_back_to_calc);
        btnClearHistory = findViewById(R.id.btn_clear_history);
    }

    private void setClickListeners() {
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);

        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        btnMul.setOnClickListener(this);
        btnDiv.setOnClickListener(this);
        btnEqual.setOnClickListener(this);
        btnPoint.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnHistory.setOnClickListener(this);

        btnBackToCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalculatorLayout();
            }
        });

        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearHistoryConfirmation();
            }
        });

        // Set item click listener for history items
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String historyItem = historyList.get(position);
                // Extract the expression part (before "=")
                String[] parts = historyItem.split("=");
                if (parts.length > 1) {
                    String result = parts[1].trim();
                    currentInput = new StringBuilder(result);
                    showCalculatorLayout();
                    updateDisplay();
                }
            }
        });

        // Set long click listener for deleting individual history items
        historyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete Item");
                builder.setMessage("Do you want to delete this history item?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        historyList.remove(position);
                        historyAdapter.notifyDataSetChanged();
                        saveHistory();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                return true;
            }
        });
    }

    private void showCalculatorLayout() {
        calculatorLayout.setVisibility(View.VISIBLE);
        historyLayout.setVisibility(View.GONE);
    }

    private void showHistoryLayout() {
        calculatorLayout.setVisibility(View.GONE);
        historyLayout.setVisibility(View.VISIBLE);
    }

    private void showClearHistoryConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear History");
        builder.setMessage("Are you sure you want to clear all calculation history?");
        builder.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                historyList.clear();
                historyAdapter.notifyDataSetChanged();
                saveHistory();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_0) appendToInput("0");
        else if (id == R.id.btn_1) appendToInput("1");
        else if (id == R.id.btn_2) appendToInput("2");
        else if (id == R.id.btn_3) appendToInput("3");
        else if (id == R.id.btn_4) appendToInput("4");
        else if (id == R.id.btn_5) appendToInput("5");
        else if (id == R.id.btn_6) appendToInput("6");
        else if (id == R.id.btn_7) appendToInput("7");
        else if (id == R.id.btn_8) appendToInput("8");
        else if (id == R.id.btn_9) appendToInput("9");
        else if (id == R.id.btn_plus) handleOperator("+");
        else if (id == R.id.btn_minus) handleOperator("-");
        else if (id == R.id.btn_mul) handleOperator("×");
        else if (id == R.id.btn_division) handleOperator("÷");
        else if (id == R.id.btn_point) handlePoint();
        else if (id == R.id.delete) handleDelete();
        else if (id == R.id.equal) calculateResult();
        else if (id == R.id.btn_history) showHistoryLayout();
    }

    private void appendToInput(String digit) {
        currentInput.append(digit);
        isOperatorLastInput = false;
        isPointLastInput = false;
        updateDisplay();
    }

    private void handleOperator(String operator) {
        // Allow user to type multiple operators consecutively (no restriction)
        currentInput.append(operator);
        isOperatorLastInput = true;
        isPointLastInput = false;
        updateDisplay();
    }

    private void handlePoint() {
        if (isPointLastInput) {
            return;
        }

        // Check if we already have a decimal point in the current number
        String expression = currentInput.toString();
        int lastOperatorIndex = Math.max(
                Math.max(
                        expression.lastIndexOf("+"),
                        expression.lastIndexOf("-")
                ),
                Math.max(
                        expression.lastIndexOf("×"),
                        expression.lastIndexOf("÷")
                )
        );

        String currentNumber = expression.substring(Math.max(0, lastOperatorIndex + 1));

        if (!currentNumber.contains(".")) {
            // If the current input is empty or ends with an operator, append "0."
            if (currentInput.length() == 0 || isOperatorLastInput) {
                currentInput.append("0");
            }

            currentInput.append(".");
            isPointLastInput = true;
            isOperatorLastInput = false;
            updateDisplay();
        }
    }

    private void handleDelete() {
        if (currentInput.length() > 0) {
            char lastChar = currentInput.charAt(currentInput.length() - 1);
            currentInput.deleteCharAt(currentInput.length() - 1);

            // Update flags based on the new last character
            if (currentInput.length() > 0) {
                char newLastChar = currentInput.charAt(currentInput.length() - 1);
                isOperatorLastInput = newLastChar == '+' || newLastChar == '-' ||
                        newLastChar == '×' || newLastChar == '÷';
                isPointLastInput = newLastChar == '.';
            } else {
                isOperatorLastInput = false;
                isPointLastInput = false;
            }

            updateDisplay();
        }
    }

    private void calculateResult() {
        String expression = currentInput.toString();

        if (expression.isEmpty()) {
            return;
        }

        try {
            // Replace multiplication and division signs with operators recognizable by evaluator
            expression = expression.replace("×", "*");
            expression = expression.replace("÷", "/");

            double result = evaluateExpression(expression);

            String resultString;

            if (result == (long) result) {
                resultString = String.format("%d", (long) result);
            } else {
                resultString = String.format("%s", result);
            }

            addToHistory(currentInput.toString() + " = " + resultString);

            currentInput = new StringBuilder(resultString);
            isOperatorLastInput = false;
            isPointLastInput = resultString.contains(".");
            updateDisplay();
        } catch (Exception e) {
            display.setText("Error");
            addToHistory(currentInput.toString() + " = Error");
            currentInput.setLength(0);
            isOperatorLastInput = false;
            isPointLastInput = false;
        }
    }

    private void updateDisplay() {
        display.setText(currentInput.toString());
    }

    private void addToHistory(String entry) {
        if (historyList.size() == MAX_HISTORY) {
            historyList.remove(0);
        }
        historyList.add(entry);
        historyAdapter.notifyDataSetChanged();
        saveHistory();
    }

    private void saveHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> historySet = new HashSet<>(historyList);
        editor.putStringSet(HISTORY_KEY, historySet);
        editor.apply();
    }

    private void loadHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> historySet = prefs.getStringSet(HISTORY_KEY, null);

        if (historySet != null) {
            historyList.clear();
            historyList.addAll(historySet);
        }
    }

    // Basic expression evaluator (supports +, -, *, /)
    private double evaluateExpression(String expression) throws Exception {
        // Using a simple Shunting Yard algorithm or built-in JavaScript engine
        // Here, let's implement a simple evaluator using two stacks

        return simpleEvaluate(expression);
    }

    private double simpleEvaluate(String expression) throws Exception {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int i = 0;
        int n = expression.length();

        while (i < n) {
            char ch = expression.charAt(i);

            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            if (ch >= '0' && ch <= '9' || ch == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < n && ((expression.charAt(i) >= '0' && expression.charAt(i) <= '9') || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(sb.toString()));
                continue;
            }

            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                    double b = numbers.pop();
                    double a = numbers.pop();
                    char op = operators.pop();
                    numbers.push(applyOp(a, b, op));
                }
                operators.push(ch);
                i++;
                continue;
            }

            throw new Exception("Invalid character in expression");
        }

        while (!operators.isEmpty()) {
            double b = numbers.pop();
            double a = numbers.pop();
            char op = operators.pop();
            numbers.push(applyOp(a, b, op));
        }

        return numbers.pop();
    }

    private int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/') return 2;
        return 0;
    }

    private double applyOp(double a, double b, char op) throws Exception {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new Exception("Division by zero");
                return a / b;
        }
        throw new Exception("Unsupported operator");
    }
}
