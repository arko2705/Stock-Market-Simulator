import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class App extends Application {
    private HashMap<String, User> users = new HashMap<>();
    private User CurrentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Stock Portfolio Manager");
        showLoginPage(primaryStage);
        primaryStage.show();
    }
    private void showLoginPage(Stage stage) {
        stage.setTitle("Stock Portfolio Manager");

        // Registration/Login Scene
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button registerButton = new Button("Register");
        Button loginButton = new Button("Login");

        Label messageLabel = new Label();

        registerButton.setOnAction(new EventHandler
        <ActionEvent>() {
        @Override
        public void handle(ActionEvent e) {
            try {
                String username = usernameField.getText();
                LocalDate dob = dobPicker.getValue();
                String password = passwordField.getText();

                if (Period.between(dob, LocalDate.now()).getYears() < 18) {
                    throw new IllegalArgumentException("User must be at least 18 years old.");
                }

                if (username.isEmpty() || password.isEmpty()) {
                    throw new IllegalArgumentException("Username and Password must not be empty.");
            }

            users.put(username, new User(username, dob, password));
            messageLabel.setText("Registration successful! You can now login.");
            usernameField.clear();
            passwordField.clear();
            dobPicker.setValue(null);
            } catch (Exception ex) {
                messageLabel.setText("Registration failed: " + ex.getMessage());
            }
        }
        });

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                if (users.containsKey(username) && users.get(username).getPassword().equals(password)) {
                    CurrentUser = users.get(username);
                    showOptionsPage(stage);
                    
                } else {
                    messageLabel.setText("Login failed: Invalid username or password.");
                }
            }
        });

        loginBox.getChildren().addAll(new Label("Register or Login"), usernameField, dobPicker, passwordField, registerButton, loginButton, messageLabel);
        Scene loginScene = new Scene(loginBox, 600, 600);
        stage.setScene(loginScene);
        stage.show();
    }

    private void showOptionsPage(Stage stage) {
        VBox optionsBox = new VBox(10);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPadding(new Insets(20));

        Label optionsLabel = new Label("Choose an option:");
        Button buyStockButton = new Button("Buy Stock");
        Button viewPortfolioButton = new Button("View Portfolio");
        Button sellStockButton = new Button("Sell Stock");
        Button viewTransactionHistoryButton = new Button("View Transaction History");
        Button logoutButton = new Button("Logout");

        buyStockButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showBuyStockPage(stage);
            }
        });
        
        viewPortfolioButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showPortfolioPage(stage);
            }
        });
        
        sellStockButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showSellStockPage(stage);
            }
        });
        viewTransactionHistoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showTransactionHistoryPage(stage);
            }
        });
        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CurrentUser = null;
                showLoginPage(stage); 
            }
        });

        optionsBox.getChildren().addAll(optionsLabel, buyStockButton, viewPortfolioButton, sellStockButton, viewTransactionHistoryButton, logoutButton);
        Scene optionsScene = new Scene(optionsBox, 400, 300);
        stage.setScene(optionsScene);
    }

    private void showBuyStockPage(Stage stage) {
        VBox buyStockBox = new VBox(10);
        buyStockBox.setAlignment(Pos.CENTER);
        buyStockBox.setPadding(new Insets(20));

        // Example stocks
        Stock[] availableStocks = {
            new Stock("Stock A", 50.0, 0),
            new Stock("Stock B", 75.0, 0),
            new Stock("Stock C", 100.0, 0),
            new Stock("Stock D", 150.0, 0),
            new Stock("Stock E", 200.0, 0), 
            new Stock("Stock F", 250.0, 0),
            new Stock("Stock G", 300.0, 0),
            new Stock("Stock H", 350.0, 0),
            new Stock("Stock I", 400.0, 0),
            new Stock("Stock J", 450.0, 0)
        };

        for (Stock stock : availableStocks) {
            HBox stockRow = new HBox(10);
            Label stockLabel = new Label(stock.getName() + " - Price: $" + stock.getPrice());
            TextField sharesField = new TextField();
            sharesField.setPromptText("Shares to buy");

            Button buyButton = new Button("Buy");
            buyButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        int sharesToBuy = Integer.parseInt(sharesField.getText());
                        CurrentUser.getPortfolio().addStock(stock, sharesToBuy);
                        CurrentUser.getTransactionHistory().add(new Transaction("Bought " + sharesToBuy + " shares of " + stock.getName()));
                        sharesField.clear();
                    } catch (NumberFormatException ex) {
                        showAlert("Error", "Please enter a valid number of shares.");
                    }
                }
            });

            stockRow.getChildren().addAll(stockLabel, sharesField, buyButton);
            buyStockBox.getChildren().add(stockRow);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showOptionsPage(stage);
            }
        });

        buyStockBox.getChildren().add(backButton);
        Scene buyStockScene = new Scene(buyStockBox, 400, 300);
        stage.setScene(buyStockScene);
    }

    private void showPortfolioPage(Stage stage) {
        VBox portfolioBox = new VBox(10);
        portfolioBox.setAlignment(Pos.CENTER);
        portfolioBox.setPadding(new Insets(20));

        Label portfolioLabel = new Label("Portfolio Overview");
        for (Stock stock : CurrentUser.getPortfolio().getStocks()) {
            Label stockLabel = new Label(stock.getName() + " - Shares: " + stock.getShares() + " - Total: $" + stock.getTotalPrice());
            portfolioBox.getChildren().add(stockLabel);
        }

        double totalMoneyInvested = CurrentUser.getPortfolio().getTotalInvested();
        Label totalMoneyLabel = new Label("Total Money Invested: $" + totalMoneyInvested);
        portfolioBox.getChildren().addAll(portfolioLabel, totalMoneyLabel);
        
        Button backButton = new Button("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showOptionsPage(stage);
            }
        });
        portfolioBox.getChildren().add(backButton);
        
        Scene portfolioScene = new Scene(portfolioBox, 400, 300);
        stage.setScene(portfolioScene);
    }

    private void showSellStockPage(Stage stage) {
        VBox sellStockBox = new VBox(10);
        sellStockBox.setAlignment(Pos.CENTER);
        sellStockBox.setPadding(new Insets(20));

        for (Stock stock : CurrentUser.getPortfolio().getStocks()) {
            HBox stockRow = new HBox(10);
            Label stockLabel = new Label(stock.getName() + " - Shares: " + stock.getShares());
            TextField sharesField = new TextField();
            sharesField.setPromptText("Shares to sell");

            Button sellButton = new Button("Sell");
            sellButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        int sharesToSell = Integer.parseInt(sharesField.getText());
                        CurrentUser.getPortfolio().sellStock(stock, sharesToSell);
                        CurrentUser.getTransactionHistory().add(new Transaction("Sold " + sharesToSell + " shares of " + stock.getName()));
                        sharesField.clear();
                    } catch (IllegalArgumentException ex) {
                        showAlert("Error", ex.getMessage());
                    }
                }
            });

            stockRow.getChildren().addAll(stockLabel, sharesField, sellButton);
            sellStockBox.getChildren().add(stockRow);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showOptionsPage(stage);
            }
        });
        sellStockBox.getChildren().add(backButton);
        
        Scene sellStockScene = new Scene(sellStockBox, 400, 300);
        stage.setScene(sellStockScene);
    }
    private void showTransactionHistoryPage(Stage stage) {
        VBox transactionHistoryBox = new VBox(10);
        transactionHistoryBox.setAlignment(Pos.CENTER);
        transactionHistoryBox.setPadding(new Insets(20));
    
        Label historyLabel = new Label("Transaction History");
        transactionHistoryBox.getChildren().add(historyLabel);
    
        for (Transaction transaction : CurrentUser.getTransactionHistory()) {
            Label transactionLabel = new Label(transaction.getDetails());
            transactionHistoryBox.getChildren().add(transactionLabel);
        }
    
        Button backButton = new Button("Back");
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showOptionsPage(stage);
            }
        });
    
        transactionHistoryBox.getChildren().add(backButton);
        Scene transactionHistoryScene = new Scene(transactionHistoryBox, 400, 300);
        stage.setScene(transactionHistoryScene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
// Supporting Classes
// User Class
class User {
    private String username;
    private LocalDate dob;
    private String password;
    private Portfolio portfolio;
    private ArrayList<Transaction> transactionHistory;

    public User(String username, LocalDate dob, String password) {
        this.username = username;
        this.dob = dob;
        this.password = password;
        this.portfolio = new Portfolio();
        this.transactionHistory = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getPassword() {
        return password;
    }
    public Portfolio getPortfolio() {
        return portfolio;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
}

// Stock Class
class Stock {
    private String name;
    private double price;
    private int shares;

    public Stock(String name, double price, int shares) {
        this.name = name;
        this.price = price;
        this.shares = shares;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getShares() {
        return shares;
    }

    public double getTotalPrice() {
        return price * shares;
    }

    public void addShares(int amount) {
        shares += amount;
    }

    public void removeShares(int amount) {
        if (amount > shares) {
            throw new IllegalArgumentException("Not enough shares to sell.");
        }
        shares -= amount;
    }
}

// Transaction Class
class Transaction {
    private String details;

    public Transaction(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}

// Portfolio Class
class Portfolio {
    private HashMap<String, Stock> stocks;

    public Portfolio() {
        this.stocks = new HashMap<>();
    }

    public void addStock(Stock stock, int amount) {
        if (stocks.containsKey(stock.getName())) {
            stocks.get(stock.getName()).addShares(amount);
        } else {
            stock.addShares(amount);
            stocks.put(stock.getName(), stock);
        }
    }

    public void sellStock(Stock stock, int amount) {
        stocks.get(stock.getName()).removeShares(amount);
        if (stocks.get(stock.getName()).getShares() == 0) {
            stocks.remove(stock.getName());
        }
    }

    public List<Stock> getStocks() {
        return new ArrayList<>(stocks.values());
    }

    public double getTotalInvested() {
        double total = 0.0;
        
        
        for (Stock stock : stocks.values()) {
            total += stock.getTotalPrice(); 
        }
        
        return total; 
    }
}