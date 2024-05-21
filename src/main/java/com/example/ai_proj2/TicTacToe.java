package com.example.ai_proj2;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class TicTacToe extends Application {
    private char[][] board = { { ' ', ' ', ' ' }, { ' ', ' ', ' ' }, { ' ', ' ', ' ' } };

    private Random randomGenerator = new Random();
    private Stage primaryStage;
    private Label scoreLabel;
    private int playerWins = 0;
    private int aiWins = 0;
    private int totalGames = 0;
    private boolean isPlayerTurn = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label label = new Label("Tic-Tac-Toe Game AI");
        label.setStyle("-fx-text-fill:red;-fx-font-size:45;-fx-font-family:italy;-fx-effect:dropshadow(gaussian,black,10,0.5,3,3)");
        scoreLabel = new Label("Player: 0 | AI: 0 | Total Games: 0");
        scoreLabel.setStyle("-fx-text-fill:red;-fx-font-size:15;");

        VBox Box = new VBox(10, label, scoreLabel, gridPane);
        Box.setAlignment(Pos.CENTER);
        Box.setPadding(new Insets(10, 10, 10, 10));
        Box.setStyle("-fx-background-color:linear-gradient(to bottom,cyan,blue,darkblue);");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button(" ");
                button.setMinSize(100, 100);
                int finalI = i;
                int finalJ = j;
                button.setStyle("-fx-background-color:cyan;-fx-font-size:45;-fx-text-fill:red");
                button.setOnAction(e -> handleButtonClick(finalI, finalJ, button));
                button.setOnMouseEntered(e -> {
                    button.setStyle("-fx-background-color:linear-gradient(to bottom,red,cyan,blue);-fx-font-size:45");
                    button.setTextFill(button.getTextFill());
                });
                button.setOnMouseExited(e -> {
                    button.setStyle("-fx-background-color:cyan;-fx-font-size:45");
                    button.setTextFill(button.getTextFill());
                });
                gridPane.add(button, j, i);
            }
        }

        Scene scene = new Scene(Box, 500, 550);
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleButtonClick(int row, int col, Button button) {
        if (button.getText().isBlank()) {
            if (board[row][col] == ' ' && isPlayerTurn) {
                button.setText("X");
                board[row][col] = 'X';

                if (isGameWon('X')) {
                    showResult("Player wins!");
                    playerWins++;
                    totalGames++;
                    updateScoreLabel();
                    resetGame();
                    return;
                }
            }

            // Check for a draw
            if (isBoardFull()) {
                showResult("It's a draw!");
                totalGames++;
                updateScoreLabel();
                resetGame();
                return;
            }

            isPlayerTurn = !isPlayerTurn;

            if (!isPlayerTurn) {
                aiMakeMove();
            }
        }
    }

    private void aiMakeMove() {
        int[] aiMove = findBestMove(board, 'O');
        int row = aiMove[0];
        int col = aiMove[1];


        Button button = getButtonByRowColumn(row, col);
        if (button != null) {
            button.setText("O");
            button.setStyle("-fx-background-color:cyan;-fx-font-size:45;-fx-text-fill:green");

        }

        board[row][col] = 'O';

        if (isGameWon('O')) {
            showResult("AI wins!");
            aiWins++;
            totalGames++;
            updateScoreLabel();
            resetGame();
        } else if (isBoardFull()) {
            showResult("It's a draw!");
            totalGames++;
            updateScoreLabel();
            resetGame();
        }

        isPlayerTurn = true;
    }

    private Button getButtonByRowColumn(int row, int col) {
        GridPane gridPane = (GridPane) primaryStage.getScene().getRoot().getChildrenUnmodifiable().get(2);
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (Button) node;
            }
        }
        return null;
    }

    private void showResult(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isGameWon(char player) {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player)
                    || (board[0][i] == player && board[1][i] == player && board[2][i] == player)) {
                return true;
            }
        }

        // Check diagonals
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player)
                || (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetGame() {
        board = new char[][] { { ' ', ' ', ' ' }, { ' ', ' ', ' ' }, { ' ', ' ', ' ' } };

        GridPane gridPane = (GridPane) primaryStage.getScene().getRoot().getChildrenUnmodifiable().get(2);
        for (javafx.scene.Node node : gridPane.getChildren()) {
            ((Button) node).setText(" ");
            ((Button) node).setStyle("-fx-background-color:cyan;-fx-font-size:45;-fx-text-fill:red");

        }

        // Randomly determine who starts first
        isPlayerTurn = randomGenerator.nextBoolean();
        if (!isPlayerTurn) {
            // If AI starts first, make the initial move
            aiMakeMove();
        }
    }

    private int[] findBestMove(char[][] board, char player) {
        int[] bestMove = new int[] { -1, -1 };
        int bestScore = (player == 'O') ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = player;

                    int score = evaluateMove(board, 0, false);

                    board[i][j] = ' ';

                    if ((player == 'O' && score > bestScore) || (player == 'X' && score < bestScore)) {
                        bestScore = score;
                        bestMove[0] = i;
                        bestMove[1] = j;
                    }
                }
            }
        }

        return bestMove;
    }

    private int evaluateMove(char[][] board, int depth, boolean isMaximizing) {
        if (isGameWon(board, 'X'))
            return -1;
        if (isGameWon(board, 'O'))
            return 1;
        if (isBoardFull(board))
            return 0;

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'O';
                        maxScore = Math.max(maxScore, evaluateMove(board, depth + 1, false));
                        board[i][j] = ' ';
                    }
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == ' ') {
                        board[i][j] = 'X';
                        minScore = Math.min(minScore, evaluateMove(board, depth + 1, true));
                        board[i][j] = ' ';
                    }
                }
            }
            return minScore;
        }
    }

    private boolean isGameWon(char[][] board, char player) {
        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player)
                    || (board[0][i] == player && board[1][i] == player && board[2][i] == player)) {
                return true;
            }
        }

        // Check diagonals
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player)
                || (board[0][2] == player && board[1][1] == player && board[2][0] == player);
    }

    private boolean isBoardFull(char[][] board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Player: " + playerWins + " | AI: " + aiWins + " | Total Games: " + totalGames);
        if (totalGames == 5 ) {

            if (playerWins >= 3 || playerWins > aiWins) {
                showResult("Player wins the match!");
                resetScores();
            } else if (aiWins >= 3 || aiWins > playerWins) {
                showResult("AI wins the match!");
                resetScores();
            }else {
                showResult("match ended its drow!");
                resetScores();
            }

        }
    }

    private void resetScores() {
        playerWins = 0;
        aiWins = 0;
        totalGames = 0;
        updateScoreLabel();
    }
}
