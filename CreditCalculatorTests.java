import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayName("Тестовый набор Кредитный калькулятор")
public class CreditCalculatorTests {

    @BeforeAll
    static void before_all(TestInfo test_info) {
        System.out.println(test_info.getDisplayName() + " - начали выполнение.");
        Configuration.browser = "firefox";
        open("https://slqamsk.github.io/cases/loan-calc/v01/");
    }
    @AfterAll
    static void after_all(TestInfo test_info) {
        System.out.println(test_info.getDisplayName() + " - закончили выполнение.");
    }

    @BeforeEach
    void before_each(TestInfo test_info) {
        System.out.println("Тест " + test_info.getDisplayName() + " - начали выполнение.");
    }

    @AfterEach
    void after_each(TestInfo test_info) {
        //closeWindow();
        System.out.println("Тест " + test_info.getDisplayName() + " - закончили выполнение.\n");
    }
    @Test
    @DisplayName("01. FR-02 Проверка блока ввода параметров")
    void test01_fr_02() {
        $("h1").shouldBe(visible);
        $("h2").shouldNotBe(visible);
        $("h1").shouldHave(text("Калькулятор кредита"));
        $("#amount").shouldBe(visible);
        $("#amount").getAttribute("placeholder").equals("100000");

        $("#amount-hint").shouldBe(visible);
        $("#amount-hint").shouldHave(text("Введите сумму от 1000 до 10000000 рублей"));
        $x("//*[@for='amount']").shouldHave(text("Сумма кредита, руб."));
        $("#term").shouldBe(visible);
        //$("#term").shouldHave(text("12"));
        $x("//*[@for='term']").shouldHave(text("Срок кредита, месяцев"));
        $("#term-hint").shouldBe(visible);
        $("#term-hint").shouldHave(text("Введите срок от 1 до 360 месяцев"));
        $("#rate").shouldBe(visible);
        //$("#rate").shouldHave(text("15.5"));
        $x("//*[@for='rate']").shouldHave(text("Процентная ставка, % годовых"));
        $("#rate-hint").shouldBe(visible);
        $("#rate-hint").shouldHave(text("Введите ставку в процентах: от 0.01 до 100"));
        $(".payment-type").shouldHave(text("Аннуитетный"));
        $(".payment-type").shouldHave(text("Дифференцированный"));
    }

    @Test
    @DisplayName("02. Расчет суммы аннуитетного платежа, открытие графика платежей")
    void test02_annuitet() {
        $("#amount").setValue("300000");
        $("#term").setValue("6");
        $("#rate").setValue("17");
        $("#calculate-btn").click();
        //SelenideElement modalWindow = $(By.xpath("./*[text()='Идёт расчёт, подождите...']")).shouldBe(visible);
        sleep(6_000);
        $("h1").shouldNotBe(visible);
        $("h2").shouldBe(visible);
        $("h2").shouldHave(text("Результаты расчёта"));
        $("#result-amount").shouldBe(visible);
        $("#result-amount").shouldHave(text("300000"));
        $("#result-term").shouldBe(visible);
        $("#result-term").shouldHave(text("6"));
        $("#result-rate").shouldBe(visible);
        $("#result-rate").shouldHave(text("17"));
        $("#result-payment-type").shouldBe(visible);
        $("#result-payment-type").shouldHave(text("Аннуитетный"));
        $("#monthly-payment").shouldBe(visible);
        $("#monthly-payment").shouldHave(text("52508.23"));
        $("#overpayment").shouldBe(visible);
        $("#total-payment").shouldBe(visible);
        $("#show-schedule-btn").click();
        Selenide.switchTo().window("График платежей");
        $("h2").shouldBe(visible);
        $("h2").shouldHave(text("График платежей"));
        $("table").$("tbody").$$("tr").get(6).$$("td").get(4).equals("0");
        Selenide.closeWindow();
        Selenide.switchTo().window("Калькулятор кредита");
        $("#new-calculation-btn").click();
    }

    @Test
    @DisplayName("03. Расчет дифференцированных платежей + проверка FR08, открытие графика платежей")
    void test03_differen() {
        //согласно FR-08 значения из предыдущего расчета автоматически подставляются обратно в активные поля ввода
        $x("//*[@value='diff']").click();
        $("#calculate-btn").click();
        //SelenideElement modalWindow = $(By.xpath("./*[text()='Идёт расчёт, подождите...']")).shouldBe(visible);
        sleep(6_000);
        $("h1").shouldNotBe(visible);
        $("h2").shouldBe(visible);
        $("h2").shouldHave(text("Результаты расчёта"));
        $("#result-amount").shouldBe(visible);
        $("#result-amount").shouldHave(text("300000"));
        $("#result-term").shouldBe(visible);
        $("#result-term").shouldHave(text("6"));
        $("#result-rate").shouldBe(visible);
        $("#result-rate").shouldHave(text("17"));
        $("#result-payment-type").shouldBe(visible);
        $("#result-payment-type").shouldHave(text("Дифференцированный"));
        $("#first-payment").shouldBe(visible);
        $("#first-payment").shouldHave(text("54250.00"));
        $("#last-payment").shouldBe(visible);
        $("#last-payment").shouldHave(text("50708.33"));
        $("#overpayment").shouldBe(visible);
        $("#total-payment").shouldBe(visible);
        $("#show-schedule-btn").click();
        Selenide.switchTo().window("График платежей");
        $("h2").shouldBe(visible);
        $("h2").shouldHave(text("График платежей"));
        $("table").$("tbody").$$("tr").get(6).$$("td").get(4).equals("0");
        Selenide.closeWindow();
        Selenide.switchTo().window("Калькулятор кредита");
        $("#new-calculation-btn").click();
        $("#clear-btn").click();
    }
    @ParameterizedTest(name = "Граничные значения сумм - положительный результат, итерация #{index}, summa: {0}")
    @ValueSource(strings = {"1000", "10000000"})
    void test03_success_gran_sum(String summa) {
        $("#amount").setValue(summa);
        $("#term").setValue("12");
        $("#rate").setValue("15.5");
        $("#calculate-btn").click();
        $("#results-container").shouldBe(exist);
        sleep(6_000);
        $("#result-amount").shouldBe(visible);
        $("#result-amount").shouldHave(text(summa));
        $("#result-term").shouldBe(visible);
        $("#result-term").shouldHave(text("12"));
        $("#result-rate").shouldBe(visible);
        $("#result-rate").shouldHave(text("15.5"));
        $("#result-payment-type").shouldBe(visible);
        $("#result-payment-type").shouldHave(text("Аннуитетный"));
        $("#monthly-payment").shouldBe(visible);
        $("#overpayment").shouldBe(visible);
        $("#total-payment").shouldBe(visible);
        $("#new-calculation-btn").click();
    }

}
