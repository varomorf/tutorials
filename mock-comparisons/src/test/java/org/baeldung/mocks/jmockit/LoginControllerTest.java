package org.baeldung.mocks.mockito;

import org.baeldung.mocks.testCase.LoginController;
import org.baeldung.mocks.testCase.LoginDao;
import org.baeldung.mocks.testCase.LoginService;
import org.baeldung.mocks.testCase.UserForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

/**
 * <p>Test for LoginController using Mockito.</p>
 * Created by Alvaro on 12/06/2016.
 */
public class LoginControllerTest {

    @Injectable
    private LoginDao loginDao;

    @Injectable
    private LoginService loginService;

    @Tested
    private LoginController loginController;

    @Test
    public void assertThatNoMethodHasBeenCalled() {
        loginController.login(null);
        // no method called
        new FullVerifications(loginService) {};
    }

    @Test
    public void assertTwoMethodsHaveBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        new Expectations(){{
            loginService.login(userForm); result = true;
            loginService.setCurrentUser("foo");
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        new FullVerifications(loginService) {};
    }

    @Test
    public void assertOnlyOneMethodHasBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        new Expectations(){{
            loginService.login(userForm); result = false;
            // no expectation for setCurrentUser
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("KO", login);
        new FullVerifications(loginService) {};
    }

    @Test
    public void mockExceptionThrowing() {
        UserForm userForm = new UserForm();
        new Expectations(){{
            loginService.login(userForm); result = new IllegalArgumentException();
            // no expectation for setCurrentUser
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("ERROR", login);
        new FullVerifications(loginService) {};
    }

    @Test
    public void stubAnObjectToPassAround(@Mocked final UserForm userForm) {
        new Expectations(){{
            userForm.getUsername(); result = "foo";
            loginService.login(userForm); result = true;
            loginService.setCurrentUser("foo");
        }};
        
        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        new FullVerifications(loginService) {};
        new FullVerifications(userForm) {};
    }

    @Test
    public void argumentMatching() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        // default matcher
        new Expectations(){{
            loginService.login((UserForm) any)); result = true;
            // complex matcher
            loginService.setCurrentUser(withDelegate(new Delegate(){
                boolean delegate(Object argument) {
                    return argument instanceof String && ((String) argument).startsWith("foo");
            }));
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        new FullVerifications(loginService) {};
    }

    @Test
    public void partialMocking() {
        // use partial mock
        LoginService partialLoginService = new LoginService();
        partialLoginService.setLoginDao(loginDao);
        loginController.loginService = partialLoginService;
        
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        // let service's login use implementation so let's mock DAO call
         new Expectations(){{
            loginDao.login(userForm); result = 1;
            // no expectation for loginService.login
            partialLoginService.setCurrentUser("foo");
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        // verify mocked call
        new FullVerifications(partialLoginService) {};
        new FullVerifications(loginDao) {};
    }
}
