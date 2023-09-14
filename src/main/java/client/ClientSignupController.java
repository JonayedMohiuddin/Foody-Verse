package client;

import dto.ClientSignUpDTO;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ClientSignupController
{
    public TextField usernameTextField;
    public PasswordField passwordTextField;
    public Label signUpMessageText;
    private ClientApplication application;

    public void setApplication(ClientApplication application)
    {
        this.application = application;
    }

    public void init()
    {
        signUpMessageText.setText("");
        resetStyle();
    }

    public void resetStyle()
    {
        usernameTextField.setStyle("");
        passwordTextField.setStyle("");
    }

    public void signupButtonClicked(ActionEvent event)
    {
        resetStyle();

        System.out.println("Signup Button Pressed");

        String name = usernameTextField.getText();
        String password = passwordTextField.getText();

        System.out.println("Name : " + name + " Password : " + password);

        if (name.equals("") || password.equals(""))
        {
            resetStyle();
            if (name.equals(""))
            {
                usernameTextField.setStyle("-fx-border-color: red");
            }
            if (password.equals(""))
            {
                passwordTextField.setStyle("-fx-border-color: red");
            }
            return;
        }

        try
        {
            application.getSocketWrapper().write(new ClientSignUpDTO(name, password));
        }
        catch (IOException e)
        {
            System.out.println("Class : ClientSignupController | Method : signupButtonClicked | While sending signup request to server");
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
        }

        try
        {
            ClientSignUpDTO clientSignUpDTO = (ClientSignUpDTO) application.getSocketWrapper().read();

            if (clientSignUpDTO.getSuccess())
            {
                System.out.println("Signup Successful");

                try
                {
                    application.showLoginPage();
                }
                catch (IOException e)
                {
                    System.out.println("Class : ClientSignupController | Method : signupButtonClicked | While showing login page");
                    System.out.println("Error : " + e.getMessage());
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Signup Unsuccessful");
                System.out.println("Message : " + clientSignUpDTO.getMessage());

                usernameTextField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");

                signUpMessageText.setText(clientSignUpDTO.getMessage());
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            System.out.println("Class : ClientSignupController | Method : signupButtonClicked | While reading signup response from server");
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
