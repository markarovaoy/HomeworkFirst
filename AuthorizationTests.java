import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
public class AuthorizationTests {
    @Test
    void test01_login_success() {
        open("https://www.saucedemo.com/");
        $("#user-name").type("standard_user");
        $("#password").setValue("secret_sauce");
        $("#login-button").click();
        sleep(5000);
        $("body").shouldHave(text("Products"));
    }
    @Test
    void test02_login_wrong_password() {
        open("https://www.saucedemo.com/");
        $("#user-name").type("standard_user");
        $("#password").setValue("parol_wrong");
        $("#login-button").click();
        sleep(5000);
        $("body").shouldHave(text("Epic sadface: Username and password do not match any user in this service"));
    }
    @Test
    void test03_login_wrong_username() {
        open("https://www.saucedemo.com/");
        $("#user-name").type("olga");
        $("#password").setValue("secret_sauce");
        $("#login-button").click();
        sleep(5000);
        $("h3").shouldHave(text("Epic sadface: Username and password do not match any user in this service"));
    }
}
