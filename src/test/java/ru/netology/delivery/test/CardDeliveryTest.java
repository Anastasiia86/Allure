package ru.netology.delivery.test;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;


import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeAll
    public static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    //Тестируемая функциональность: если заполнить форму
    //повторно теми же данными за исключением "Даты встречи",
    //то система предложит перепланировать время встречи.
    @Test
    public void shouldSuccessfulFormSubmission() {
        open("http://localhost:9999");
        DataGenerator.UserData userData = DataGenerator.Registration.generateUser("Ru");
        //Заполнение и первоначальная отправка формы:
        $("[data-test-id=city] input").setValue(userData.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        //Запланированная дата (текущая дата + 4 дня):
        String scheduledDate = DataGenerator.generateDate(4);
        $("[data-test-id=date] input").setValue(scheduledDate);
        $("[data-test-id=name] input").setValue(userData.getName());
        $("[data-test-id=phone] input").setValue(userData.getPhone());
        $("[data-test-id=agreement]").click();
        $(".button").shouldHave(Condition.text("Запланировать")).click();
        //Проверка на видимость, содержание текста и время загрузки:
        $("[data-test-id=success-notification]").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + scheduledDate),
                        Duration.ofSeconds(15));
        //Изменение ранне введенной даты и отправка формы:
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        //Перенесенная дата (текущая дата + 14 дней):
        String rescheduledDate = DataGenerator.generateDate(14);
        $("[data-test-id=date] input").setValue(rescheduledDate);
        $(".button").shouldHave(Condition.text("Запланировать")).click();
        //Взаимодействие с опцией перепланировки,
        //а также проверка на видимость, содержание текста и время загрузки:
        $("[data-test-id=replan-notification]").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Необходимо подтверждение" +
                                " У вас уже запланирована встреча на другую дату. Перепланировать?"),
                        Duration.ofSeconds(15));
        $("[data-test-id=replan-notification] .button")
                .shouldHave(Condition.text("Перепланировать")).click();
        //Итоговая проверка на видимость, содержание текста и время загрузки:
        $("[data-test-id=success-notification]").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Успешно! Встреча успешно запланирована на " + rescheduledDate),
                        Duration.ofSeconds(15));
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }
}