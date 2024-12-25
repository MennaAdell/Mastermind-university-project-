import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameController {
    private final int codeLength = 4;
    private final int maxAttempts = 10;
    private final String[] colorNames = {"Red", "Green", "Blue", "Yellow", "Orange", "Purple"};
    private final Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.MAGENTA};
    private String[] secretCode;
    private int attemptsLeft;
    private JFrame frame;
    private JButton[] colorButtons;
    private JLabel feedbackLabel;
    private boolean firstWarningShown = false;

    public void startGame() {
        attemptsLeft = maxAttempts;
        secretCode = generateSecretCode();
        setupGUI();
    }


    private String[] generateSecretCode() {
        Random random = new Random();
        String[] code = new String[codeLength];
        for (int i = 0; i < codeLength; i++) {
            code[i] = colorNames[random.nextInt(colorNames.length)];
        }
        return code;
    }

    private void setupGUI() {
        frame = new JFrame("Mastermind Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); //space between elements

     //Guess Panel
        JPanel guessPanel = new JPanel();
        guessPanel.setLayout(new GridLayout(1, codeLength, 10, 10)); //space between squares
        colorButtons = new JButton[codeLength];

        for (int i = 0; i < codeLength; i++) {
            colorButtons[i] = new JButton();
            colorButtons[i].setBackground(Color.WHITE);
            colorButtons[i].setOpaque(true);
            colorButtons[i].setPreferredSize(new Dimension(200, 80));
            colorButtons[i].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            int index = i;
            colorButtons[i].addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    cycleColor(index);
                }
            });
            guessPanel.add(colorButtons[i]);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(guessPanel, gbc);


        JButton guessButton = new JButton("Submit");
        guessButton.setBackground(Color.GRAY);
        guessButton.setOpaque(true);
        guessButton.setBorderPainted(false);
        guessButton.setPreferredSize(new Dimension(200, 80));
        guessButton.setForeground(Color.WHITE);
        guessButton.setFont(new Font("Arial", Font.BOLD, 30));
        guessButton.addActionListener(new GuessButtonListener());

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(guessButton, gbc);


        feedbackLabel = new JLabel("Attempts left: " + attemptsLeft, SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 18));
        feedbackLabel.setForeground(Color.BLACK);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(feedbackLabel, gbc);


        frame.add(mainPanel, BorderLayout.CENTER);


        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private void cycleColor(int index) {
        Color currentColor = colorButtons[index].getBackground();
        int currentIndex = -1;
        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals(currentColor)) {
                currentIndex = i;
                break;
            }
        }


        int nextIndex = (currentIndex + 1) % colors.length;
        colorButtons[index].setBackground(colors[nextIndex]);
    }

    private class GuessButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (!areAllColorsSelected()) {
                if (!firstWarningShown) {
                    feedbackLabel.setText("Please select a color for all positions.");
                    firstWarningShown = true;
                }
                return;
            }

            firstWarningShown = false;

            String[] guess = new String[codeLength];
            for (int i = 0; i < codeLength; i++) {
                guess[i] = getColorName(colorButtons[i].getBackground());
            }
            checkGuess(guess);
        }
    }

    private boolean areAllColorsSelected() {
        for (JButton button : colorButtons) {
            if (button.getBackground().equals(Color.WHITE)) {
                return false;
            }
        }
        return true;
    }

    private void checkGuess(String[] guess) {
        if (attemptsLeft <= 0) {
            feedbackLabel.setText("Game Over! You've run out of attempts.");
            return;
        }

        int blackPegs = 0;
        int whitePegs = 0;
        boolean[] secretUsed = new boolean[codeLength];
        boolean[] guessUsed = new boolean[codeLength];


        for (int i = 0; i < codeLength; i++) {
            if (guess[i].equalsIgnoreCase(secretCode[i])) {
                blackPegs++;
                secretUsed[i] = true;
                guessUsed[i] = true;
            }
        }


        for (int i = 0; i < codeLength; i++) {
            if (!guessUsed[i]) {
                for (int j = 0; j < codeLength; j++) {
                    if (!secretUsed[j] && guess[i].equalsIgnoreCase(secretCode[j])) {
                        whitePegs++;
                        secretUsed[j] = true;
                        break;
                    }
                }
            }
        }

        if (blackPegs == codeLength) {
            feedbackLabel.setText("Congratulations! You've cracked the code!");
        } else {
            attemptsLeft--;
            feedbackLabel.setText("Black Pegs: " + blackPegs + ", White Pegs: " + whitePegs + ". Attempts left: " + attemptsLeft);
            if (attemptsLeft == 0) {
                feedbackLabel.setText("Game Over! The correct code was: " + String.join(", ", secretCode));
            }
        }
    }


    private String getColorName(Color color) {
        for (int i = 0; i < colors.length; i++) {
            if (colors[i].equals(color)) {
                return colorNames[i];
            }
        }
        return "Unknown";
    }
}