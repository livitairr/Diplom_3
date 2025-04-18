package test.ui;

import UiTests.TestsSetUp;
import data.UserData;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pages.LoginPage;
import pages.MainPage;
import pages.ProfilePage;
import pages.RegisterPage;
import steps.UserSteps;

import static org.junit.Assert.assertEquals;

@Epic("Навигация авторизованного пользователя на сайте")
public class NavigationTest extends TestsSetUp {

    private UserSteps userSteps;
    private Response loginResponse;
    private String email;
    private String password;

    private MainPage mainPage;
    private ProfilePage profilePage;
    private LoginPage loginPage;
    private RegisterPage registerPage;

    @Before
    @Step("Инициализация тестового окружения, создание пользователя и установка токена")
    @Description("Создаёт пользователя, авторизует его, устанавливает токены в localStorage и обновляет сессию")
    public void setUp() {
        super.setUp();
        userSteps = new UserSteps();
        mainPage = new MainPage(driver);
        profilePage = new ProfilePage(driver);
        loginPage = new LoginPage(driver);
        registerPage = new RegisterPage(driver);

        mainPage.open();
        var user = UserData.getValidUser();
        email = user.getEmail();
        password = user.getPassword();
        userSteps.createUser(user);
        loginResponse = userSteps.loginUser(user);
        userSteps.getAccessToken(loginResponse);
        userSteps.setTokenInLocalStorage(driver, userSteps.accessToken);
        userSteps.setRefreshTokenInLocalStorage(driver, userSteps.refreshToken);
        driver.navigate().refresh();
    }

    @Test
    @DisplayName("Переход в личный кабинет через кнопку 'Личный кабинет'")
    @Description("Тест проверяет, что нажатие кнопки 'Личный кабинет' ведет в личный кабинет")
    public void testNavigateToPersonalCabinet() {
        mainPage.clickToPersonalAccountFromMainPage();
        assertEquals(ProfilePage.PROFILE_URL, driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Переход из личного кабинета в конструктор через кнопку 'Конструктор'")
    @Description("Тест проверяет, что нажатие кнопки 'Конструктор' ведет из личного кабинета в конструктор")
    public void testNavigateToConstructorFromPersonalCabinetByClickConstructorButton() {
        mainPage.clickToPersonalAccountFromMainPage();
        profilePage.clickToConstructorButton();
        assertEquals(MainPage.BASE_URL, driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Переход из личного кабинета в конструктор через логотип Stellar Burgers")
    @Description("Тест проверяет, что нажатие логотипа Stellar Burgers ведет из личного кабинета в конструктор")
    public void testNavigateToConstructorFromPersonalCabinetUsingLogo() {
        mainPage.clickToPersonalAccountFromMainPage();
        profilePage.clickToLogoButton();
        assertEquals(MainPage.BASE_URL, driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Выход из аккаунта через кнопку 'Выход'")
    @Description("Тест проверяет, что нажатие кнопки 'Выход' в личном кабинете разлогинивает пользователя")
    public void testLogoutFromPersonalCabinet() {
        mainPage.clickToPersonalAccountFromMainPage();
        profilePage.clickLogoutButton();
        assertEquals(LoginPage.EXPECTED_LOGIN_TEXT, driver.findElement(RegisterPage.LOGIN_HEADER).getText());
    }

    @After
    @Step("Очистка данных после теста")
    @Description("Удаляет пользователя, созданного перед тестом")
    public void tearDown() {
        super.tearDown();
        if (loginResponse != null) {
            userSteps.getAccessToken(loginResponse);
            userSteps.deleteUser();
        }
    }
}
