package org.baeldung.mocks.testCase;

public class LoginController {

    private LoginService loginService;

    public String login(UserForm userForm){
        if(null == userForm){
            return "ERROR";
        }else{
            boolean logged;

            try {
                logged = loginService.login(userForm);
            } catch (Exception e) {
                return "ERROR";
            }

            if(logged){
                loginService.setCurrentUser(userForm.username);
                return "OK ";
            }else{
                return "KO";
            }
        }
    }

    // standard setters and getters
}
